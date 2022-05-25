package com.xianghe.ivy.http.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/11/5 10:23
 * @Desc: 针对后台返回list为String类型异常
 *   Throwable:java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at path $.data
 */
public class GsonListTypeAdapter implements JsonDeserializer<List<?>> {

    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            try {
                JsonArray jsonArray = json.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                    List list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonElement element = jsonArray.get(i);
                        Object item = context.deserialize(element, itemType);
                        list.add(item);
                    }
                    return list;
                } else {
                    return Collections.emptyList();
                }
            } catch (JsonParseException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        } else {
            //和接口类型不符，返回空List
            return Collections.emptyList();
        }
    }
}
