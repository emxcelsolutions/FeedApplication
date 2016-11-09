package feedapp.app.com.feedapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feedapp.app.com.feedapp.model.CarDetail;
import feedapp.app.com.feedapp.model.ClientDetailList;
import feedapp.app.com.feedapp.model.ClientInfo;
import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.DriverDetail;
import feedapp.app.com.feedapp.model.GuestDetail;
import feedapp.app.com.feedapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    /*ToDo: Variable Declaration */
    private TextInputLayout mTextInputLayout;
    private EditText mEditText_Contact;
    private String contact;
    private Button mButton_Login;
    private APIService mAPIService;
    private List<ClientDetailList> mClientDetailList=new ArrayList<>();
    private List<CarDetail> mCarDetailList=new ArrayList<>();
    private List<DriverDetail> mDriverDetailList=new ArrayList<>();
    private  List<GuestDetail> mGuestDetailList=new ArrayList<>();
    private String baseURL="http://192.227.159.120:8080/";
    private   Call<ClientInfo> mCall;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO initialization() called here
        initialization();

        //TODO login button click event here
        mButton_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    contact = mEditText_Contact.getText().toString();
                if (!contact.equals("")) {
                    if (contact.length() == 10) {
                        mAPIService = setupRetrofit(baseURL);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("contactNo", contact);
                        mCall = mAPIService.getClientInfoCall(params);
                        Log.e("url", "" + mCall.request().url());
                        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                        loginAsyncTask.execute();

                    } else {
                        mTextInputLayout.setError("PLEASE ENTER 10 DIGIT MOBILE NUMBER");
                    }
                } else {
                    mTextInputLayout.setError("PLEASE ENTER CONTACT NUMBER");
                }

            }
                else {
                    Toast.makeText(LoginActivity.this, "Please check your internet connection ", Toast.LENGTH_SHORT).show();
                }
        }
        });
    }

    //TODO initialization method here
    private void initialization() {
        mEditText_Contact=(EditText) findViewById(R.id.et_contact);
        mButton_Login=(Button) findViewById(R.id.mButton_login);
        mTextInputLayout=(TextInputLayout) findViewById(R.id.ti_contact);
    }

    //TODO SetupRetrofit Method here
    public static APIService setupRetrofit(String url) {
        Retrofit mRetrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        APIService mAPIService=mRetrofit.create(APIService.class);
        return mAPIService;
    }

    /*ToDo: Networrk checking method*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //ToDo: AsyncTask for LoginActivity
    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mCall.enqueue(new Callback<ClientInfo>() {
                @Override
                public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                    try {
                        mClientDetailList = response.body().getClientDetailList();

                        // String tripID = mClientDetailList.get(0).getTripID();
                        for (int i = 0; i < mClientDetailList.size(); i++) {
                            mGuestDetailList.add(mClientDetailList.get(i).getGuestDetail());
                            mCarDetailList.add(mClientDetailList.get(i).getCarDetail());
                            mDriverDetailList.add(mClientDetailList.get(i).getCarDetail().getDriverDetail());
                        }
                        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("contact",contact);
                        bundle.putParcelableArrayList("ClientDetailList", (ArrayList<? extends Parcelable>) mClientDetailList);
                        bundle.putParcelableArrayList("GuestDetailList", (ArrayList<? extends Parcelable>) mGuestDetailList);
                        bundle.putParcelableArrayList("CarDetailList", (ArrayList<? extends Parcelable>) mCarDetailList);
                        bundle.putParcelableArrayList("DriverDetailList", (ArrayList<? extends Parcelable>) mDriverDetailList);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,"CONNECTION PROBLEM",Toast.LENGTH_SHORT).show();
                        mButton_Login.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ClientInfo> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "There is an error in server side", Toast.LENGTH_SHORT).show();
                    mButton_Login.setEnabled(true);
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please Wait");
            pDialog.setCancelable(false);
            pDialog.show();
            mButton_Login.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pDialog.isShowing())
                pDialog.cancel();
        }
    }
}
