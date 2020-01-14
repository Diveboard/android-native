package com.diveboard.viewModel;

import com.diveboard.dataaccess.datamodel.Picture;

import java.util.ArrayList;
import java.util.List;

public class PhotosViewModel {

    public ArrayList<PhotoViewModel> photos = new ArrayList<>();

    public void setPhotos(List<Picture> pictures) {
        photos.clear();
        if (pictures == null) {
            return;
        }
        for (Picture picture : pictures) {
            PhotoViewModel e = new PhotoViewModel(picture);
            photos.add(e);
        }
    }
}