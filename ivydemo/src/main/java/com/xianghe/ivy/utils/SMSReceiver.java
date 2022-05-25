package com.xianghe.ivy.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.wwwjf.base.utils.ToastUtil;
import com.xianghe.ivy.R;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SMSMethod.SMS_SEND_ACTIOIN.equals(intent.getAction())){
            try{
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        ToastUtil.show(context, context.getResources().getString(R.string.sms_send_success), Toast.LENGTH_SHORT);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        ToastUtil.show(context, context.getResources().getString(R.string.sms_send_failed), Toast.LENGTH_SHORT);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if(SMSMethod.SMS_DELIVERED_ACTION.equals(intent.getAction())){
            /* android.content.BroadcastReceiver.getResultCode()方法 */
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    ToastUtil.show(context, context.getResources().getString(R.string.sms_delivered), Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    /* 短信未送达 */
                    ToastUtil.show(context, context.getResources().getString(R.string.sms_not_delivered), Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
            }
        }
    }
}
