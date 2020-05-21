package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.diveboard.dataaccess.PhotoUploadResponse;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.util.ResponseCallback;

public class PhotosPage extends PhotoGaleryFragment {
    private DrawerLayout drawerLayout;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupToolbar(view);
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photos;
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.inflateMenu(R.menu.dive_list);
        actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        actionBar.setTitle(getString(R.string.menu_links_photos));
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
    protected void makeRequest() {
        ac.getUserService().getUserAsync(getWalletCallback(), true);
    }

    @Override
    protected boolean isSupportFavorite() {
        return false;
    }

    @Override
    protected void linkNewPicture(PhotoUploadResponse data) {
        user.walletPicturesIds.add(data.result.id);
        ac.getUserService().saveUserAsync(getWalletCallback(), user);
    }

    private ResponseCallback<User> getWalletCallback() {
        return new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                user = data;
                successRefresh(data.walletPictures);
            }

            @Override
            public void error(Exception error) {
                errorRefresh(error);
            }
        };
    }
}
