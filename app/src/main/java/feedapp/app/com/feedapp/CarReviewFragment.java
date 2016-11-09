package feedapp.app.com.feedapp;
//Car Fragment

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.CarDetailList;
import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.CarTypeByData;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.FeedbackList;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.SearchDriverName;
import feedapp.app.com.feedapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarReviewFragment extends Fragment {

    /*ToDo: Variable Declaration */
    private View mView;
    private EditText mOverallFeedback;
    private EditText mFromdate;
    private EditText mTodate;
    private ImageView mSerach;
    private RatingBar mRbDriving;
    private RatingBar mRbDriverBehaviour;
    private RatingBar mRbDriveronPerfo;
    private RatingBar mRbCarCondition;
    private RatingBar mRbOverallService;
    private RatingBar mRbAverageRating;
    private Spinner mSpinner_car;
    private TextView mTextView_availability;
    private ImageView mImageView;

    private ScrollView mScrollView;
    private String baseURL = "http://192.227.159.120:8080/";
    private String fdate, tdate;
    private boolean touch = false;
    private int total_trips;

    private APIService mApiService;
    private List<CarDetailList> mCarReviewList = new ArrayList<>();
    private List<FeedbackList> mFeedbackList;

    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    Date from_date,to_date;
    long total_diff;
    long available;

    private ProgressDialog mDialog;
    private Call<CarTypeByData> mCall;
    public CarReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_car_review, container, false);

        //TODO intialization() called here
        initialization();
        mSerach.setEnabled(false);

        /*ToDo: settingup retrofit with api */
        mApiService = LoginActivity.setupRetrofit(baseURL);

        mCall = mApiService.getCarTypeByDataCall();
        Log.e("url", "" + mCall.request().url());
        CarReviewAsyncTask mCarReviewAsyncTask=new CarReviewAsyncTask();
        mCarReviewAsyncTask.execute();

        //TODO date picker dialog show method called here
        datePickerShow();

        //TODO set date and time method called here
        setDateTimeField();

        /*if(mSpinner_car.getCount()==0){
            mSerach.setEnabled(false);
        }
        else if(mSpinner_car.getCount()!=0){
            mSerach.setEnabled(true);
        }*/

        //TODO search button onClick() here
        mSerach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO date validation method called here
                String sp_selected = mSpinner_car.getSelectedItem().toString();
                mOverallFeedback.setText("OVERALL FEEDBACK");
                //TODO date validation method called here
                if (!sp_selected.equalsIgnoreCase("PLEASE SELECT CAR NUMBER")) {
                    fdate = mFromdate.getText().toString();
                    tdate = mTodate.getText().toString();

                    if (dateValidation() || (fdate.equals("") && tdate.equals(""))) {
                        mApiService = LoginActivity.setupRetrofit(baseURL);
                        Map<String, String> data = new HashMap<>();
                        data.put("type", "car");
                        data.put("fromDate", fdate);
                        data.put("toDate", tdate);
                        data.put("carNo", sp_selected);

                        Call<SearchDriverName> mCall = mApiService.getSearchDriverNameCall(data);
                        Log.e("url", "" + mCall.request().url());
                        mCall.enqueue(new Callback<SearchDriverName>() {
                            @Override
                            public void onResponse(Call<SearchDriverName> call, Response<SearchDriverName> response) {

                                try {
                                    mFeedbackList = response.body().getFeedbackList();
                                    total_trips = mFeedbackList.size();
                                    if(total_trips==0)
                                        Toast.makeText(getContext(), "No Reviews Found", Toast.LENGTH_SHORT).show();
                                    float driving = 0, d_behaviour = 0, d_performance = 0, c_condition = 0, overallService = 0, average = 0;
                                    long available=0;
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
                                    for (int i = 0; i < total_trips; i++) {
                                        driving += Float.parseFloat(mFeedbackList.get(i).getDrivingRating().toString());
                                        d_behaviour += Float.parseFloat(mFeedbackList.get(i).getDriverBehaviourRating().toString());
                                        d_performance += Float.parseFloat(mFeedbackList.get(i).getDriverTestingRating().toString());
                                        c_condition += Float.parseFloat(mFeedbackList.get(i).getCarConditionRating().toString());
                                        overallService += Float.parseFloat(mFeedbackList.get(i).getOverallServiceRating().toString());
                                        average += Float.parseFloat(mFeedbackList.get(i).getAverageRating().toString());
                                        available+=(simpleDateFormat.parse(mFeedbackList.get(i).getEndDate().replace("-","/")).getTime()-simpleDateFormat.parse(mFeedbackList.get(i).getStartDate().replace("-","/")).getTime())/(24 * 60 * 60 * 1000)+1;
                                    }
                                    mRbDriving.setRating(driving / total_trips);
                                    mRbDriverBehaviour.setRating(d_behaviour / total_trips);
                                    mRbDriveronPerfo.setRating(d_performance / total_trips);
                                    mRbCarCondition.setRating(c_condition / total_trips);
                                    mRbOverallService.setRating(overallService / total_trips);
                                    mRbAverageRating.setRating(average / total_trips);
                                    if(!(fdate.equals("") && tdate.equals(""))) {
                                        total_diff = ((to_date.getTime() - from_date.getTime())/(24 * 60 * 60 * 1000))+1;
                                        long not_available=total_diff-available;
                                        mTextView_availability.setText("Not Available For " + not_available + " Days Out Of "+ total_diff + "Days");
                                    }
                                    setOverallFeedback(average / total_trips);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Sorry..Unable To Get Response..", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchDriverName> call, Throwable t) {

                            }
                        });
                    } else {

                        Toast.makeText(getContext(), "Please Select Valid Date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please Select Car Number", Toast.LENGTH_SHORT).show();

                }

            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),GetReviews.class));
                getActivity().finish();
            }
        });

        return mView;
    }

    //TODO date validation here
    private boolean dateValidation() {
        try {

            Date mFromDate = dateFormatter.parse(mFromdate.getText().toString().replace("/", "-"));
            Date mToDate = dateFormatter.parse(mTodate.getText().toString().replace("/", "-"));

            if (mFromDate.compareTo(mToDate) <= 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*ToDo: logic for creating overall feedback*/
    private void setOverallFeedback(float average) {
        if (average <= 1) {
            mOverallFeedback.setText("VERY BAD");
        } else if (average > 1 && average <= 2) {
            mOverallFeedback.setText("POOR");
        } else if (average > 2 && average <= 3) {
            mOverallFeedback.setText("AVERAGE");
        } else if (average > 3 && average <= 4) {
            mOverallFeedback.setText("GOOD");
        } else if (average > 4 && average <= 5) {
            mOverallFeedback.setText("VERY GOOD");
        }
    }

    //TODO initialization method here
    private void initialization() {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        mScrollView = (ScrollView) mView.findViewById(R.id.sv_clientReviewScreen);
        mFromdate = (EditText) mView.findViewById(R.id.met_fromDate);
        mTodate = (EditText) mView.findViewById(R.id.met_toDate);
        mOverallFeedback = (EditText) mView.findViewById(R.id.met_overallFeedback);
        mSpinner_car = (Spinner) mView.findViewById(R.id.sp_carno);
        mSerach = (ImageView) mView.findViewById(R.id.miv_search);

        mRbDriving = (RatingBar) mView.findViewById(R.id.mrb_driving);
        mRbDriving.setIsIndicator(true);

        mRbDriverBehaviour = (RatingBar) mView.findViewById(R.id.mrb_driverBehaviour);
        mRbDriverBehaviour.setIsIndicator(true);

        mRbDriveronPerfo = (RatingBar) mView.findViewById(R.id.mrb_driversOnPerformanceTime);
        mRbDriveronPerfo.setIsIndicator(true);

        mRbCarCondition = (RatingBar) mView.findViewById(R.id.mrb_carCondition);
        mRbCarCondition.setIsIndicator(true);

        mRbOverallService = (RatingBar) mView.findViewById(R.id.mrb_overallServices);
        mRbOverallService.setIsIndicator(true);

        mRbAverageRating = (RatingBar) mView.findViewById(R.id.mrb_averageRating);
        mRbAverageRating.setIsIndicator(true);

        mTextView_availability=(TextView)mView.findViewById(R.id.tv_car_availability);
        mImageView=(ImageView) mView.findViewById(R.id.toolbar_leftArrow);
    }

    //TODO Date Picker Dialog Here
    private void datePickerShow() {

        mFromdate.setInputType(InputType.TYPE_NULL);
        mFromdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus = true) {
                    fromDatePickerDialog.show();
                    v.clearFocus();
                }
            }
        });


        mTodate.setInputType(InputType.TYPE_NULL);
        mTodate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus = true) {
                    toDatePickerDialog.show();
                    v.clearFocus();
                }
            }
        });
    }

    //TODO setting date and time on edit text here
    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                mFromdate.setText(simpleDateFormat.format(newDate.getTime()));
                try {
                    from_date=simpleDateFormat.parse(mFromdate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                mTodate.setText(simpleDateFormat.format(newDate.getTime()));
                try {
                    to_date=simpleDateFormat.parse(mTodate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    //ToDo: AsyncTask for carReviewFragment
    private class CarReviewAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            mCall.enqueue(new Callback<CarTypeByData>() {
                @Override
                public void onResponse(Call<CarTypeByData> call, Response<CarTypeByData> response) {
                    try {

                        mCarReviewList = response.body().getCarDetailList();
                        final List<String> carList = new ArrayList<String>();
                        carList.add(0, "PLEASE SELECT CAR NUMBER");

                        for (int i = 0; i < mCarReviewList.size(); i++) {
                            carList.add(mCarReviewList.get(i).getCarNo());
                        }
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, carList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner_car.setAdapter(adapter);
                        mSerach.setEnabled(true);
                        mSpinner_car.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (!touch) {
                                    carList.remove(0);
                                    adapter.notifyDataSetChanged();
                                    touch = true;
                                }
                                return false;
                            }
                        });
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<CarTypeByData> call, Throwable t) {

                }

            });

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(mDialog.isShowing())
                mDialog.cancel();
        }

        @Override
        protected void onPreExecute() {

            mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Please Wait");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
