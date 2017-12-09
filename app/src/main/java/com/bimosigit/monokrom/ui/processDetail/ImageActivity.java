package com.bimosigit.monokrom.ui.processDetail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.util.BitmapConverter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.iv_result)
    ImageView resultImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        byte[] bytes = getIntent().getByteArrayExtra(MonokromConstant.IMAGE_BYTE_ARRAY);
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        resultImageView.setImageBitmap(bitmap);
    }
}

