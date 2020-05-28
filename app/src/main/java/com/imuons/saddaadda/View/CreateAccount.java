package com.imuons.saddaadda.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.imuons.saddaadda.EntityClass.LoginEntity;
import com.imuons.saddaadda.EntityClass.RegitrationEntity;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.responseModel.LoginResponseModel;
import com.imuons.saddaadda.responseModel.RandomUserIdResponse;
import com.imuons.saddaadda.responseModel.RegisterResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccount extends AppCompatActivity {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etRefralCode)
    EditText etRefralCode;
    @BindView(R.id.etUserId)
    EditText etUserId;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etCmfPassword)
    EditText etCmfPassword;
    @BindView(R.id.pin)
    EditText pin;
    @BindView(R.id.c_pin)
    EditText cpin;

    @BindView(R.id.submitBtn)
    TextView submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        callRendomNumberAPI();
    }

    @OnClick(R.id.submitBtn)
    void submitCall() {
        String userId = etUserId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String referralCode = etRefralCode.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String cmf_password = etCmfPassword.getText().toString().trim();
        String pintxt = pin.getText().toString().trim();
        String cpintxt = cpin.getText().toString().trim();

        if (userId.isEmpty()) {
            etUserId.setError("Please enter User ID");
        } else if (password.isEmpty()) {
            etPassword.setError("Please enter Password");
        } else if (name.isEmpty()) {
            etName.setError("Please enter Name");
        } else if (referralCode.isEmpty()) {
            etRefralCode.setError("Please enter Referral code");
        } else if (mobile.isEmpty()) {
            etMobile.setError("Please enter Mobile no ");
        } else if (cmf_password.isEmpty()) {
            etCmfPassword.setError("Please enter Confirm password");
        } else if (!cmf_password.matches(password)) {
            etCmfPassword.setError("Confirm Password not match");
        } else if (pintxt.isEmpty()) {
            pin.setError("Please enter pin");
        } else if (!cmf_password.matches(password)) {
            cpin.setError("Confirm pin not match");
        } else
            callRegisterApi(userId, name, password, mobile, referralCode , pintxt);

    }

    private void callRegisterApi(String userId, String name, String password, String mobile, String referralCode , String pin) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(CreateAccount.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Call call = apiService.RegisterApi(new RegitrationEntity(userId, name, password, mobile, referralCode , pin));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    dialog.dismiss();
                    RegisterResponse authResponse = (RegisterResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            etUserId.setText(String.valueOf(authResponse.getData()));

                            callLoginApi(new LoginEntity(authResponse.getData().getUserId(), authResponse.getData().getPassword()));
                        } else {
                            Toast.makeText(CreateAccount.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(CreateAccount.this).showDialog(CreateAccount.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(CreateAccount.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }

    }


    private void callLoginApi(LoginEntity loginEntity) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Dialog dialog = ViewUtils.getProgressBar(CreateAccount.this);
            Call call = apiService.LoginApi(loginEntity);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    dialog.dismiss();
                    LoginResponseModel authResponse = (LoginResponseModel) response.body();
                    if (authResponse != null) {
                        Log.i("LoginResponse::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            AppCommon.getInstance(CreateAccount.this).setUserObject(new Gson().toJson(authResponse.getData()));
                            AppCommon.getInstance(CreateAccount.this).setToken(authResponse.getData().getAccessToken());
                            AppCommon.getInstance(CreateAccount.this).setUserLogin(loginEntity.getUser_id());
                            AppCommon.getInstance(CreateAccount.this).setSesstionId(authResponse.getData().getSession_id());
                            startActivity(new Intent(CreateAccount.this, HomeActivity.class));
                        } else {
                            Toast.makeText(CreateAccount.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(CreateAccount.this).showDialog(CreateAccount.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(CreateAccount.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRendomNumberAPI() {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Dialog dialog = ViewUtils.getProgressBar(CreateAccount.this);
            Call call = apiService.GetRendomNumber();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    dialog.dismiss();
                    RandomUserIdResponse authResponse = (RandomUserIdResponse) response.body();
                    if (authResponse != null) {
                        Log.i("RendomResponse::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                              etUserId.setText(String.valueOf(authResponse.getData().getUserId()));
                        } else {
                            Toast.makeText(CreateAccount.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(CreateAccount.this).showDialog(CreateAccount.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(CreateAccount.this).clearNonTouchableFlags(CreateAccount.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(CreateAccount.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }
}