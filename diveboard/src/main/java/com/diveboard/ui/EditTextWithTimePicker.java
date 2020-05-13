package com.diveboard.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.diveboard.util.DateConverter;

import java.util.Calendar;

import static android.text.format.DateFormat.is24HourFormat;

public class EditTextWithTimePicker implements TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener, View.OnClickListener {
    private final Context context;
    private final DateConverter conversion;
    private EditText editText;

    public EditTextWithTimePicker(EditText editTextViewID) {
        this.editText = editTextViewID;
        this.editText.setOnFocusChangeListener(this);
        this.editText.setOnClickListener(this);
        context = editText.getContext();
        conversion = new DateConverter(context);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        editText.setText(conversion.convertTimeToString(calendar));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        onClick(v);
    }

    @Override
    public void onClick(View v) {
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