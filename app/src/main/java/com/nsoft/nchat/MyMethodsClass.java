package com.nsoft.nchat;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyMethodsClass {
    Context context;

    public MyMethodsClass(Context context) {
        this.context = context;
    }

    public interface ResponseCallback {
        void onSuccess(JSONObject jsonObject);

        void onError(VolleyError error);
    }

    public interface JsonArrayCallback {
        void onSuccess(JSONArray jsonArray);

        void onError(VolleyError error);
    }

    public String generateUniqueId(String name) {
        String[] nameList = name.split(" ");
        String firstName = nameList[0];
        Random random = new Random();
        String result = firstName.toLowerCase() + "_" + (random.nextInt(9000) + 1000);
        return result;
    }

    public void accountCreatePostRequest(String name, String email, String password, String userId, ResponseCallback callback) {
        String url = "https://backend.nsoftcompany.xyz/users";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    callback.onSuccess(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(objectRequest);
    }

    public void loginPostRequest(String email, String password, ResponseCallback callback) {
        String url = "https://backend.nsoftcompany.xyz/login";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    callback.onSuccess(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void userDetailsPostRequest(String userId, ResponseCallback callback) {
        String url = "https://backend.nsoftcompany.xyz/users/" + userId;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    callback.onSuccess(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void getChatList(String userId, JsonArrayCallback callback) {
        String url = "https://backend.nsoftcompany.xyz/data/" + userId;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray != null || !(jsonArray.length() > 0)) {
                    callback.onSuccess(jsonArray);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError);
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}
