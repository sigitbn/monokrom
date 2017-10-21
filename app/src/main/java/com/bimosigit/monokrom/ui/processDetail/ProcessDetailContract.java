package com.bimosigit.monokrom.ui.processDetail;

import android.graphics.Bitmap;

import com.bimosigit.monokrom.ui.BaseView;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessDetailContract {

    interface View extends BaseView {
        void onGrayScaleProcessSuccess(Bitmap bitmap);

        void onConvolutionProcessSuccess(Bitmap bitmap);

        void onEqualizationProcessSuccess(Bitmap bitmap);

        void onThresholdingProcessSuccess(Bitmap bitmap);

        void onImageLoaded(Bitmap bitmap);
    }

    interface Presenter {
        void processGrayScale(Bitmap bitmap);

        void processConvolution(Bitmap bitmap);

        void processEqualization(Bitmap bitmap);

        void processThresholding(Bitmap bitmap);

        void loadImage(String path);
    }
}
