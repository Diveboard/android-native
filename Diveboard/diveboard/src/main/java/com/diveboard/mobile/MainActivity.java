package com.diveboard.mobile;

import android.os.Bundle;

import com.diveboard.model.DiveboardModel;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationController ac = (ApplicationController) getApplicationContext();
        //set my user id
        DiveboardModel mModel = new DiveboardModel(1, ac);
        ac.setModel(mModel);
        mModel.loadData();
    }
}
