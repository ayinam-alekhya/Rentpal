package com.rentpal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiUtil {
    private static final String BASE_URL = "http://localhost:8080/api";

    public static String get(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("GET request failed with response code: " + responseCode);
        }
    }

    public static String post(String endpoint, String jsonInputString) throws Exception {
        System.out.println("Sending POST request to: " + BASE_URL + endpoint);
        System.out.println("Request body: " + jsonInputString);
        
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);
        
        // Read response regardless of status code for debugging
        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }
        
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        
        System.out.println("Response body: " + response.toString());
        
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            return response.toString();
        } else {
            throw new Exception("POST request failed with response code: " + responseCode + ", response: " + response.toString());
        }
    }
        // --- helper: read either input or error stream
        private static String readStream(HttpURLConnection conn) throws IOException {
            InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();
            if (is == null) return "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        }
          // --- PUT with JSON body (preferred for /complaints/{id}/status)
    public static String put(String endpoint, String jsonBody) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        if (jsonBody != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
        }

        int code = connection.getResponseCode();
        String body = readStream(connection);

        // treat 200/201/204 as success
        if (code == HttpURLConnection.HTTP_OK ||
            code == HttpURLConnection.HTTP_CREATED ||
            code == HttpURLConnection.HTTP_NO_CONTENT) {
            return body;
        }
        throw new Exception("PUT " + endpoint + " failed: " + code + " body=" + body);
    }

    // --- PUT without body (useful for query-param style)
    public static String put(String endpoint) throws Exception {
        return put(endpoint, null);
    }

    // --- (optional) small helper for safe query param
    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static boolean delete(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        int code = connection.getResponseCode();
        // many APIs return 204 No Content on delete
        return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NO_CONTENT;
    }
}

//     public static String put(String endpoint, String jsonInputString) throws Exception {
//         URL url = new URL(BASE_URL + endpoint);
//         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//         connection.setRequestMethod("PUT");
//         connection.setRequestProperty("Content-Type", "application/json; utf-8");
//         connection.setRequestProperty("Accept", "application/json");
//         connection.setDoOutput(true);

//         try (OutputStream os = connection.getOutputStream()) {
//             byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
//             os.write(input, 0, input.length);
//         }

//         int responseCode = connection.getResponseCode();
//         if (responseCode == HttpURLConnection.HTTP_OK) {
//             BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
//             StringBuilder response = new StringBuilder();
//             String inputLine;
//             while ((inputLine = in.readLine()) != null) {
//                 response.append(inputLine);
//             }
//             in.close();
//             return response.toString();
//         } else {
//             throw new Exception("PUT request failed with response code: " + responseCode);
//         }
//     }

//     public static boolean delete(String endpoint) throws Exception {
//         URL url = new URL(BASE_URL + endpoint);
//         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//         connection.setRequestMethod("DELETE");
//         connection.setRequestProperty("Content-Type", "application/json");

//         int responseCode = connection.getResponseCode();
//         return responseCode == HttpURLConnection.HTTP_OK;
//     }
// }