package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Peter on 2017/8/18.
 */

public final class QueryUtils {
    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){}

    /**
     * 查询 USGS 数据集并返回 {@link Earthquake} 对象的列表。
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        // 创建 URL 对象
        URL url = createUrl(requestUrl);

        // 执行 URL 的 HTTP 请求并接收返回的 JSON 响应
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // 从 JSON 响应提取相关域并创建 {@link Earthquake} 的列表
        List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

        // 返回 {@link Earthquake} 的列表
        return earthquakes;
    }

    /**
     * 返回通过解析给定 JSON 响应构建的 {@link Earthquake} 对象
     * 列表。
     */
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON){
        // 如果 JSON 字符串为空或 null，将提早返回。
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        //创建一个可以添加地震的空 ArrayList
        List<Earthquake> earthquakes = new ArrayList<>();

        // 尝试解析 JSON 响应字符串。如果格式化 JSON 的方式存在问题，
        // 则将抛出 JSONException 异常对象。
        // 捕获该异常以便应用不会崩溃，并将错误消息打印到日志中。
        try {
            // 通过 JSON 响应字符串创建 JSONObject
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            // 提取与名为 "features" 的键关联的 JSONArray，
            // 该键表示特征（或地震）列表。
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            // 针对 earthquakeArray 中的每个地震，创建 {@link Earthquake} 对象
            for (int i = 0; i < earthquakeArray.length(); i++){
                // 获取地震列表中位置 i 处的单一地震
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // 针对给定地震，提取与名为 "properties" 的键关联的 JSONObject，
                // 该键表示该地震所有属性的
                // 列表。
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // 提取名为 "mag" 的键的值
                double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                // 使用 JSON 响应中的震级、位置、时间和 url，
                // 创建新的 {@link Earthquake} 对象。
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // 将该新 {@link Earthquake} 添加到地震列表。
                earthquakes.add(earthquake);
            }
        }catch (JSONException e){
            // 在 "try" 块中执行上述任一语句时若系统抛出错误，
            // 则在此处捕获异常，以便应用不会崩溃。在日志消息中打印
            // 来自异常的消息。
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
