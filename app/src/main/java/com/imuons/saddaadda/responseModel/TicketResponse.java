package com.imuons.saddaadda.responseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imuons.saddaadda.DataModel.TicketDataModel;

public class TicketResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private TicketDataModel data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TicketDataModel getData() {
        return data;
    }

    public void setData(TicketDataModel data) {
        this.data = data;
    }
}
