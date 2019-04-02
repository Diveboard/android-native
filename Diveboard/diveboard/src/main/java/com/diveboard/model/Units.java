package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Units {


    public enum UnitsType {
        Metric("Metric"),
        Imperial("Imperial");
        private final String text;

        UnitsType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
