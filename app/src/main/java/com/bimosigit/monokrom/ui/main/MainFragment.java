package com.bimosigit.monokrom.ui.main;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private static final int REQUEST_OPEN_GALLERY = 1;
    @BindView(R.id.rv_images)
    RecyclerView imagesRecyclerView;

    Unbinder unbinder;
    private FirebaseRecyclerAdapter<String, ImageViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference imagesRef;
    private DatabaseReference mDataReference;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        mDataReference = FirebaseDatabase.getInstance().getReference("images");
        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");

        setRecyclerView();

        return view;
    }

    @OnClick(R.id.fab_home)
    public void onViewClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OPEN_GALLERY);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OPEN_GALLERY) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mCurrentPhotoPath = cursor.getString(columnIndex);
                uploadImage(mCurrentPhotoPath);
                cursor.close();
            }
        }
    }

    private void setRecyclerView() {
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

        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        imagesRecyclerView.setAdapter(adapter);
    }


    private void writeNewImageNameToDB(String name) {
        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(name);
    }

    void uploadImage(String path) {
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

    void loadImage(final ImageView itemImageView, String filename) {

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
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteImage(String fileName) {

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}