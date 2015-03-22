package sg.edu.nus.CS3218ChngXinni;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SpectrogramActivity extends Activity {
    public  static          CSurfaceViewSpectrogram   	surfaceView;
    private SoundSamplerSpectrogram   	soundSamplerSpectrogram;
    public  static short[]  buffer;
    public  static int      bufferSize;     // in bytes

    public void goToMainActivity(View view){
        try
        {
            surfaceView.drawFlag = Boolean.valueOf(false);
            soundSamplerSpectrogram.destroyAudioRecord();
            soundSamplerSpectrogram = null;

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
        setContentView(R.layout.activity_spectrogram);

        try {
            soundSamplerSpectrogram = new SoundSamplerSpectrogram(this);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot instantiate SoundSamplerSpectrogram", Toast.LENGTH_LONG).show();
        }

        try {
            soundSamplerSpectrogram.init();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Cannot initialize SoundSamplerSpectrogram.", Toast.LENGTH_LONG).show();
        }

        surfaceView = (CSurfaceViewSpectrogram)findViewById(R.id.surfaceView);
        surfaceView.drawThread.setBuffer(buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    public void captureSoundSpectrogram(View v) {
        if (surfaceView.drawThread.soundCapture) {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(false);
            surfaceView.drawThread.segmentIndex = -1;
        }
        else {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(true);

        }
    }
}
