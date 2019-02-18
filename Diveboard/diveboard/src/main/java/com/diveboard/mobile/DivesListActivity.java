package com.diveboard.mobile;

import android.os.Bundle;
import android.widget.ListView;

import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.viewModel.DivesListViewModel;
import com.diveboard.model.Header;
import com.diveboard.viewModel.DiveItem;

public class DivesListActivity extends NavDrawer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_dives_list);
        ApplicationController AC = (ApplicationController) getApplicationContext();
        ListView listView = findViewById(R.id.listView);
        ObjectListAdapter<DiveItem> adapter = new ObjectListAdapter<>(
                this,
                DivesListViewModel.create(AC.getModel().getDives(), AC.getModel().getPreference().getUnitsTyped()).items,
                R.layout.dives_row,
                BR.list_item);

        listView.setAdapter(adapter);

        ActivityDivesListBinding binding = (ActivityDivesListBinding) mBinding;
        binding.setVheader(new Header("LOGBOOK"));
    }
}
