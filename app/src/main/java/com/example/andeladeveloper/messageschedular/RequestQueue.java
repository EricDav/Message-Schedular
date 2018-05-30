package com.example.andeladeveloper.messageschedular;

import com.loopj.android.http.*;

/**
 * Created by David on 11/05/2018.
 */

public class RequestQueue {
    private static final String BASE_URL = "https://worklist1.herokuapp.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
