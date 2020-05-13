package com.diveboard.util;

import android.content.Context;

import com.diveboard.mobile.R;

public class ResourceHolder {
    private Context context;

    public ResourceHolder(Context context) {
        this.context = context;
    }

    public String[] getVisibilityValues() {
        return context.getResources().getStringArray(R.array.visibility_values);
    }

    public String[] getCurrentValues() {
        return context.getResources().getStringArray(R.array.current_values);
    }

    public String[] getCylindersCountValues() {
        return context.getResources().getStringArray(R.array.cylinders);
    }

    public String[] getMaterialsValues() {
        return context.getResources().getStringArray(R.array.tank_materials_values);
    }

    public String[] getGasMixValues() {
        return context.getResources().getStringArray(R.array.gas_mixes_values);
    }
}
