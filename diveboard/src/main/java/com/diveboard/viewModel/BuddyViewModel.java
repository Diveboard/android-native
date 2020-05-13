package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.diveboard.dataaccess.datamodel.Buddy;
import com.diveboard.mobile.BR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//one buddy supported for now
public class BuddyViewModel extends BaseObservable {
    @Bindable
    Buddy buddy;
    private List<Buddy> buddies;

    public BuddyViewModel(List<Buddy> buddies) {
        this.buddies = buddies;
        setBuddy(buddies);
    }

    public Buddy getBuddy() {
        return this.buddy;
    }

    private void setBuddy(List<Buddy> buddies) {
        for (Buddy buddy : buddies) {
//            if (buddy.id != null) {
            //might require to copy field by field
            this.buddy = buddy;
            return;
//            }
        }
    }

    public void setBuddy(Buddy buddy) {
        //might require to copy field by field
        this.buddy = buddy;
        notifyPropertyChanged(BR.buddy);
    }

    public List<Buddy> getModel() {
        if (buddy == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(buddy);
    }
}
