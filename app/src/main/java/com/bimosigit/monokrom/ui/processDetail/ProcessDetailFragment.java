package com.bimosigit.monokrom.ui.processDetail;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.bimosigit.monokrom.processor.MergeByteImage;
import com.bimosigit.monokrom.util.BitmapConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;
    private byte[] bytesOriginalImage;
    private byte[] bytesResult;
    private List<Component> components;

    private FirebaseStorage storage;
    private StorageReference imagesRef;
    private DatabaseReference mDataReference;

    public ProcessDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataReference = FirebaseDatabase.getInstance().getReference("images");
        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");

        presenter = new ProcessDetailPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_process_detail, container, false);
        unbinder = ButterKnife.bind(this, view);


        hashMapKey = new ArrayList<>();
        bitmapHashMap = new HashMap<>();
        components = new ArrayList<>();
        hashMapKey = new ArrayList<>();
        refreshLayout.setEnabled(false);

//        bytesOriginalImage = getActivity().getIntent().getByteArrayExtra(MonokromConstant.IMAGE_BYTE_ARRAY);

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

        String filename = getActivity().getIntent().getStringExtra(MonokromConstant.IMAGE_FILENAME);

        final long ONE_MEGABYTE = 1024 * 1024;
        imagesRef.child(filename).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bytesOriginalImage = bytes;
                Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesOriginalImage);
                hashMapKey.add(MonokromConstant.ORIGINAL_FILENAME);
                bitmapHashMap.put(MonokromConstant.ORIGINAL_FILENAME, bitmap);
                bytesOriginalImage = BitmapConverter.bitmap2ByteArray(bitmap);
                adapter.notifyDataSetChanged();
                Log.d("Image size", bytesOriginalImage.length + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
//
//        imagesRef.child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                    hashMapKey.add(MonokromConstant.ORIGINAL_FILENAME);
//                    bitmapHashMap.put(MonokromConstant.ORIGINAL_FILENAME, bitmap);
//                    bytesOriginalImage = BitmapConverter.bitmap2ByteArray(bitmap);
//                    adapter.notifyDataSetChanged();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesOriginalImage);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onGrayScaleProcessSuccess(byte[] bytes) {
        setLoading(false);
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.GRAYSCALE_FILENAME);
        bitmapHashMap.put(MonokromConstant.GRAYSCALE_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processConvolution(bytes);
//        presenter.processEqualization(bytes);
    }

    @Override
    public void onConvolutionProcessSuccess(byte[] bytes) {
        setLoading(false);
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.CONVOLUTION_FILENAME);
        bitmapHashMap.put(MonokromConstant.CONVOLUTION_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processEqualization(bytes);

    }

    @Override
    public void onConvolutionProcessSuccess(List<byte[]> bytesList) {
        String[] filter = {"Konvolusi", "Sobel", "Prewit"};
        for (int i = 0; i < bytesList.size(); i++) {
            Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesList.get(i));

            hashMapKey.add(MonokromConstant.CONVOLUTION_FILENAME + filter[i]);
            bitmapHashMap.put(MonokromConstant.CONVOLUTION_FILENAME + filter[i], bitmap);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEqualizationProcessSuccess(byte[] bytes) {
        setLoading(false);
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        hashMapKey.add(MonokromConstant.EQUALIZATION_FILENAME);
        bitmapHashMap.put(MonokromConstant.EQUALIZATION_FILENAME, bitmap);
        adapter.notifyDataSetChanged();

        presenter.processThresholding(bytes);
    }

    @Override
    public void onThresholdingProcessSuccess(List<byte[]> bytesList) {
        setLoading(false);
        for (int i = 0; i < bytesList.size(); i++) {
            Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytesList.get(i));
            hashMapKey.add(MonokromConstant.BLACKWHITE_FILENAME + i);
            bitmapHashMap.put(MonokromConstant.BLACKWHITE_FILENAME + i, bitmap);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onThresholdingProcessSuccess(List<Component> components, byte[] bytes) {
        setLoading(false);
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
        setLoading(false);
        byte[] blackWhiteImage = MergeByteImage.getBlackImage(person.getBytes());

        for (Component component : components) {

            blackWhiteImage = MergeByteImage.merge(blackWhiteImage, component.getComponentPixels());
//                    blackWhiteImage = MergeByteImage.drawCentroid(blackWhiteImage, component.getCentroid());
        }

//        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(person.getBytes());
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(blackWhiteImage);
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
        setLoading(false);
        Toast.makeText(getActivity(), "Person information saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedSkin(List<byte[]> faces) {
        setLoading(false);
        for (int i = 0; i < faces.size(); i++) {
            Bitmap bitmap = BitmapConverter.byteArray2Bitmap(faces.get(i));
            hashMapKey.add(MonokromConstant.BLACKWHITE_FILENAME + i);
            bitmapHashMap.put(MonokromConstant.BLACKWHITE_FILENAME + i, bitmap);
        }
        adapter.notifyDataSetChanged();
    }


    @OnClick({R.id.btn_process, R.id.btn_save, R.id.btn_check, R.id.btn_set_skin, R.id.btn_check_skin})
    public void onViewClicked(View view) {
        setLoading(true);
        switch (view.getId()) {
            case R.id.btn_set_skin:
                presenter.setSkin(bytesOriginalImage);
                Toast.makeText(getActivity(), "Data disimpan", Toast.LENGTH_SHORT).show();
                setLoading(false);
                return;

            case R.id.btn_check_skin:
                presenter.checkSkin(bytesOriginalImage);
                break;

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
                    setLoading(false);
                    return;
                }
                break;
        }
    }

    @Override
    public void setLoading(final boolean active) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(active);
            }
        });
    }
}
