package sg.edu.nus.CS3218ChngXinni;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;



public class CalculusActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private String   trigo;
    private double   sineAmplitude;
    private double   cosineAmplitude;
    public static double   sineFrequency;
    public static double   cosineFrequency;

    public static double  fundFreq;

    private double   sinePhase;
    private double   cosinePhase;
    private TextView displayFunction;
    private TextView integralTextView;

    public  CSurfaceViewCalculus   	surfaceView;
    public  static short[]  buffer;
    public  double[]        buffer_double;

    public  static int      bufferSize;     // in bytes
    public  static double   deltaTime;      // sampling interval for buffer

    private SeekBar     seekBar1;
    private TextView    seekBar1TextView;
    public  static int  seekBar1Value;
    public  static short[]  sig1;

    public  static short[]  productSig;
    public  double[]        productSig_double;
    public  static int  period;



    public void goToMainActivity(View view) {

        try
        {
            CSurfaceViewCalculus.drawFlag = Boolean.valueOf(false);
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
        setContentView(R.layout.activity_calculus);
        displayFunction = (TextView)findViewById(R.id.displayFunction);
        integralTextView = (TextView)findViewById(R.id.integralTextView);

        trigo = "sine";
        sineAmplitude=0.0;
        cosineAmplitude=0.0;
        sineFrequency=0.0;
        cosineFrequency=0.0;
        sinePhase = 0.0;
        cosinePhase=0.0;
        bufferSize = 1024;
        buffer = new short[bufferSize];
        buffer_double = new double[bufferSize];

        surfaceView = (CSurfaceViewCalculus)findViewById(R.id.surfaceView);

        seekBar1 = (SeekBar)findViewById(R.id.seekBar1); // make seekbar object
        seekBar1.setOnSeekBarChangeListener(this); // set seekbar listener.
        seekBar1TextView = (TextView)findViewById(R.id.seekBar1TextView);

        sig1              = new short[bufferSize];
        productSig        = new short[bufferSize];
        productSig_double = new double[bufferSize];

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sinePressed(View view) {
        trigo = "sine";
    }

    public void cosinePressed(View view) {
        trigo = "cosine";
    }

    public void AplusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sineAmplitude += 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosineAmplitude += 1.0;
        }

        composeSignal();

    }

    public void AminusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sineAmplitude -= 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosineAmplitude -= 1.0;
        }

        composeSignal();


    }

    public void FplusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sineFrequency += 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosineFrequency += 1.0;
        }

        composeSignal();

    }

    public void FminusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sineFrequency -= 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosineFrequency -= 1.0;
        }

        composeSignal();

    }

    public void PplusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sinePhase += 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosinePhase += 1.0;
        }

        composeSignal();

    }

    public void PminusPressed(View view){

        if (trigo.equalsIgnoreCase("sine")) {
            sinePhase -= 1.0;

        } else if (trigo.equalsIgnoreCase("cosine")) {
            cosinePhase -= 1.0;
        }

        composeSignal();

    }




    public void composeSignal() {

        displayFunction.setText(String.valueOf(sineAmplitude) + " sin( 2π" +
                String.valueOf(sineFrequency) + " + " + String.valueOf(sinePhase) + " )" + " + " +
                String.valueOf(cosineAmplitude) + " cos( 2π" +
                String.valueOf(cosineFrequency) + " + " + String.valueOf(cosinePhase) + " )");

        // define 1 second duration of signal
        deltaTime             = 1.0 / (double)bufferSize;  // sampling interval

        double radStepSine    = 2*Math.PI*sineFrequency   / (double)bufferSize;
        double radStepCosine  = 2*Math.PI*cosineFrequency / (double)bufferSize;
        double freqSine       = 0.0;
        double freqCosine     = 0.0;
        double sinePhaseRad   = sinePhase*Math.PI/180;
        double cosinePhaseRad = cosinePhase*Math.PI/180;
        for (int i=0; i<bufferSize; i++) {
            buffer_double[i] = sineAmplitude*Math.sin(freqSine + sinePhaseRad) + cosineAmplitude*Math.cos(freqCosine + cosinePhaseRad);
            buffer[i] = (short)buffer_double[i];
            freqSine   += radStepSine;
            freqCosine += radStepCosine;
        }
        surfaceView.drawThread.setBuffer(buffer);

        findFundFreq();
        period = (int)(1.0/fundFreq/deltaTime);

    }


    public void findFundFreq() {
        // compute the fundamental frequency of the composite signal (sine + cosine)
        // hint: need to use findGCD

        //--- missing codes -----

        this.fundFreq= findGCD((long)sineFrequency, (long)cosineFrequency);

    }

    /**** the following GCD code is taken from
     http://stackoverflow.com/questions/4201860/how-to-find-gcf-lcm-on-a-set-of-numbers
     ****/

    private static long findGCD(long a, long b)
    {
        while (b > 0)
        {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }



    private double integrateByTrapezium()
    {
        // do integration by sum of trapeziums here
        // the function to integrate is stored in productSig_double
        // time width of each trapezium is deltaTime.

        // ----- missing code ---------
        double sum = 0;
        double ans = 0;

        for (int i = 0; i < productSig_double.length; i++) {
            if (i == 0) { // a
                sum = productSig_double[i];
            } else if (i == productSig_double.length - 1) { // b
                sum = sum + productSig_double[i];
            } else { // in between
                sum = sum + (2 * productSig_double[i]);
            }
        }

        ans = deltaTime * 0.5 * sum;
        Log.e("Meow", "Integral is " + ans);
        return ans;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        seekBar1Value = progress;
        seekBar1TextView.setText("Frequency: "+ seekBar1Value);

        // define 1 second duration of signal
        double freq1     = (double)seekBar1Value;
        double radStep1  = 2*Math.PI*freq1   / (double)bufferSize;
        double rad = 0.0;
        for (int i=0; i<bufferSize; i++) {
            sig1[i] = (short)(sineAmplitude*Math.sin(rad));

            productSig_double[i] = buffer_double[i] * sineAmplitude * Math.sin(rad);
            productSig[i] = (short) productSig_double[i];

            rad    += radStep1;
        }

        // compute the integral of product of composite signal and basis function

        double integral = integrateByTrapezium();

        integralTextView.setText("integral = " + String.valueOf(integral));
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
    }

}

