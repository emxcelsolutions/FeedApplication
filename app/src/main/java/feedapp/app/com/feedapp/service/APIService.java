package feedapp.app.com.feedapp.service;

import java.util.Map;

import feedapp.app.com.feedapp.CarReviewFragment;
import feedapp.app.com.feedapp.model.CarReviewFragmentResponse.CarTypeByData;
import feedapp.app.com.feedapp.model.ClientDetailList;
import feedapp.app.com.feedapp.model.ClientInfo;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.DriverTypeByData;
import feedapp.app.com.feedapp.model.DriverReviewFragmentResponse.SearchDriverName;
import feedapp.app.com.feedapp.model.feedbackresponse.FeedbackResponse;
import feedapp.app.com.feedapp.model.loginModel.LoginValidation;
import feedapp.app.com.feedapp.model.tokenModel.TokenValidation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by ADMIN on 06-10-2016.
 */

public interface APIService {

    //Todo interface for accessing api using retrofit

    @GET("/endsystem/getTripIdByContactNo")
    Call<ClientInfo> getClientInfoCall(
            @QueryMap Map<String,String> params
    );

    @GET("/endsystem/getSaveFeedbackData")
    Call<FeedbackResponse> getFeedbackResponse(
            @QueryMap Map<String,String> params
    );

    @GET("/endsystem/getCarTypeByData")
    Call<CarTypeByData> getCarTypeByDataCall();

    @GET("endsystem/getDriverTypeByData")
    Call<DriverTypeByData> getDriverTypeByDataCall();

    @GET("endsystem/getTypeByFeedbackData")
    Call<SearchDriverName> getSearchDriverNameCall(
            @QueryMap Map<String,String> params
    );

    @GET("endsystem/getValidContactNo")
    Call<LoginValidation> getLoginValidationCall(
            @QueryMap Map<String,String> params
    );

    @GET("endsystem/updateTokenFCMWithContactNo")
    Call<TokenValidation> getTokenValidationCall(
            @QueryMap Map<String,String> params
    );

}
