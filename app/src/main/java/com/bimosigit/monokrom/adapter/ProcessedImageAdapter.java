package com.bimosigit.monokrom.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimosigit.monokrom.R;
import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.ui.processDetail.ImageActivity;
import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ProcessedImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> hashMapKey;
    private HashMap<String, Bitmap> bitmapHashMap;


    public ProcessedImageAdapter(Context context, List<String> hashMapKey, HashMap<String, Bitmap> bitmapHashMap) {
        this.context = context;
        this.bitmapHashMap = bitmapHashMap;
        this.hashMapKey = hashMapKey;
    }

    @Override
    public int getCount() {
        return bitmapHashMap.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View grid;
        String key = hashMapKey.get(position);

        if (view == null) {
            grid = LayoutInflater.from(context).inflate(R.layout.card_item_image_with_text, null);
        } else {
            grid = view;
        }

        TextView textView = grid.findViewById(R.id.tv_item);
        ImageView imageView = grid.findViewById(R.id.iv_item);

        textView.setText(key);
        imageView.setImageBitmap(bitmapHashMap.get(key));


        final Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(MonokromConstant.IMAGE_BYTE_ARRAY, BitmapConverter.
                bitmap2ByteArray(bitmapHashMap.get(key)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(intent);
            }
        });

        return grid;
    }
}
