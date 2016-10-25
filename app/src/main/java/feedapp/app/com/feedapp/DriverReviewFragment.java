package feedapp.app.com.feedapp;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.DriverDetailList;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.DriverTypeByData;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.FeedbackList;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.SearchDriverName;
import feedapp.app.com.feedapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverReviewFragment extends Fragment {

    /*ToDo: Variable Declaration */
    private View mView;
    private EditText mOverallFeedback;
    private EditText mFromdate;
    private EditText mTodate;
    private String fdate,tdate;
    private ImageView mSearch;
    private Spinner mSpinner;
    private RatingBar mRbDriving;
    private RatingBar mRbDriverBehaviour;
    private RatingBar mRbDriveronPerfo;
    private RatingBar mRbCarCondition;
    private RatingBar mRbOverallService;
    private RatingBar mRbAverageRating;
    private TextView mTextView_availability;

    private String baseURL = "http://192.227.159.120:8080/";
    private APIService mApiService;

    private  List<FeedbackList> mFeedbackList;
    private  List<DriverDetailList> mDriverReviewList = new ArrayList<>();

    private boolean touch=false;
    private  int total_trips;

    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    Date from_date,to_date;
    long total_diff;

    private ProgressDialog pDialog;
    private Call<DriverTypeByData> mCall;
    public DriverReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_driver_review, container, false);

        //TODO intialization() called here
        initialization();
        mSearch.setEnabled(false);

        /*ToDo: settingup retrofit with api */
        mApiService = LoginActivity.setupRetrofit(baseURL);
        mCall = mApiService.getDriverTypeByDataCall();
        Log.e("url", "" + mCall.request().url());
        DriverReviewAsyncTask mDriverReviewAsyncTask=new DriverReviewAsyncTask();
        mDriverReviewAsyncTask.execute();


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
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sp_selected=mSpinner.getSelectedItem().toString();
                mOverallFeedback.setText("OVERALL FEEDBACK");
                //TODO date validation method called here
                if(!sp_selected.equalsIgnoreCase("PLEASE SELECT DRIVER NAME")){
                    fdate = mFromdate.getText().toString();
                    tdate = mTodate.getText().toString();

                    if(dateValidation() || (fdate.equals("") && tdate.equals(""))){

                        mApiService=LoginActivity.setupRetrofit(baseURL);
                        Map<String, String> data = new HashMap<>();
                        data.put("type","driver");
                        data.put("fromDate",fdate);
                        data.put("toDate",tdate);
                        data.put("driName",sp_selected);

                        Call<SearchDriverName> mCall=mApiService.getSearchDriverNameCall(data);
                        Log.e("url", ""+mCall.request().url());
                        mCall.enqueue(new Callback<SearchDriverName>() {
                            @Override
                            public void onResponse(Call<SearchDriverName> call, Response<SearchDriverName> response) {

                                try {
                                    mFeedbackList=response.body().getFeedbackList();
                                    total_trips=mFeedbackList.size();
                                    if(total_trips==0)
                                        Toast.makeText(getContext(), "No Reviews Found", Toast.LENGTH_SHORT).show();
                                    float driving=0,d_behaviour=0,d_performance=0,c_condition=0,overallService=0,average=0;
                                    long available=0;
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
                                    for(int i=0;i<total_trips;i++)
                                    {
                                        driving+=Float.parseFloat(mFeedbackList.get(i).getDrivingRating().toString());
                                        d_behaviour+=Float.parseFloat(mFeedbackList.get(i).getDriverBehaviourRating().toString());
                                        d_performance+=Float.parseFloat(mFeedbackList.get(i).getDriverTestingRating().toString());
                                        c_condition+=Float.parseFloat(mFeedbackList.get(i).getCarConditionRating().toString());
                                        overallService+=Float.parseFloat(mFeedbackList.get(i).getOverallServiceRating().toString());
                                        average+=Float.parseFloat(mFeedbackList.get(i).getAverageRating().toString());
                                        available+=(simpleDateFormat.parse(mFeedbackList.get(i).getEndDate().replace("-","/")).getTime()-simpleDateFormat.parse(mFeedbackList.get(i).getStartDate().replace("-","/")).getTime())/(24 * 60 * 60 * 1000)+1;
                                    }
                                    mRbDriving.setRating(driving/total_trips);
                                    mRbDriverBehaviour.setRating(d_behaviour/total_trips);
                                    mRbDriveronPerfo.setRating(d_performance/total_trips);
                                    mRbCarCondition.setRating(c_condition/total_trips);
                                    mRbOverallService.setRating(overallService/total_trips);
                                    mRbAverageRating.setRating(average/total_trips);
                                    if(!(fdate.equals("") && tdate.equals(""))) {
                                        total_diff = ((to_date.getTime() - from_date.getTime())/(24 * 60 * 60 * 1000))+1;
                                        long not_available=total_diff-available;
                                        mTextView_availability.setText("Not Available For " + not_available + " Days Out Of "+ total_diff + " Days");
                                    }
                                    setOverallFeedback(average/total_trips);
                                }catch (Exception e) {
                                    Toast.makeText(getContext(), "Sorry..Unable To Get Response..", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchDriverName> call, Throwable t) {

                            }
                        });
                    }
                    else{

                        Toast.makeText(getContext(), "Please Select Valid Date", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "Please Select Driver Name", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return mView;
    }

    /*ToDo: logic for creating overall feedback*/
    private void setOverallFeedback(float average) {
        if(average<=1){
            mOverallFeedback.setText("VERY BAD");
        }else if(average>1 && average<=2){
            mOverallFeedback.setText("POOR");
        }else if(average>2 && average<=3){
            mOverallFeedback.setText("AVERAGE");
        }else if(average>3 && average<=4){
            mOverallFeedback.setText("GOOD");
        }else if(average>4 && average<=5){
            mOverallFeedback.setText("VERY GOOD");
        }
    }

    //TODO date validation here
    private boolean dateValidation() {
        try {

            Date mFromDate = dateFormatter.parse(mFromdate.getText().toString().replace("/","-"));
            Date mToDate = dateFormatter.parse(mTodate.getText().toString().replace("/","-"));

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

    //TODO initialization method here
    private void initialization() {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mSpinner = (Spinner) mView.findViewById(R.id.sp_driver);
        mOverallFeedback = (EditText) mView.findViewById(R.id.met_overallFeedbackDriverSide);
        mSearch = (ImageView) mView.findViewById(R.id.miv_searchDriverSide);

        mRbDriving = (RatingBar) mView.findViewById(R.id.mrb_drivingDriverSide);
        mRbDriving.setIsIndicator(true);

        mRbDriverBehaviour = (RatingBar) mView.findViewById(R.id.mrb_driverBehaviourDriverSide);
        mRbDriverBehaviour.setIsIndicator(true);

        mRbDriveronPerfo = (RatingBar) mView.findViewById(R.id.mrb_driversOnPerformanceTimeDriverSide);
        mRbDriveronPerfo.setIsIndicator(true);

        mRbCarCondition = (RatingBar) mView.findViewById(R.id.mrb_carConditionDriverSide);
        mRbCarCondition.setIsIndicator(true);

        mRbOverallService = (RatingBar) mView.findViewById(R.id.mrb_overallServicesDriverSide);
        mRbOverallService.setIsIndicator(true);

        mRbAverageRating = (RatingBar) mView.findViewById(R.id.mrb_averageRatingDriverSide);
        mRbAverageRating.setIsIndicator(true);

        mTextView_availability=(TextView)mView.findViewById(R.id.tv_driver_availability);
    }

    //TODO Date Picker Dialog Here
    private void datePickerShow() {

        mFromdate = (EditText) mView.findViewById(R.id.met_fromDateDriverSide);
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

        mTodate = (EditText) mView.findViewById(R.id.met_toDateDriverSide);
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
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
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
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                mTodate.setText(simpleDateFormat.format(newDate.getTime()));
                try {
                    to_date=simpleDateFormat.parse(mTodate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    //ToDo: AsyncTask for driverReviewFragment
    private class DriverReviewAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            mCall.enqueue(new Callback<DriverTypeByData>() {
                @Override
                public void onResponse(Call<DriverTypeByData> call, Response<DriverTypeByData> response) {
                    try {
                        mDriverReviewList = response.body().getDriverDetailList();
                        final List<String> driverList=new ArrayList<String>();
                        driverList.add(0,"PLEASE SELECT DRIVER NAME");
                        for (int i = 0; i < mDriverReviewList.size(); i++) {
                            driverList.add(mDriverReviewList.get(i).getDriName());
                        }

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, driverList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner.setAdapter(adapter);
                        mSearch.setEnabled(true);
                        mSpinner.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(!touch)
                                {
                                    driverList.remove(0);
                                    adapter.notifyDataSetChanged();
                                    touch=true;
                                }
                                return false;
                            }
                        });
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<DriverTypeByData> call, Throwable t) {

                }

            });


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(pDialog.isShowing())
                pDialog.cancel();
        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please Wait");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
