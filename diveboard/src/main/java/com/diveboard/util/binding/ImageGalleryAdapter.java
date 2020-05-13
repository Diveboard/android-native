package com.diveboard.util.binding;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.mobile.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {
    private final boolean supportFavorites;
    private List<Picture> pictures;
    private int contextMenuPosition;

    public ImageGalleryAdapter(List<Picture> pictures, boolean supportFavorites) {
        this.pictures = pictures == null ? new ArrayList<>() : pictures;
        this.supportFavorites = supportFavorites;
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
        if (supportFavorites) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setContextMenuPosition(position);
                    return false;
                }
            });
        }
        holder.position = position;
        Picasso.get()
                .load(pictures.get(position).small)
                .error(R.drawable.error)
                .into(holder.photoImageView);
    }

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    public void setContextMenuPosition(int contextMenuPosition) {
        this.contextMenuPosition = contextMenuPosition;
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private final ImageView photoImageView;
        public int position;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            photoImageView = itemView.findViewById(R.id.iv_photo);
            itemView.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //menuInfo is null
            menu.add(Menu.NONE, R.id.ctx_menu_set_cover, Menu.NONE, R.string.menu_set_cover);
        }
    }
}
