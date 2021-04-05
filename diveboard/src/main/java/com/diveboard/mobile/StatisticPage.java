package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.mobile.databinding.ActivityStatisticBinding;
import com.diveboard.model.DivesService;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.StatisticViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class StatisticPage extends Fragment {

    private ApplicationController ac;
    private BarChart chart;
    private View view;
    private StatisticViewModel viewModel;

    public static ItemBinderBase<Object> getItemBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.country_row);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityStatisticBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_statistic, container, false);
        view = binding.getRoot();
        binding.setView(this);
        setupToolbar(view);
        chart = view.findViewById(R.id.chart);
        setupChart(chart);
        ac = (ApplicationController) getActivity().getApplicationContext();
        DivesService divesService = ac.getDivesService();
        divesService.getDivesAsync(new ResponseCallback<DivesResponse>() {
            @Override
            public void success(DivesResponse data) {
                viewModel = StatisticViewModel.create(data.result, ac, ac.getUserPreferenceService().getUnits());
                binding.setModel(viewModel);
                drawChart(viewModel);
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(ac, e.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.logError(StatisticPage.class, "Cannot get dives", e);
            }
        }, false);
        return view;
    }

    private void drawChart(StatisticViewModel statistic) {
        List<BarEntry> entries = new ArrayList<>();
        int lastYear = -1;
        int previousYear = 0;
        //ned to put values into chart into sorted way otherwise they flicker on horizontal scrolling
        SortedSet<Integer> keySet = new TreeSet<>(statistic.divesPerYear.keySet());
        for (Integer year : keySet) {
            entries.add(new BarEntry(year, statistic.divesPerYear.get(year)));
            lastYear = Math.max(year, lastYear);
            //fullfil missing years with zeros
            if (previousYear != 0) {
                for (int i = previousYear + 1; i < year; i++) {
                    entries.add(new BarEntry(i, 0));
                }
            }
            previousYear = year;
        }
        BarDataSet set = new BarDataSet(entries, "Dives per year");
        set.setColor(getResources().getColor(R.color.diveboardGray));
        MyXAxisFormatter formatter = new MyXAxisFormatter();
        set.setValueFormatter(formatter);
        BarData barData = new BarData(set);
        barData.setValueTextSize(12);
        barData.setValueTextColor(getResources().getColor(R.color.textColorPrimaryDark));
        barData.setValueFormatter(formatter);
        chart.setData(barData);
        chart.setVisibleXRangeMaximum(6);
        if (lastYear != -1) {
            chart.moveViewToX(lastYear);
        }
        chart.animateXY(1000, 1000);
        chart.invalidate();
    }

    private void setupChart(BarChart chart) {
        YAxis axisRight = chart.getAxisRight();
        YAxis axisLeft = chart.getAxisLeft();
        XAxis xAxis = chart.getXAxis();
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(true);
        chart.getLegend().setEnabled(false);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setDrawGridLines(false);
        axisRight.setDrawLabels(false);
        axisRight.setDrawAxisLine(false);
        axisRight.setDrawGridLines(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MyXAxisFormatter());
        xAxis.setTextSize(12);
        xAxis.setTextColor(getResources().getColor(R.color.textColorPrimaryDark));
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationOnClickListener(v -> ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START));
    }

    public void showCountries() {
        MyCountriesDialog dialog = new MyCountriesDialog(viewModel);
        dialog.show(getFragmentManager(), "my_countries");
    }

    public void showDive(Dive dive) {
        if (dive == null) {
            return;
        }
        StatisticPageDirections.ActionStatisticsToDiveDetails action = StatisticPageDirections.actionStatisticsToDiveDetails(dive.shakenId);
        Navigation.findNavController(view).navigate(action);
    }

    static class MyXAxisFormatter extends ValueFormatter {

        private DecimalFormat decimalFormat = new DecimalFormat("#");

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return decimalFormat.format(barEntry.getY());
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return decimalFormat.format(value);
        }
    }

}
