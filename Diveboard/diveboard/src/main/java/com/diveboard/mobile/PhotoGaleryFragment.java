package com.diveboard.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diveboard.dataaccess.PhotoUploadResponse;
import com.diveboard.dataaccess.PhotosRepository;
import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.ImageGalleryAdapter;
import com.diveboard.viewModel.PhotosViewModel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class PhotoGaleryFragment extends Fragment {
    private static final int GALLERY_REQUEST = 13;
    private static final int REQUEST_PERMISSION_CODE = 25;
    protected ApplicationController ac;
    private PhotosViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO: should be better way to avoid view duplication while navigating back from spot selection
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }

        ac = (ApplicationController) getContext().getApplicationContext();
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        view = binding.getRoot();
        viewModel = new PhotosViewModel();
        recyclerView = view.findViewById(R.id.listView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        //TODO: autocalculate the width
//        recyclerView.setLayoutManager(new GridAutofitLayoutManager(ac, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100 + 8, ac.getResources().getDisplayMetrics())));
        GridLayoutManager layout = new GridLayoutManager(ac, 3);
        recyclerView.setLayoutManager(layout);
        binding.setVariable(BR.model, viewModel);
        binding.setVariable(BR.view, this);
        refresh();
        setupPullToRefresh(view);
        return view;
    }

    void disablePullToRefresh() {
        pullToRefresh.setEnabled(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ImageGalleryAdapter) recyclerView.getAdapter()).getContextMenuPosition();
        } catch (Exception e) {
            Log.d("PhotoGaleryFragment", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.ctx_menu_set_cover:
                if (position != 0) {
                    Collections.swap(viewModel.photos, position, 0);
                    refreshView();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void refreshView() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    protected abstract int getLayoutId();

    protected void refresh() {
        pullToRefresh.setRefreshing(true);
        makeRequest();
    }

    protected abstract void makeRequest();

    protected void errorRefresh(Exception error) {
        pullToRefresh.setRefreshing(false);
        Utils.logError(PhotosPage.class, "Cannot get photos", error);
        Toast.makeText(ac, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    protected void successRefresh(List<Picture> walletPictures) {
        pullToRefresh.setRefreshing(false);
        viewModel.setUploading(false);
        viewModel.photos = walletPictures;
        recyclerView.setAdapter(new ImageGalleryAdapter(walletPictures, isSupportFavorite()));
    }

    protected abstract boolean isSupportFavorite();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        viewModel.setUploading(true);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        PhotosRepository photosRepository = ac.getPhotosRepository();
                        ResponseCallback<PhotoUploadResponse> uploadFinishedCallback = getUploadFinishedCallback();
                        /*PhotoUploadResponse data1 = new PhotoUploadResponse();
                        data1.result = new Picture();
                        data1.result.id = 123;
                        uploadFinishedCallback.success(data1);*/

                        photosRepository.uploadPhoto(uploadFinishedCallback, bitmap);
                    } catch (IOException e) {
                        Utils.logError(PhotosPage.class, "Cannot load image", e);
                    }
                    break;
            }
        }
        {
            Toast.makeText(ac, "Cannot load image: " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    protected abstract void linkNewPicture(PhotoUploadResponse data);

    private ResponseCallback<PhotoUploadResponse> getUploadFinishedCallback() {
        return new ResponseCallback<PhotoUploadResponse>() {
            @Override
            public void success(PhotoUploadResponse data) {
                linkNewPicture(data);
                viewModel.setUploading(false);
                refreshView();
            }

            @Override
            public void error(Exception e) {
                viewModel.setUploading(false);
                Toast.makeText(ac, e.getMessage(), Toast.LENGTH_LONG).show();
                Utils.logError(PhotosPage.class, "Cannot upload image", e);
            }
        };
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

    private void setupPullToRefresh(View view) {
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this::refresh);
    }
}
