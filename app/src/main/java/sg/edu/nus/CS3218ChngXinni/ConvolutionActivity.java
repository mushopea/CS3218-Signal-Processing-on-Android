package sg.edu.nus.CS3218ChngXinni;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ConvolutionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_convolution, menu);
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

    public void goToQ1(View view) {
        Intent intent1;
        intent1 = new Intent(this, ConvolutionTimeActivity.class);
        startActivity(intent1);
    }


    public void goToQ2(View view) {
        Intent intent2;
        intent2 = new Intent(this, ConvolutionFreqActivity.class);
        startActivity(intent2);
    }

    public void goToQ3(View view) {
        Intent intent3;
        intent3 = new Intent(this, CorrelationTimeActivity.class);
        startActivity(intent3);
    }


    public void goToQ4(View view) {
        Intent intent4;
        intent4 = new Intent(this, CorrelationFreqActivity.class);
        startActivity(intent4);
    }
}
