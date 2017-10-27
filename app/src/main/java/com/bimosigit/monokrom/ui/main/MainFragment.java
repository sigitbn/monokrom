package com.bimosigit.monokrom.ui.main;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements MainContract.View {


    private static final int REQUEST_OPEN_GALLERY = 1;
    @BindView(R.id.rv_images)
    RecyclerView imagesRecyclerView;

    MainContract.Presenter presenter;

    Unbinder unbinder;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new MainPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        presenter.createRecyclerViewAdapter();

        return view;
    }

    @OnClick(R.id.fab_home)
    public void onViewClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OPEN_GALLERY);
    }


    @Override
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
                presenter.uploadImage(mCurrentPhotoPath);
                cursor.close();
            }
        }
    }

    @Override
    public void onRecyclerViewAdapterCreated(FirebaseRecyclerAdapter adapter) {

        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        imagesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void startActivity(Class targetClass, byte[] bytes) {
        try {
            Intent intent = new Intent(getActivity(), targetClass);
            intent.putExtra(MonokromConstant.IMAGE_BYTE_ARRAY, bytes);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.startAdapterListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stopAdapterListening();
    }
}
