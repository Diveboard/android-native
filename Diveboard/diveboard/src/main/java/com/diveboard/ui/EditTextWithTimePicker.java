package com.diveboard.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.diveboard.util.DateConverter;

import java.util.Calendar;

import static android.text.format.DateFormat.is24HourFormat;

public class EditTextWithTimePicker implements TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {
    private final Context context;
    private final DateConverter conversion;
    private EditText editText;

    public EditTextWithTimePicker(EditText editTextViewID) {
        this.editText = editTextViewID;
        this.editText.setOnFocusChangeListener(this);
        context = editText.getContext();
        conversion = new DateConverter(context);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        editText.setText(conversion.convertTimeToString(calendar));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        Calendar calendar = conversion.convertStringToTime(editText.getText().toString());
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        TimePickerDialog dialog = new TimePickerDialog(
                context,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24HourFormat(context));
        dialog.show();
    }
}