package com.diveboard.mobile.editdive;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Review;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditReviewDialogFragment extends DialogFragment implements
			OnEditorActionListener {
	
	public interface EditReviewDialogListener {
		void onReviewEditComplete(DialogFragment dialog);
	}
	private DiveboardModel		mModel;
	private Dive mDive;
	private EditReviewDialogListener mListener;
	private RatingBar overall, difficulty, fish, life, wreck;
	private TextView hintOverall, hintDifficulty, hintFish, hintLife, hintWreck;
	private int mTextSize = 14;

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
		
		View view = inflater.inflate(R.layout.dialog_edit_review, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		mDive = mModel.getDives().get(getArguments().getInt("index"));
		Typeface faceR = mModel.getLatoR();

		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		TextView title = (TextView) view.findViewById(R.id.title);
		System.out.println("title " + title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_review_title));
		
		ImageView delete1 = (ImageView) view.findViewById(R.id.DeleteOverall);
		ImageView delete2 = (ImageView) view.findViewById(R.id.DeleteDifficulty);
		ImageView delete3 = (ImageView) view.findViewById(R.id.DeleteMarine);
		ImageView delete4 = (ImageView) view.findViewById(R.id.DeleteBigFish);
		ImageView delete5 = (ImageView) view.findViewById(R.id.DeleteWreck);
		
		TextView overallTV = (TextView) view.findViewById(R.id.OverallTV);
		TextView difficultyTV = (TextView) view.findViewById(R.id.difficultyTV);
		TextView lifeTV = (TextView) view.findViewById(R.id.lifeTV);
		TextView fishTV = (TextView) view.findViewById(R.id.fishTV);
		TextView wreckTV = (TextView) view.findViewById(R.id.wreckTV);
		overallTV.setTextSize(mTextSize);
		difficultyTV.setTextSize(mTextSize);
		lifeTV.setTextSize(mTextSize);
		fishTV.setTextSize(mTextSize);
		wreckTV.setTextSize(mTextSize);
		
		hintOverall = (TextView) view.findViewById(R.id.OverallHintTV);
		hintDifficulty = (TextView) view.findViewById(R.id.DifficultyHintTV);
		hintLife = (TextView) view.findViewById(R.id.LifeHintTV);
		hintFish = (TextView) view.findViewById(R.id.FishHintTV);
		hintWreck = (TextView) view.findViewById(R.id.wreck_hintTV);
		hintOverall.setTypeface(faceR);
		hintOverall.setTextSize(mTextSize);
		hintDifficulty.setTypeface(faceR);
		hintDifficulty.setTextSize(mTextSize);
		hintLife.setTypeface(faceR);
		hintLife.setTextSize(mTextSize);
		hintFish.setTypeface(faceR);
		hintFish.setTextSize(mTextSize);
		hintWreck.setTypeface(faceR);
		hintWreck.setTextSize(mTextSize);
		
		delete1.setOnClickListener(deleteReview);
		delete2.setOnClickListener(deleteReview);
		delete3.setOnClickListener(deleteReview);
		delete4.setOnClickListener(deleteReview);
		delete5.setOnClickListener(deleteReview);

		int rating = 0;
		overall = (RatingBar) view.findViewById(R.id.OverallRatingBar);
		difficulty = (RatingBar) view.findViewById(R.id.DifficultyRatingBar);
		fish = (RatingBar) view.findViewById(R.id.FishRatingBar);
		life = (RatingBar) view.findViewById(R.id.LifeRatingBar);
		wreck = (RatingBar) view.findViewById(R.id.WreckRatingBar);

		//We load the previous values of the reviews if there were any
				if (mDive.getDiveReviews() != null) {

					if (mDive.getDiveReviews().getOverall() != null) {
						rating = mDive.getDiveReviews().getOverall();
						overall.setRating(rating);
						switch (rating) {
						case 1:
							hintOverall.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintOverall.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintOverall.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintOverall.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintOverall.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						}
					}
					if (mDive.getDiveReviews().getDifficulty() != null) {
						rating = mDive.getDiveReviews().getDifficulty();
						difficulty.setRating(rating);

						switch (rating) {
						case 1:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_trivial));
							break;

						case 2:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_simple));
							break;

						case 3:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_somewhat_simple));
							break;

						case 4:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_tricky));
							break;

						case 5:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_hardcore));
							break;

						}

					}
					if (mDive.getDiveReviews().getBigFish() != null) {
						rating = mDive.getDiveReviews().getBigFish();
						fish.setRating(rating);

						switch (rating) {
						case 1:
							hintFish.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintFish.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintFish.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintFish.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintFish.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						}
					}
					if (mDive.getDiveReviews().getMarine() != null) {
						rating = mDive.getDiveReviews().getMarine();
						life.setRating(rating);

						switch (rating) {
						case 1:
							hintLife.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintLife.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintLife.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintLife.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintLife.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						}
					}
					if (mDive.getDiveReviews().getWreck() != null) {
						rating = mDive.getDiveReviews().getWreck();
						wreck.setRating(rating);

						switch (rating) {
						case 1:
							hintWreck.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintWreck.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintWreck.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintWreck.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintWreck.setText(getResources().getString(
									R.string.hint_excellent));
							break;
						}
					}
				}
				
				
				//Handle dynamically the update of the hints of the review
				overall.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						// TODO Auto-generated method stub
						final int tempRating = (int) overall.getRating();
						switch (tempRating) {

						case 1:
							hintOverall.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintOverall.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintOverall.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintOverall.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintOverall.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						default:
							break;

						}
					}
				});

				difficulty.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						// TODO Auto-generated method stub
						final int tempRating = (int) difficulty.getRating();
						switch (tempRating) {

						case 1:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_trivial));
							break;

						case 2:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_simple));
							break;

						case 3:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_somewhat_simple));
							break;

						case 4:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_tricky));
							break;

						case 5:
							hintDifficulty.setText(getResources().getString(
									R.string.hint_hardcore));
							break;

						default:
							break;

						}
					}
				});
				
				life.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						// TODO Auto-generated method stub
						final int tempRating = (int) life.getRating();
						switch (tempRating) {

						case 1:
							hintLife.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintLife.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintLife.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintLife.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintLife.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						default:
							break;

						}
					}
				});
				
				fish.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						// TODO Auto-generated method stub
						final int tempRating = (int) fish.getRating();
						switch (tempRating) {

						case 1:
							hintFish.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintFish.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintFish.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintFish.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintFish.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						default:
							break;

						}
					}
				});
				
				wreck.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						// TODO Auto-generated method stub
						final int tempRating = (int) wreck.getRating();
						switch (tempRating) {

						case 1:
							hintWreck.setText(getResources().getString(
									R.string.hint_terrible));
							break;

						case 2:
							hintWreck.setText(getResources().getString(
									R.string.hint_poor));
							break;

						case 3:
							hintWreck.setText(getResources().getString(
									R.string.hint_average));
							break;

						case 4:
							hintWreck.setText(getResources().getString(
									R.string.hint_very_good));
							break;

						case 5:
							hintWreck.setText(getResources().getString(
									R.string.hint_excellent));
							break;

						default:
							break;

						}
					}
				});

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
				mListener.onReviewEditComplete(EditReviewDialogFragment.this);

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
	
	OnClickListener deleteReview = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.DeleteOverall:
				hintOverall.setText("");
				overall.setRating(0);
				break;

			case R.id.DeleteDifficulty:
				hintDifficulty.setText("");
				difficulty.setRating(0);
				break;

			case R.id.DeleteMarine:
				hintLife.setText("");
				life.setRating(0);
				break;

			case R.id.DeleteBigFish:
				hintFish.setText("");
				fish.setRating(0);
				break;

			case R.id.DeleteWreck:
				hintWreck.setText("");
				wreck.setRating(0);
				break;

			}

		}
	};

}