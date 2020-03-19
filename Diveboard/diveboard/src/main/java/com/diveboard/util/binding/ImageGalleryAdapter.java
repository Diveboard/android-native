package com.diveboard.util.binding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.mobile.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

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
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.position = position;
        Picasso.get()
                .load(pictures.get(position).small)
                .error(R.drawable.error)
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView photoImageView;
        public int position;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            photoImageView = itemView.findViewById(R.id.iv_photo);
        }

        @Override
        public void onClick(View v) {
            StfalconImageViewer.Builder<Picture> builder =
                    new StfalconImageViewer.Builder<>(v.getContext(),
                            pictures,
                            (imageView, image) -> Picasso.get().load(image.large).into(imageView));
            builder.withTransitionFrom(photoImageView);
            StfalconImageViewer<Picture> viewer = builder.build();
            viewer.setCurrentPosition(position);
            viewer.show();
        }
    }
}
