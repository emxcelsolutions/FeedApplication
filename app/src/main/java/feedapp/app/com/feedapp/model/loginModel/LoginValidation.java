
package feedapp.app.com.feedapp.model.loginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginValidation {

    @SerializedName("validContactNo")
    @Expose
    private ValidContactNo validContactNo;

    /**
     * 
     * @return
     *     The validContactNo
     */
    public ValidContactNo getValidContactNo() {
        return validContactNo;
    }

    /**
     * 
     * @param validContactNo
     *     The validContactNo
     */
    public void setValidContactNo(ValidContactNo validContactNo) {
        this.validContactNo = validContactNo;
    }

}
