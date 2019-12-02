package com.diveboard.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.diveboard.dataaccess.DiveboardSearchBuddyRepository;
import com.diveboard.dataaccess.SearchShopRepository;
import com.diveboard.dataaccess.datamodel.SearchBuddy;
import com.diveboard.dataaccess.datamodel.SearchBuddyResponse;
import com.diveboard.dataaccess.datamodel.SearchShop;
import com.diveboard.dataaccess.datamodel.SearchShopResponse;
import com.diveboard.mobile.databinding.DiveDetailsPeopleBinding;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.BindingArrayAdapter;
import com.diveboard.viewModel.DiveDetailsViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DiveDetailsPeopleFragment extends Fragment {
    private static final int BUDDY_MESSAGE_TEXT_CHANGED = 1;
    private static final int DIVECENTER_MESSAGE_TEXT_CHANGED = 2;
    private static final long DELAY_MS = 300;
    private DiveboardSearchBuddyRepository searchBuddyRepository;
    private DiveDetailsViewModel viewModel;
    private DiveDetailsPeopleBinding binding;
    private ApplicationController ac;
    private AppCompatAutoCompleteTextView buddySuggest;
    private ProgressBar buddyProgress;
    private BindingArrayAdapter<SearchBuddy> buddyAdapter;
    private BuddyAutoSuggestHandler buddyHandler;
    private SearchShopRepository searchShopRepository;
    private AppCompatAutoCompleteTextView diveCenterSuggest;
    private ProgressBar diveCenterProgress;
    private BindingArrayAdapter<SearchShop> diveCenterAdapter;
    private DiveCenterAutoSuggestHandler diveCenterHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_people, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        searchBuddyRepository = ac.getDiveboardSearchBuddyRepository();
        searchShopRepository = ac.getSearchShopRepository();
        binding = DataBindingUtil.bind(view);
        binding.setView(this);
        if (viewModel != null) {
            binding.setModel(viewModel);
        }
        setupBuddyAutocompleteList(view);
        setupDiveCenterAutocompleteList(view);
        return view;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
        if (binding != null) {
            binding.setModel(viewModel);
        }
    }

    private void setupDiveCenterAutocompleteList(View view) {
        diveCenterSuggest = view.findViewById(R.id.diveCenter);
        diveCenterProgress = view.findViewById(R.id.progress_bar2);
        diveCenterAdapter = new BindingArrayAdapter<>(ac, R.layout.dive_center_item);
        diveCenterSuggest.setAdapter(diveCenterAdapter);
        diveCenterSuggest.setOnItemClickListener((adapterView, view1, position, id) -> {
            diveCenterHandler.removeMessages(DIVECENTER_MESSAGE_TEXT_CHANGED);
            SearchShop shop = (SearchShop) adapterView.getItemAtPosition(position);
            viewModel.setDiveCenter(shop.getShop());
            Utils.hideKeyboard(ac, view1);
        });
        diveCenterSuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                diveCenterHandler.removeMessages(DIVECENTER_MESSAGE_TEXT_CHANGED);
                diveCenterHandler.sendMessageDelayed(diveCenterHandler.obtainMessage(DIVECENTER_MESSAGE_TEXT_CHANGED, s.toString()), DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        diveCenterHandler = new DiveCenterAutoSuggestHandler(this);
    }

    private void setupBuddyAutocompleteList(View view) {
        buddySuggest = view.findViewById(R.id.buddy);
        buddyProgress = view.findViewById(R.id.progress_bar);
        buddyAdapter = new BindingArrayAdapter<>(ac, R.layout.buddy_item);
        buddySuggest.setAdapter(buddyAdapter);
        buddySuggest.setOnItemClickListener((adapterView, view1, position, id) -> {
            Utils.hideKeyboard(ac, view1);
            diveCenterHandler.removeMessages(BUDDY_MESSAGE_TEXT_CHANGED);
            SearchBuddy buddy = (SearchBuddy) adapterView.getItemAtPosition(position);
            viewModel.getBuddy().setBuddy(buddy.getBuddy());
        });
        buddySuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buddyHandler.removeMessages(BUDDY_MESSAGE_TEXT_CHANGED);
                buddyHandler.sendMessageDelayed(buddyHandler.obtainMessage(BUDDY_MESSAGE_TEXT_CHANGED, s.toString()), DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buddyHandler = new BuddyAutoSuggestHandler(this);
    }

    private static class BuddyAutoSuggestHandler extends Handler {

        private WeakReference<DiveDetailsPeopleFragment> outerRef;

        BuddyAutoSuggestHandler(DiveDetailsPeopleFragment outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            DiveDetailsPeopleFragment outer = outerRef.get();
            if (outer == null) {
                return;
            }
            outer.buddyProgress.setVisibility(View.GONE);
            String term = msg.obj.toString();
            if (term.length() < 2) {
                outer.buddyAdapter.setData(new ArrayList<>());
                return;
            }
            //don't search when user selected an item from suggest or text was populated by binding initially
            if (outer.viewModel.getBuddy().getBuddy() != null && term.equals(outer.viewModel.getBuddy().getBuddy().nickname)) {
                return;
            }
            if (!NetworkUtils.isConnected(outer.ac)) {
                Toast.makeText(outer.ac, R.string.no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            outer.buddyProgress.setVisibility(View.VISIBLE);
            outer.searchBuddyRepository.search(term, new ResponseCallback<SearchBuddyResponse, Exception>() {
                @Override
                public void success(SearchBuddyResponse data) {
                    DiveDetailsPeopleFragment outer12 = outerRef.get();
                    if (outer12 == null) {
                        return;
                    }
                    outer12.buddyProgress.setVisibility(View.GONE);
                    if (!term.equals(outer12.buddySuggest.getText().toString())) {
                        return;
                    }
                    outer12.buddyAdapter.setData(data);
                }

                @Override
                public void error(Exception e) {
                    Toast.makeText(outer.ac, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static class DiveCenterAutoSuggestHandler extends Handler {
        private WeakReference<DiveDetailsPeopleFragment> outerRef;

        DiveCenterAutoSuggestHandler(DiveDetailsPeopleFragment outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            DiveDetailsPeopleFragment outer = outerRef.get();
            if (outer == null) {
                return;
            }
            outer.diveCenterProgress.setVisibility(View.GONE);
            String term = msg.obj.toString();
            if (term.length() < 2) {
                outer.diveCenterAdapter.setData(new ArrayList<>());
                return;
            }
            //don't search when user selected an item from suggest or text was populated by binding initially
            if (outer.viewModel.getDiveCenter() != null && term.equals(outer.viewModel.getDiveCenter().name)) {
                return;
            }
            if (!NetworkUtils.isConnected(outer.ac)) {
                Toast.makeText(outer.ac, R.string.no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            outer.diveCenterProgress.setVisibility(View.VISIBLE);
            outer.searchShopRepository.search(term, new ResponseCallback<SearchShopResponse, Exception>() {
                @Override
                public void success(SearchShopResponse data) {
                    DiveDetailsPeopleFragment outer12 = outerRef.get();
                    if (outer12 == null) {
                        return;
                    }
                    outer12.diveCenterProgress.setVisibility(View.GONE);
                    if (!term.equals(outer12.diveCenterSuggest.getText().toString())) {
                        return;
                    }
                    outer12.diveCenterAdapter.setData(data);
                }

                @Override
                public void error(Exception e) {
                    Toast.makeText(outer.ac, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
