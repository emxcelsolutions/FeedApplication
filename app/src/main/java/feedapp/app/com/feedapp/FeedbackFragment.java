package feedapp.app.com.feedapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feedapp.app.com.feedapp.model.CarDetail;
import feedapp.app.com.feedapp.model.ClientDetailList;
import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.DriverDetail;
import feedapp.app.com.feedapp.model.ClientInfo;
import feedapp.app.com.feedapp.model.GuestDetail;
import feedapp.app.com.feedapp.model.feedbackresponse.FeedbackResponse;
import feedapp.app.com.feedapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedbackFragment extends Fragment {

    /*ToDo: Variable Declaration */
    private View mView;
    private EditText medittext_jdate, medittext_jplace, medittext_drivername, medittext_contactnumber, medittext_carno, medittext_carname, medittext_remark;
    private RatingBar mrating_drivingstar, mrating_driverbehaviour, mrating_driverperform, mrating_carcandition, mrating_overservice;
    private Button mButton_submit;
    private TextView mTextView_tripid;
    private List<ClientDetailList> mClientDetailLists;
    private ClientDetailList mClientDetailList;
    private List<CarDetail> mCarDetails;
    private CarDetail carDetail;
    private List<DriverDetail> mDriverDetails;
    private DriverDetail driverDetail;
    private String remarks;
    private String baseURL="http://192.227.159.120:8080/";
    private float driverbehaviourRating;
    private float drivingStarRating;
    private float driverPerformanceRating;
    private float carConditionRating;
    private float overallServiceRating;
    private  APIService mApiService;
    private TextView mTextViewToolbar;
    private ImageView mImageView;
    private ScrollView mScrollView;
    private String mFeedbackList;/*=new ArrayList<>();*/
    private String feed_response;
    private String tripID;
    private String contact;
    private  List<GuestDetail> mGuestDetailList=new ArrayList<>();


    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_feedback, container, false);

        MainActivity.mImageView.setVisibility(View.VISIBLE);

        //TODO initialization method called here
        initialization();

        //TODO Getting lists from bundle
        Bundle mBundle=getArguments();
        contact=mBundle.getString("contact");
        int position=mBundle.getInt("position");
        mClientDetailLists=mBundle.getParcelableArrayList("ClientDetailList");
        mClientDetailList=mClientDetailLists.get(position);
        mCarDetails=mBundle.getParcelableArrayList("CarDetailList");
        carDetail=mCarDetails.get(position);
        mDriverDetails=mBundle.getParcelableArrayList("DriverDetailList");
        driverDetail=mDriverDetails.get(position);

        //TODO setting texts of trip details
        mTextView_tripid.setText("TRANSACTION ID "+mClientDetailList.getTripID());
        medittext_jdate.setText(mClientDetailList.getStartDate().toString());
        medittext_jplace.setText(mClientDetailList.getPlaceName().toString());
        medittext_drivername.setText(driverDetail.getDriName().toString());
        medittext_contactnumber.setText(driverDetail.getDriContact().toString());
        medittext_carno.setText(carDetail.getCarNo().toString());
        medittext_carname.setText(carDetail.getCarName());

        //TODO rating bar click events method
        ratingbarClickEvents();

        remarks=medittext_remark.getText().toString();

        /*Todo: grtting TripID*/
        tripID=mClientDetailList.getTripID();

        //TODO submit button click event here
        mButton_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setupRetrofit();
                mApiService=setupRetrofit(baseURL);

                Map<String, String> data = new HashMap<>();
                data.put("tripID",String.valueOf(tripID));
                data.put("drivingRating", String.valueOf(drivingStarRating));
                data.put("driverBehaviourRating", String.valueOf(driverbehaviourRating));
                data.put("driverTestingRating",String.valueOf(driverPerformanceRating));
                data.put("carConditionRating",String.valueOf(carConditionRating));
                data.put("overallServiceRating",String.valueOf(overallServiceRating));
                data.put("remark",remarks);

                Call<FeedbackResponse> mCall=mApiService.getFeedbackResponse(data);
                Log.e("url", ""+mCall.request().url());
                mCall.enqueue(new Callback<FeedbackResponse>() {
                    @Override
                    public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {

                        try {
                            mFeedbackList=response.body().getStatus();
                        }catch (Exception e) {
                            Toast.makeText(getContext(), "Sorry... Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FeedbackResponse> call, Throwable t) {

                    }
                });
                new AlertDialog.Builder(getContext())
                        .setTitle("Successfull")
                        .setMessage("Thank You For Your Valuable Feedback")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mApiService = LoginActivity.setupRetrofit(baseURL);
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("contactNo", contact);
                                Call<ClientInfo> mCall = mApiService.getClientInfoCall(params);
                                Log.e("url", "" + mCall.request().url());
                                mCall.enqueue(new Callback<ClientInfo>() {
                                    @Override
                                    public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                                        mClientDetailLists = response.body().getClientDetailList();
                                        for (int i = 0; i < mClientDetailLists.size(); i++) {
                                            mGuestDetailList.add(mClientDetailLists.get(i).getGuestDetail());
                                            mCarDetails.add(mClientDetailLists.get(i).getCarDetail());
                                            mDriverDetails.add(mClientDetailLists.get(i).getCarDetail().getDriverDetail());
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contact",contact);
                                        bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailLists);
                                        bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                                        bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetails);
                                        bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetails);
                                        TripFragment mTripFragment = new TripFragment();
                                        mTripFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.content_main, mTripFragment);
                                        MainActivity.current_fragment="Trip";
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onFailure(Call<ClientInfo> call, Throwable t) {

                                    }
                                });
                            }
                        })
                        .show();
            }
        });
        return mView;
    }

    /*ToDo: method for setting up rertrofit*/
    private APIService setupRetrofit(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        mApiService =retrofit.create(APIService.class);
        return mApiService;
    }

    //TODO initialization method implementation here
    private void initialization() {
        mTextView_tripid=(TextView) mView.findViewById(R.id.mtextview_transationid);
        medittext_jdate = (EditText) mView.findViewById(R.id.medittext_jdate);
        medittext_jplace = (EditText) mView.findViewById(R.id.medittext_jplace);
        medittext_drivername = (EditText) mView.findViewById(R.id.medittext_drivername);
        medittext_contactnumber = (EditText) mView.findViewById(R.id.medittext_contactnumber);
        medittext_carno = (EditText) mView.findViewById(R.id.medittext_carno);
        medittext_carname = (EditText) mView.findViewById(R.id.medittext_carname);
        medittext_remark = (EditText) mView.findViewById(R.id.medittext_remark);
        mrating_drivingstar = (RatingBar) mView.findViewById(R.id.mrating_drivingstar);
        mrating_driverbehaviour = (RatingBar) mView.findViewById(R.id.mrating_driverbehaviour);
        mrating_driverperform = (RatingBar) mView.findViewById(R.id.mrating_driverperform);
        mrating_carcandition = (RatingBar) mView.findViewById(R.id.mrating_carcandition);
        mrating_overservice = (RatingBar) mView.findViewById(R.id.mrating_overservice);
        mButton_submit = (Button) mView.findViewById(R.id.mButton_submit);
        mScrollView = (ScrollView)mView.findViewById(R.id.sv_feed_activity);

    }

    //TODO ratingbar onClick here
    private void ratingbarClickEvents() {
        mrating_drivingstar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                drivingStarRating=v;
            }
        });

        mrating_driverbehaviour.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                driverbehaviourRating=v;
            }
        });

        mrating_driverperform.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                driverPerformanceRating=v;
            }
        });

        mrating_carcandition.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                carConditionRating=v;
            }
        });

        mrating_overservice.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                overallServiceRating=v;
            }
        });
    }

}
