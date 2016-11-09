
package feedapp.app.com.feedapp.model.tokenModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenValidation {

    @SerializedName("validContactNo")
    @Expose
    private ValidToken validToken;

    /**
     *
     * @return
     *     The validContactNo
     */
    public ValidToken getValidToken() {
        return validToken;
    }

    /**
     *
     * @param validToken
     *     The validContactNo
     */
    public void setValidToken(ValidToken validToken) {
        this.validToken = validToken;
    }

}
