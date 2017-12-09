package com.bimosigit.monokrom.ui.processDetail;

import android.os.AsyncTask;
import android.util.Log;

import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.processor.BlackWhite;
import com.bimosigit.monokrom.processor.ChainCode;
import com.bimosigit.monokrom.processor.Convolution;
import com.bimosigit.monokrom.processor.Equalization;
import com.bimosigit.monokrom.processor.FaceDetection;
import com.bimosigit.monokrom.processor.Fourier;
import com.bimosigit.monokrom.processor.MergeByteImage;
import com.bimosigit.monokrom.processor.SkinFilter;
import com.bimosigit.monokrom.processor.Thresholding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessDetailPresenter implements ProcessDetailContract.Presenter {

    ProcessDetailContract.View view;
    private List<Integer> thresholds;

    ProcessDetailPresenter(ProcessDetailFragment view) {
        this.view = view;
    }

    @Override
    public void processGrayScale(byte[] bytes) {
//        view.onGrayScaleProcessSuccess(GrayScale.getImageGrayScale(bytes));
        new FourierTask(view).execute(bytes);

//        ArrayList<byte[]> bytes1 = new ArrayList<>();
//        bytes1.add(Fourier.getBytesFromFFT(bytes));
//        view.onConvolutionProcessSuccess(bytes1);
    }

    private static class FourierTask extends AsyncTask<byte[], Void, byte[]> {

        ProcessDetailContract.View view;

        public FourierTask(ProcessDetailContract.View view) {
            this.view = view;
        }

        @Override
        protected byte[] doInBackground(byte[]... bytes) {
            return Fourier.getBytesFromFFT(bytes[0]);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            ArrayList<byte[]> bytes1 = new ArrayList<>();
            bytes1.add(bytes);
            view.onConvolutionProcessSuccess(bytes1);
            view.setLoading(false);

        }
    }

    @Override
    public void processConvolution(byte[] bytes) {
        thresholds = Thresholding.getThreshold(bytes);
        view.onConvolutionProcessSuccess(Convolution.getImageConvolution(bytes));

//        view.onConvolutionProcessSuccess(Convolution.getImageConvolutionList(bytes));

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
    public void ProcessRecognition(byte[] bytes, final List<Component> components) {
        final Person person = FaceDetection.recognize(bytes, components);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("persons");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person result = person;
                double minimumDiffRatio = 9999;
                int minimumDistance = 9999;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Person current = child.getValue(Person.class);


                    List<Component> componentListCurrent = new ArrayList<>();
                    for (DataSnapshot componentSnapShot : child.child("components").getChildren()) {
                        componentListCurrent.add(componentSnapShot.getValue(Component.class));
                    }

                    if (current != null) {

                        current.setComponents(componentListCurrent);
                        current = FaceDetection.compare(person, current);
                        current.setName(child.child("name").getValue(String.class));

                        if (current.getDistance() < minimumDistance) {
                            minimumDistance = current.getDistance();
                            Log.d("Total Diff", minimumDistance + ", " + "Nama :" + (current.getName() != null ? current.getName() : "null" + "") + current.getGoldenRatio());
                            result = current;
                        }
//                        double currentDiffRatio = Math.abs(current.getGoldenRatio() - person.getGoldenRatio());
//                        if (minimumDiffRatio > currentDiffRatio) {
//                            result = current;
//                            minimumDiffRatio = currentDiffRatio;
//                        }
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
        DatabaseReference databaseReference = database.getReference("persons");
        Person person = FaceDetection.recognize(bytesResult, components);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(person);

        for (Component component : components) {
            String componentKey = databaseReference.child(key).child("components").push().getKey();
            databaseReference.child(key).child("components").child(componentKey).setValue(component);
//            databaseReference.child(key).child("components").child(componentKey).child("componentPixels").setValue(Arrays.asList(component.getComponentPixels()));
//            databaseReference.child(key).child("components").child(componentKey).child("componentPixels").setValue(componentPixels);
        }
        view.onImageSaved(person);
    }

    @Override
    public void setSkin(byte[] bytes) {
        SkinFilter.setSkins(bytes);
    }

    @Override
    public void checkSkin(byte[] bytes) {
        view.onCheckedSkin(FaceDetection.getFaces(bytes));
    }


}
