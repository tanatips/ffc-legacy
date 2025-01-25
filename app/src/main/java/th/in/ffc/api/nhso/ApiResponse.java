package th.in.ffc.api.nhso;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("AUTHEN_CODE")
    private String authenCode;

    @SerializedName("Data_Error")
    private String dataError;

    public String getAuthenCode() {
        return authenCode;
    }

    public String getDataError() {
        return dataError;
    }
}