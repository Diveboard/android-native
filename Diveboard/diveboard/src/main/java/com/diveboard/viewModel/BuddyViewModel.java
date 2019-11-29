package com.diveboard.viewModel;

import com.diveboard.dataaccess.datamodel.Buddy;

import java.util.List;

//one buddy supported for now
public class BuddyViewModel {
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
    }

    public List<Buddy> getModel() {
        //replace existing buddy with newly created one
        for (int i = 0; i < buddies.size(); i++) {
            Buddy buddy = buddies.get(i);
            //need this workaround to not lose buddy after resave
            buddy.setId(buddy.id);
//            if (buddy.id != null) {
            buddies.set(i, buddy);
            return buddies;
//            }
        }
        return buddies;
    }
}
