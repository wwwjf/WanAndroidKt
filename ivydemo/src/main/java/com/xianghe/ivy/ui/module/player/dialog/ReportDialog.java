package com.xianghe.ivy.ui.module.player.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.ReportDialogTypeItemAdapter;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.utils.KLog;

import java.io.Serializable;
import java.util.ArrayList;


public class ReportDialog extends DialogFragment implements View.OnClickListener, IRecyclerItemClickListener {
    private static final String TAG = "ReportDialog";

    private static final String KEY_ITEMS = "key_items";

    //private RadioGroup mRgReportReason;
    private RecyclerView mViewList;
    private View mBtnClose;
    private EditText mEtOthers;
    private View mBtnSubmit;

    private ArrayList<Item> mItems;
    private ReportDialogTypeItemAdapter mAdapter;

    private OnContentListener mListener = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mItems = (ArrayList<Item>) arguments.getSerializable(KEY_ITEMS);
        mAdapter = new ReportDialogTypeItemAdapter(mItems);
    }


    private void findView(View rootView, Bundle savedInstanceState) {
        //mRgReportReason = rootView.findViewById(R.id.rg_report_reason);
        mViewList = rootView.findViewById(R.id.view_list);
        mBtnClose = rootView.findViewById(R.id.btn_close);
        mEtOthers = rootView.findViewById(R.id.et_others);
        mBtnSubmit = rootView.findViewById(R.id.btn_submit);
    }

    private void initView(View rootView, Bundle savedInstanceState) {
        mViewList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mViewList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initListener(View view, Bundle savedInstanceState) {
        mBtnClose.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mAdapter.setItemClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                onClickClose();
                break;
            case R.id.btn_submit:
                onClickBtnSubmit();
                break;
        }
    }

    private void onClickClose() {
        dismiss();
    }

    private void onClickBtnSubmit() {
        if (mListener != null) {
            Item item = mAdapter.getItem(mAdapter.getSelectedIndex());
            mListener.onBtnSubmitClick(this, item, mEtOthers.getText());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss(this);
        }
    }

    public OnContentListener getListener() {
        return mListener;
    }

    public void setContentListener(OnContentListener listener) {
        mListener = listener;
    }


    /**
     * 通过 id 获取 item
     *
     * @param id id
     */
    @Nullable
    private Item findItemById(int id) {
        for (Item item : mItems) {
            if (id == item.getId()) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        KLog.i(TAG, "onItemClick position = " + position);
        mAdapter.setItemSelected(position);
    }


    /**
     * 自定的的接口回调 (管理粒度比较粗,可根据需求细化)
     */
    public static interface OnContentListener {

        /**
         * dialog 消失回调
         *
         * @param dialog dialog
         */
        public void onDismiss(DialogFragment dialog);

        /**
         * 提交按钮点击回调
         *
         * @param dialog dialog
         * @param item   item
         * @param others 补充内容
         */
        public void onBtnSubmitClick(DialogFragment dialog, Item item, @Nullable CharSequence others);
    }

    public static class Item implements Serializable {
        private int id;
        private String tile;

        public Item(@IdRes int id, String tile) {
            this.id = id;
            this.tile = tile;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTile() {
            return tile;
        }

        public void setTile(String tile) {
            this.tile = tile;
        }
    }

    public static class Builder {

        private ArrayList<Item> mItems = new ArrayList<>();

        public Builder addItem(Item item) {
            mItems.add(item);
            return this;
        }

        public ReportDialog build() {
            Bundle argument = new Bundle();
            argument.putSerializable(KEY_ITEMS, mItems);

            ReportDialog dialog = new ReportDialog();
            dialog.setArguments(argument);
            return dialog;
        }
    }
}
