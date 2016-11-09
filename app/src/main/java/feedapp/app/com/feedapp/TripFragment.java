package feedapp.app.com.feedapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import feedapp.app.com.feedapp.util.AppConstant;
import feedapp.app.com.feedapp.util.FeedSharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TripFragment extends Fragment {

    /*ToDo: Variable Declaration */
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;

    private APIService mAPIService;
    private List<ClientDetailList> mClientDetailList=new ArrayList<>();
    private List<CarDetail> mCarDetailList=new ArrayList<>();
    private List<DriverDetail> mDriverDetailList=new ArrayList<>();
    private  List<GuestDetail> mGuestDetailList=new ArrayList<>();

    public TripFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trip, container, false);

        AppConstant.isAlreadyLoggedIn=true;

        mImageView=(ImageView) view.findViewById(R.id.toolbar_leftArrow);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),GetReviews.class));
                getActivity().finish();
            }
        });

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("PLEASE WAIT");
        mProgressDialog.setMessage("GETTING TRIPS");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Map<String,String> mTripMap=new HashMap<>();
        mTripMap.put("contactNo",new FeedSharedPreferences(getContext()).getValue("contact",""));

        mAPIService=AppConstant.setupRetrofit(AppConstant.baseURL);
        Call<ClientInfo> mClientInfoCall=mAPIService.getClientInfoCall(mTripMap);
        Log.e("url", "" + mClientInfoCall.request().url());
        mClientInfoCall.enqueue(new Callback<ClientInfo>() {
            @Override
            public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                mClientDetailList = response.body().getClientDetailList();
                if(mClientDetailList.size()>0){
                    for (int i = 0; i < mClientDetailList.size(); i++) {
                        mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                        mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                        mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("contact",new FeedSharedPreferences(getContext()).getValue("contact",""));
                    bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                    bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                    bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                    bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                    //TODO recycler view here
                    mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView_tripid);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(new TripAdapter(getActivity(), bundle));
                    mProgressDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "YOUR FEEDBACKS ARE UP TO DATE", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ClientInfo> call, Throwable t) {
                Toast.makeText(getContext(), "CONNECTION PROBLEM", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

        return view;
    }
}
