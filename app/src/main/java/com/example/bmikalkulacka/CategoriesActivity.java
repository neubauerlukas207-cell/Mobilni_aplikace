package com.example.bmikalkulacka;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Aktivita zobrazující přehled kategorií BMI ve formě tabulky s ikonami.
 * Texty, ikony i barvy se načítají ze seznamů hodnot v res/values/arrays.xml.
 */
public class CategoriesActivity extends AppCompatActivity {

    /** Index kategorie, kterou má tabulka zvýraznit (-1 = žádnou). */
    public static final String EXTRA_CATEGORY = "com.example.bmikalkulacka.EXTRA_CATEGORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int highlighted = getIntent().getIntExtra(EXTRA_CATEGORY, -1);
        fillTable(highlighted);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fillTable(int highlighted) {
        TableLayout table = findViewById(R.id.tableCategories);
        LayoutInflater inflater = LayoutInflater.from(this);

        String[] names = getResources().getStringArray(R.array.bmi_categories);
        String[] ranges = getResources().getStringArray(R.array.bmi_ranges);
        int[] colors = getResources().getIntArray(R.array.bmi_colors);
        TypedArray icons = getResources().obtainTypedArray(R.array.bmi_icons);

        try {
            for (int i = 0; i < names.length; i++) {
                TableRow row = (TableRow) inflater.inflate(R.layout.item_category_row, table, false);

                ImageView icon = row.findViewById(R.id.ivCategoryIcon);
                icon.setImageDrawable(icons.getDrawable(i));
                icon.setContentDescription(getString(R.string.desc_category_icon, names[i]));

                TextView name = row.findViewById(R.id.tvCategoryName);
                name.setText(names[i]);

                TextView range = row.findViewById(R.id.tvCategoryRange);
                range.setText(ranges[i]);
                range.setTextColor(colors[i]);

                if (i == highlighted) {
                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_highlight));
                    name.setTypeface(name.getTypeface(), android.graphics.Typeface.BOLD);
                }

                table.addView(row);
            }
        } finally {
            icons.recycle();
        }
    }
}
