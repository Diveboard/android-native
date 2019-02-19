package com.diveboard.mobile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.widget.ListView;

import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.model.Header;
import com.diveboard.viewModel.DiveItemViewModel;
import com.diveboard.viewModel.DivesListViewModel;

public class DivesListActivity extends NavDrawer implements Runnable {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_dives_list);

        DivesListViewModel viewModel = ViewModelProviders.of(this).get(DivesListViewModel.class);
        viewModel.init((ApplicationController) getApplicationContext());

        ListView listView = findViewById(R.id.listView);
        ObjectListAdapter<DiveItemViewModel> adapter = new ObjectListAdapter<>(
                this,
                viewModel.items,
                R.layout.dives_row,
                BR.listItem);
        listView.setAdapter(adapter);

        ActivityDivesListBinding binding = (ActivityDivesListBinding) mBinding;
        binding.setVheader(new Header("LOGBOOK", "CLASSIC", this));
        binding.setModel(viewModel);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void run() {
        DivesActivity.switchView(this, false);
    }
}
