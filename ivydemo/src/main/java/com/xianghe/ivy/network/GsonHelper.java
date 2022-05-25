package com.xianghe.ivy.network;

import com.birdsport.cphome.ui.widget.gson.DoubleTypeAdapter;
import com.birdsport.cphome.ui.widget.gson.FloatTypeAdapter;
import com.birdsport.cphome.ui.widget.gson.IntegerTypeAdapter;
import com.birdsport.cphome.ui.widget.gson.LongTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.xianghe.ivy.http.gson.GsonListTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public final class GsonHelper {

    private static Gson sGson;
    private static JsonParser sJsonParser = new JsonParser();


    /**
     * 将json数据转化为实体数据
     * @param jsonData json字符串
     * @param entityClass 类型
     * @return 实体
     */
    public static <T> T convertEntity(String jsonData, Class<T> entityClass) {
        T entity = null;
        try {
            entity = getsGson().fromJson(jsonData, entityClass);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 将json数据转化为实体列表数据
     * @param jsonData json字符串
     * @param entityClass 类型
     * @return 实体列表
     */
    public static <T> List<T> convertEntities(String jsonData, Class<T> entityClass) {
        List<T> entities = null;
        try {
            entities = new ArrayList<>();
            JsonArray jsonArray = sJsonParser.parse(jsonData).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                entities.add(getsGson().fromJson(element, entityClass));
            }
            return entities;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将 Object 对象转为 String
     * @param jsonObject json对象
     * @return json字符串
     */
    public static String object2JsonStr(Object jsonObject) {
        return sGson.toJson(jsonObject);
    }

    public static Gson getsGson() {
        if (sGson == null) {
            synchronized (GsonHelper.class) {
                sGson =new GsonBuilder()
                        .registerTypeHierarchyAdapter(List.class,new GsonListTypeAdapter())
                        .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
                        .registerTypeAdapter(int.class, new IntegerTypeAdapter())
                        .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                        .registerTypeAdapter(double.class, new DoubleTypeAdapter())
                        .registerTypeAdapter(Long.class, new LongTypeAdapter())
                        .registerTypeAdapter(long.class, new LongTypeAdapter())
                        .registerTypeAdapter(Float.class, new FloatTypeAdapter())
                        .registerTypeAdapter(float.class, new FloatTypeAdapter())
//                        .setLenient() //设置宽松的容错性
//                        .setPrettyPrinting() //设置漂亮的打印（打印出来的有缩进风格）
                        .create();
            }
        }
        return sGson;
    }
}