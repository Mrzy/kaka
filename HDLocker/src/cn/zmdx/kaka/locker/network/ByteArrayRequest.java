
package cn.zmdx.kaka.locker.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ProgressListener;
import com.android.volley.toolbox.HttpHeaderParser;

public class ByteArrayRequest extends Request<byte[]> implements ProgressListener {

    private Listener<byte[]> mListener;

    private byte[] mResponseData;

    private ProgressListener mProgressListener;

    public ByteArrayRequest(String url, Listener<byte[]> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        mResponseData = response.data;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    public byte[] getResponseData() {
        return mResponseData;
    }

    public void setOnProgressListener(ProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public void onProgress(long transferredBytes, long totalSize) {
        if (null != mProgressListener) {
            mProgressListener.onProgress(transferredBytes, totalSize);
        }
    }
}
