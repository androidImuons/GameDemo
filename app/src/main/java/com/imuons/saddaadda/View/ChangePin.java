package com.imuons.saddaadda.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.imuons.saddaadda.EntityClass.ChangePinEntity;
import com.imuons.saddaadda.EntityClass.ResetPinEntity;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.responseModel.PinResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePin  extends Activity {

    @BindView(R.id.etOtp)
    EditText oldPin;
    @BindView(R.id.etNewPin)
    EditText newPin;
    @BindView(R.id.etConfirmPin)
    EditText confirmPin;
    @BindView(R.id.otp_et)
    EditText etOtp2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.submitBtn)
    void submit(){
        String otpTxt = oldPin.getText().toString().trim();
        String newPinTxt = newPin.getText().toString().trim();
        String cPin = confirmPin.getText().toString().trim();
        String otpNew = etOtp2.getText().toString().trim();
        if(otpTxt.isEmpty()){
            oldPin.setError("Please Enter old pin");
        }else if(newPinTxt.isEmpty()){
            newPin.setError("Please error new pin");
        }else if(cPin.isEmpty()){
            confirmPin.setError("Please error new pin");
        }else if(otpNew.isEmpty()){
            etOtp2.setError("Please error OTP");
        }else {
            callChangePin(otpTxt , newPinTxt , cPin , otpNew);
        }
    }

    private void callChangePin(String oldPin, String newPinTxt, String cPin , String otp) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(ChangePin.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class , AppCommon.getInstance(this).getToken());
            Call call = apiService.ChangePinApi(new ChangePinEntity(oldPin ,newPinTxt ,cPin , otp));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(ChangePin.this).clearNonTouchableFlags(ChangePin.this);
                    dialog.dismiss();
                    PinResponse authResponse = (PinResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            Toast.makeText(ChangePin.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePin.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(ChangePin.this).showDialog(ChangePin.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(ChangePin.this).clearNonTouchableFlags(ChangePin.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(ChangePin.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

}
