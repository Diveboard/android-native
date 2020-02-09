package com.diveboard.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.mobile.databinding.ActivityPhotosBinding;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.ImageGalleryAdapter;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.PhotoViewModel;
import com.diveboard.viewModel.PhotosViewModel;
import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;

public class PhotosPage extends Fragment {
    private static final int GALLERY_REQUEST = 13;
    private static final int REQUEST_PERMISSION_CODE = 25;
    private ApplicationController ac;
    private PhotosViewModel viewModel;
    private ImageGalleryAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private DrawerLayout drawerLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ac = (ApplicationController) getContext().getApplicationContext();
        ActivityPhotosBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_photos, container, false);
        View view = binding.getRoot();
        setupToolbar(view);
        viewModel = new PhotosViewModel();
        recyclerView = view.findViewById(R.id.listView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        //TODO: autocalculate the width
//        recyclerView.setLayoutManager(new GridAutofitLayoutManager(ac, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100 + 8, ac.getResources().getDisplayMetrics())));
        GridLayoutManager layout = new GridLayoutManager(ac, 3);
        recyclerView.setLayoutManager(layout);
        binding.setModel(viewModel);
        binding.setView(this);
        refresh();
        return view;
    }

    private void refresh() {
        pullToRefresh.setRefreshing(true);
        ac.getUserService().getUserAsync(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                pullToRefresh.setRefreshing(false);
                viewModel.setPhotos(data.walletPictures);
                recyclerView.setAdapter(new ImageGalleryAdapter(data.walletPictures));
            }

            @Override
            public void error(Exception error) {
                pullToRefresh.setRefreshing(true);
                Utils.logError(PhotosPage.class, "Cannot get photos", error);
                Toast.makeText(ac, error.getMessage(), Toast.LENGTH_LONG);
            }
        }, true);
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.inflateMenu(R.menu.dive_list);
        actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        actionBar.setTitle(getString(R.string.photos));
        actionBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        actionBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.refresh:
                    //todo: force refresh
                    refresh();
                    break;
            }
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                        carImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Utils.logError(PhotosPage.class, "Cannot load image", e);
                    }
                    break;
            }
        }
        {
            Toast.makeText(ac, "Cannot load image: " + resultCode, Toast.LENGTH_LONG);
        }
    }

    public void addPhoto() {
        if (ActivityCompat.checkSelfPermission(ac, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGalery();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    private void openGalery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public ItemBinder<PhotoViewModel> itemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.item_image);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalery();
                }
            }
        }
    }
}
