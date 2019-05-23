package com.pnustud.termprojectapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationRequest extends StringRequest {
    final static private String URL = "http://skh2929209.cafe24.com/LocationRequest.php";
    private Map<String, String> parameters;

    public LocationRequest(Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
