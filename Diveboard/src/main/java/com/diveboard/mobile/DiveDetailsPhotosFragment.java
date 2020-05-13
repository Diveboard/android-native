package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.diveboard.dataaccess.PhotoUploadResponse;
import com.diveboard.viewModel.DiveDetailsViewModel;

public class DiveDetailsPhotosFragment extends PhotoGaleryFragment {
    private DiveDetailsViewModel viewModel;
    private ApplicationController ac;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        disablePullToRefresh();
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dive_details_photos;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected void makeRequest() {
        successRefresh(viewModel.pictures);
    }

    @Override
    protected boolean isSupportFavorite() {
        return true;
    }

    @Override
    protected void linkNewPicture(PhotoUploadResponse data) {
        viewModel.pictures.add(data.result);
    }
}
