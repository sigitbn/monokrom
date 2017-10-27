package com.bimosigit.monokrom.ui.processDetail;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.adapter.ProcessedImageAdapter;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.ArrayList;
import java.util.HashMap;
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

    List<String> hashMapKey;
    HashMap<String, Bitmap> bitmapHashMap;
    ProcessedImageAdapter adapter;
    ProcessDetailContract.Presenter presenter;

    Unbinder unbinder;
    private byte[] bytesOriginalImage;
    private byte[] bytesResult;
    private List<Component> components;

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

        bytesOriginalImage = getActivity().getIntent().getByteArrayExtra(MonokromConstant.IMAGE_BYTE_ARRAY);

        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesOriginalImage);
        hashMapKey = new ArrayList<>();
        bitmapHashMap = new HashMap<>();
        components = new ArrayList<>();
        hashMapKey = new ArrayList<>();

        hashMapKey.add(MonokromConstant.ORIGINAL_FILENAME);
        bitmapHashMap.put(MonokromConstant.ORIGINAL_FILENAME, bitmap);
        setGridView();

        return view;
    }

    private void setGridView() {
        adapter = new ProcessedImageAdapter(getActivity(), hashMapKey, bitmapHashMap);
        imagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), hashMapKey.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        imagesGridView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onGrayScaleProcessSuccess(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.GRAYSCALE_FILENAME);
        bitmapHashMap.put(MonokromConstant.GRAYSCALE_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processConvolution(bytes);
//        presenter.processEqualization(bytes);
    }

    @Override
    public void onConvolutionProcessSuccess(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.CONVOLUTION_FILENAME);
        bitmapHashMap.put(MonokromConstant.CONVOLUTION_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processEqualization(bytes);

    }

    @Override
    public void onEqualizationProcessSuccess(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.EQUALIZATION_FILENAME);
        bitmapHashMap.put(MonokromConstant.EQUALIZATION_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processThresholding(bytes);
    }

    @Override
    public void onThresholdingProcessSuccess(List<byte[]> bytesList) {
        for (int i = 0; i < bytesList.size(); i++) {
            Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesList.get(i));
            hashMapKey.add(MonokromConstant.BLACKWHITE_FILENAME + i);
            bitmapHashMap.put(MonokromConstant.BLACKWHITE_FILENAME + i, bitmap);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onThresholdingProcessSuccess(List<Component> components, byte[] bytes) {

        this.components.clear();
        this.components.addAll(components);
        bytesResult = bytes;

        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.BLACKWHITE_FILENAME);
        bitmapHashMap.put(MonokromConstant.BLACKWHITE_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onImageRecognized(Person person) {


        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(person.getBytes());
        String personName = person.getName();

        if (personName == null) {
            personName = "unknown";
        }
        hashMapKey.add(personName);
        bitmapHashMap.put(personName, bitmap);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onImageSaved(Person person) {
        Toast.makeText(getActivity(), "Person information saved", Toast.LENGTH_SHORT).show();
    }


    @OnClick({R.id.btn_process, R.id.btn_save, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_process:
                presenter.processGrayScale(bytesOriginalImage);
                break;

            case R.id.btn_save:
                presenter.saveData(bytesResult, components);
                break;

            case R.id.btn_check:
                if (bytesResult != null) {
                    presenter.ProcessRecognition(bytesResult, components);
                } else {
                    Toast.makeText(getActivity(), "Citra belum diproses", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
