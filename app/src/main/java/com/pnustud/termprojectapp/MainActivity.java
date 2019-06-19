package com.pnustud.termprojectapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    private ArrayList<DBLocation> LocationList;
    private ArrayList<Marker> MarkerList;
    private GoogleMap mMap;
    private Location currentLocation;
    private LocationManager mLocationManager;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static boolean isLogined;
    private String loginedUserId;
    private String loginedUserNick = "Guest";
    private NavigationView navigationView;
    private EditText searchBox;
    private double lat;
    private double lng;
    private SharedPreferences preference_login_data;
    private int backButtonCount = 0;
    private ImageButton FilterButton;
    private boolean filterToilet = true;
    private boolean[] filterType = new boolean[8];// 0~6


// Dijikstra value
    private static int INF = 9999999;
    private static final int NO_PARENT = -1;


    //임시
    public double[][] place_info = {{35.231427, 129.082265},{35.231624, 129.081460},{35.231526, 129.081392}
            ,{35.231476, 129.081193} , {35.231666, 129.080700}, {35.231826, 129.080297}
            ,{35.231524, 129.080126} , {35.232602, 129.080700} , {35.232874, 129.080743} , {35.233116, 129.080689}
            ,{35.233421, 129.080785},{35.232871, 129.082108},{35.233136, 129.082175}
            ,{35.233563, 129.082308},{35.233740, 129.082442},{35.234058, 129.081082}
            ,{35.233729, 129.079521},{35.233764, 129.079333},{35.233733, 129.079108},{35.233803, 129.078663}};
    public int[][] adjacencyMatrix;
    private double dest_lat;
    private double dest_lng;

    private void pre_process(){
        adjacencyMatrix = new int[20][20];
        for(int i =0 ; i < 20 ; ++i){
            for(int j=0; j<20; ++j){
                adjacencyMatrix[i][j] = INF;
            }
        }
        adjacencyMatrix[0][1] =5;
        adjacencyMatrix[1][0]= 5;
        adjacencyMatrix[1][2] =1;
        adjacencyMatrix[2][1] =1;
        adjacencyMatrix[2][3] =1;
        adjacencyMatrix[3][2] = 1;
        adjacencyMatrix[3][4] = 3;
        adjacencyMatrix[4][3] = 3;

        adjacencyMatrix[4][5] = 2;
        adjacencyMatrix[5][4] = 2;
        adjacencyMatrix[5][6] = 2;
        adjacencyMatrix[6][5] = 2;
        adjacencyMatrix[5][7] = 13;
        adjacencyMatrix[7][5] =13;

        adjacencyMatrix[7][8] = 1;
        adjacencyMatrix[8][7] = 1;
        adjacencyMatrix[8][9] =1;
        adjacencyMatrix[9][8] = 1;

        adjacencyMatrix[9][10] = 2;
        adjacencyMatrix[10][9] =2;

        adjacencyMatrix[1][11] = 12;
        adjacencyMatrix[11][1] = 12;
        adjacencyMatrix[11][12] =2;
        adjacencyMatrix[12][11] =2;

        adjacencyMatrix[12][13] =3;
        adjacencyMatrix[13][12] =3;
        adjacencyMatrix[13][14]=1;
        adjacencyMatrix[14][13]=1;

        adjacencyMatrix[14][15]=7;
        adjacencyMatrix[15][14]=7;
        adjacencyMatrix[10][12]=7;
        adjacencyMatrix[12][10]=7;

        adjacencyMatrix[10][16]=9;
        adjacencyMatrix[16][10]=9;

        adjacencyMatrix[16][17]=1;
        adjacencyMatrix[17][16]=1;
        adjacencyMatrix[17][18]=1;
        adjacencyMatrix[18][17]=1;
        adjacencyMatrix[18][19]=3;
        adjacencyMatrix[19][18]=3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pre_process();
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

        // 필터(팝업메뉴) 관련 설정//
        for(int i = 0 ; i<7;i++){
            filterType[i] = true;
        }

        FilterButton = (ImageButton)findViewById(R.id.Button_Filter);
        FilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFIlterDialog();
            }
        });
/*
        FilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenu);
                final PopupMenu popup = new PopupMenu(wrapper, FilterButton);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                MenuItem FilterToilet = popup.getMenu().findItem(R.id.Filter_Toilet);
                if(filterToilet)FilterToilet.setChecked(true);
                else FilterToilet.setChecked(false);

                for(int i = 1 ; i< 8; i++){
                    MenuItem item = popup.getMenu().getItem(i);
                    item.setChecked(filterType[i-1]);
                }
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getOrder() == 0)  filterToilet = !filterToilet;
                        else filterType[item.getOrder() - 1] = !filterType[item.getOrder() - 1];
                        PrintMarkers();
                        return true;
                    }
                });
                popup.show();
            }
        }); // 필터 설정 끝//
*/
        init_input_search();
    }

    private void init_input_search(){
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String search = searchBox.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    MapUpdate();
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        String searchString = searchBox.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {

        }
        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(this, "geoLocate : " + address.toString(), Toast.LENGTH_LONG).show();
            Log.d("ks", "geoLocate : " + address.toString());
            dest_lat = list.get(0).getLatitude();
            dest_lng = list.get(0).getLongitude();

//            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(dest_lat, dest_lng)));

            LatLngBounds mMapBoundary = new LatLngBounds(
                    new LatLng( (dest_lat < lat ?  dest_lat : lat ) , (dest_lng < lng ?  dest_lng : lng))
                    , new LatLng( ( dest_lat > lat ?  dest_lat : lat ) , (dest_lng > lng ?  dest_lng : lng)) );
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,50 ));


            double[] dest_info = {dest_lat, dest_lng};
            double[] start_info = {lat, lng};
            int dest_node_num = find_closest_node(place_info, dest_info);
            int start_node_num = find_closest_node(place_info, start_info);
            //Toast.makeText(this, dest_node_num + " is selected ", Toast.LENGTH_LONG).show();
            List<Integer> pathToGoal = dijkstra(adjacencyMatrix, start_node_num, dest_node_num);
            addPolylinesToMap(pathToGoal);

            List<Integer> a1 = new ArrayList<>() ;
            List<Integer> a2 = new ArrayList<>() ;
            a1.add(start_node_num);
            a2.add(dest_node_num);

            addDottedPolylinesToMap(a1,0);
            addDottedPolylinesToMap(a2,1);

            draw_goal_node(place_info[dest_node_num]);
            draw_goal_node(place_info[start_node_num]);
        }
    }

    private int find_closest_node(double[][] mPlace_info,double[] cur_node_info){
        double min_dist = 999999;
        int closest_node=-1;
        for(int i = 0 ; i < mPlace_info.length ; ++i){
            double lat_diff = mPlace_info[i][0] - cur_node_info[0];
            double lng_diff = mPlace_info[i][1] - cur_node_info[1];
            double lat_sqr = lat_diff*lat_diff;
            double lng_sqr = lng_diff*lng_diff;
            //mMap.addMarker(new MarkerOptions().position(new LatLng(mPlace_info[i][0],mPlace_info[i][1])));
            double cur_dist_diff = lat_sqr + lng_sqr;
            if(min_dist > cur_dist_diff){
                closest_node = i;
                min_dist = cur_dist_diff;
            }
        }
        return closest_node;
    }
    private static final int PATTERN_GAP_LENGTH_PX = 10;  // 1   //6/19
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);//6/19
    private static final Dot DOT = new Dot();//6/19
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);  // 2//6/19

    private void addDottedPolylinesToMap(List<Integer> pathToGoal , int flag){
        List<LatLng> newPathToGoal = new ArrayList<>();
        for(Integer cur_node : pathToGoal){
            double cur_lat = place_info[cur_node][0];
            double cur_lng = place_info[cur_node][1];
            newPathToGoal.add(new LatLng(cur_lat,cur_lng));
        }
        if (flag == 0 )
            newPathToGoal.add(new LatLng(lat,lng));
        else
            newPathToGoal.add(new LatLng(dest_lat,dest_lng));
        PolylineOptions polylineOptions = new PolylineOptions().pattern(PATTERN_DOTTED).color(Color.BLUE);
        for(LatLng point : newPathToGoal) polylineOptions.add(point);
        Polyline polyline = mMap.addPolyline(polylineOptions);//6/19
    }

    //****** 6/10
    private void addPolylinesToMap(List<Integer> pathToGoal){
        List<LatLng> newPathToGoal = new ArrayList<>();
        for(Integer cur_node : pathToGoal){
            double cur_lat = place_info[cur_node][0];
            double cur_lng = place_info[cur_node][1];
            newPathToGoal.add(new LatLng(cur_lat,cur_lng));
        }
        Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newPathToGoal));
        polyline.setColor(ContextCompat.getColor(this,R.color.dark_gray));
    }


    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    private  List<Integer> dijkstra(int[][] adjacencyMatrix,
                                    int startVertex,int goalVertex) {
        List<Integer> pathToGoal = new ArrayList<>();
        int nVertices = adjacencyMatrix[0].length;

        // shortestDistances[i] will hold the
        // shortest distance from src to i
        int[] shortestDistances = new int[nVertices];

        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices];

        // Initialize all distances as+
        // INFINITE and added[] as false
        for (int vertexIndex = 0; vertexIndex < nVertices;
             vertexIndex++) {
            shortestDistances[vertexIndex] = Integer.MAX_VALUE;
            added[vertexIndex] = false;
        }

        // Distance of source vertex from
        // itself is always 0
        shortestDistances[startVertex] = 0;

        // Parent array to store shortest
        // path tree
        int[] parents = new int[nVertices];

        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;

        // Find shortest path for all
        // vertices
        for (int i = 1; i < nVertices; i++) {

            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                if (!added[vertexIndex] &&
                        shortestDistances[vertexIndex] <
                                shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }

            // Mark the picked vertex as
            // processed
            added[nearestVertex] = true;

            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0
                        && ((shortestDistance + edgeDistance) <
                        shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

        printSolution(startVertex,goalVertex, shortestDistances, parents,pathToGoal);
        return pathToGoal;
    }

    // A utility function to print
    // the constructed distances
    // array and shortest paths
    private  void printSolution(int startVertex,int goalVertex,
                                int[] distances,
                                int[] parents,List<Integer> pathToGoal)
    {
        int nVertices = distances.length;
        System.out.print("Vertex\t Distance\tPath");

        for (int vertexIndex = 0;
             vertexIndex < nVertices;
             vertexIndex++)
        {
            if (vertexIndex != startVertex && vertexIndex ==goalVertex)
            {
                // System.out.print("\n" + startVertex + " -> ");
                //System.out.print(vertexIndex + " \t\t ");
                //System.out.print(distances[vertexIndex] + "\t\t");
                printPath(vertexIndex, parents,pathToGoal);

            }
        }

    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private void printPath(int currentVertex,
                           int[] parents,List<Integer>pathToGoal)
    {
        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT)
        {
            //draw_goal_node(place_info[currentVertex]);
            return;
        }
        /*
        if(parents[currentVertex]==-1){
            draw_goal_node(place_info[currentVertex]);
        }*/
        printPath(parents[currentVertex], parents,pathToGoal);
        //System.out.print(currentVertex + " ");
        pathToGoal.add(currentVertex);
        //draw_goal_node(place_info[currentVertex]);
    }
    //****** 6/9
    private void draw_goal_node(double[] mNode_info){
 //       mMap.addMarker(new MarkerOptions().position(new LatLng(mNode_info[0] , mNode_info[1])));

        DBLocation loc = new DBLocation(-1, "길찾기 검색 결과", mNode_info[0], mNode_info[1], 0, 7, 0);
        LocationList.add(loc);
        float color = BitmapDescriptorFactory.HUE_BLUE;
        LatLng latlng = new LatLng(mNode_info[0] , mNode_info[1]);
        Marker newmarker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(loc.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        );
        newmarker.setTag(LocationList.size()-1);
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

        MapUpdate();
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                MapUpdate();
                ha.postDelayed(this, 100000);
            }
        }, 100000);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){

            public void onMapLongClick(LatLng point){
                    Toast.makeText(MainActivity.this,
                            point.latitude + ", " + point.longitude,
                            Toast.LENGTH_SHORT).show();
                }
            });


            // check auto login
        preference_login_data = this.getSharedPreferences("sFile",MODE_PRIVATE);
        if(preference_login_data.getBoolean("autologin", false)){
            String savedEmail = preference_login_data.getString("email", "");
            String savedPass = preference_login_data.getString("password", "");
            autoLogin(savedEmail, savedPass, this);
        }
    }

    public void initMap(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 1, this);
            // 5초마다 또는 1미터 이동될때마다 location 새로고침 설정

            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // 초기위치 얻어와서 지도 화면을 현재 위치로 이동
            lat = currentLocation.getLatitude();
            lng = currentLocation.getLongitude();
            LatLng CurrentLocation = new LatLng(lat,lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(CurrentLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        } else {
            // 허가 없으면 요청하기
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
            if(isLogined){
                logout();
            }else{
                callLoginDialog();
            }
        } else if (id == R.id.nav_register) {
            if(isLogined){
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
            sendIntent.putExtra(Intent.EXTRA_TEXT,  loginedUserNick +"님의 현재 위치 : "+lat + ", "+ lng + "\n\n http://skh2929209.cafe24.com/image.html");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }else if (id == R.id.nav_setting) {
            // call setting activity
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callFIlterDialog(){

        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.filter_dialog);

        final CheckBox checkbox_toilet = (CheckBox) myDialog.findViewById(R.id.Filter_Toilet);
        final CheckBox checkbox_rest = (CheckBox) myDialog.findViewById(R.id.Filter_Restaurant);
        final CheckBox checkbox_cafe = (CheckBox) myDialog.findViewById(R.id.Filter_Cafe);
        final CheckBox checkbox_library = (CheckBox) myDialog.findViewById(R.id.Filter_Library);
        final CheckBox checkbox_hospital = (CheckBox) myDialog.findViewById(R.id.Filter_Hospital);
        final CheckBox checkbox_bank = (CheckBox) myDialog.findViewById(R.id.Filter_Bank);
        final CheckBox checkbox_park = (CheckBox) myDialog.findViewById(R.id.Filter_Park);
        final CheckBox checkbox_etc = (CheckBox) myDialog.findViewById(R.id.Filter_ETC);

        checkbox_toilet.setChecked(filterToilet);

        checkbox_rest.setChecked(filterType[0]);
        checkbox_cafe.setChecked(filterType[1]);
        checkbox_library.setChecked(filterType[2]);
        checkbox_hospital.setChecked(filterType[3]);
        checkbox_bank.setChecked(filterType[4]);
        checkbox_park.setChecked(filterType[5]);
        checkbox_etc.setChecked(filterType[6]);

        checkbox_toilet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterToilet = !filterToilet;
                mMap.clear();
                PrintMarkers();
            }
        });
        checkbox_rest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[0] = !filterType[0];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_cafe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[1] = !filterType[1];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_library.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[2] = !filterType[2];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_hospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[3] = !filterType[3];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_bank.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[4] = !filterType[4];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_park.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[5] = !filterType[5];
                mMap.clear();
                PrintMarkers();
            }
        });

        checkbox_etc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterType[6] = !filterType[6];
                mMap.clear();
                PrintMarkers();
            }
        });

        Window window = myDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        //lp.x = 0;
        lp.y = 360;

        myDialog.show();

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

                                isLogined  = true;
                                loginedUserId = jsonResponse.getString("userId");
                                loginedUserNick = jsonResponse.getString("userNick");
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
                                isLogined  = false;
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

        Log.d("AutoTest","AUTOLOGIN 호출");
        isLogined  = false;
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        loginedUserId = jsonResponse.getString("userId");
                        loginedUserNick = jsonResponse.getString("userNick");
                        Log.d("AutoTest","JSON 성공!" + loginedUserNick);
                        isLogined  = true;
                        loginSuccess();
                    }
                    else{
                        Log.d("AutoTest","JSON 실패!");
                        isLogined  = false;
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

        Log.d("AutoTest","AutoLogin()  Return : " + isLogined);
        return isLogined;
    }

    private void loginSuccess(){
        // 첫번째 버튼인 로그인 버튼의 글자를 로그아웃으로 변경
        Menu menu = navigationView.getMenu();
        SubMenu submenu = menu.getItem(0).getSubMenu();
        submenu.getItem(0).setTitle("로그아웃");

        // 메세지를 출력
        View headerView = navigationView.getHeaderView(0);
        TextView emailText = (TextView) headerView.findViewById(R.id.textView_print_email);
        String string = loginedUserNick + "님 안녕하세요";

        Log.d("Auto","AUTOLOGIN : " + isLogined+"님 안녕하세요");
        emailText.setText(string);
    }

    private void logout(){
        isLogined = false;
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
        MarkerList.clear();
        LocationList.clear();
        mMap.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final DBLocation temp = new DBLocation();
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        temp.setId(jsonObject.getInt("id"));
                        temp.setName(jsonObject.getString("name"));
                        temp.setLatitude(jsonObject.getDouble("latitude"));
                        temp.setLongitude(jsonObject.getDouble("longitude"));
                        temp.setToilet(jsonObject.getInt("toilet"));
                        temp.setType(jsonObject.getInt("type"));
                        temp.setReport(jsonObject.getInt("report"));
                        LocationList.add(new DBLocation(temp));
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
        float color;
        int type;
        DBLocation loc;
        for(int i = 0; i < LocationList.size() ;i++){
            loc = LocationList.get(i);
            type = loc.getType();
            boolean filter = false;
            if(filterToilet == true && loc.getToilet()==1)filter=true; // 화장실 필터 켜졋고 화장실이 있는건물일 경우 출력
            if(filterType[type]) filter = true; // 필터 켜진 type의 건물일경우 출력
            if(!filter)continue;    //위 의 경우가 아닌경우 출력 안함
            switch (type){
                case 0://음식점
                    color = BitmapDescriptorFactory.HUE_RED;
                    break;
                case 1://카페
                    color = BitmapDescriptorFactory.HUE_VIOLET;
                    break;
                case 2://도서관
                    color = BitmapDescriptorFactory.HUE_YELLOW;
                    break;
                case 3://병원
                    color = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case 4://은행
                    color = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case 5://공원
                    color = BitmapDescriptorFactory.HUE_CYAN;
                    break;
                case 6://기타
                    color = BitmapDescriptorFactory.HUE_AZURE;
                    break;
                default:// 길찾기에서 생성된 임시 마커
                    color = BitmapDescriptorFactory.HUE_BLUE;
                    break;
            }

            LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
            Marker newmarker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(loc.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
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
        final int id = loc.getId();

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
            case 6:
                typeField.setText("기타");
                break;
            default:
                typeField.setText("검색된 마커");
                break;
        }
        if(loc.getToilet() == 1){
            toiletField.setText("화장실 있음");
        }else{
            toiletField.setText("화장실 없음");
        }
        reportField.setText("신고횟수 :" + loc.getReport()+ "회");

        button_report.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                myDialog.dismiss();
                if(isLogined){
                    Response.Listener<String> ReportListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    };
                    Log.d("AutoTest","ID  : " + id  +"에대해서 호출");
                    ReportRequest reportListener = new ReportRequest(id, ReportListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(reportListener);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("로그인이 필요한 기능입니다. 먼저 로그인 해주세요")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                }
            }
        });

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return false;
    }


    // 이하 4개는 LocationListener implements 하기위해서 선언만 해둠
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
