package com.imuons.saddaadda.View;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.imuons.saddaadda.EntityClass.OtpEnitity;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.responseModel.OptResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_item_view);
        ButterKnife.bind(this);

        LinearLayout profile = findViewById(R.id.menu_profile);
        LinearLayout all_Transaction = findViewById(R.id.menu_allTransaction);
        LinearLayout withdrawal = findViewById(R.id.menu_withdraw);
        LinearLayout sellHistory = findViewById(R.id.menu_sellHistory);
        LinearLayout changePassword = findViewById(R.id.menu_changePassword);
        LinearLayout changePin = findViewById(R.id.menu_changePin);
        LinearLayout logout = findViewById(R.id.menu_logout);
        LinearLayout buyHistory = findViewById(R.id.menu_buyHistory);
        LinearLayout setting=findViewById(R.id.menu_setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMore.this, ActivityLangSelection.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
        all_Transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
            }
        });
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellCoinActivity.class));
            }
        });
        sellHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellHistoryReportActivity.class));
            }
        });
        buyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BuyActivityHistory.class));
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callsendOtp(false);
                //startActivity(new Intent(getApplicationContext(), ChangePassword.class));
            }
        });
        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callsendOtp(true);
            }
        });
        logout.setOnClickListener(v -> showAlertDialog());
    }

    private void callsendOtp(boolean b) {

        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(ActivityMore.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class, AppCommon.getInstance(this).getToken());
            Call call = apiService.SendOTP_FOR_PIN(new OtpEnitity(AppCommon.getInstance(this).getUserId()));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(ActivityMore.this).clearNonTouchableFlags(ActivityMore.this);
                    dialog.dismiss();
                    OptResponse authResponse = (OptResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            Toast.makeText(ActivityMore.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (b) {
                                startActivity(new Intent(getApplicationContext(), ChangePin.class));
                            } else {
                                startActivity(new Intent(ActivityMore.this, ChangePassword.class));
                            }

                        } else {
                            Toast.makeText(ActivityMore.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(ActivityMore.this).showDialog(ActivityMore.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(ActivityMore.this).clearNonTouchableFlags(ActivityMore.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(ActivityMore.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.app_name));
        adb.setIcon(R.mipmap.ic_launcher_round);
        adb.setMessage(getString(R.string.r_u_sure_logout_message));
        adb.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // AppCommon.getInstance(ActivityMore.this).clearPreference();
                AppCommon.getInstance(ActivityMore.this).setUserLogin(AppCommon.getInstance(ActivityMore.this).getUserId(), false);
                startActivity(new Intent(ActivityMore.this, SelectionPage.class));
                finishAffinity();
                Toast.makeText(ActivityMore.this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            }

        });
        adb.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    @OnClick(R.id.menu_buyTransReport)
    void buyTransReport() {
        startActivity(new Intent(getApplicationContext(), BuyTransReportActivity.class));
    }

    @OnClick(R.id.menu_withTransReport)
    void withTransReport() {
        startActivity(new Intent(getApplicationContext(), WithDrawTransReportActivity.class));
    }
    @OnClick(R.id.menu_nineReport)
    void ransReport(){
        startActivity(new Intent(getApplicationContext(), IncomeDirectReport.class)
                .putExtra("title" , getString(R.string.nineReport))
                .putExtra("type" ,0));
    }
    @OnClick(R.id.menu_sevenUpDwon)
    void withTransReport1(){
        startActivity(new Intent(getApplicationContext(), IncomeDirectReport.class)
                .putExtra("title" , getString(R.string.sevenUpDown))
                .putExtra("type" ,1));
    }
    @OnClick(R.id.menu_shatakDirectReport)
    void withTransReport2(){
        startActivity(new Intent(getApplicationContext(), IncomeDirectReport.class)
                .putExtra("title" , getString(R.string.shatakReport))
                .putExtra("type" ,2));
    }



}
