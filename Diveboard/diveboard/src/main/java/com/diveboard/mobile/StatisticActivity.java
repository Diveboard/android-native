package com.diveboard.mobile;

import android.os.Bundle;
import android.widget.ListView;

import com.diveboard.mobile.databinding.ActivityStatisticBinding;
import com.diveboard.model.Dive;
import com.diveboard.model.Header;
import com.diveboard.model.Statistic;
import com.diveboard.model.Units;
import com.diveboard.viewModel.StatisticItem;
import com.diveboard.viewModel.StatisticViewModel;


public class StatisticActivity extends NavDrawer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_statistic);
        ApplicationController AC = (ApplicationController) getApplicationContext();
        ListView listView = findViewById(R.id.listView);
        ObjectListAdapter<StatisticItem> adapter = new ObjectListAdapter<>(
                this,
                StatisticViewModel.create(Statistic.create(AC.getModel().getDives()), AC.getModel().getPreference().getUnitsTyped()).items,
                R.layout.statistic_row,
                BR.statisticItem);

        listView.setAdapter(adapter);

        ActivityStatisticBinding binding = (ActivityStatisticBinding) mBinding;
        binding.setVheader(new Header("Statistic"));
    }
}
