package com.bimosigit.monokrom.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.ui.processDetail.ProcessDetailActivity;
import com.bimosigit.monokrom.viewholder.ImageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by sigitbn on 10/21/17.
 */

public class MainPresenter implements MainContract.Presenter {

    private FirebaseRecyclerAdapter<String, ImageViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference imagesRef;
    private DatabaseReference mDataReference;

    MainContract.View view;

    MainPresenter(MainFragment view) {
        this.view = view;

        mDataReference = FirebaseDatabase.getInstance().getReference("images");
        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");
    }

    @Override
    public void createRecyclerViewAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(MonokromConstant.FIREBASE_CHILD_IMAGES);

        final FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(query, String.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<String, ImageViewHolder>(options) {
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
                loadImage(holder.itemImageView, fileName);

                holder.itemImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View itemView) {
                        view.startActivity(ProcessDetailActivity.class, fileName);
                    }
                });

                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.getAdapterPosition() < adapter.getItemCount()) {
                            deleteImage(fileName);
                            adapter.getRef(holder.getAdapterPosition()).removeValue();
                            adapter.notifyItemRemoved(holder.getAdapterPosition());
                            adapter.notifyItemRangeChanged(holder.getAdapterPosition(), adapter.getItemCount());
                            adapter.notifyDataSetChanged();

                        } else {

                        }

                    }
                });
            }
        };
        view.onRecyclerViewAdapterCreated(adapter);
    }

    @Override
    public void uploadImage(String path) {
        final Uri file = Uri.fromFile(new File(path));
        StorageReference child = imagesRef.child(file.getLastPathSegment());
        UploadTask uploadTask = child.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                writeNewImageNameToDB(file.getLastPathSegment());
            }
        });
    }

    @Override
    public void startAdapterListening() {
        adapter.startListening();
    }

    @Override
    public void stopAdapterListening() {
        adapter.stopListening();
    }

    private void writeNewImageNameToDB(String name) {
        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(name);
    }

    private void loadImage(final ImageView itemImageView, String filename) {

        StorageReference ref = imagesRef.child(filename);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    itemImageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
