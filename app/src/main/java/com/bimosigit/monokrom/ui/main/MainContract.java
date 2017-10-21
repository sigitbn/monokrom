package com.bimosigit.monokrom.ui.main;

import com.bimosigit.monokrom.ui.BaseView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * Created by sigitbn on 10/21/17.
 */

public class MainContract {

    interface View extends BaseView {
        void onRecyclerViewAdapterCreated(FirebaseRecyclerAdapter adapter);

        void startActivity(Class activityClass, String path);
    }

    interface Presenter {
        void createRecyclerViewAdapter();

        void uploadImage(String path);

        void startAdapterListening();

        void stopAdapterListening();
    }
}
