package com.diveboard.util.binding;

import android.widget.TextView;

import org.apache.commons.validator.routines.DateValidator;

import br.com.ilhasoft.support.validation.rule.Rule;
import br.com.ilhasoft.support.validation.util.EditTextHandler;

public class LocaleDateRule  extends Rule<TextView, String> {

    private final DateValidator dateValidator;

    public LocaleDateRule(TextView view, String value, String errorMessage) {
        super(view, value, errorMessage);
        dateValidator = new DateValidator();
    }

    @Override
    public boolean isValid(TextView view) {
        return dateValidator.isValid(view.getText().toString(), value);
    }

    @Override
    public void onValidationSucceeded(TextView view) {
        EditTextHandler.removeError(view);
    }

    @Override
    public void onValidationFailed(TextView view) {
        EditTextHandler.setError(view, errorMessage);
    }
}