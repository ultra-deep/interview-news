package com.saviway.saviway.others.api.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.saviway.saviway.others.data.preference.SettingManager;
import com.saviway.saviway.others.util.ConvertUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * the point of upload data to the cloud or server
 * 9/22/16.
 */
public class UploadRequest  {
    private static final int maxBufferSize = 2 * 1024 * 8;
    private final SettingManager settingManager;
    private String mUUrl;
    private static final int BUFFER_SIZE = 1 * 1024 * 1024;
    private static final String lineEnd = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****";
    private List<DataPart> inputStreamToUpload;
    private int httpResponseCode;
    private String mServerResponseMessage;
    private StringBuilder response = new StringBuilder();
    protected UploadListener mListener;
    private Map<String, String> mHeaders;
    private Map<String, String> extraFields;
    private final MyRequestThread.ThreadHandler mThreadHandler;
    private MyRequestThread mMyRequestThread;
    
    // constructors
    
    public UploadRequest(SettingManager settingManager) {
        this.settingManager = settingManager;
        mThreadHandler = new MyRequestThread.ThreadHandler(Looper.getMainLooper());
        inputStreamToUpload = new ArrayList<>();
    }
    /**
     * @param url full path of destination
     */
    public UploadRequest url(String url) {
        mUUrl = url;
        return this;
    }
    /**
     * @param extraFields add extra fields as key-value (string)
     */
    public UploadRequest extraField(Map<String, String> extraFields) {
        this.extraFields = extraFields;
        return this;
    }
    /**
     * @param extraFields add extra fields as key-value (Object)
     */
    public UploadRequest extraField_fromObject(Map<String, Object> extraFields) {
        Map<String, String> mapString = new HashMap<>();
        for (String key : extraFields.keySet()) {
            Object value = extraFields.get(key);
            if (value != null) {
                mapString.put(key, value.toString());
            }
        }
        this.extraFields = mapString;
        return this;
    }
    /**
     * @param headers add http headers
     */
    public UploadRequest headers(Map<String, String> headers) {
        mHeaders = headers;
        return this;
    }
//    public UploadRequest addDataPart(Bitmap bitmapToUpload , String field) {
//        return addDataPart(bitmapToUpload , field , Bitmap.CompressFormat.PNG);
//    }
    /**
     * @param bitmapToUpload add Bitmap part to upload
     * @param field the field name of attachment
     * @param format the Bitmap format
     */
    public UploadRequest addDataPart(Bitmap bitmapToUpload , String field , Bitmap.CompressFormat format) {
        byte[] bytes = ConvertUtils.bitmap_to_byteArray(bitmapToUpload , format);
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        String filename = new Date().getTime() + ".jpg";
        return addDataPart(new DataPart(inputStream , field , bytes.length,filename ));
    }
    /**
     * @param fileToUpload add file part to upload
     * @param field the field name
     * @throws FileNotFoundException handle any exception of the attached file
     */
    public UploadRequest addDataPart(File fileToUpload, String field) throws FileNotFoundException {
        return addDataPart(new DataPart(new FileInputStream(fileToUpload) , field , fileToUpload.length() , fileToUpload.getName()));
    }
    /**
     * @param inputStream the stream directly
     * @param length total length of the stream
     * @param field field name
     * @param filename the stream file name
     */
    public UploadRequest addDataPart(InputStream inputStream, long length, String field, String filename) throws FileNotFoundException {
        return addDataPart(new DataPart(inputStream , field , length , filename));
    }
    /**
     * @param dataPart add data part to upload
     */
    public UploadRequest addDataPart(DataPart dataPart) {
        inputStreamToUpload.add(dataPart);
        return this;
    }
    /**
     * @param context the application context
     * @param uri the content as uri
     * @param field the filed name of attahment
     */
    public UploadRequest addDataPart(Context context , Uri uri, String field) throws FileNotFoundException {
        if (uri == null || context == null) {
            return this;
        }
        long contentLength = context.getContentResolver().openAssetFileDescriptor(uri, "r").getLength();
        String filename = ConvertUtils.getFilenameOf(context, uri);

        if (filename == null || filename.isEmpty()) {
            String[] split = uri.getPath().split("/");
            if (split.length > 0) {
                filename = split[split.length-1];
            }
            if (filename == null || filename.isEmpty()) {
                filename = new Date().getTime() + "";
            }
        }
        return addDataPart(new DataPart(context.getContentResolver().openInputStream(uri) , field, contentLength , filename));
    }
    /**
     * @param listener the listener for whats happening on uploading
     */
    public UploadRequest listener(UploadListener listener) {
        this.mListener = listener;
        return this;
    }
    
    // Overrides : AsyncTask
    /**
     * will call on any receive data from server
     */
    protected void onAnyResponse() {
        mMyRequestThread = null;
    }
    /**
     * @param response
     * @param httpResponseCode
     */
    protected void onHttpResponse(String response, int httpResponseCode) {
        if (getListener() != null) {
            getListener().onUploadFinish(this, httpResponseCode , response);
        }
    }
    /**
     * ourred when the request failed
     */
    protected void onHttpFailRequest(Exception exception) {
        if (getListener() != null) {
            if (exception instanceof UploadCancelException) {
                getListener().onUploadCanceled(this);
            } else {
                getListener().onUploadError(this, exception);
            }

        }
    }
    
    // METHODS
    /**
     * @return the http url
     */
    public String getUrl() {
        return mUUrl;
    }
    /**
     * @return the http headers of request
     */
    public Map<String, String> getHeaders() {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
            mHeaders.put("Authorization", "Bearer " + settingManager.getToken());
            mHeaders.put("Accept", "application/json;");
        }
        return mHeaders;
    }
    /**
     * @return extra fields for upload
     */
    public Map<String, String> getExtraFields() {
        return extraFields;
    }
    /**
     * @return part os of upload request
     */
    public List<DataPart> getParts() {
        return inputStreamToUpload;
    }
    /**
     * @return access to delegated listener
     */
    public UploadListener getListener() {
        return mListener;
    }
    /**
     * initialize and make the request
     */
    public void request() {
        if (mMyRequestThread != null) {
            mMyRequestThread.cancel(true);
        }
        mMyRequestThread = new MyRequestThread(this);
        mMyRequestThread.start();
    }
    
    // Classes
    /**
     * Make Async the request to upload
     */
    private static class MyRequestThread extends Thread {
        private static final String TAG = "HHH";

        private final UploadRequest req;
        private int httpResponseCode = 999;
        private StringBuilder response = new StringBuilder();
        private Exception mException;
        private boolean mIsCanceled;
        private float progress;
        public MyRequestThread(UploadRequest baseHttpRequester) {
            this.req = baseHttpRequester;
        }
        @Override public synchronized void run() {

            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            BufferedReader in = null;
            try {
                progress = 0;
                URL url = new URL(req.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true); // Allow Inputs
                connection.setDoOutput(true); // Allow Outputs
                connection.setUseCaches(false); // Don't use a Cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setAllowUserInteraction(false);
//                connection.setRequestProperty("Content-length", "" + req.getUploadContentLength());
//                connection.setFixedLengthStreamingMode(802504);
                m_init_headers(connection, req.getHeaders());
                connection.connect();
                outputStream = new DataOutputStream(connection.getOutputStream());
                //-------------------------------------------
                if(req.getExtraFields() != null && req.getExtraFields().isEmpty() == false){
                    for (String key : req.getExtraFields().keySet()) {
                        addFormField(key , req.getExtraFields().get(key) , outputStream);
                    }
                }
                //-------------------------------------------
                for (DataPart part : req.getParts()) {
                    addFormAttachPart(part, outputStream);
                }
                // send multipart form data necessary after file data...
                outputStream.write(lineEnd.getBytes());
                outputStream.flush();
                outputStream.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
                outputStream.flush();
                // Responses from the server (code and message)
                httpResponseCode = connection.getResponseCode();
                //                mServerResponseMessage = connection.getResponseMessage();
                if (httpResponseCode >= 400) {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                //                Log.i("uploadFile", "HTTP Response is : " + mServerResponseMessage + ": " + httpResponseCode);
                //close the streams //
//                req.getParts().close();
                outputStream.flush();
                outputStream.close();
                if (httpResponseCode >= 400) {
                    throw new BaseHttpRequester.BaseHttpException(response.toString(), httpResponseCode);
                }
            }  catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }

            //---------------------

            Message msg = Message.obtain();
            msg.arg1 = ThreadHandler.FINISH;
            msg.obj = this;
            req.mThreadHandler.sendMessage(msg);
        }
        private void publishProgress(float progress) {
            this.progress = progress;
            Message msg = Message.obtain();
            msg.arg1 = ThreadHandler.PROGRESS;
            msg.obj = this;
            req.mThreadHandler.sendMessage(msg);
        }
        private boolean isCancelled() {
            return mIsCanceled;
        }
        public void cancel(boolean force) {

            mIsCanceled = true;
            interrupt();
        }
        public float getProgress() {
            return progress;
        }
        private void m_init_headers(HttpURLConnection connection, Map<String, String> headers) {
            if (headers != null && headers.isEmpty() == false) {
                Set<String> keys = headers.keySet();
                for (String key : keys)
                {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
        }
        private void addFormField(String name, String value , DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.write((twoHyphens + boundary + lineEnd).getBytes());
            dataOutputStream.write(("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd).getBytes());
            dataOutputStream.write(("Content-Type: text/plain; charset=utf-8" + lineEnd).getBytes());
            dataOutputStream.write(lineEnd.getBytes());
            //-------
            dataOutputStream.writeBytes(value);
            dataOutputStream.writeBytes(lineEnd);
            //-------
            dataOutputStream.flush();
        }
        private void addFormFilePart(String field, String fileName, DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + field + "\"; filename=\"" + fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + lineEnd);
            dataOutputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            //-------
            dataOutputStream.flush();
        }
        private void addFormAttachPart( DataPart dataPart, DataOutputStream outputStream) throws IOException, UploadCancelException {

            InputStream inputStream = dataPart.inputStream;


            outputStream.write((twoHyphens + boundary + lineEnd).getBytes());
            outputStream.flush();
            outputStream.write(("Content-Disposition: form-data; name=\"" + dataPart.field + "\";filename=\"" + dataPart.filename + "\"" + lineEnd).getBytes());
            outputStream.flush();
            outputStream.write(lineEnd.getBytes());
            outputStream.flush();


            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            // read file and write it into form...
            long progress = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) > 0)
            //                while ((bytesRead = inputStreamToUpload.read(buffer)) > 0)
            {
                if (isCancelled()) {
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    throw new UploadCancelException();
                }
                progress += bufferSize;
                outputStream.write(buffer, 0, bufferSize);
                outputStream.flush();
                publishProgress((float) (progress / (double) dataPart.contentLength));
                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
            }
            // send multipart form data necessary after file data...
            outputStream.write(lineEnd.getBytes());
            outputStream.flush();
            dataPart.inputStream.close();
            //            outputStream.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            //            outputStream.flush();
        }
        
        public static class ThreadHandler extends Handler {
            public static final int PROGRESS = 100;
            public static final int FINISH = 1;
            public ThreadHandler(@NonNull Looper looper) {
                super(looper);
            }
            @Override public void handleMessage(Message msg) {
                if (msg != null) {
                    super.handleMessage(msg);
                    MyRequestThread thread = (MyRequestThread) msg.obj;
                    if (msg.arg1 == FINISH) {
                        thread.req.onAnyResponse();
                        if (thread.mException != null) {
                            thread.req.onHttpFailRequest(thread.mException);
                        } else {
                            thread.req.onHttpResponse(thread.response.toString(), thread.httpResponseCode);
                        }
                    } else if (msg.arg1 == PROGRESS && thread.req.getListener() != null) {
                        if (thread.req.getListener() != null) {
                            thread.req.getListener().onUploadProgress(thread.req , thread.getProgress());
                        }
                    }
                }
            }
        }
    }
    /**
     * throw when upload canceled
     */
    private static class UploadCancelException extends Exception {}
    /**
     * base behaviour of request listener
     */
    public interface UploadListener {
        void onUploadFinish(UploadRequest request, int serverResponseCode, String serverResponseMessage);
        void onUploadProgress(UploadRequest request, float progress);
        void onUploadError(UploadRequest request, Exception e);
        void onUploadCanceled(UploadRequest request);
    }
    /**
     * simple implementation of request listener
     */
    public static class UploadListener_ implements UploadListener {
        @Override public void onUploadFinish(UploadRequest request, int serverResponseCode, String serverResponseMessage) {
        }
        @Override public void onUploadProgress(UploadRequest request, float progress) {
        }
        @Override public void onUploadError(UploadRequest request, Exception e) {
        }
        @Override public void onUploadCanceled(UploadRequest request) {
        }
    }
    /**
     * keep data part for upload
     */
    public static class DataPart{
        public DataPart(InputStream inputStream,String field , long contentLength, String filename) {
            this.inputStream = inputStream;
            this.contentLength = contentLength;
            this.filename = filename;
            this.field = field;
        }
        public InputStream inputStream;
        public long contentLength;
        public String filename;
        public String field;
    }
}
