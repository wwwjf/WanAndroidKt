package com.xianghe.ivy.http.mapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WXRespMapper implements HttpRespMapper{
    @Override
    public boolean isSuccess(@Nullable Object resp) {
        return true;
    }

    @NotNull
    @Override
    public String getErrorMessage(@Nullable Object resp) {
        return " ";
    }

    @Override
    public int getErrorCode(@Nullable Object resp) {
        return 0;
    }

    @Override
    public int getInfoCode(@Nullable Object resp) {
        return 0;
    }
}
