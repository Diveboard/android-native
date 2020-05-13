package com.diveboard.util.binding;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import androidx.databinding.BindingAdapter;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.diveboard.ui.EditTextWithDatePicker;
import com.diveboard.ui.EditTextWithTimePicker;

import java.util.List;

public class TextInputEditTextBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("attachCalendar")
    public static void attachCalendar(EditText view, String dialogType) {
        if ("date".equals(dialogType)) {
            new EditTextWithDatePicker(view);
        }
        if ("time".equals(dialogType)) {
            new EditTextWithTimePicker(view);
        }
    }

    @BindingAdapter("autoCompleteList")
    public static void autoCompleteList(AppCompatAutoCompleteTextView view, List list) {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(view), android.R.layout.simple_dropdown_item_1line, list);
        view.setAdapter(adapter);
    }

    private static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
