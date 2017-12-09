package com.bimosigit.monokrom.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by sigitbn on 10/21/17.
 */

public class MainPresenter implements MainContract.Presenter {

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

    private void writeNewImageNameToDB(String name) {
        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(name);
    }

//    private void loadImage(final ImageView itemImageView, String filename) {
//
//
//
//        try {
//            final File localFile = File.createTempFile("Images", "bmp");
//            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    final Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    itemImageView.setImageBitmap(bitmap);
//                    itemImageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View item) {
//                            byte[] bytesArray = BitmapConverter.bitmap2ByteArray(bitmap);
//                            view.startActivity(ProcessDetailActivity.class, bytesArray);
//                        }
//                    });
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
