package com.diveboard.util.binding;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.diveboard.mobile.R;

public class RecyclerTitleAnimationAdapter {
    @BindingAdapter("android:recyclerTitleAnimation")
    public static void attachAnimation(TextView view, ObservableArrayList<?> list) {
        if (!list.isEmpty()) {
            startAnimation(R.dimen.nonEmptyTitleMargin, R.dimen.nonEmptyListTextSize, view);
        }
        list.addOnListChangedCallback(getSafetyStopListener(view));
    }

    private static ObservableList.OnListChangedCallback<ObservableList<Object>> getSafetyStopListener(TextView textView) {
        return new ObservableList.OnListChangedCallback<ObservableList<Object>>() {
            @Override
            public void onChanged(ObservableList<Object> sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<Object> sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<Object> sender, int positionStart, int itemCount) {
                startAnimation(R.dimen.nonEmptyTitleMargin, R.dimen.nonEmptyListTextSize, textView);
            }

            @Override
            public void onItemRangeMoved(ObservableList<Object> sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<Object> sender, int positionStart, int itemCount) {
                if (sender.isEmpty()) {
                    startAnimation(R.dimen.emptyTitleMargin, R.dimen.emptyListTextSize, textView);
                }
            }
        };
    }

    private static void startAnimation(int targetMarginResName, int targetTextSizeResName, TextView textView) {
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        float scaledDensity = textView.getResources().getDisplayMetrics().scaledDensity;

        ValueAnimator marginAnimator = ValueAnimator.ofInt(margin.bottomMargin, (int) textView.getResources().getDimension(targetMarginResName));
        marginAnimator.addUpdateListener(animation -> margin.bottomMargin = (Integer) animation.getAnimatedValue());

        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(textView.getTextSize() / scaledDensity, (int) (((int) textView.getResources().getDimension(targetTextSizeResName)) / scaledDensity));
        textSizeAnimator.addUpdateListener(valueAnimator -> textView.setTextSize((float) valueAnimator.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(textSizeAnimator, marginAnimator);
        set.start();
    }
}