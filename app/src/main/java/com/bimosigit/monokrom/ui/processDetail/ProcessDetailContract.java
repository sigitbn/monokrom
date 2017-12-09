package com.bimosigit.monokrom.ui.processDetail;

import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.ui.BaseView;

import java.util.List;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessDetailContract {

    interface View extends BaseView {
        void onGrayScaleProcessSuccess(byte[] bytes);

        void onConvolutionProcessSuccess(byte[] bytes);

        void onConvolutionProcessSuccess(List<byte[]> bytesList);

        void onEqualizationProcessSuccess(byte[] bytes);

        void onThresholdingProcessSuccess(List<byte[]> bytesList);

        void onThresholdingProcessSuccess(List<Component> components, byte[] bytes);

        void onImageRecognized(Person person);

        void onImageSaved(Person person);

        void onCheckedSkin(List<byte[]> faces);
        void setLoading(boolean active);
    }

    interface Presenter {
        void processGrayScale(byte[] bytes);

        void processConvolution(byte[] bytes);

        void processEqualization(byte[] bytes);

        void processThresholding(byte[] bytes);

        void ProcessRecognition(byte[] bytes, List<Component> components);

        void saveData(byte[] bytesResult, List<Component> components);

        void setSkin(byte[] bytes);

        void checkSkin(byte[] bytes);
    }
}
