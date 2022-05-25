package com.xianghe.ivy.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianghe.ivy.R;

/**
 * 支付方式：支付宝、微信
 * 初始化需要根据数据设置是否已绑定支付宝-setIsBindAlipay()、微信-setIsBindWechat()
 *
 */
public class PaywayView extends LinearLayout {

    private Context mContext;
    /**
     * 是否显示支付宝支付,默认true
     */
    private boolean mIsShowAlipay = true;

    /**
     * 是否显示微信支付
     */
    private boolean mIsShowWechat = true;
    /**
     * 是否绑定支付宝
     */
    private boolean mIsAlipayBind;
    /**
     * 是否绑定微信
     */
    private boolean mIsWechatBind;

    private View mViewAlipay;
    private View mViewWechat;
    private TextView mTvAlipayIsBind;
    private TextView mTvWechatIsBind;
    public OnPaywayClickListener mPaywayClickListener;


    public PaywayView(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public PaywayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public PaywayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_payway, null);
        mViewAlipay = view.findViewById(R.id.ll_view_payway_alipay);
        mViewWechat = view.findViewById(R.id.ll_view_payway_wechat);
        mTvAlipayIsBind = view.findViewById(R.id.tv_view_payway_alipay_isbind);
        mTvWechatIsBind = view.findViewById(R.id.tv_view_payway_wechat_isbind);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PaywayView, defStyle, 0);

        mIsShowAlipay = a.getBoolean(R.styleable.PaywayView_isShowAlipay,mIsShowAlipay);
        mIsShowWechat = a.getBoolean(R.styleable.PaywayView_isShowWechat,mIsShowWechat);
        mIsAlipayBind = a.getBoolean(R.styleable.PaywayView_isBindAlipay,mIsAlipayBind);
        mIsWechatBind = a.getBoolean(R.styleable.PaywayView_isBindWechat,mIsWechatBind);

        mViewAlipay.setOnClickListener(v -> {

            if (mPaywayClickListener == null){
                return;
            }

            if (mIsAlipayBind){
                mPaywayClickListener.withdrawAlipay();
            } else {
                mPaywayClickListener.bindAlipay();
            }

        });

        mViewWechat.setOnClickListener(v -> {

            if (mPaywayClickListener == null){
                return;
            }

            if (mIsWechatBind){
                mPaywayClickListener.withdrawWechat();
            } else {
                mPaywayClickListener.bindWechat();
            }

        });
        a.recycle();

        updateView();
        addView(view,0);
    }

    public void setPaywayClickListener(OnPaywayClickListener listener){
        this.mPaywayClickListener = listener;
    }

    public boolean getIsBindAlipay() {
        return mIsAlipayBind;
    }

    public void setIsBindAlipay(boolean isBindAlipay) {
        mIsAlipayBind = isBindAlipay;
        updateView();
    }

    public boolean getIsBindWechat(){
        return mIsWechatBind;
    }

    public void setIsBindWechat(boolean isBindWechat){
        mIsWechatBind = isBindWechat;
        updateView();
    }



    public boolean getIsShowAlipay() {
        return mIsShowAlipay;
    }

    public void setIsShowAlipay(boolean isShowAlipay) {
        mIsShowAlipay = isShowAlipay;
        updateView();
    }


    public boolean getIsShowWechat() {
        return mIsShowWechat;
    }

    public void setIsShowWechat(boolean isShowWechat) {
        mIsShowWechat = isShowWechat;
        updateView();
    }



    private void updateView() {
        if (mIsShowAlipay){
            if (mIsAlipayBind){
                mTvAlipayIsBind.setText("点击提现");
            }
        } else {
            mViewAlipay.setVisibility(GONE);
        }

        if (mIsShowWechat){
            if (mIsWechatBind){
                mTvWechatIsBind.setText("点击提现");
            }
        } else {
            mViewWechat.setVisibility(GONE);
        }
    }

    public interface OnPaywayClickListener{
        void bindAlipay();

        void bindWechat();

        void withdrawAlipay();

        void withdrawWechat();
    }
}
