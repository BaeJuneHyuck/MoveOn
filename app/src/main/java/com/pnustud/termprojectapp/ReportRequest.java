package com.pnustud.termprojectapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;


public class ReportRequest extends StringRequest {

    final static private String URL = "http://skh2929209.cafe24.com/ReportRequest.php";
    private Map<String, String> parameters;

    public ReportRequest(int id, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters=new HashMap<>();
        parameters.put("id", id+"");
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}