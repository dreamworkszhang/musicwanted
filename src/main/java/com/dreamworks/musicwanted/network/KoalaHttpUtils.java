package com.dreamworks.musicwanted.network;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhang on 2016/4/23.
 */
public class KoalaHttpUtils {

    private static KoalaHttpUtils instance;
    private Map<String, ArrayList<String>> activityRequests = new ConcurrentHashMap<>();
    private Map<String, Call> urlMap = new ConcurrentHashMap<>();

    private KoalaHttpUtils() {

    }

    public static KoalaHttpUtils getInstance() {
        if (instance == null) {
            synchronized (KoalaHttpUtils.class) {
                if (instance == null) {
                    instance = new KoalaHttpUtils();
                }
            }
        }
        return instance;
    }

    public Response syncTask(RequestEntry req) throws IOException {
        Map<String, String> headers = req.getHeaders();
        Map<String, String> params = req.getParams();
        Request.Builder builder = new Request.Builder();
        if (headers.size() > 0) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                builder.addHeader(iterator.next().getKey(), iterator.next().getValue());
            }
        }
        if (params.size() > 0) {
            if (req.type == KoalaHttpType.GET) {
                req.url += appendGetUrl(params);
            } else if (req.type == KoalaHttpType.POST) {
                FormBody.Builder body = new FormBody.Builder();
                Set<Map.Entry<String, String>> entries = params.entrySet();
                Iterator<Map.Entry<String, String>> iterator = entries.iterator();

                while (iterator.hasNext()) {
                    body.addEncoded(iterator.next().getKey(), iterator.next().getValue());
                }
                builder.post(body.build());
            }
        }
        builder.url(req.url);
        builder.tag(req.tag);
        Request request = builder.build();
        Call call = KoalaHttpTask.getInstance().getOkHttpClient().newCall(request);

        ArrayList<String> list = activityRequests.get(req.tag);
        if (list == null) {
            list = new ArrayList<>();
            Collections.synchronizedList(list);
            activityRequests.put(req.tag, list);
        }

        if (!list.contains(req.url)) {
            list.add(req.url);
        }

        urlMap.put(req.url, call);

        return call.execute();
    }

    private String appendGetUrl(Map<String, String> params) {
        String str = "";
        if (params.size() > 0) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            boolean isFirst = true;
            while (iterator.hasNext()) {
                if (isFirst) {
                    //noinspection deprecation
                    str += "?" + iterator.next().getKey() + "=" + URLEncoder.encode(iterator.next().getValue());
                    isFirst = false;
                } else {
                    str += "&" + iterator.next().getKey() + "=" + URLEncoder.encode(iterator.next().getValue());
                }
            }
        }
        return str;
    }

    public void cancelRequest(String tag) {
        ArrayList<String> reqUrls = activityRequests.get(tag);
        for (String reqUrl : reqUrls) {
            if (urlMap.containsKey(reqUrl)) {
                Call call = urlMap.get(reqUrl);
                call.cancel();
                urlMap.remove(reqUrl);
            }
        }
        reqUrls.clear();
        activityRequests.remove(tag);
    }

    public void removeCall(String tag, String url) {
        ArrayList<String> reqUrls = activityRequests.get(tag);
        if (reqUrls.contains(url)) {
            reqUrls.remove(url);
        }
    }

}
