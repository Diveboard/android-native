package com.diveboard.viewModel;

import com.diveboard.model.Picture;

public class DrawerHeaderViewModel {
    public Picture profileImagePath;
    public String name;
    public String email;

    public DrawerHeaderViewModel(Picture profileImagePath, String name, String email) {
        this.profileImagePath = profileImagePath;
        this.name = name;
        this.email = email;
    }
}