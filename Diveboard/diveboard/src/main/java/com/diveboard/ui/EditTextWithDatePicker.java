package com.diveboard.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.diveboard.util.DateConverter;

import java.util.Calendar;
import java.util.TimeZone;

public class EditTextWithDatePicker implements View.OnFocusChangeListener {
    private final Context context;
    private final DateConverter conversion;
    private EditText editText;

    public EditTextWithDatePicker(EditText editTextViewID) {
        this.editText = editTextViewID;
        this.editText.setOnFocusChangeListener(this);
        context = editText.getContext();
        conversion = new DateConverter(context);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        Calendar calendar = conversion.convertStringToDate(editText.getText().toString());
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        DatePickerDialog dialog = new DatePickerDialog(
                context,
                new DateSetListener(),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar date = Calendar.getInstance(TimeZone.getDefault());
            date.set(year, monthOfYear, dayOfMonth);
            editText.setText(conversion.convertDateToString(date));
        }
    }
}