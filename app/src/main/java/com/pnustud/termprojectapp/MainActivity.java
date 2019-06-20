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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
    public double[][] place_info = {{35.231275, 129.082879},{35.231386, 129.082436},{35.231637, 129.081455}
            ,{35.231513, 129.081383} , {35.231477, 129.081204}, {35.231086, 129.081423}
            ,{35.230803, 129.081587} , {35.230587, 129.082129} , {35.231145, 129.080469} , {35.231669, 129.080701}
            ,{35.231823, 129.080314},{35.232585, 129.080685},{35.232882, 129.080737} //12
            ,{35.233113, 129.080681},{35.233416, 129.080801},{35.233364, 129.081251} //15
            ,{35.232862, 129.082085},{35.233143, 129.082157},{35.233560, 129.082301} //18
            ,{35.233742, 129.082428},{35.233602, 129.080856},{35.234078, 129.081076} //21
            ,{35.234185, 129.081100},{35.234312, 129.080601},{35.234550, 129.079400} //24
            ,{35.233719, 129.079536},{35.233752, 129.079292},{35.233749, 129.079109} //27
            ,{35.233758, 129.078925},{35.233811, 129.078678},{35.233934, 129.078020} //30
            ,{35.234951, 129.078359},{35.235036, 129.078395},{35.234811, 129.079448} //33
            ,{35.235883, 129.079803},{35.235691, 129.080098},{35.235323, 129.080876} //36
            ,{35.235052, 129.081630},{35.235023, 129.081862},{35.234247, 129.081303} //39
            ,{35.234211, 129.081898},{35.235531, 129.081866},{35.236558, 129.078873} //42
            ,{35.234101, 129.082636},{35.236303, 129.079119}
    };
    public int[][] adjacencyMatrix;
    private double dest_lat;
    private double dest_lng;

    private void pre_process(){
        adjacencyMatrix = new int[50][50];
        for(int i =0 ; i < 50 ; ++i){
            for(int j=0; j<50; ++j){
                adjacencyMatrix[i][j] = INF;
            }
        }
        adjacencyMatrix[0][1] =41;
        adjacencyMatrix[1][0]= 41;
        adjacencyMatrix[1][2] =92;
        adjacencyMatrix[2][1] =92;
        adjacencyMatrix[2][3] =15;
        adjacencyMatrix[3][2] = 15;
        adjacencyMatrix[3][4] = 17;
        adjacencyMatrix[4][3] = 17;

        adjacencyMatrix[4][5] = 43;
        adjacencyMatrix[5][4] = 43;

        adjacencyMatrix[5][3] = 45;
        adjacencyMatrix[3][5] = 45;

        adjacencyMatrix[5][6] = 38;
        adjacencyMatrix[6][5] = 38;
        adjacencyMatrix[6][7] = 55;
        adjacencyMatrix[7][6] =55;

        adjacencyMatrix[6][7] = 110;
        adjacencyMatrix[7][6] = 110;

        adjacencyMatrix[8][9] = 61;
        adjacencyMatrix[9][8] = 61;

        adjacencyMatrix[9][4] = 50;
        adjacencyMatrix[4][9] = 50;

        adjacencyMatrix[9][10] = 40;
        adjacencyMatrix[10][9] = 40;

        adjacencyMatrix[10][11] = 91;
        adjacencyMatrix[11][10] = 91;

        adjacencyMatrix[11][12] = 31;
        adjacencyMatrix[12][11] = 31;

        adjacencyMatrix[12][13] = 27;
        adjacencyMatrix[13][12] = 27;

        adjacencyMatrix[13][14] = 33;
        adjacencyMatrix[14][13] = 33;

        adjacencyMatrix[14][15] = 39;
        adjacencyMatrix[15][14] = 39;

        adjacencyMatrix[15][17] = 87;
        adjacencyMatrix[17][15] = 87;

        adjacencyMatrix[17][18] = 49;
        adjacencyMatrix[18][17] = 49;

        adjacencyMatrix[16][17] = 33;
        adjacencyMatrix[17][16] = 33;

        adjacencyMatrix[2][16] = 145;
        adjacencyMatrix[16][2] = 145;

        adjacencyMatrix[18][19] = 23;
        adjacencyMatrix[19][18] = 23;

        adjacencyMatrix[19][21] = 128;
        adjacencyMatrix[21][19] = 128;

        adjacencyMatrix[19][43] = 42;
        adjacencyMatrix[43][19] = 42;

        adjacencyMatrix[40][43] = 70;
        adjacencyMatrix[43][40] = 70;

        adjacencyMatrix[14][20] = 20;
        adjacencyMatrix[20][14] = 20;

        adjacencyMatrix[20][21] = 55;
        adjacencyMatrix[21][20] = 55;

        adjacencyMatrix[22][21] = 12;
        adjacencyMatrix[21][22] = 12;

        adjacencyMatrix[22][39] = 21;
        adjacencyMatrix[39][22] = 21;

        adjacencyMatrix[39][40] = 53;
        adjacencyMatrix[40][39] = 53;

        adjacencyMatrix[38][40] = 90;
        adjacencyMatrix[40][38] = 90;

        adjacencyMatrix[38][41] = 56;
        adjacencyMatrix[41][38] = 56;

        adjacencyMatrix[37][38] = 20;
        adjacencyMatrix[38][37] = 20;

        adjacencyMatrix[36][37] = 76;
        adjacencyMatrix[37][36] = 76;

        adjacencyMatrix[23][36] = 113;
        adjacencyMatrix[36][23] = 113;

        adjacencyMatrix[22][23] = 45;
        adjacencyMatrix[23][22] = 45;

        adjacencyMatrix[23][24] = 114;
        adjacencyMatrix[24][23] = 114;

        adjacencyMatrix[24][33] = 30;
        adjacencyMatrix[33][24] = 30;

        adjacencyMatrix[34][33] = 121;
        adjacencyMatrix[33][34] = 121;

        adjacencyMatrix[34][35] = 30;
        adjacencyMatrix[35][34] = 30;

        adjacencyMatrix[35][36] = 83;
        adjacencyMatrix[36][35] = 83;

        adjacencyMatrix[24][27] = 92;
        adjacencyMatrix[27][24] = 92;

        adjacencyMatrix[27][26] = 16;
        adjacencyMatrix[26][27] = 16;

        adjacencyMatrix[26][25] = 20;
        adjacencyMatrix[25][26] = 20;

        adjacencyMatrix[25][14] = 100;
        adjacencyMatrix[14][25] = 100;

        adjacencyMatrix[28][27] = 14;
        adjacencyMatrix[27][28] = 14;

        adjacencyMatrix[28][29] = 27;
        adjacencyMatrix[29][28] = 27;

        adjacencyMatrix[29][30] = 61;
        adjacencyMatrix[30][29] = 61;

        adjacencyMatrix[30][31] = 117;
        adjacencyMatrix[31][30] = 117;

        adjacencyMatrix[31][32] = 9;
        adjacencyMatrix[32][31] = 9;

        adjacencyMatrix[32][33] =100 ;
        adjacencyMatrix[33][32] = 100;

        adjacencyMatrix[32][42] = 174;
        adjacencyMatrix[42][32] = 174;

        adjacencyMatrix[42][44] = 25;
        adjacencyMatrix[44][42] = 25;

        adjacencyMatrix[34][44] = 90;
        adjacencyMatrix[44][34] = 90;
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
                    geoLocate(search);
                }
                return false;
            }
        });
    }

    private void geoLocate(String searchString) {
        //String searchString = searchBox.getText().toString();

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
            find_path(dest_lat, dest_lng);
        }
    }

    private void find_path(double destination_lat, double destination_lng){
        mMap.clear();
        PrintMarkers();
        LatLngBounds mMapBoundary = new LatLngBounds(
                new LatLng( (destination_lat < lat ?  destination_lat : lat ) , (destination_lng < lng ?  destination_lng : lng))
                , new LatLng( ( destination_lat > lat ?  destination_lat : lat ) , (destination_lng > lng ?  destination_lng : lng)) );
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,150 ));

        double[] dest_info = {destination_lat, destination_lng};
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
                    /*Toast.makeText(MainActivity.this,
                            point.latitude + ", " + point.longitude,
                            Toast.LENGTH_SHORT).show();*/
                DBLocation loc = new DBLocation(-1, "선택된 위치", point.latitude , point.longitude, 0, 7, 0);
                LocationList.add(loc);
                float color = BitmapDescriptorFactory.HUE_BLUE;
                LatLng latlng = new LatLng(point.latitude , point.longitude);
                Marker newmarker = mMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(loc.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
                newmarker.setTag(LocationList.size()-1);
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
                locationRegister(lat, lng);
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

    private void locationRegister(final double loc_lat, final double loc_lng){
        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.location_register_dialog);

        final EditText nameText = (EditText)myDialog.findViewById(R.id.location_dialog_name);
        final Spinner spinner = (Spinner)myDialog.findViewById(R.id.location_dialog_spinner);
        final CheckBox toiletBox = (CheckBox)myDialog.findViewById(R.id.location_dialog_toilet);
        TextView latlngBox = (TextView)myDialog.findViewById(R.id.location_dialog_textview);
        Button registerButton = (Button)myDialog.findViewById(R.id.location_dialog_reg);

        latlngBox.setText("대상 위치: " + loc_lat + ", " + loc_lng);
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
                                builder.setMessage("장소가 추가되었습니다")
                                        .setPositiveButton("확인", null)
                                        .create()
                                        .show();
                                myDialog.dismiss();
                                MapUpdate();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("장소추가에 실패했습니다.")
                                        .setNegativeButton("확인", null)
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LocationRegisterRequest registerRequest = new LocationRegisterRequest(locName, loc_lat,loc_lng,locToilet, locType, responseListener);
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
            BitmapDescriptor markericon;
            if(filterToilet == true && loc.getToilet()==1)filter=true; // 화장실 필터 켜졋고 화장실이 있는건물일 경우 출력
            if(filterType[type]) filter = true; // 필터 켜진 type의 건물일경우 출력
            if(!filter)continue;    //위 의 경우가 아닌경우 출력 안함
            switch (type){
                case 0://음식점
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_0_food);
                    break;
                case 1://카페
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_1_coffee);
                    break;
                case 2://도서관
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_2_library);
                    break;
                case 3://병원
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_3_hospital);
                    break;
                case 4://은행
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_4_bank);
                    break;
                case 5://공원
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_5_park);
                    break;
                case 6://기타
                    markericon = BitmapDescriptorFactory.fromResource(R.drawable.icon_6_etc);
                    break;
                default:// 길찾기에서 생성된 임시 마커
                    color = BitmapDescriptorFactory.HUE_BLUE;
                    markericon = BitmapDescriptorFactory.defaultMarker(color);
                    break;
            }

            LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
            Marker newmarker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(loc.getName())
                    .icon(markericon)
            );
            newmarker.setTag(i);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final TextView nameField,typeField,toiletField,reportField;
        final Button button_report, button_navigation;

        final Context context = this;
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.location_marker_dialog);

        button_report = (Button) myDialog.findViewById(R.id.location_marker_dialog_reportButton);
        button_navigation = (Button) myDialog.findViewById(R.id.location_marker_dialog_naviButton);

        nameField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_name);
        typeField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_type);
        toiletField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_toilet);
        reportField = (TextView) myDialog.findViewById(R.id.location_marker_dialog_report);

        nameField.setText(marker.getTitle());
        int tag = (int)marker.getTag();
        DBLocation loc = LocationList.get(tag);
        final int id = loc.getId();
        final double marker_lat = loc.getLatitude();
        final double marker_lng = loc.getLongitude();
        int markerType = loc.getType();

        reportField.setText("신고횟수 :" + loc.getReport()+ "회");
        switch(markerType){
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
                typeField.setText("위도 :"+loc.getLatitude());
                reportField.setText("등록되지 않음");
                break;
        }
        if(loc.getToilet() == 1){
            toiletField.setText("화장실 있음");
        }else if(markerType == 7){
            toiletField.setText("경도 :" +loc.getLongitude());
        }else{
            toiletField.setText("화장실 없음");
        }
        button_navigation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                dest_lat = marker_lat;
                dest_lng = marker_lng;
                find_path(marker_lat,marker_lng);
            }
        });

        if(markerType == 7){
            button_report.setText("장소등록");
            button_report.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    locationRegister(marker_lat, marker_lng);
                    myDialog.dismiss();
                }
            });
        }else{
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

        }

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
