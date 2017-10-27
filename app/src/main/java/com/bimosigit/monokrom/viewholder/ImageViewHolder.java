package com.bimosigit.monokrom.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimosigit.monokrom.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sigitbn on 10/20/17.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.iv_item)
    public ImageView itemImageView;

    @BindView(R.id.tv_item)
    public TextView itemTextView;

    @BindView(R.id.btn_delete)
    public ImageButton deleteButton;

    public ImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
