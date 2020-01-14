package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.ImageGalleryAdapter;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.PhotoViewModel;
import com.diveboard.viewModel.PhotosViewModel;

public class PhotosPage extends Fragment {

    private DrawerLayout drawerLayout;
    private View listView;
    private ApplicationController ac;
    private PhotosViewModel viewModel;
    private ImageGalleryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ac = (ApplicationController) getContext().getApplicationContext();
        ActivityDivesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_dives_list, container, false);
        View view = binding.getRoot();
        viewModel = new PhotosViewModel();
        RecyclerView recyclerView = view.findViewById(R.id.listView);
        //TODO: autocalculate the width
//        recyclerView.setLayoutManager(new GridAutofitLayoutManager(ac, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100 + 8, ac.getResources().getDisplayMetrics())));
        GridLayoutManager layout = new GridLayoutManager(ac, 3);
        recyclerView.setLayoutManager(layout);
//        recyclerView.setAdapter(adapter);
        ac.getUserService().getUserAsync(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                viewModel.setPhotos(data.walletPictures);
                recyclerView.setAdapter(new ImageGalleryAdapter(data.walletPictures));
            }

            @Override
            public void error(Exception error) {
                Utils.logError(PhotosPage.class, "Cannot get photos", error);
                Toast.makeText(ac, error.getMessage(), Toast.LENGTH_LONG);
            }
        }, true);
        return view;
    }

    public ItemBinder<PhotoViewModel> itemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.item_image);
    }
}
