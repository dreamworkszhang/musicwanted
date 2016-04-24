package com.dreamworks.musicwanted.network;


import android.text.TextUtils;

import com.dreamworks.musicwanted.R;
import com.dreamworks.musicwanted.global.MainApplication;
import com.dreamworks.musicwanted.model.BaseModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by zhang on 2016/4/22.
 */
public class RequestHandler {

    public static <T> T generalRequestSync(KoalaTaskListener<T> listener, RequestEntry req) {
        T result = null;

        if (listener != null) {
            Class cls = parseClass(listener);
            try {
                result = (T) cls.newInstance();
                if (result != null) {

                    Response response = KoalaHttpUtils.getInstance().syncTask(req);
                    if (response != null) {
                        if (response.isSuccessful()) {
                            result = parseJSON(response.body().string(), cls);
                            if (result != null) {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.opt("code") != null) {
                                    if (((BaseModel) result).code == 0 || ((BaseModel) result).code == 200) {
                                        ((BaseModel) result).code = KoalaHttpStatus.OK;
                                    }
                                }
                                return result;
                            } else {
                                result = (T) cls.newInstance();
                                ((BaseModel) result).code = KoalaHttpStatus.JSON_PARSE_ERROR;
                                ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.json_parse_error);
                                return result;
                            }
                        } else {
                            ((BaseModel) result).code = KoalaHttpStatus.IO_ERROR;
                            ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.io_error);
                            return result;
                        }
                    } else {
                        ((BaseModel) result).code = KoalaHttpStatus.NET_ERROR;
                        ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.net_error);
                        return result;
                    }
                } else {
                    ((BaseModel) result).code = KoalaHttpStatus.JSON_PARSE_ERROR;
                    ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.json_parse_error);
                    return result;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                ((BaseModel) result).code = KoalaHttpStatus.IO_ERROR;
                ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.io_error);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                ((BaseModel) result).code = KoalaHttpStatus.JSON_PARSE_ERROR;
                ((BaseModel) result).msg = MainApplication.getApp().getString(R.string.json_parse_error);
                return result;
            }

        } else {
            return null;
        }

        return null;
    }

    private static Class parseClass(KoalaTaskListener listener) {
        Type type = listener.getClass().getGenericSuperclass();

        if (type != null && type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return (Class) types[0];
        }

        return null;
    }

    private static <T> T parseJSON(String json, Class cls) {
        Gson gson = new Gson();
        T result;
        try {
            if (!TextUtils.isEmpty(json)) {
                result = (T) gson.fromJson(json, cls);
            } else {
                result = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;

    }

}
