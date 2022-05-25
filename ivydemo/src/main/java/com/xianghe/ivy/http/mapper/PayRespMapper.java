package com.xianghe.ivy.http.mapper;

import com.xianghe.ivy.model.response.BasePayResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PayRespMapper  implements HttpRespMapper{
    @Override
    public boolean isSuccess(@Nullable Object resp) {
        if(resp instanceof BasePayResponse){
            BasePayResponse bpr = (BasePayResponse) resp;
            return bpr.getCode() ==200;
        }
        return false;
    }

    @NotNull
    @Override
    public String getErrorMessage(@Nullable Object resp) {
        if (resp instanceof BasePayResponse){
            if (((BasePayResponse) resp).getMessage() == null ||"".equals(((BasePayResponse) resp).getMessage() == null)){
                return "request error";
            } else {
                return ((BasePayResponse) resp).getMessage();
            }
        }
        return "http request error";
    }

    @Override
    public int getErrorCode(@Nullable Object resp) {
        if (resp instanceof BasePayResponse){
            return ((BasePayResponse) resp).getCode();
        }
        return 0;
    }

    @Override
    public int getInfoCode(@Nullable Object resp) {
        return 0;
    }
}
