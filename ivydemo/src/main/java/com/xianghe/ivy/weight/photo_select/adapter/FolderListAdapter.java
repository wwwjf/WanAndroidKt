package com.xianghe.ivy.weight.photo_select.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.xianghe.ivy.R;
import com.xianghe.ivy.weight.photo_select.ISNav;
import com.xianghe.ivy.weight.photo_select.bean.Folder;
import com.xianghe.ivy.weight.photo_select.common.OnFolderChangeListener;
import com.xianghe.ivy.weight.photo_select.config.ISListConfig;
import com.yuyh.easyadapter.abslistview.EasyLVAdapter;
import com.yuyh.easyadapter.abslistview.EasyLVHolder;

import java.util.List;

public class FolderListAdapter extends EasyLVAdapter<Folder> {

    private Context context;
    private List<Folder> folderList;
    private ISListConfig config;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList, ISListConfig config) {
        super(context, folderList, R.layout.view_photo_select_item_img_sel_folder);
        this.context = context;
        this.folderList = folderList;
        this.config = config;
    }

    @Override
    public void convert(EasyLVHolder holder, final int position, Folder folder) {
        if (position == 0) {
            holder.setText(R.id.tvFolderName, context.getResources().getString(R.string.photo_select_all_images))
                    .setText(R.id.tvImageNum, String.format(context.getResources().getString(R.string.photo_select_total), getTotalImageSize()));
            ImageView ivFolder = holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                ISNav.getInstance().displayImage(context, folder.cover.path, ivFolder);
            }
        } else {
            holder.setText(R.id.tvFolderName, folder.name)
                    .setText(R.id.tvImageNum, String.format(context.getResources().getString(R.string.photo_select_total), folder.images.size()));
            ImageView ivFolder = holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                ISNav.getInstance().displayImage(context, folder.cover.path, ivFolder);
            }
        }

        if (selected == position) {
            holder.setVisible(R.id.indicator, true);
        } else {
            holder.setVisible(R.id.indicator, false);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectIndex(position);
            }
        });
    }

    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null && folders.size() > 0) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int position) {
        if (selected == position)
            return;
        if (listener != null)
            listener.onChange(position, folderList.get(position));
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selected;
    }

    public void setOnFloderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }
}
