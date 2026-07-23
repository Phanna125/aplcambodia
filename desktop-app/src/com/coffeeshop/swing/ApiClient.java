package com.coffeeshop.swing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final Gson gson = new GsonBuilder().create();
    private static String jwtToken = null;

    public static void setJwtToken(String token) {
        jwtToken = token;
    }
    
    public static String getJwtToken() {
        return jwtToken;
    }

    public static <T> T get(String path, TypeToken<T> typeToken) throws Exception {
        HttpURLConnection conn = setupConnection(path, "GET");
        return executeRequest(conn, typeToken);
    }
    
    public static <T> T get(String path, Class<T> clazz) throws Exception {
        HttpURLConnection conn = setupConnection(path, "GET");
        return executeRequest(conn, clazz);
    }

    public static <T> T post(String path, Object body, Class<T> responseClass) throws Exception {
        HttpURLConnection conn = setupConnection(path, "POST");
        conn.setDoOutput(true);
        writeBody(conn, body);
        return executeRequest(conn, responseClass);
    }
    
    public static <T> T post(String path, Object body, TypeToken<T> typeToken) throws Exception {
        HttpURLConnection conn = setupConnection(path, "POST");
        conn.setDoOutput(true);
        writeBody(conn, body);
        return executeRequest(conn, typeToken);
    }

    public static <T> T patch(String path, Object body, Class<T> responseClass) throws Exception {
        // HttpURLConnection doesn't support PATCH natively, we use a workaround
        HttpURLConnection conn = setupConnection(path, "POST");
        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        conn.setDoOutput(true);
        writeBody(conn, body);
        return executeRequest(conn, responseClass);
    }

    public static <T> T put(String path, Object body, Class<T> responseClass) throws Exception {
        HttpURLConnection conn = setupConnection(path, "PUT");
        conn.setDoOutput(true);
        writeBody(conn, body);
        return executeRequest(conn, responseClass);
    }

    public static void delete(String path) throws Exception {
        HttpURLConnection conn = setupConnection(path, "DELETE");
        int code = conn.getResponseCode();
        if (code >= 400) {
            throw new Exception("HTTP Delete Error: " + code);
        }
    }

    private static HttpURLConnection setupConnection(String path, String method) throws Exception {
        URL url = URI.create(BASE_URL + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        
        if (jwtToken != null && !jwtToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
        }
        return conn;
    }

    private static void writeBody(HttpURLConnection conn, Object body) throws Exception {
        String json = gson.toJson(body);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    private static <T> T executeRequest(HttpURLConnection conn, Class<T> responseClass) throws Exception {
        String responseStr = readResponse(conn);
        return gson.fromJson(responseStr, responseClass);
    }

    private static <T> T executeRequest(HttpURLConnection conn, TypeToken<T> typeToken) throws Exception {
        String responseStr = readResponse(conn);
        return gson.fromJson(responseStr, typeToken.getType());
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        boolean isError = status >= 400;
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                isError ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            if (isError) {
                throw new Exception("HTTP Error " + status + ": " + response.toString());
            }
            return response.toString();
        }
    }
}
