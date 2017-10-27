package com.bimosigit.monokrom.ui.processDetail;

import android.util.Log;

import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.processor.BlackWhite;
import com.bimosigit.monokrom.processor.ChainCode;
import com.bimosigit.monokrom.processor.Convolution;
import com.bimosigit.monokrom.processor.Equalization;
import com.bimosigit.monokrom.processor.FaceDetection;
import com.bimosigit.monokrom.processor.GrayScale;
import com.bimosigit.monokrom.processor.MergeByteImage;
import com.bimosigit.monokrom.processor.Thresholding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessDetailPresenter implements ProcessDetailContract.Presenter {

    private ProcessDetailContract.View view;
    private List<Integer> thresholds;

    ProcessDetailPresenter(ProcessDetailFragment view) {
        this.view = view;
    }

    @Override
    public void processGrayScale(byte[] bytes) {

        view.onGrayScaleProcessSuccess(GrayScale.getImageGrayScale(bytes));
    }

    @Override
    public void processConvolution(byte[] bytes) {
        thresholds = Thresholding.getThreshold(bytes);
        view.onConvolutionProcessSuccess(Convolution.getImageConvolution(bytes));
    }

    @Override
    public void processEqualization(byte[] bytes) {
        view.onEqualizationProcessSuccess(Equalization.getImageEqualization(bytes));
    }

    @Override
    public void processThresholding(byte[] bytes) {

        for (int threshold : thresholds) {
            byte[] blackWhiteImage = BlackWhite.getBlackWhite(bytes, threshold);

            List<Component> components = ChainCode.getComponents(blackWhiteImage);
            if (components.size() > 5) {

                blackWhiteImage = MergeByteImage.getBlackImage(blackWhiteImage);

                for (Component component : components) {

                    blackWhiteImage = MergeByteImage.merge(blackWhiteImage, component.getComponentPixels());
//                    blackWhiteImage = MergeByteImage.drawCentroid(blackWhiteImage, component.getCentroid());
                }
//                bytesList.add(blackWhiteImage);
                view.onThresholdingProcessSuccess(components, blackWhiteImage);
//                view.onComponentsDetected(components, blackWhiteImage);
                break;
            }
        }


    }

    @Override
    public void ProcessRecognition(byte[] bytes, List<Component> components) {
        final Person person = FaceDetection.recognize(bytes, components);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("person");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person result = person;
                double minimumDiffRatio = 9999;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Person current = child.getValue(Person.class);
                    if (current != null) {
                        double currentDiffRatio = Math.abs(current.getGoldenRatio() - person.getGoldenRatio());
                        if (minimumDiffRatio > currentDiffRatio) {
                            result = current;
                            minimumDiffRatio = currentDiffRatio;
                        }
                    }

                }

                result.setBytes(person.getBytes());
                view.onImageRecognized(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void saveData(byte[] bytesResult, List<Component> components) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("person");
        Person person = FaceDetection.recognize(bytesResult, components);
        databaseReference.push().setValue(person);

        view.onImageSaved(person);
    }


}
