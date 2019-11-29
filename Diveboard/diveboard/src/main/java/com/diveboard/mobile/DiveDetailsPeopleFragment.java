package com.diveboard.mobile;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.diveboard.dataaccess.DiveboardSearchBuddyRepository;
import com.diveboard.dataaccess.datamodel.SearchBuddy;
import com.diveboard.dataaccess.datamodel.SearchBuddyResponse;
import com.diveboard.mobile.databinding.DiveDetailsPeopleBinding;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.binding.BindingArrayAdapter;
import com.diveboard.viewModel.DiveDetailsViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.diveboard.mobile.SelectSpotPage.DELAY_MS;
import static com.diveboard.mobile.SelectSpotPage.MESSAGE_TEXT_CHANGED;

public class DiveDetailsPeopleFragment extends Fragment {

    DiveboardSearchBuddyRepository searchBuddyRepository;
    private DiveDetailsViewModel viewModel;
    private DiveDetailsPeopleBinding binding;
    private ApplicationController ac;
    private AppCompatAutoCompleteTextView suggest;
    private ProgressBar progress;
    private BindingArrayAdapter<SearchBuddy> adapter;
    private AutoSuggestHandler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_people, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        searchBuddyRepository = ac.getDiveboardSearchBuddyRepository();
        binding = DataBindingUtil.bind(view);
        binding.setView(this);
        if (viewModel != null) {
            binding.setModel(viewModel);
        }
        setupAutocompleteList(view);
        return view;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
        if (binding != null) {
            binding.setModel(viewModel);
        }
    }

    private void setupAutocompleteList(View view) {
        suggest = view.findViewById(R.id.buddy);
        progress = view.findViewById(R.id.progress_bar);
        adapter = new BindingArrayAdapter<>(ac, R.layout.buddy_item);
        suggest.setAdapter(adapter);
        suggest.setOnItemClickListener((adapterView, view1, position, id) -> {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);

            SearchBuddy buddy = (SearchBuddy) adapterView.getItemAtPosition(position);
            viewModel.getBuddy().setBuddy(buddy.getBuddy());
        });
        suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(MESSAGE_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, s.toString()), DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new DiveDetailsPeopleFragment.AutoSuggestHandler(this);
    }

    private static class AutoSuggestHandler extends Handler {

        private WeakReference<DiveDetailsPeopleFragment> outerRef;

        AutoSuggestHandler(DiveDetailsPeopleFragment outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            DiveDetailsPeopleFragment outer = outerRef.get();
            if (outer == null) {
                return;
            }
            outer.progress.setVisibility(View.GONE);
            String term = msg.obj.toString();
            if (term.length() < 2) {
                outer.adapter.setData(new ArrayList<>());
                return;
            }
            //don't search when user selected an item from suggest
            if (outer.viewModel.getBuddy().getBuddy() != null && term.equals(outer.viewModel.getBuddy().getBuddy().nickname)) {
                return;
            }
            outer.progress.setVisibility(View.VISIBLE);
            outer.searchBuddyRepository.search(term, new ResponseCallback<SearchBuddyResponse, Exception>() {
                @Override
                public void success(SearchBuddyResponse data) {
                    DiveDetailsPeopleFragment outer12 = outerRef.get();
                    if (outer12 == null) {
                        return;
                    }
                    outer12.progress.setVisibility(View.GONE);
                    if (!term.equals(outer12.suggest.getText().toString())) {
                        return;
                    }
                    outer12.adapter.setData(data);
                }

                @Override
                public void error(Exception e) {
                    Toast.makeText(outer.ac, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
