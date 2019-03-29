package com.diveboard.mobile;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diveboard.model.User;
import com.diveboard.viewModel.DrawerHeaderViewModel;

public class NavigationDrawerFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        User user = ApplicationController.getInstance().getModel().getUser();
        //TODO: refactor: getSessionEmail should be at user level
        DrawerHeaderViewModel viewModel = new DrawerHeaderViewModel();

        ViewDataBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.navigation_drawer_fragment, container, false);
        View view = binding.getRoot();
        return view;
    }
}
