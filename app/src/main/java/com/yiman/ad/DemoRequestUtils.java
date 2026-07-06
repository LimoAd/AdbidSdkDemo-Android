package com.yiman.ad;

import android.text.TextUtils;
import android.util.Log;

import com.adbid.sdk.AdbidSdk;
import com.adbid.sdk.AdbidSdkInfoCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server bidding 模拟请求服务端进行比价的工具类
 */
public class DemoRequestUtils {
    public static final ExecutorService SINGLE_THREAD_EXECUTOR =
            Executors.newSingleThreadExecutor(r -> new Thread(r, "BIDDING_THREAD"));
    private static final String TAG = DemoRequestUtils.class.getSimpleName();
    protected static final String SERVER_BIDDING_URL =
            "http://pre-ads-bid.leadmoad.com/api/adx/bidding";
    private static final String POST_DATA =
            "{\"id\":\"f11a0d31-6f69-45b7-8a60-448ddad5702b\",\"timestamp\":1778295909173,\"device\":{\"device_type\":1,\"android_id\":\"567834249b14e285\",\"oaid\":\"f5cd4f70c1319b5a\",\"mac\":\"\",\"bootMark\":\"de992407-551e-429f-896e-85502\",\"updateMark\":\"1778295903.700856078\",\"language\":\"zh\",\"manu\":\"Xiaomi\",\"brand\":\"Redmi\",\"model\":\"22041211AC\",\"width\":1440,\"height\":3036,\"os_type\":1,\"os_version\":\"13\",\"user_agent\":\"Mozilla\\/5.0 (Linux; Android 13; 22041211AC Build\\/TP1A.220624.014; wv) AppleWebKit\\/537.36 (KHTML, like Gecko) Version\\/4.0 Chrome\\/147.0.7727.55 Mobile Safari\\/537.36\",\"density\":3.7375,\"orientation\":2,\"imsi\":\"\",\"client_time\":\"2026-05-09 11:05:09.166+0800\",\"net_work\":{\"ip\":\"124.127.72.83\",\"ip_v6\":\"{ccmni0=[2408:8509:24c0:70da:18ad:c220:18c0:dedc], ccmni1=[2408:8409:2521:3de:18ad:c220:1e21:e7ac]}\",\"isp_type\":46001,\"network_type\":0},\"geo\":{}},\"zone\":{\"id\":\"AD_UNIT_ID\",\"num\":1,\"min_price\":0,\"ad_format\":1,\"supper_twist\":true},\"app\":{\"app_id\":\"10005\",\"bundle\":\"com.adbid.sdk.demo\",\"version\":\"2.1.0.11\",\"app_name\":\"%E9%A2%86%E6%91%A9%E5%B9%BF%E5%91%8A%E6%B5%8B%E8%AF%95\"},\"ext\":{\"sdk_info\":\"SDK_INFO\"}}";

    public void requestBiddingToken(String posId, RequestCallBack callBack) {
        SINGLE_THREAD_EXECUTOR.execute(() -> {
            try {

                HttpURLConnection connection =
                        (HttpURLConnection) new URL(SERVER_BIDDING_URL).openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

                for (Map.Entry<String, String> entry : getRequestProperty().entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }


                AdbidSdk.getInstance(MyApplication.myApplication.getApplicationContext())
                        .getSdkInfo(posId, new AdbidSdkInfoCallback() {
                            @Override public void onInfoCallback(String info) {
                                try {
                                    if (info == null) {
                                        notifyFailure(callBack);
                                        return;
                                    }
                                    String postData = POST_DATA.replace("AD_UNIT_ID", posId)
                                            .replace("SDK_INFO", info);
                                    byte[] postDataBytes =
                                            postData.getBytes(StandardCharsets.UTF_8);

                                    OutputStream out = connection.getOutputStream();
                                    out.write(postDataBytes);
                                    out.flush();
                                    out.close();
                                    handleResponse(getStringContent(connection), callBack);
                                } catch (IOException e) {
                                    notifyFailure(callBack);
                                }
                            }
                        });

            } catch (IOException e) {
               Log.i (TAG,"请求 token 失败： " + e.getMessage());
                notifyFailure(callBack);
            }
        });
    }


    protected Map<String, String> getRequestProperty() {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("User-Agent", "GDTMobApp/0 CFNetwork/1220.1 Darwin/19.6.0");
        map.put("Accept", "application/json");
        map.put("Accept-Language", "en-us");
        map.put("X-OpenRTB-Version", "2.5");
        return map;
    }

    protected void handleResponse(String response, RequestCallBack callBack) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String token = jsonObject.optString("token");

            if (TextUtils.isEmpty(token)) {
               Log.i (TAG,"回包中无 token");
                notifyFailure(callBack);
            } else {
               Log.i (TAG,"请求 token 成功");
                if (callBack != null) {
                    Log.d(TAG, "requestBiddingToken: " + token);
                    callBack.onSuccess(token);
                }
            }
        } catch (Exception e) {
           Log.i (TAG,"请求 token 失败");
            Log.e("requestBiddingToken", e.getMessage());
            notifyFailure(callBack);
        }
    }

    private void notifyFailure(RequestCallBack callBack) {
        if (callBack != null) {
            callBack.onFailure();
        }
    }

    private static byte[] getBytesContent(HttpURLConnection connection)
            throws IllegalStateException, IOException {

        int responseCode = connection.getResponseCode();

        Log.d(TAG, "responseCode = " + responseCode);

        InputStream in;

        if (responseCode >= HttpURLConnection.HTTP_OK &&
                responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {

            in = connection.getInputStream();

        } else {

            in = connection.getErrorStream();

            Log.e(TAG, "error response message = " + connection.getResponseMessage());
        }

        if (in == null) {
            return null;
        }

        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        try {

            byte[] buffer = new byte[1024];

            int len;

            while ((len = in.read(buffer)) > 0) {

                bo.write(buffer, 0, len);
            }

        } finally {

            try {
                in.close();
            } catch (IOException ignore) {
            }
        }

        return bo.toByteArray();
    }

    private static String getStringContent(HttpURLConnection connection) throws IOException {
        byte[] bytes = getBytesContent(connection);
        if (bytes == null) {
            return null;
        } else if (bytes.length == 0) {
            return "";
        }

        String charset = null;
        try {
            charset = connection.getContentEncoding();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (charset == null) {
            charset = "UTF-8";
        }
        return new String(bytes, charset);
    }

    public interface RequestCallBack {
        void onSuccess(String result);

        void onFailure();
    }

}
