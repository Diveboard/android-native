package com.diveboard.util.binding;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {
    private List<Picture> pictures;

    public ImageGalleryAdapter(List<Picture> pictures) {
        this.pictures = pictures == null ? new ArrayList<>() : pictures;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_image, parent, false);
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        //8dp margins four times, 3 images, -1 for rounding
//        int targetItemSize = ((width - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8 * 4, context.getResources().getDisplayMetrics())) / 3);
//        ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
//        layoutParams.height = targetItemSize;
//        layoutParams.width = targetItemSize;
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get()
                .load(pictures.get(position).thumbnail)
                .error(R.drawable.error)
                .fit()
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView photoImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            photoImageView = itemView.findViewById(R.id.iv_photo);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
