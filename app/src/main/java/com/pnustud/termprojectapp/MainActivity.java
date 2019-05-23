package com.pnustud.termprojectapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    ArrayList<DBLocation> LocationList;
    ArrayList<Marker> MarkerList;
    private GoogleMap mMap;
    Location loc;
    LocationManager mLocationManager;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static boolean is_logined;
    private String logined_userId;
    private String logined_userNick = "Guest";
    private NavigationView navigationView;
    private EditText searchBox;
    double lat;
    double lng;
    SharedPreferences preference_login_data;
    int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationList = new ArrayList<>();
        MarkerList = new ArrayList<>();
        searchBox = findViewById(R.id.editSearch2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
             this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String search = searchBox.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("검색기능 만들어주세요 :" +  search)
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, "Permission Error init", Toast.LENGTH_LONG).show();
        }
        initMap();
        mMap.setOnMarkerClickListener(this);
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                MapUpdate();
                ha.postDelayed(this, 15000);
            }
        }, 15000);
    }

    public void initMap(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            lat = loc.getLatitude();
            lng = loc.getLongitude();
            LatLng SEOUL = new LatLng(lat,lng);
/*
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(SEOUL);
            markerOptions.title("현재위치");
            mMap.addMarker(markerOptions);
*/
            mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            Toast.makeText(this, lat +","+ lng, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            Toast.makeText(this, "request", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if(backButtonCount >= 1)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                backButtonCount = 0;
            }
            else
            {
                Toast.makeText(this, "이전 버튼을 한번 더 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            if(is_logined){
                logout();
            }else{
                // check auto login
                //preference_login_data = PreferenceManager.getDefaultSharedPreferences(this);

                preference_login_data = this.getSharedPreferences("sFile",MODE_PRIVATE);
                if(preference_login_data.getBoolean("autologin", false)){
                    String savedEmail = preference_login_data.getString("email", "");
                    String savedPass = preference_login_data.getString("password", "");
                    if(autoLogin(savedEmail, savedPass, this)){
                        Toast.makeText(this, "자동로그인 완료:" + logined_userNick, Toast.LENGTH_LONG).show();
                        loginSuccess();
                    }else{
                        Toast.makeText(this, "자동로그인에 실패 하였습니다.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    callLoginDialog();
                }
            }
        } else if (id == R.id.nav_register) {
            if(is_logined){
                locationRegister();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("로그인이 필요한 기능입니다. 먼저 로그인 해주세요")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_share) {
            // 위치를 공유하는 텍스트를 만들어서 전송하자
            // MOVEON 그림이랑 같이 전송되게 하고싶은데 그건 좀 어려운듯
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,  logined_userNick +"님의 현재 위치 : "+lat + ", "+ lng + "\n http://skh2929209.cafe24.com/Login.php");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }else if (id == R.id.nav_setting) {
            // call setting activity
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callLoginDialog()
    {
        final EditText usernameField,passwordField;
        final Button button_register;
        final Button button_login;
        final CheckBox checkbox_remain;

        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.login_diaolog);

        button_login = (Button) myDialog.findViewById(R.id.login_dialog_login);
        button_register = (Button) myDialog.findViewById(R.id.login_dialog_reg);
        usernameField = (EditText) myDialog.findViewById(R.id.login_dialog_email);
        passwordField = (EditText) myDialog.findViewById(R.id.login_dialog_password);
        checkbox_remain = (CheckBox) myDialog.findViewById(R.id.checkBox) ;

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                Intent intent = new Intent(context, RegisterActivity.class);
                context.startActivity(intent);
            }
        });

        button_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                final String userId = usernameField.getText().toString();
                final String userPassword = passwordField.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){

                                is_logined  = true;
                                logined_userId = jsonResponse.getString("userId");
                                logined_userNick = jsonResponse.getString("userNick");
                                if(checkbox_remain.isChecked()){
                                    SharedPreferences.Editor editor = preference_login_data.edit();
                                    editor.putBoolean("autologin",true);
                                    editor.putString("email", userId);
                                    editor.putString("password", userPassword);
                                    editor.commit();
                                }
                                myDialog.dismiss();
                                loginSuccess();
                            }
                            else{
                                is_logined  = false;
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("잘못된 이메일이나 비밀번호입니다")
                                        .setNegativeButton("확인", null)
                                        .create()
                                        .show();
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userId, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(loginRequest);
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private boolean autoLogin(final String userId, final String userPassword, final Context context){
        is_logined  = false;
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        logined_userId = jsonResponse.getString("userId");
                        logined_userNick = jsonResponse.getString("userNick");
                        is_logined  = true;
                    }
                    else{
                        is_logined  = false;
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(userId, userPassword, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(loginRequest);
        return is_logined;
    }

    private void loginSuccess(){
        // 첫번째 버튼인 로그인 버튼의 글자를 로그아웃으로 변경
        Menu menu = navigationView.getMenu();
        SubMenu submenu = menu.getItem(0).getSubMenu();
        submenu.getItem(0).setTitle("로그아웃");

        // 메세지를 출력
        View headerView = navigationView.getHeaderView(0);
        TextView emailText = (TextView) headerView.findViewById(R.id.textView_print_email);
        String string = logined_userNick + "님 안녕하세요";
        emailText.setText(string);
    }

    private void logout(){
        is_logined = false;
        // 자동 로그인 기록을 지워줌
        preference_login_data = this.getSharedPreferences("sFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = preference_login_data.edit();
        editor.clear(); //clear all stored data
        editor.commit();

        // 첫번째 버튼인 로그인 버튼의 글자를 로그인으로 변경
        Menu menu = navigationView.getMenu();
        SubMenu submenu = menu.getItem(0).getSubMenu();
        submenu.getItem(0).setTitle("로그인");

        // 메세지 출력
        View headerView = navigationView.getHeaderView(0);
        TextView emailText = (TextView) headerView.findViewById(R.id.textView_print_email);
        String string = "로그인해주세요";
        emailText.setText(string);
    }

    private void locationRegister(){
        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.location_register_dialog);

        final EditText nameText = (EditText)myDialog.findViewById(R.id.location_dialog_name);
        final Spinner spinner = (Spinner)myDialog.findViewById(R.id.location_dialog_spinner);
        final CheckBox toiletBox = (CheckBox)myDialog.findViewById(R.id.location_dialog_toilet);
        TextView latlngBox = (TextView)myDialog.findViewById(R.id.location_dialog_textview);
        Button registerButton = (Button)myDialog.findViewById(R.id.location_dialog_reg);

        latlngBox.setText("현재 위치: " + lat + ", " + lng);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locName = nameText.getText().toString();
                int locType = spinner.getSelectedItemPosition();
                String locToilet;
                if (toiletBox.isChecked()) locToilet = "1";
                else locToilet = "0";

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("success.")
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show();
                                myDialog.dismiss();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                LocationRegisterRequest registerRequest = new LocationRegisterRequest(locName, lat,lng,locToilet, locType, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(registerRequest);
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    void MapUpdate(){
        mMap.clear();
        MarkerList.clear();
        LocationList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final DBLocation temp = new DBLocation();
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        temp.setName(jsonObject.getString("name"));
                        temp.setLatitude(jsonObject.getDouble("latitude"));
                        temp.setLongitude(jsonObject.getDouble("longitude"));
                        temp.setToilet(jsonObject.getInt("toilet"));
                        temp.setType(jsonObject.getInt("type"));
                        temp.setReport(jsonObject.getInt("report"));
                        LocationList.add(new DBLocation(temp));
                        Log.d("test", "temp "+i);
                    }
                    PrintMarkers();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        LocationRequest locationRequest = new LocationRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(locationRequest);
    }

    void PrintMarkers(){
        DBLocation loc;

        Log.d("Print Markers", "total size =  "+ LocationList.size());
        for(int i = 0; i < LocationList.size() ;i++){
            loc = LocationList.get(i);
            LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
            Log.d("Print Markers", "index : " + i + "latlng =  " + latlng.latitude + ", "+latlng.longitude);
            Marker newmarker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(loc.getName())
            );
            newmarker.setTag(i);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final TextView nameField,typeField,toiletField,reportField;
        final Button button_report;

        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.location_marker_dialog);

        button_report = (Button) myDialog.findViewById(R.id.location_marker_dialog_reportButton);

        nameField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_name);
        typeField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_type);
        toiletField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_toilet);
        reportField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_report);

        nameField.setText(marker.getTitle());
        int tag = (int)marker.getTag();
        DBLocation loc = LocationList.get(tag);
        switch( loc.getType()){
            case 0:
                typeField.setText("음식점");
                break;
            case 1:
                typeField.setText("카페");
                break;
            case 2:
                typeField.setText("도서관");
                break;
            case 3:
                typeField.setText("병원");
                break;
            case 4:
                typeField.setText("은행");
                break;
            case 5:
                typeField.setText("공원");
                break;
            default:
                typeField.setText("기타");
                break;
        }
        if(loc.getToilet() == 1){
            toiletField.setText("화장실 있음");
        }else{
            toiletField.setText("화장실 없음");
        }
        reportField.setText("신고횟수 :" + loc.getReport()+ "회");

        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return false;
    }
}
