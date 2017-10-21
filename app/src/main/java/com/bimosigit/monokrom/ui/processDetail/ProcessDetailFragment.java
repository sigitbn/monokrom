package com.bimosigit.monokrom.ui.processDetail;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.adapter.ProcessedImageAdapter;
import com.bimosigit.monokrom.constant.MonokromConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessDetailFragment extends Fragment implements ProcessDetailContract.View {


    @BindView(R.id.gv_images)
    GridView imagesGridView;

    List<Bitmap> bitmaps;
    ProcessedImageAdapter adapter;
    ProcessDetailContract.Presenter presenter;

    Unbinder unbinder;

    public ProcessDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProcessDetailPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_process_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        bitmaps = new ArrayList<>();
        adapter = new ProcessedImageAdapter(getContext(), bitmaps);
        imagesGridView.setAdapter(adapter);

        String filename = getActivity().getIntent().getStringExtra(MonokromConstant.IMAGE_PATH);
        presenter.loadImage(filename);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_process)
    public void onViewClicked() {
    }

    @Override
    public void onGrayScaleProcessSuccess(Bitmap bitmap) {

    }

    @Override
    public void onConvolutionProcessSuccess(Bitmap bitmap) {

    }

    @Override
    public void onEqualizationProcessSuccess(Bitmap bitmap) {

    }

    @Override
    public void onThresholdingProcessSuccess(Bitmap bitmap) {

    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        bitmaps.clear();
        bitmaps.add(bitmap);
        adapter.notifyDataSetChanged();
    }
}
