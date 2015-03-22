package sg.edu.nus.CS3218ChngXinni;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LiveFFTActivity extends Activity {
    public  static          CSurfaceViewLiveFFT   	surfaceView;
    private SoundSamplerLiveFFT   	soundSamplerLiveFFT;
    public  static short[]  buffer;
    public  static int      bufferSize;     // in bytes

    public void goToMainActivity(View view){
        try
        {
            surfaceView.drawFlag = Boolean.valueOf(false);

            soundSamplerLiveFFT = null;
            surfaceView.drawThread.join();

        }
        catch (InterruptedException localInterruptedException)
        {
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_fft);

        try {
            soundSamplerLiveFFT = new SoundSamplerLiveFFT(this);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot instantiate SoundSamplerLiveFFT", Toast.LENGTH_LONG).show();
        }

        try {
            soundSamplerLiveFFT.init();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Cannot initialize SoundSamplerLiveFFT.", Toast.LENGTH_LONG).show();
        }

        surfaceView = (CSurfaceViewLiveFFT)findViewById(R.id.surfaceView);
        surfaceView.drawThread.setBuffer(buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    public void captureSoundLiveFFT(View v) {
        if (surfaceView.drawThread.soundCapture) {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(false);
            surfaceView.drawThread.segmentIndex = -1;
        }
        else {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(true);

        }
    }
}
