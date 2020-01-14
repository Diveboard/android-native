package com.diveboard.viewModel;

import com.diveboard.dataaccess.datamodel.Picture;

public class PhotoViewModel {
    public final String thumbnail;

    public PhotoViewModel(Picture picture) {
        thumbnail = picture.thumbnail;
    }
}
