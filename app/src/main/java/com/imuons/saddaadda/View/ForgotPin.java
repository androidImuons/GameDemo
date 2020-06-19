package com.imuons.saddaadda.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
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

public class ForgotPin extends AppCompatActivity {

    @BindView(R.id.etOtp)
    EditText etOtp;
    @BindView(R.id.etNewPin)
    EditText newPin;
    @BindView(R.id.etConfirmPin)
    EditText confirmPin;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.submitBtn)
    void submit(){
        String otpTxt = etOtp.getText().toString().trim();
        String newPinTxt = newPin.getText().toString().trim();
        String cPin = confirmPin.getText().toString().trim();
        if(otpTxt.isEmpty()){
            etOtp.setError(getString(R.string.please_enter_otp));
        }else if(newPinTxt.isEmpty()){
            newPin.setError(getString(R.string.pls_enter_new_pin));
        }else if(cPin.isEmpty()){
            confirmPin.setError(getString(R.string.confirm_pin));
        }else {
            callResetPin(otpTxt , newPinTxt , cPin);
        }
    }

    private void callResetPin(String otpTxt, String newPinTxt, String cPin) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(ForgotPin.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class , AppCommon.getInstance(this).getToken());
            Call call = apiService.ResetPinApi(new ResetPinEntity(AppCommon.getInstance(this).getUserId() , otpTxt ,newPinTxt ,cPin));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(ForgotPin.this).clearNonTouchableFlags(ForgotPin.this);
                    dialog.dismiss();
                    PinResponse authResponse = (PinResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            Toast.makeText(ForgotPin.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPin.this , PinEnterActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(ForgotPin.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(ForgotPin.this).showDialog(ForgotPin.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(ForgotPin.this).clearNonTouchableFlags(ForgotPin.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(ForgotPin.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }
}
