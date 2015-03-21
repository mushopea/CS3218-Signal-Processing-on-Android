package sg.edu.nus.CS3218ChngXinni;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class CalculatorActivity extends ActionBarActivity {
    private float result;
    private String currentOperator;
    private TextView calculatorDisplay;
    private float currentNumber;
    private boolean decimalPressed;
    private int decimalPlace;
    private int maxDecimalPlaces = 6;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        result = 0;
        currentOperator = "";
        calculatorDisplay = (TextView) findViewById(R.id.calculatorDisplay);
        currentNumber = 0;
        decimalPressed = false;
        decimalPlace = 0;
    }
    public void dotOnClick(View view){
        decimalPressed = true;
        decimalPlace = 0;
    }
    public void digitsOnClick(View view){
        float numberPressed = Float.parseFloat(((Button)
                view).getText().toString());
        if (decimalPressed) {
            decimalPlace++;
            if (decimalPlace < maxDecimalPlaces) {
                float decimals = (float) (numberPressed / Math.pow(10.0,
                        decimalPlace));
                currentNumber = currentNumber + decimals;
                String displayStr = String.format("%." + decimalPlace +
                        "f",currentNumber);
                calculatorDisplay.setText(displayStr);
//calculatorDisplay.setText(String.valueOf(currentNumber));
            }
        } else {
            currentNumber = currentNumber * 10 + numberPressed;
            calculatorDisplay.setText(String.valueOf(currentNumber));
        }
        if(currentOperator.equals("=")) result = currentNumber;
    }
    public void operatorOnClick(View view){
        if(currentOperator == ""){
            result = currentNumber;
        }else{
            if(currentOperator.equals("+")) result += currentNumber;
            else if(currentOperator.equals("-")) result -= currentNumber;
            else if(currentOperator.equals("x")) result *= currentNumber;
            else if(currentOperator.equals("/")) result /= currentNumber;
        }
        System.out.printf("result:%f \n",result);
        currentOperator = ((Button) view).getText().toString();
        if (currentOperator.equals("=")) {
            calculatorDisplay.setText(String.valueOf(result));
        } else {
            String displayStr = String.format("%." + decimalPlace + "f", result);
            calculatorDisplay.setText(displayStr);
        }
        currentNumber = 0;
        decimalPressed = false;
        decimalPlace = 0;
    }
    public void sqrtOnClick(View view) {
        result = (float)Math.sqrt(currentNumber);
        calculatorDisplay.setText(String.valueOf(result));
        currentNumber = result;
        decimalPressed = false;
        decimalPlace = 0;
    }

    public void sinOnClick(View view) {
        float currentNumberToRadians = (float)Math.toRadians(currentNumber);
        result = (float)Math.sin(currentNumberToRadians);
        calculatorDisplay.setText(String.valueOf(result));
        currentNumber = result;
        decimalPressed = false;
        decimalPlace = 0;
    }
    public void cosOnClick(View view) {
        float currentNumberToRadians = (float)Math.toRadians(currentNumber);
        result = (float)Math.cos(currentNumberToRadians);
        calculatorDisplay.setText(String.valueOf(result));
        currentNumber = result;
        decimalPressed = false;
        decimalPlace = 0;
    }
    public void tanOnClick(View view) {
        float currentNumberToRadians = (float)Math.toRadians(currentNumber);
        result = (float)Math.tan(currentNumberToRadians);
        calculatorDisplay.setText(String.valueOf(result));
        currentNumber = result;
        decimalPressed = false;
        decimalPlace = 0;
    }

    public void clear(View view){
        decimalPressed = false;
        decimalPlace = 0;
        currentNumber = 0;
        calculatorDisplay.setText(String.valueOf(currentNumber));
    }
    public void allClear(View view){
        decimalPressed = false;
        decimalPlace = 0;
        currentNumber = 0;
        currentOperator = "";
        result = 0;
        calculatorDisplay.setText(String.valueOf(result));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
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
}