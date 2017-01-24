package com.example.kareem.IIFYM_Tracker;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * Created by Yomna on 1/24/2017.
 */

// This temp
    // pushing purposes

public class Temp {
    private String userUnitSystem;
    private TextView lblHeightUnit1, lblHeightUnit2, lblWeightUnit;
    private EditText etxtHeightParam1, etxtHeightParam2, etxtWeight;
    private LinearLayout linearlayoutHeight, linearlayoutWeight;

    public void unitSystemChange()
    {
        if(userUnitSystem.equals("Metric"))
        {
            linearlayoutHeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f));
            linearlayoutWeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f));

            etxtWeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f));

            etxtHeightParam1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.8f));
            lblHeightUnit1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f));

            etxtHeightParam2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f));
            lblHeightUnit2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f));

            etxtHeightParam2.setVisibility(View.GONE);
            lblHeightUnit2.setVisibility(View.GONE);
        }
        else if(userUnitSystem.equals("Imperial"))
    {
        linearlayoutHeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.67f));
        linearlayoutWeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.33f));

        etxtWeight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.8f));
        lblWeightUnit.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f));

        etxtHeightParam1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.4f));
        lblHeightUnit1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.1f));

        etxtHeightParam2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.4f));
        lblHeightUnit2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.1f));

        etxtHeightParam2.setVisibility(View.VISIBLE);
        lblHeightUnit2.setVisibility(View.VISIBLE);
    }
    }
}