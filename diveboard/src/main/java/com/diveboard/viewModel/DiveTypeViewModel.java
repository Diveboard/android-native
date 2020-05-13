package com.diveboard.viewModel;

public class DiveTypeViewModel {
    public boolean selected;
    public String diveType;

    public DiveTypeViewModel(String diveType) {
        this.diveType = diveType;
    }

    public String getTitle() {
        if (diveType == null || diveType.length() == 0) {
            return diveType;
        }
        //TODO: make every separate word from upper case
        return Character.toUpperCase(diveType.charAt(0)) + diveType.substring(1).toLowerCase();
    }
}