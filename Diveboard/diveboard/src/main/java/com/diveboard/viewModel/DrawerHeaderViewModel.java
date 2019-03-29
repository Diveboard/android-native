package com.diveboard.viewModel;

import com.diveboard.mobile.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class DrawerHeaderViewModel extends BaseObservable {
    private String profileImageUrl;
    private String name;
    private String email;

    @Bindable
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        notifyPropertyChanged(BR.profileImageUrl);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
}