package com.diveboard.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diveboard.mobile.R;

public class StatisticItemView extends LinearLayout {

    private String title;
    private String stringValue;
    private TextView txtTitle;
    private TextView txtValue;

    public StatisticItemView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StatisticItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public StatisticItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatisticItemView, 0, 0);
        try {
            title = a.getString(R.styleable.StatisticItemView_title);
            stringValue = a.getString(R.styleable.StatisticItemView_stringValue);
        } finally {
            a.recycle();
        }
        View rootView = inflate(context, R.layout.statistic_item_view, this);
        txtTitle = rootView.findViewById(R.id.lTitle);
        txtValue = rootView.findViewById(R.id.lValue);
        refreshUi();
    }

    private void refreshUi() {
        txtTitle.setText(title);
        txtValue.setText(stringValue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitleString) {
        this.title = mTitleString;
        refreshUi();
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String mValueString) {
        this.stringValue = mValueString;
        refreshUi();
    }
}
