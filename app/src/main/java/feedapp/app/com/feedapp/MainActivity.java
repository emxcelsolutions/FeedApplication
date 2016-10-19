package feedapp.app.com.feedapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feedapp.app.com.feedapp.adapter.TripAdapter;
import feedapp.app.com.feedapp.model.CarDetail;
import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.DriverDetail;
import feedapp.app.com.feedapp.model.ClientDetailList;
import feedapp.app.com.feedapp.model.ClientInfo;
import feedapp.app.com.feedapp.model.GuestDetail;
import feedapp.app.com.feedapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /*ToDo: Variable Declaration */
    private Bundle mBundle;
    public static ImageView mImageView;
    public static String current_fragment="";

    private APIService mAPIService;
    private String baseURL="http://192.227.159.120:8080/";
    private String contact;
    private List<ClientDetailList> mClientDetailList=new ArrayList<>();
    private List<CarDetail> mCarDetailList=new ArrayList<>();
    private List<DriverDetail> mDriverDetailList=new ArrayList<>();
    private  List<GuestDetail> mGuestDetailList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*ToDo: toolbar initialization here*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBundle = getIntent().getExtras();
        contact=mBundle.getString("contact");

        mImageView=(ImageView) findViewById(R.id.toolbar_leftArrow);
        mImageView.setVisibility(View.GONE);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_fragment.equalsIgnoreCase("Get Review")){
                    mAPIService = LoginActivity.setupRetrofit(baseURL);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("contactNo", contact);
                    Call<ClientInfo> mCall = mAPIService.getClientInfoCall(params);
                    Log.e("url", "" + mCall.request().url());
                    mCall.enqueue(new Callback<ClientInfo>() {
                        @Override
                        public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                            mClientDetailList = response.body().getClientDetailList();
                            for (int i = 0; i < mClientDetailList.size(); i++) {
                                mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                                mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                                mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("contact",contact);
                            bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                            bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                            bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                            bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                            TripFragment mTripFragment = new TripFragment();
                            mTripFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.content_main, mTripFragment);
                            current_fragment="Trip";
                            fragmentTransaction.commit();
                        }

                        @Override
                        public void onFailure(Call<ClientInfo> call, Throwable t) {

                        }
                    });
                }else if(current_fragment.equalsIgnoreCase("Feedback")){
                    mAPIService = LoginActivity.setupRetrofit(baseURL);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("contactNo", contact);
                    Call<ClientInfo> mCall = mAPIService.getClientInfoCall(params);
                    Log.e("url", "" + mCall.request().url());
                    mCall.enqueue(new Callback<ClientInfo>() {
                        @Override
                        public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                            mClientDetailList = response.body().getClientDetailList();
                            for (int i = 0; i < mClientDetailList.size(); i++) {
                                mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                                mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                                mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                            bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                            bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                            bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                            TripFragment mTripFragment = new TripFragment();
                            mTripFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.content_main, mTripFragment);
                            current_fragment="Trip";
                            fragmentTransaction.commit();
                        }

                        @Override
                        public void onFailure(Call<ClientInfo> call, Throwable t) {

                        }
                    });
                }else if(current_fragment.equalsIgnoreCase("Car Review")){
                    initGetReviewFragment();
                }else if(current_fragment.equalsIgnoreCase("Driver Review")){
                    initGetReviewFragment();
                }
            }
        });
        /*ToDo: Drawerlayout initialization here*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*ToDo: NavigationView initialization here*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*TODO initTripFragment called here*/
        initTripFragment(mBundle);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(current_fragment.equalsIgnoreCase("Get Review")){
                mAPIService = LoginActivity.setupRetrofit(baseURL);
                Map<String, String> params = new HashMap<String, String>();
                params.put("contactNo", contact);
                Call<ClientInfo> mCall = mAPIService.getClientInfoCall(params);
                Log.e("url", "" + mCall.request().url());
                mCall.enqueue(new Callback<ClientInfo>() {
                    @Override
                    public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                        mClientDetailList = response.body().getClientDetailList();
                        for (int i = 0; i < mClientDetailList.size(); i++) {
                            mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                            mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                            mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("contact",contact);
                        bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                        bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                        bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                        bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                        TripFragment mTripFragment = new TripFragment();
                        mTripFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_main, mTripFragment);
                        current_fragment="Trip";
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onFailure(Call<ClientInfo> call, Throwable t) {

                    }
                });
            }else if(current_fragment.equalsIgnoreCase("Feedback")){
                mAPIService = LoginActivity.setupRetrofit(baseURL);
                Map<String, String> params = new HashMap<String, String>();
                params.put("contactNo", contact);
                Call<ClientInfo> mCall = mAPIService.getClientInfoCall(params);
                Log.e("url", "" + mCall.request().url());
                mCall.enqueue(new Callback<ClientInfo>() {
                    @Override
                    public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                        mClientDetailList = response.body().getClientDetailList();
                        for (int i = 0; i < mClientDetailList.size(); i++) {
                            mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                            mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                            mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                        bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                        bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                        bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                        TripFragment mTripFragment = new TripFragment();
                        mTripFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_main, mTripFragment);
                        current_fragment="Trip";
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onFailure(Call<ClientInfo> call, Throwable t) {

                    }
                });
            }else if(current_fragment.equalsIgnoreCase("Car Review")){
                initGetReviewFragment();
            }else if(current_fragment.equalsIgnoreCase("Driver Review")){
                initGetReviewFragment();
            }else {
                super.onBackPressed();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tripid) {
            initTripFragment(mBundle);
        } else if (id == R.id.nav_getreview) {
            //TODO get review fragment called here
            initGetReviewFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //TODO trip fragment called here
    private void initTripFragment(Bundle bundle) {
        mAPIService = LoginActivity.setupRetrofit(baseURL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("contactNo", contact);
        Call<ClientInfo> mCall = mAPIService.getClientInfoCall(params);
        Log.e("url", "" + mCall.request().url());
        mCall.enqueue(new Callback<ClientInfo>() {
            @Override
            public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                mClientDetailList = response.body().getClientDetailList();
                for (int i = 0; i < mClientDetailList.size(); i++) {
                    mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                    mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                    mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                }
                Bundle bundle = new Bundle();
                bundle.putString("contact",contact);
                bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                TripFragment mTripFragment = new TripFragment();
                mTripFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, mTripFragment);
                current_fragment="Trip";
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure(Call<ClientInfo> call, Throwable t) {

            }
        });
    }

    private void initGetReviewFragment(){
        GetReviewFragment getReviewFragment = new GetReviewFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, getReviewFragment);
        current_fragment="Get Review";
        fragmentTransaction.commit();
    }
}
