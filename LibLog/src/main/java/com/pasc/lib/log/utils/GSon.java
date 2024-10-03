package com.pasc.lib.log.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GSon {
    private static class InstanceHolder {
        public static Gson gson = new Gson();
    }

    public static Gson getGson() {
        return InstanceHolder.gson;
    }

    public static String toJson(Object object) {
        String json  = "";
        try{
           json = InstanceHolder.gson.toJson(object);
        }catch (Exception e) {
            json = object.toString();
        }
        return json;
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        json = CleanPath.cleanString(json);
        return InstanceHolder.gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        json = CleanPath.cleanString(json);
        return InstanceHolder.gson.fromJson(json, classOfT);
    }

    public static Map<String, String> mapFromJson(String json) {
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        return fromJson(json, type);
    }
}
