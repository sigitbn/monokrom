package com.bimosigit.monokrom.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.ui.GlideApp;
import com.bimosigit.monokrom.ui.main.MainFragment;
import com.bimosigit.monokrom.ui.processDetail.ProcessDetailActivity;
import com.bimosigit.monokrom.viewholder.ImageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

/**
 * Created by sigitbn on 11/6/17.
 */

public class MainFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<String, ImageViewHolder> {


    private StorageReference imagesRef;
    private MainFragment fragment;

    public MainFirebaseRecyclerAdapter(FirebaseRecyclerOptions<String> options, StorageReference imagesRef, MainFragment fragment) {
        super(options);
        this.imagesRef = imagesRef;
        this.fragment = fragment;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_main_image, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(final ImageViewHolder holder, int position, final String fileName) {

        StorageReference ref = imagesRef.child(fileName);

        GlideApp.with(fragment)
                .load(ref)
                .into(holder.itemImageView);
        holder.itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fragment.getContext(), ProcessDetailActivity.class);
                intent.putExtra(MonokromConstant.IMAGE_FILENAME, fileName);
                fragment.startActivity(intent);
            }
        });
        holder.itemTextView.setText(fileName);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() < getItemCount()) {
                    deleteImage(fileName);
                    getRef(holder.getAdapterPosition()).removeValue();
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                    notifyDataSetChanged();

                } else {

                }

            }
        });
    }

    private void deleteImage(String fileName) {

        StorageReference fileRef = imagesRef.child(fileName);

        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
}
