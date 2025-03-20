package deakin.sit.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Declare views
    private EditText mValueEditText;
    private RadioButton mRadioLength;
    private RadioButton mRadioWeight;
    private RadioButton mRadioTemperature;
    private Spinner mSourceUnitSpinner;
    private Spinner mDestUnitSpinner;
    private Button mConvertButton;
    private TextView mResultTextView;
    private TextView mResultLabelTextView;

    // Declare adapters for spinners
    private ArrayAdapter<String> srcLengthArrayAdapter;
    private ArrayAdapter<String> dstLengthArrayAdapter;
    private ArrayAdapter<String> srcWeightArrayAdapter;
    private ArrayAdapter<String> dstWeightArrayAdapter;
    private ArrayAdapter<String> temperatureArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        mValueEditText = findViewById(R.id.valueEditText);

        mRadioLength = findViewById(R.id.radioLength);
        mRadioWeight = findViewById(R.id.radioWeight);
        mRadioTemperature = findViewById(R.id.radioTemperature);

        mSourceUnitSpinner = findViewById(R.id.sourceUnitSpinner);
        mDestUnitSpinner = findViewById(R.id.destUnitSpinner);

        mConvertButton = findViewById(R.id.convertButton);

        mResultTextView = findViewById(R.id.resultTextView);
        mResultLabelTextView = findViewById(R.id.resultLabelTextView);

        // Init adapters for spinner
        initLengthArrayAdapter();
        initWeightArrayAdapter();
        initTemperatureArrayAdapter();

        // Set default radio & adapter (temperature)
        mRadioTemperature.setChecked(true);
        mSourceUnitSpinner.setAdapter(temperatureArrayAdapter);
        mDestUnitSpinner.setAdapter(temperatureArrayAdapter);

        // Setup radio buttons, changing spinner data based on selected radio button
        mRadioLength.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                mSourceUnitSpinner.setAdapter(srcLengthArrayAdapter);
                mDestUnitSpinner.setAdapter(dstLengthArrayAdapter);
            }
        });
        mRadioWeight.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                mSourceUnitSpinner.setAdapter(srcWeightArrayAdapter);
                mDestUnitSpinner.setAdapter(dstWeightArrayAdapter);
            }
        });
        mRadioTemperature.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                mSourceUnitSpinner.setAdapter(temperatureArrayAdapter);
                mDestUnitSpinner.setAdapter(temperatureArrayAdapter);
            }
        });

        // Onclick listener
        mConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputString = mValueEditText.getText().toString();

                if (inputString.isEmpty()) {
                    mResultTextView.setText("Please enter your value");
                } else {
                    try {
                        // Convert value to double
                        double inputVal;
                        double result;
                        inputVal = Double.parseDouble(inputString);

                        // Get source unit
                        String sourceUnit = mSourceUnitSpinner.getSelectedItem().toString();
                        String destUnit = mDestUnitSpinner.getSelectedItem().toString();
                        mResultLabelTextView.setText(sourceUnit + " -> " + destUnit);

                        convertValue(inputVal, sourceUnit, destUnit);
                    } catch (NumberFormatException e){
                        mResultTextView.setText("Invalid number");
                    }
                }
            }
        });
    }

    // ------ General method for each conversion option ------ //
    private void convertValue(double srcValue, String srcUnit, String dstUnit) {
        double result;
        String resultText;
        // Length conversion
        if (dstUnit.equals("km")) {
            result = toKm(srcValue, srcUnit);
            resultText = String.format("%1$,.6f", result);
            mResultTextView.setText(resultText + " km");
        } else if (dstUnit.equals("cm")) {
            result = toCm(srcValue, srcUnit);
            resultText = String.format("%1$,.6f", result);
            mResultTextView.setText(resultText + " cm");
        }
        // Weight conversion
        else if (dstUnit.equals("kg")) {
            result = toKg(srcValue, srcUnit);
            resultText = String.format("%1$,.6f", result);
            mResultTextView.setText(resultText + " kg");
        } else if (dstUnit.equals("g")) {
            result = toG(srcValue, srcUnit);
            resultText = String.format("%1$,.6f", result);
            mResultTextView.setText(resultText + " g");
        }
        // Temperature conversion
        else if (dstUnit.equals("Celsius")) {
            result = toCelsius(srcValue, srcUnit);
            resultText = String.format("%1$,.2f", result);
            mResultTextView.setText(resultText + " C");
        } else if (dstUnit.equals("Fahrenheit")) {
            result = toFahrenheit(srcValue, srcUnit);
            resultText = String.format("%1$,.2f", result);
            mResultTextView.setText(resultText + " F");
        } else if (dstUnit.equals("Kelvin")) {
            result = toKelvin(srcValue, srcUnit);
            resultText = String.format("%1$,.2f", result);
            mResultTextView.setText(resultText + " K");
        }
        // Invalid conversion
        else {
            mResultTextView.setText("Destination unit is not supported");
        }
    }

    // ------ Specific methods for length conversion ------ //
    private double toCm(double srcValue, String srcUnit) {
        if (srcUnit.equals("inch")) {
            // From inch -> cm
            return srcValue * 2.54;
        } else if (srcUnit.equals("foot")) {
            // From foot -> cm
            return srcValue * 30.48;
        } else if (srcUnit.equals("yard")) {
            // From yard -> cm
            return srcValue * 91.44;
        } else if (srcUnit.equals("mile")) {
            // From mile -> km -> cm
            double asKm = toKm(srcValue, "mile");
            return toCm(asKm, "km");
        } else if (srcUnit.equals("km")) {
            // From km -> cm
            return srcValue * 100000;
        }
        return srcValue;
    }

    private double toKm(double srcValue, String srcUnit) {
        // From mile -> km
        if (srcUnit.equals("mile")) {
            return srcValue * 1.60934;
        }
        // From srcUnit -> cm -> km
        double asCm = toCm(srcValue, srcUnit);
        return asCm / 100000;
    }

    // ------ Specific methods for weight conversion ------ //
    private double toKg(double srcValue, String srcUnit) {
        if (srcUnit.equals("pound")) {
            // From pound -> kg
            return srcValue * 0.453592;
        } else if (srcUnit.equals("ounce")) {
            // From ounce -> g -> kg
            double asG = toG(srcValue, "ounce");
            return toKg(asG, "g");
        } else if (srcUnit.equals("ton")) {;
            // From ton -> kg
            return srcValue * 907.185;
        } else if (srcUnit.equals("g")) {
            // From g -> kg
            return srcValue * 1000;
        }
        return srcValue;
    }

    private double toG(double srcValue, String srcUnit) {
        // From ounce -> g
        if (srcUnit.equals("ounce")) {
            return srcValue * 28.3495;
        }
        // From srcUnit -> kg -> g
        double asKg = toKg(srcValue, srcUnit);
        return asKg / 1000;
    }

    // ------ Specific methods for temperature conversion ------ //
    private double toCelsius(double srcValue, String srcUnit) {
        if (srcUnit.equals("Fahrenheit")) {
            // From fahrenheit -> celsius
            return (srcValue - 32) / 1.8;
        } else if (srcUnit.equals("Kelvin")) {
            // From kelvin -> celsius
            return srcValue - 273.15;
        }
        return srcValue;
    }

    private double toFahrenheit(double srcValue, String srcUnit) {
        if (srcUnit.equals("Celsius")) {
            // From celsius -> fahrenheit
            return (srcValue * 1.8) + 32;
        } else if (srcUnit.equals("Kelvin")) {
            // From kelvin -> celsius -> fahrenheit
            double asCelsius = toCelsius(srcValue, "Kelvin");
            return toFahrenheit(asCelsius, "Celsius");
        }
        return srcValue;
    }

    private double toKelvin(double srcValue, String srcUnit) {
        if (srcUnit.equals("Celsius")) {
            // From celsius -> kelvin
            return srcValue + 273.15;
        } else if (srcUnit.equals("Fahrenheit")) {
            // From fahrenheit -> celsius -> kelvin
            double asCelsius = toCelsius(srcValue, "Fahrenheit");
            return toKelvin(asCelsius, "Celsius");
        }
        return srcValue;
    }

    // ------ Init adapters for spinners ------ //
    private void initLengthArrayAdapter() {
        List<String> srcLengthArray = new ArrayList<String>();
        srcLengthArray.add("inch");
        srcLengthArray.add("foot");
        srcLengthArray.add("yard");
        srcLengthArray.add("mile");
        srcLengthArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                srcLengthArray
        );
        srcLengthArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> dstLengthArray = new ArrayList<String>();
        dstLengthArray.add("km");
        dstLengthArray.add("cm");
        dstLengthArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                dstLengthArray
        );
        dstLengthArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initWeightArrayAdapter() {
        List<String> srcWeightArray = new ArrayList<String>();
        srcWeightArray.add("pound");
        srcWeightArray.add("ounce");
        srcWeightArray.add("ton");
        srcWeightArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                srcWeightArray
        );
        srcWeightArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> dstWeightArray = new ArrayList<String>();
        dstWeightArray.add("kg");
        dstWeightArray.add("g");
        dstWeightArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                dstWeightArray
        );
        dstWeightArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initTemperatureArrayAdapter() {
        List<String> temperatureArray;
        temperatureArray = new ArrayList<String>();
        temperatureArray.add("Celsius");
        temperatureArray.add("Fahrenheit");
        temperatureArray.add("Kelvin");
        temperatureArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                temperatureArray
        );
        temperatureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}