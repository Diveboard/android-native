package com.diveboard.mobile;

import android.os.Bundle;

import com.diveboard.mobile.databinding.ActivityStatisticBinding;
import com.diveboard.model.Dive;
import com.diveboard.model.Header;
import com.diveboard.model.Statistic;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticActivity extends NavDrawer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_statistic);
        ApplicationController AC = (ApplicationController) getApplicationContext();

        ActivityStatisticBinding binding = (ActivityStatisticBinding) mBinding;
        binding.setStatistic(Statistic.create(AC.getModel().getDives()));

        //TODO: adjust units in view
        binding.setVheader(new Header("Statistic"));
    }
}
