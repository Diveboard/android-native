package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.mobile.BR;

import java.util.ArrayList;
import java.util.List;

public class PhotosViewModel extends BaseObservable {

    public List<Picture> photos = new ArrayList<>();

    private boolean uploading = false;

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
        notifyPropertyChanged(com.diveboard.mobile.BR.uploading);
    }

    @Bindable
    public boolean isUploading() {
        return uploading;
    }
}