package com.pnustud.termprojectapp;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText)findViewById(R.id.input_email);
        final EditText passwordText = (EditText)findViewById(R.id.input_pass);
        final EditText passwordConfirmText = (EditText)findViewById(R.id.input_pass_confirm);
        final EditText nickText = (EditText)findViewById(R.id.input_nickname);

        Button registerButton = (Button)findViewById(R.id.button_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userPasswordConfirm = passwordConfirmText.getText().toString();
                String userNick = nickText.getText().toString();

                if(!userPassword.equalsIgnoreCase(userPasswordConfirm)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isEmailValid(userId)){
                    Toast.makeText(RegisterActivity.this, "잘못된 이메일입니다", Toast.LENGTH_LONG).show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("success.")
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show();

                                finish();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("fail.")
                                        .setNegativeButton("RETRY", null)
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userId, userPassword, userNick, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
