package shree.firebaseandroid.utils;

/**
 * Created by Shrinivas on 11-01-2018.
 */
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static shree.firebaseandroid.fragments.SubActivityDetails.context;

public class URLConnection {
    private static final String BASE_URL = "https://wotrqa.azurewebsites.net/api/services/app";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String abc="";

    public static void get(Context context, String url, Header[] headers, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        String abc= getAbsoluteUrl(url);
        client.get(context,abc, headers, params, responseHandler);
        Log.v("data",""+ client.get(context,abc, headers, params, responseHandler));

    }

    private static String getAbsoluteUrl(String relativeUrl) {

        return BASE_URL + relativeUrl;
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
          abc= getAbsoluteUrl(url);
          client.put(abc,params,responseHandler);

    }
}