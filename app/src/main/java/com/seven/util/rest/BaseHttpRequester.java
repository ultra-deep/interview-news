package com.seven.util.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64DataException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.seven.model.Cache;
import com.seven.util.data.HttpCacher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * the base of make request to the server or cloud
 * 11/7/17.
 */
public class BaseHttpRequester implements Cloneable {
    public static final String HOST = "https://saviway.com";
    public static final String BASE_URL = HOST + "/api";
    public static final int _401_AUTHENTICATE_ERROR = 401;
    public static final int _400_BAD_REQUEST = 400;
    public static final int _403_FORBIDDEN = 403;
    public static final int _404_NOT_FOUND = 404;
    public static final int _408_REQUEST_TIMEOUT = 408;
    public static final int _503_SERVER_UNAVAILABLE = 503;
    public static final int _500_SERVER_TIMEOUT = 500;
    public static final int _406_NOT_ACCEPTABLE = 406;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    /**
     * time to waite receive http response
     */
    private static final int TIMEOUT_IN_MILLISECOND = 60000;
    /**
     * a json model converter
     */
    private static Gson mGson;
    /**
     * setting manager for get or save the login and application data
     */
//    protected final SettingManager settingManager;
    //------------------------------------------------------------
    /**
     * using cahce the http request and response
     */
    private HttpCatchable mHttpCatchable;
    /**
     * the thread of for make http request
     */
    private MyRequestThread mMyRequestThread;
    /**
     * a handler of send message data between processes
     */
    private final MyRequestThread.ThreadHandler mThreadHandler;
    private String mHttpMethod = GET;
    private String mUrl;
    private Map<String, String> mHeaders;
    private HttpResponseListener mResponseListener;
    protected HttpErrorListener mErrorListener;
    private String mBody;
    private Throwable throwable;

    // Builder Methods

    public BaseHttpRequester(String token) {
//        this.settingManager = settingManager;
        mThreadHandler = new MyRequestThread.ThreadHandler(Looper.getMainLooper());
    }
    /**
     * @param url full path of http url to request
     */
    public BaseHttpRequester url(String url) {
        try {
            // Normalize
            URL aUrl = new URL(url);
            URI uri = new URI(aUrl.getProtocol(), aUrl.getUserInfo(), aUrl.getHost(), aUrl.getPort(), aUrl.getPath(), aUrl.getQuery(), aUrl.getRef());
            url = uri.toURL().toString();
            mUrl = url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }
    /**
     * set method of request as GET
     */
    public BaseHttpRequester get() {
        mHttpMethod = GET;
        return this;
    }
    /**
     * set method of request as PUT
     */
    public BaseHttpRequester put() {
        mHttpMethod = PUT;
        return this;
    }
    /**
     * set method of request as POST
     */
    public BaseHttpRequester post() {
        mHttpMethod = POST;
        return this;
    }
    /**
     * set method of request as DELETE
     */
    public BaseHttpRequester delete() {
        mHttpMethod = DELETE;
        return this;
    }
    /**
     * @param errorListener occured when the http request failed
     */
    public BaseHttpRequester errorListener(HttpErrorListener errorListener) {
        mErrorListener = errorListener;
        return this;
    }
    /**
     * @param responseListener ocurred when receive any response data
     */
    public BaseHttpRequester listener(HttpResponseListener responseListener) {
        mResponseListener = responseListener;
        return this;
    }
    /**
     * @param body the body param of the request
     */
    public BaseHttpRequester body(String body) {
        mBody = body;
        return this;
    }

    // getter and Setter
    /**
     * @return the full path of http url
     */
    public String getUrl() {
        return mUrl;
    }
    /**
     * @return the request body
     */
    public String getBody() {
        if (mBody == null) {
            mBody = "";
        }
        return mBody;
    }
    /**
     * @return true if waiting for response
     */
    public boolean isRequesting() {
        return mMyRequestThread != null;
    }
    /**
     * set http method
     * @param httpMethod use GET, POST, PUT, DELETE
     */
    public void setHttpMethod(String httpMethod) {
        mHttpMethod = httpMethod;
    }
    /**
     * @return current http request method (verb)
     */
    public String getHttpMethod() {
        return mHttpMethod;
    }
    /**
     * cancel the http request
     */
    public void cancel() {
        if (mMyRequestThread != null) {
            mMyRequestThread.cancel(true);
        }
    }
    /**
     * @return current http cache manager
     */
    private HttpCatchable getHttpCatchable() {
        return mHttpCatchable;
    }
    /**
     * set http cache manager
     * @param httpCatchable
     */
    public void setHttpCatchable(HttpCatchable httpCatchable) {
        mHttpCatchable = httpCatchable;
    }
    private HttpCacher mCacher;
    public void setHttpCatchable(HttpCacher cacher) {
        mCacher = cacher;
    }
    /**
     * @return the headers of http request
     */
    public Map<String, String> getHeaders() {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
            mHeaders.put("Content-Type", "application/json;");
            mHeaders.put("Accept", "application/json;");
        }
//        mHeaders.put("Authorization", "Bearer " );
        return mHeaders;
    }
    /**
     * @param url full url path of http request
     */
    public void setUrl(String url) {
        mUrl = url;
    }
    /**
     * @return a json to pojo converter
     */
    public static Gson json() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }
    public HttpResponseListener getmResponseListener() {
        return mResponseListener;
    }

    // METHODS

    public void request() {
        throwable = null;
        if (mMyRequestThread != null) {
            mMyRequestThread.cancel(true);
        }
        mMyRequestThread = new MyRequestThread(this);

        if (mHttpCatchable != null) {
            String response = mHttpCatchable.getBody(getUrl());
            if (!response.isEmpty()) {
                onHttpResponse(response, 200, true);
            }
        }

        if (mCacher != null) {
            String response = mCacher.get(getUrl()).getResponse();
            if (!response.isEmpty()) {
                onHttpResponse(response, 200, true);
            }
        }
        mMyRequestThread.start();
    }
    protected void onHttpFailRequest(Throwable e) {
        throwable = e;
        mErrorListener.onHttpError(e, this);
    }
    protected void onHttpResponse(String data, int httpCode, boolean cached) {
        try {
            try {
                if (data.contains("\"message\"") && data.contains("\"status\"")) {
                    BaseResponseDto dto = json().fromJson(data , BaseResponseDto.class);
                    if (dto.isSuccess() == false) {
                        onHttpFailRequest(new Base64DataException(dto.getMessage()));
                        return;
                    }
                }
            } catch (Exception ignored) { }
            mResponseListener.onHttpResponse(data, httpCode, cached);
        } catch (Exception e) {
            Log.e("localError", " url **************" + mUrl);
            e.printStackTrace();
            mErrorListener.onHttpError(e, BaseHttpRequester.this);
        }
    }
    protected void onAnyResponse() {
        mMyRequestThread = null;
    }
    @Override public BaseHttpRequester clone() {
        BaseHttpRequester rt = new BaseHttpRequester("");
        rt.setUrl(mUrl);
        rt.setHttpCatchable(mHttpCatchable);
        rt.setHttpMethod(mHttpMethod);
        rt.getHeaders().clear();
        rt.getHeaders().putAll(getHeaders());
        return rt;
    }
    protected int getQueryStringLength() {
        int paramsIndex = getUrl().indexOf("?");
        if (paramsIndex >= 0) {
            return getUrl().substring(paramsIndex).length();
        }
        return 0;
    }
    public Throwable getThrowable() {
        return throwable;
    }

    // listeners

    public interface HttpErrorListener {
        public final HttpErrorListener EMPTY = new HttpErrorListener() {
            @Override public void onHttpError(Throwable e, BaseHttpRequester requester) {
            }
        };
        void onHttpError(Throwable e, BaseHttpRequester requester);
    }
    public interface HttpResponseListener {
        void onHttpResponse(String response, int responseCode, boolean cached) throws Exception;

    }

    // Classes
    /**
     * the request thread
     */
    private static class MyRequestThread extends Thread {
        private static final String TAG = "HHH";
        private final BaseHttpRequester req;
        private int httpResponseCode = 999;
        private StringBuilder response = new StringBuilder();
        private Exception mException;
        private boolean mIsCanceled;
        public MyRequestThread(BaseHttpRequester baseHttpRequester) {
            this.req = baseHttpRequester;
        }
        @Override public synchronized void run() {
            mException = null;
            HttpURLConnection connection = null;
            BufferedReader in = null;
            int retryCounter = 0;
            try {

                retryCounter++;
                URL url = new URL(req.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(req.getHttpMethod());
                connection.setConnectTimeout(TIMEOUT_IN_MILLISECOND);
                connection.setReadTimeout(TIMEOUT_IN_MILLISECOND);
                connection.setAllowUserInteraction(true);
                connection.setDefaultUseCaches(false);
                connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
                connection.setRequestProperty("Connection", "close");
                if (req.getBody().isEmpty()) {
//                    connection.setRequestProperty("Content-Length", "" + req.getQueryStringLength());
                } else {
                    connection.setRequestProperty("Content-Length", "" + req.getBody().getBytes("UTF-8").length);
                }
                connection.setUseCaches(false);
                //			connection.setDoInput(true);
                //			connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(true);
                //			connection.setChunkedStreamingMode(1024 * 1024 * 1024);
                for (String key : req.getHeaders().keySet()) {
                    connection.setRequestProperty(key, req.getHeaders().get(key));
                }
                //        con.setRequestProperty("Accept-Language", "UTF-8");
                //        con.setRequestProperty("Authorization", "Bearer " + );
                if (!req.getBody().isEmpty()) {
                    byte[] outputInBytes = req.getBody().getBytes("UTF-8");
                    connection.setFixedLengthStreamingMode(outputInBytes.length);
                    OutputStream os = connection.getOutputStream();
                    os.write(outputInBytes);
                    os.flush();
                    os.close();
                }
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$ HTTP REQUEST $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    Log.d(TAG, "============ url : " + req.getUrl() );
//                    Log.d(TAG, "============ headers : " );
//                    for (String key : req.getHeaders().keySet()) {
//                        Log.d(TAG, key + " : " + req.getHeaders().get(key) );
//                    }
//                }

                httpResponseCode = connection.getResponseCode();

                if (httpResponseCode >= 400) {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            } finally {
                if (connection != null) connection.disconnect();
                if (in != null) try {
                    in.close();
                } catch (Exception ignored) {
                }
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "====================== response ================ ");
//                    Log.d(TAG, response.toString());
//                }
            }

            cacheResponseIffNeed(response.toString(), httpResponseCode);

            Message msg = Message.obtain();
            msg.obj = this;
            req.mThreadHandler.sendMessage(msg);

        }
        private void cacheResponseIffNeed(String response, int httpResponseCode) {
            if (httpResponseCode < 400) {
                try {
                    if (req.getHttpCatchable() != null) {
                        boolean isEmptyCache = req.getHttpCatchable().getBody(req.getUrl()).isEmpty();
                        req.getHttpCatchable().put(req.getUrl(), response, httpResponseCode);
                        if (!isEmptyCache) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    if (req.mCacher != null) {
                        boolean isEmptyCache = req.mCacher.get(req.getUrl()).getResponse() != null;
                        req.mCacher.cache(new Cache(req.getUrl() , response));
                        if (!isEmptyCache) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        public void cancel(boolean force) {

            mIsCanceled = true;
            interrupt();
        }

        public static class ThreadHandler extends Handler {
            public ThreadHandler(@NonNull Looper looper) {
                super(looper);
            }
            @Override public void handleMessage(Message msg) {
                if (msg != null) {
                    super.handleMessage(msg);
                    MyRequestThread thread = (MyRequestThread) msg.obj;
                    thread.req.onAnyResponse();
                    if (thread.mException != null) {
                        thread.req.onHttpFailRequest(thread.mException);
                    } else if (thread.httpResponseCode >= 400) {
                        BaseHttpException s = new BaseHttpException(new Exception(thread.response.toString()), thread.httpResponseCode);
                        thread.req.onHttpFailRequest(s);
                    } else {
                        thread.req.onHttpResponse(thread.response.toString(), thread.httpResponseCode, false);
                    }
                }
            }
        }
    }
    /**
     * the base cacheable of http response
     */
    public static class HttpCatchable {
        private final SharedPreferences mSharedPreferences;

        public HttpCatchable(Context context) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        /**
         * cache a http response
         * @param body the http response
         * @param httpResponseCode the http response code
         */
        public void put(@NonNull String url, @NonNull String body, int httpResponseCode) {
            if (httpResponseCode < 400) {
                mSharedPreferences.edit().putString(url, body).apply();
                mSharedPreferences.edit().putInt(url + "_RESPONSE_CODE", httpResponseCode).apply();
            }
        }

        @NonNull public String getBody(String url) {
            return mSharedPreferences.getString(url, "").trim();
        }
        /**
         * get cached response code
         */
        public int getResponseCode(String url) {
            return mSharedPreferences.getInt(url + "_RESPONSE_CODE", 9999);
        }
    }
    /**
     * the base of error model in requests
     * NOT USABLE IN THIS PROJECT
     */
    public static class HttpErrorModel extends BaseResponseDto {
    }
    /**
     * the base of all http responses
     */
    public static class BaseResponseDto {
        @Expose @SerializedName("status") private String status;
        @Expose @SerializedName("message") private String message;
        @Expose @SerializedName("errors") private Object errors;
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public boolean isSuccess() {
            return "success".equalsIgnoreCase(status);
        }
        public Object getErrors() {
            return errors;
        }
        public void setErrors(Object errors) {
            this.errors = errors;
        }
    }
    /**
     * the base of http exception
     */
    public static class BaseHttpException extends Exception {
        private final int mStatusCode;
        private HttpErrorModel mHttpErrorModel;

        public BaseHttpException(Throwable throwable, int statusCode) {
            super(throwable);
            mStatusCode = statusCode;
            try {
                mHttpErrorModel = json().fromJson(throwable.getMessage(), HttpErrorModel.class);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        public BaseHttpException(String message) {
            this(new Exception(message), -499);
        }

        public BaseHttpException(String message, int statusCode) {
            this(new Exception(message), statusCode);
        }

        public int getStatusCode() {
            return mStatusCode;
        }

        public HttpErrorModel getHttpErrorModel() {
            return mHttpErrorModel;
        }

        @Override public String getMessage() {
            if (getHttpErrorModel() != null) {
                return getHttpErrorModel().getMessage();
            }
            if (super.getCause() != null && super.getCause().getMessage() != null && !super.getCause().getMessage().isEmpty()) {
                return super.getCause().getMessage();
            }
            return super.getMessage();
        }
    }
    /**
     * http cancel exception
     * NOT USABLE IN THIS PROJECT
     */
    public static class HttpCancelException extends BaseHttpException {
        public HttpCancelException(Throwable throwable, int statusCode) {
            super(throwable, statusCode);
        }

        public HttpCancelException(String message) {
            super(message);
        }

        public HttpCancelException(String message, int statusCode) {
            super(message, statusCode);
        }
    }
    /**
     * the client exception
     */
    public static class HttpLocalErrorException extends BaseHttpException {
        public HttpLocalErrorException(Throwable throwable, int statusCode) {
            super(throwable, statusCode);
        }

        public HttpLocalErrorException(String message) {
            super(message);
        }

        public HttpLocalErrorException(String message, int statusCode) {
            super(message, statusCode);
        }
    }
    /**
     * the server exception
     * NOT USABLE IN THIS PROJECT
     */
    public static class HttpServerException extends BaseHttpException {
        public HttpServerException(Throwable throwable, int statusCode) {
            super(throwable, statusCode);
        }

        public HttpServerException(String message) {
            super(message);
        }

        public HttpServerException(String message, int statusCode) {
            super(message, statusCode);
        }
    }
    /**
     * the http fail exception
     * NOT USABLE IN THIS PROJECT
     */
    public static class HttpFailedException extends BaseHttpException {
        public HttpFailedException(Throwable throwable, int statusCode) {
            super(throwable, statusCode);
        }

        public HttpFailedException(String message) {
            super(message);
        }

        public HttpFailedException(String message, int statusCode) {
            super(message, statusCode);
        }
    }

}
