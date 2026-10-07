package com.example.bmikalkulacka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/** Smoke test celé aplikace: spustí aktivity a projde hlavní scénáře. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, qualifiers = "cs")
public class AppSmokeTest {

    private MainActivity startMain() {
        return Robolectric.buildActivity(MainActivity.class).setup().get();
    }

    @Test
    public void inputTypes_matchAssignment() {
        MainActivity a = startMain();
        EditText h = a.findViewById(R.id.etHeight);
        EditText w = a.findViewById(R.id.etWeight);
        assertEquals(InputType.TYPE_CLASS_NUMBER, h.getInputType());
        assertEquals(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, w.getInputType());
        assertEquals(a.getString(R.string.hint_height), h.getHint().toString());
        assertEquals(a.getString(R.string.hint_weight), w.getHint().toString());
        assertEquals(a.getString(R.string.btn_calculate),
                ((Button) a.findViewById(R.id.btnCalculate)).getText().toString());
    }

    @Test
    public void emptyInputs_showError() {
        MainActivity a = startMain();
        a.findViewById(R.id.btnCalculate).performClick();
        String err = a.getString(R.string.error_empty);
        assertEquals(err, ((EditText) a.findViewById(R.id.etHeight)).getError().toString());
        assertEquals(err, ((EditText) a.findViewById(R.id.etWeight)).getError().toString());
        assertEquals(err, ((TextView) a.findViewById(R.id.tvResult)).getText().toString());
    }

    @Test
    public void invalidNumber_showsInvalidError() {
        MainActivity a = startMain();
        ((EditText) a.findViewById(R.id.etHeight)).setText("180");
        ((EditText) a.findViewById(R.id.etWeight)).setText("7.5.1");
        a.findViewById(R.id.btnCalculate).performClick();
        assertEquals(a.getString(R.string.error_invalid),
                ((EditText) a.findViewById(R.id.etWeight)).getError().toString());
    }

    @Test
    public void validInput_showsBmiAndCategory() {
        MainActivity a = startMain();
        ((EditText) a.findViewById(R.id.etHeight)).setText("175");
        ((EditText) a.findViewById(R.id.etWeight)).setText("70.5");
        a.findViewById(R.id.btnCalculate).performClick();
        String result = ((TextView) a.findViewById(R.id.tvResult)).getText().toString();
        assertTrue(result, result.contains("23,0"));
        assertTrue(result, result.contains("Normální hmotnost"));
        assertNull(((EditText) a.findViewById(R.id.etWeight)).getError());
    }

    @Test
    public void errorsAreCleared_afterSuccessfulCalculation() {
        MainActivity a = startMain();
        a.findViewById(R.id.btnCalculate).performClick();
        ((EditText) a.findViewById(R.id.etHeight)).setText("180");
        ((EditText) a.findViewById(R.id.etWeight)).setText("80");
        a.findViewById(R.id.btnCalculate).performClick();
        assertNull(((EditText) a.findViewById(R.id.etHeight)).getError());
        assertNull(((EditText) a.findViewById(R.id.etWeight)).getError());
    }

    @Test
    public void decimalComma_isAccepted() {
        MainActivity a = startMain();
        ((EditText) a.findViewById(R.id.etHeight)).setText("160");
        ((EditText) a.findViewById(R.id.etWeight)).setText("90,0");
        a.findViewById(R.id.btnCalculate).performClick();
        String result = ((TextView) a.findViewById(R.id.tvResult)).getText().toString();
        assertTrue(result, result.contains("35,2"));
        assertTrue(result, result.contains("Obezita II. stupně"));
    }

    @Test
    public void infoButton_opensCategories_andBackFinishes() {
        MainActivity a = startMain();
        ImageButton info = a.findViewById(R.id.btnInfo);
        assertNotNull(info.getContentDescription());
        info.performClick();
        Intent next = Shadows.shadowOf(a).getNextStartedActivity();
        assertEquals(CategoriesActivity.class.getName(), next.getComponent().getClassName());

        ActivityController<CategoriesActivity> c =
                Robolectric.buildActivity(CategoriesActivity.class, next).setup();
        CategoriesActivity cat = c.get();
        TableLayout table = cat.findViewById(R.id.tableCategories);
        int categories = cat.getResources().getStringArray(R.array.bmi_categories).length;
        assertEquals(6, categories);
        assertEquals(1 + categories, table.getChildCount()); // hlavička + řádky

        assertFalse(cat.isFinishing());
        cat.findViewById(R.id.btnBack).performClick();
        assertTrue(cat.isFinishing());
    }
}
