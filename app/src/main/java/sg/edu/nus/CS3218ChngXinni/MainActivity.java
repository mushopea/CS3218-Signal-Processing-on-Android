package sg.edu.nus.CS3218ChngXinni;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button + fontawesome icon font
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome-webfont.ttf");
        Button calculatorButton = (Button) findViewById(R.id.calculatorbutton);
        calculatorButton.setTypeface(font);
        Button graphingButton = (Button) findViewById(R.id.graphingbutton);
        graphingButton.setTypeface(font);
        Button soundButton = (Button) findViewById(R.id.soundbutton);
        soundButton.setTypeface(font);
        Button calculusButton = (Button) findViewById(R.id.calculusbutton);
        calculusButton.setTypeface(font);
        Button fftButton = (Button) findViewById(R.id.fftbutton);
        fftButton.setTypeface(font);
        Button liveFftButton = (Button) findViewById(R.id.livefftbutton);
        liveFftButton.setTypeface(font);
        Button spectogramButton = (Button) findViewById(R.id.spectogrambutton);
        spectogramButton.setTypeface(font);

        // choose app text view + open sans font
        Typeface openSans = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");
        TextView chooseApp = (TextView) findViewById(R.id.chooseapptextview);
        chooseApp.setTypeface(openSans);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // CUSTOM FUNCTIONS START

    // go to calculator view when the calculator button is pressed
    public void goToCalculator(View view) {
        Log.e("Meow", "goToCalculator function triggered");

        Intent intent;
        intent = new Intent(this, CalculatorActivity.class);
        startActivity(intent);
    }

    // go to graph view when the graph button is pressed
    public void goToGraphing(View view) {
        Log.e("Meow", "goToGraphing function triggered");

        Intent intent2;
        intent2 = new Intent(this, GraphActivity.class);
        startActivity(intent2);
    }

    // go to sound view when the sound button is pressed
    public void goToSound(View view) {
        Log.e("Meow", "goToSound function triggered");

        Intent intent3;
        intent3 = new Intent(this, SoundActivity.class);
        startActivity(intent3);
    }

    // go to calc view when the calculus button is pressed
    public void goToCalculus(View view) {
        Log.e("Meow", "goToCalculus function triggered");

        Intent intent4;
        intent4 = new Intent(this, CalculusActivity.class);
        startActivity(intent4);
    }

    // Tutorial on FFT (Tutorial 5a)
    public void goToFft(View view) {
        Log.e("Meow", "goToFft function triggered");

        Intent intent5;
        intent5 = new Intent(this, FFTActivity.class);
        startActivity(intent5);
    }

    // Tutorial on Live FFT (Tutorial 5b)
    public void goToLiveFft(View view) {
        Log.e("Meow", "goToLiveFft function triggered");

        Intent intent6;
        intent6 = new Intent(this, LiveFFTActivity.class);
        startActivity(intent6);
    }

    // Tutorial on Live FFT (Tutorial 5c)
    public void goToSpectrogram(View view) {
        Log.e("Meow", "goToSpectrogram function triggered");

        Intent intent7;
        intent7 = new Intent(this, SpectrogramActivity.class);
        startActivity(intent7);
    }
}