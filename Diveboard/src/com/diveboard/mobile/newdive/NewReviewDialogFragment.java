package com.diveboard.mobile.newdive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Review;
import com.diveboard.model.Temperature;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class NewReviewDialogFragment extends DialogFragment implements
			OnEditorActionListener {
	
	public interface EditReviewDialogListener {
		void onReviewEditComplete(DialogFragment dialog);
	}

	private Dive mDive;
	private ListView mReview;
	private EditReviewDialogListener mListener;
	private RatingBar overall, difficulty, fish, life, wreck;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (EditReviewDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement onReviewEditComplete");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Typeface faceR = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_review, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext())
				.getTempDive();

		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		TextView title = (TextView) view.findViewById(R.id.title);
		System.out.println("title " + title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_review_title));
		
		ImageView delete1 = (ImageView) view.findViewById(R.id.DeleteOverall);
		delete1.setImageDrawable(getResources().getDrawable(R.drawable.recyclebin));
		ImageView delete2 = (ImageView) view.findViewById(R.id.DeleteDifficulty);
		delete1.setImageDrawable(getResources().getDrawable(R.drawable.recyclebin));
		ImageView delete3 = (ImageView) view.findViewById(R.id.DeleteMarine);
		delete1.setImageDrawable(getResources().getDrawable(R.drawable.recyclebin));
		ImageView delete4 = (ImageView) view.findViewById(R.id.DeleteBigFish);
		delete1.setImageDrawable(getResources().getDrawable(R.drawable.recyclebin));
		ImageView delete5 = (ImageView) view.findViewById(R.id.DeleteWreck);
		delete1.setImageDrawable(getResources().getDrawable(R.drawable.recyclebin));
		
		//onClick

		overall = (RatingBar) view.findViewById(R.id.OverallRatingBar);
		difficulty = (RatingBar) view.findViewById(R.id.DifficultyRatingBar);
		fish = (RatingBar) view.findViewById(R.id.FishRatingBar);
		life = (RatingBar) view.findViewById(R.id.LifeRatingBar);
		wreck = (RatingBar) view.findViewById(R.id.WreckRatingBar);

		if (mDive.getDiveReviews() != null) {

			if (mDive.getDiveReviews().getOverall() != null)
				overall.setRating(mDive.getDiveReviews().getOverall());

			if (mDive.getDiveReviews().getDifficulty() != null)
				difficulty.setRating(mDive.getDiveReviews().getDifficulty());

			if (mDive.getDiveReviews().getBigFish() != null)
				fish.setRating(mDive.getDiveReviews().getBigFish());

			if (mDive.getDiveReviews().getMarine() != null)
				life.setRating(mDive.getDiveReviews().getMarine());

			if (mDive.getDiveReviews().getWreck() != null)
				wreck.setRating(mDive.getDiveReviews().getWreck());

		}

		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		Button save = (Button) view.findViewById(R.id.save);
		save.setTypeface(faceR);
		save.setText(getResources().getString(R.string.save));
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject json = new JSONObject();

				System.out.println("Overall ratting: " + overall.getRating());
				Integer overall_int = (int) overall.getRating();
				Integer difficulty_int = (int) difficulty.getRating();
				Integer life_int = (int) life.getRating();
				Integer fish_int = (int) fish.getRating();
				Integer wreck_int = (int) wreck.getRating();

				try {
					if (overall_int != 0)
						json.put("overall", overall_int);
					if (difficulty_int != 0)
						json.put("difficulty", difficulty_int);
					if (life_int != 0)
						json.put("marine", life_int);
					if (fish_int != 0)
						json.put("bigfish", fish_int);
					if (wreck_int != 0)
						json.put("wreck", wreck_int);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Review temp_review = new Review(json);
				mDive.setDiveReviews(temp_review);
				mListener.onReviewEditComplete(NewReviewDialogFragment.this);

				dismiss();
			}
		});

		faceR = null;
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// if (EditorInfo.IME_ACTION_DONE == actionId)
		// {
		// if (mVisibility.getSelectedItemPosition() == 0)
		// mDive.setVisibility(null);
		// else
		// {
		// String[] visibility =
		// ((String)mVisibility.getSelectedItem()).split(" ");
		// mDive.setVisibility(visibility[0].toLowerCase());
		// }
		// mListener.onVisibilityEditComplete(NewReviewDialogFragment.this);
		// dismiss();
		// return true;
		// }
		return false;
	}

}