package com.xianghe.ivy.weight.photo_select.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.weight.photo_select.ISNav;
import com.xianghe.ivy.weight.photo_select.bean.Image;
import com.xianghe.ivy.weight.photo_select.common.Constant;
import com.xianghe.ivy.weight.photo_select.common.OnItemClickListener;
import com.xianghe.ivy.weight.photo_select.config.ISListConfig;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;


public class ImageListAdapter extends EasyRVAdapter<Image> {

    private boolean showCamera;
    private boolean mutiSelect;

    private ISListConfig config;
    private Context context;
    private OnItemClickListener listener;

    public ImageListAdapter(Context context, List<Image> list, ISListConfig config) {
        super(context, list, R.layout.view_photo_select_item_img_sel, R.layout.view_photo_select_item_img_sel_take_photo);
        this.context = context;
        this.config = config;
    }

    @Override
    protected void onBindData(final EasyRVHolder viewHolder, final int position, final Image item) {

        if (position == 0 && showCamera) {
            ImageView iv = viewHolder.getView(R.id.ivTakePhoto);
            iv.setImageResource(R.drawable.photo_select_ic_take_photo);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(position, item);
                }
            });
            return;
        }

        if (mutiSelect) {
            viewHolder.getView(R.id.ivPhotoCheaked).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, item);
                        if (ret == 1) { // ????????????
                            if (Constant.imageList.contains(item.path)) {
                                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.photo_select_ic_checked);
                            } else {
                                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.photo_select_ic_uncheck);
                            }
                        }
                    }
                }
            });
        }

        viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onImageClick(position, item);
            }
        });

        final ImageView iv = viewHolder.getView(R.id.ivImage);
        ISNav.getInstance().displayImage(context, item.path, iv);

        if (mutiSelect) {
            viewHolder.setVisible(R.id.ivPhotoCheaked, true);
            if (Constant.imageList.contains(item.path)) {
                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.photo_select_ic_checked);
            } else {
                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.photo_select_ic_uncheck);
            }
        } else {
            viewHolder.setVisible(R.id.ivPhotoCheaked, false);
        }
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMutiSelect(boolean mutiSelect) {
        this.mutiSelect = mutiSelect;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return 1;
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
