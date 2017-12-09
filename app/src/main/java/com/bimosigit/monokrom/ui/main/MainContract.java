package com.bimosigit.monokrom.ui.main;

import com.bimosigit.monokrom.ui.BaseView;

/**
 * Created by sigitbn on 10/21/17.
 */

public class MainContract {

    interface View extends BaseView {
//        void startActivity(Class activityClass, byte[] bytes);
    }

    interface Presenter {
        void uploadImage(String path);
    }
}
