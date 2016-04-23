package com.dreamworks.musicwanted.network;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类是用来封装Request数据的
 * Created by zhang on 2016/4/21.
 */
public class RequestEntry {

    public String tag;
    public String url;
    public KoalaHttpType type;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    public RequestEntry(String url, String tag, KoalaHttpType type) {
        this.url = url;
        this.tag = tag;
        this.type = type;
    }

    public Map<String, String> addParam(String key, String value) {
        params.put(key, value);
        return params;
    }

    public Map<String, String> addAllParams(Map<String, String> p) {
        params.putAll(p);
        return params;
    }

    public void removeParams() {
        params.clear();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> addHeader(String key, String value) {
        headers.put(key, value);
        return headers;
    }

    public Map<String, String> addAllHeaders(Map<String, String> h) {
        headers.putAll(h);
        return headers;
    }

    public void removeHeaders() {
        headers.clear();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
