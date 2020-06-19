package com.imuons.saddaadda.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketDetailDataModel {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("tranid")
    @Expose
    private String tranid;
    @SerializedName("from_userid")
    @Expose
    private String fromUserid;
    @SerializedName("from_username")
    @Expose
    private String fromUsername;
    @SerializedName("from_country")
    @Expose
    private Object fromCountry;
    @SerializedName("to_userid")
    @Expose
    private String toUserid;
    @SerializedName("to_username")
    @Expose
    private String toUsername;
    @SerializedName("to_country")
    @Expose
    private Object toCountry;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("to_account_no")
    @Expose
    private String toAccountNo;
    @SerializedName("to_holder_name")
    @Expose
    private String toHolderName;
    @SerializedName("to_bank_name")
    @Expose
    private String toBankName;
    @SerializedName("to_branch_name")
    @Expose
    private String toBranchName;
    @SerializedName("to_ifsc_code")
    @Expose
    private String toIfscCode;
    @SerializedName("to_tez_no")
    @Expose
    private String toTezNo;
    @SerializedName("to_phonepe_no")
    @Expose
    private String toPhonepeNo;
    @SerializedName("to_paytm_no")
    @Expose
    private Object toPaytmNo;
    @SerializedName("to_mobikwik_no")
    @Expose
    private Object toMobikwikNo;
    @SerializedName("to_mobile")
    @Expose
    private String toMobile;
    private final static long serialVersionUID = -1550607946320264672L;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTranid() {
        return getValidStrig(tranid);
    }

    public void setTranid(String tranid) {
        this.tranid = tranid;
    }

    public String getFromUserid() {
        return fromUserid;
    }

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }

    public String getFromUsername() {
        return getValidStrig(fromUsername);
    }

    private String getValidStrig(String data){
        return data == null ? "" : data;
    }
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public Object getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(Object fromCountry) {
        this.fromCountry = fromCountry;
    }

    public String getToUserid() {
        return toUserid;
    }

    public void setToUserid(String toUserid) {
        this.toUserid = toUserid;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Object getToCountry() {
        return toCountry;
    }

    public void setToCountry(Object toCountry) {
        this.toCountry = toCountry;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getToAccountNo() {
        return toAccountNo;
    }

    public void setToAccountNo(String toAccountNo) {
        this.toAccountNo = toAccountNo;
    }

    public String getToHolderName() {
        return toHolderName;
    }

    public void setToHolderName(String toHolderName) {
        this.toHolderName = toHolderName;
    }

    public String getToBankName() {
        return toBankName;
    }

    public void setToBankName(String toBankName) {
        this.toBankName = toBankName;
    }

    public String getToBranchName() {
        return toBranchName;
    }

    public void setToBranchName(String toBranchName) {
        this.toBranchName = toBranchName;
    }

    public String getToIfscCode() {
        return toIfscCode;
    }

    public void setToIfscCode(String toIfscCode) {
        this.toIfscCode = toIfscCode;
    }

    public String getToTezNo() {
        return toTezNo;
    }

    public void setToTezNo(String toTezNo) {
        this.toTezNo = toTezNo;
    }

    public String getToPhonepeNo() {
        return toPhonepeNo;
    }

    public void setToPhonepeNo(String toPhonepeNo) {
        this.toPhonepeNo = toPhonepeNo;
    }

    public String getToPaytmNo() {
        return (String) toPaytmNo;
    }

    public void setToPaytmNo(Object toPaytmNo) {
        this.toPaytmNo = toPaytmNo;
    }

    public Object getToMobikwikNo() {
        return toMobikwikNo;
    }

    public void setToMobikwikNo(Object toMobikwikNo) {
        this.toMobikwikNo = toMobikwikNo;
    }

    public String getToMobile() {
        return toMobile;
    }

    public void setToMobile(String toMobile) {
        this.toMobile = toMobile;
    }

}
