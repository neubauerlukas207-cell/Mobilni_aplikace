package com.example.bmikalkulacka;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_CATEGORY = "state_category";

    private EditText etHeight;
    private EditText etWeight;
    private TextView tvResult;

    /** Index kategorie posledního výpočtu (-1 = zatím nepočítáno). */
    private int lastCategory = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        tvResult = findViewById(R.id.tvResult);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        ImageButton btnInfo = findViewById(R.id.btnInfo);

        btnCalculate.setOnClickListener(v -> calculateBmi());
        btnInfo.setOnClickListener(v -> openCategories());

        if (savedInstanceState != null) {
            lastCategory = savedInstanceState.getInt(STATE_CATEGORY, -1);
            applyResultColor();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CATEGORY, lastCategory);
    }

    /** Načte vstupy, ošetří je, spočítá BMI a zobrazí výsledek. */
    private void calculateBmi() {
        // 1) Načtení textových hodnot z EditText polí
        String heightText = etHeight.getText().toString().trim();
        String weightText = etWeight.getText().toString().trim();

        // Smazání chyb z předchozího pokusu
        etHeight.setError(null);
        etWeight.setError(null);

        // 2) Kontrola prázdných polí
        boolean empty = false;
        if (heightText.isEmpty()) {
            etHeight.setError(getString(R.string.error_empty));
            empty = true;
        }
        if (weightText.isEmpty()) {
            etWeight.setError(getString(R.string.error_empty));
            empty = true;
        }
        if (empty) {
            showError(getString(R.string.error_empty));
            return;
        }

        // 3) Převod String -> double s ošetřením NumberFormatException
        double height;
        double weight;
        try {
            height = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            etHeight.setError(getString(R.string.error_invalid));
            showError(getString(R.string.error_invalid));
            return;
        }
        try {
            // Některé klávesnice v české lokalizaci nabízí desetinnou čárku.
            weight = Double.parseDouble(weightText.replace(',', '.'));
        } catch (NumberFormatException e) {
            etWeight.setError(getString(R.string.error_invalid));
            showError(getString(R.string.error_invalid));
            return;
        }

        if (height <= 0) {
            etHeight.setError(getString(R.string.error_not_positive));
            showError(getString(R.string.error_not_positive));
            return;
        }
        if (weight <= 0) {
            etWeight.setError(getString(R.string.error_not_positive));
            showError(getString(R.string.error_not_positive));
            return;
        }

        // 4) Výpočet BMI = hmotnost / (výška v m)^2
        double bmi = BmiCalculator.calculate(height, weight);
        lastCategory = BmiCalculator.categoryIndex(bmi);

        // 5) Sestavení výsledku z textových zdrojů a zobrazení v TextView
        String[] categories = getResources().getStringArray(R.array.bmi_categories);
        String bmiText = String.format(Locale.getDefault(), "%.1f", bmi);
        tvResult.setText(getString(R.string.bmi_result, bmiText, categories[lastCategory]));
        applyResultColor();
    }

    private void showError(String message) {
        lastCategory = -1;
        tvResult.setText(message);
        tvResult.setTextColor(ContextCompat.getColor(this, R.color.cat_obesity3));
    }

    /** Obarví výsledek barvou kategorie (barvy jsou v res/values/arrays.xml). */
    private void applyResultColor() {
        if (lastCategory < 0) {
            return;
        }
        int[] colors = getResources().getIntArray(R.array.bmi_colors);
        tvResult.setTextColor(colors[lastCategory]);
    }

    /** Otevře aktivitu s přehledem kategorií BMI. */
    private void openCategories() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        intent.putExtra(CategoriesActivity.EXTRA_CATEGORY, lastCategory);
        startActivity(intent);
    }
}
