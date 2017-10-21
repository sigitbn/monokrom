package com.bimosigit.monokrom.ui.processDetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessDetailPresenter implements ProcessDetailContract.Presenter {

    private final StorageReference imagesRef;
    ProcessDetailContract.View view;

    ProcessDetailPresenter(ProcessDetailFragment view) {
        this.view = view;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");

    }

    @Override
    public void processGrayScale(Bitmap bitmap) {
        view.onGrayScaleProcessSuccess(bitmap);
    }

    @Override
    public void processConvolution(Bitmap bitmap) {
        view.onConvolutionProcessSuccess(bitmap);
    }

    @Override
    public void processEqualization(Bitmap bitmap) {
        view.onEqualizationProcessSuccess(bitmap);
    }

    @Override
    public void processThresholding(Bitmap bitmap) {
        view.onThresholdingProcessSuccess(bitmap);
    }

    @Override
    public void loadImage(String filename) {
        StorageReference ref = imagesRef.child(filename);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    view.onImageLoaded(bitmap);
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
}
