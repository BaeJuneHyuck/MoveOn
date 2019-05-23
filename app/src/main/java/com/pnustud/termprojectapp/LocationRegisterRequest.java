package com.pnustud.termprojectapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationRegisterRequest extends StringRequest {
    final static private String URL = "http://skh2929209.cafe24.com/BRegister.php";
    private Map<String, String> parameters;

    public LocationRegisterRequest(String locName, double lat, double lng ,String toilet, int type, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("name", locName);
        parameters.put("latitude", Double.toString(lat));
        parameters.put("longitude", Double.toString(lng));
        parameters.put("toilet", toilet);
        parameters.put("type", type+"");
        parameters.put("report", "0");
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
