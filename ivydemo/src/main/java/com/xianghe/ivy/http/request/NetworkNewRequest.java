package com.xianghe.ivy.http.request;

import com.google.gson.JsonElement;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.network.IvyService;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;

public class NetworkNewRequest implements INetworkRequest {
    @NotNull
    @Override
    public Observable<String> postString(@NotNull String url, @NotNull Map<String, ?> params) {
        return null;
    }

    @NotNull
    @Override
    public Observable<String> getString(@NotNull String url, @NotNull Map<String, ?> params) {
        return null;
    }

    @NotNull
    @Override
    public Observable<JsonElement> postRequest(@NotNull String url, @NotNull Map<String, ? extends Object> params) {
        return null;
    }

    @NotNull
    @Override
    public Observable<JsonElement> getRequest(@NotNull String url, @NotNull Map<String, ?> params) {
        if (isFullUrl(url)){
            IvyApp.getInstance().getIvyService().getByFullUrl(url, (Map<String, Object>) params);
        }
        return null;
    }

    @Override
    public <T> T parseString(@NotNull String json, @NotNull Type type) {
        return GsonHelper.getsGson().fromJson(json,type);
    }

    @Override
    public <T> T parseJsonElement(@NotNull JsonElement json, @NotNull Type type) {
        return GsonHelper.getsGson().fromJson(json,type);
    }


    private boolean isFullUrl(String url) {
        return url.toLowerCase().contains("http") || url.toLowerCase().contains("https");
    }
}
