package com.diveboard.mobile;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class DiveDetailsActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_details2);
        setupTabs();
    }

    private void setupTabs() {
        ViewPager viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext());
        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_details_label);
//        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_people_label);
//        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_notes_label);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
