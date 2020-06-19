package com.imuons.saddaadda.View;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.imuons.saddaadda.DataModel.DetailsTicketResponseModel;
import com.imuons.saddaadda.DataModel.TicketDetailDataModel;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpDetailsActivity extends AppCompatActivity {

    @BindView(R.id.txt_tran_id)
    TextView txt_tran_id;
    @BindView(R.id.txtpaytm)
    TextView txtpaytm;
    @BindView(R.id.txt_from_user)
    TextView txt_from_user;
    @BindView(R.id.txt_to_user)
    TextView txt_to_user;
    @BindView(R.id.txt_amount)
    TextView txt_amount;
    @BindView(R.id.txt_contact)
    TextView txt_contact;
    @BindView(R.id.txt_google_pay)
    TextView txt_google_pay;
    @BindView(R.id.txt_phone_pay)
    TextView txt_phone_pay;
    @BindView(R.id.txtholder_name)
    TextView txtholder_name;
    @BindView(R.id.account_no)
    TextView account_no;
    @BindView(R.id.branch_name)
    TextView branch_name;
    @BindView(R.id.bank_name)
    TextView bank_name;
    @BindView(R.id.ifsc_code)
    TextView ifsc_code;


    private Bundle bundle;
    private String trna_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_details);
        bundle = getIntent().getExtras();
        if (bundle.containsKey("tran_id")) {
            trna_id = bundle.getString("tran_id");
        }
        ButterKnife.bind(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
    }

    @OnClick(R.id.cross)
    void cross() {
        finish();
    }

    private void getDetails() {
        if (AppCommon.getInstance(getApplicationContext()).isConnectingToInternet(getApplicationContext())) {
            AppCommon.getInstance(getApplicationContext()).setNonTouchableFlags(HelpDetailsActivity.this);
            AppService apiService = ServiceGenerator.createService(AppService.class, AppCommon.getInstance(getApplicationContext()).getToken());
            Dialog dialog = ViewUtils.getProgressBar(HelpDetailsActivity.this);
            Map<String, Object> map = new HashMap<>();
            map.put("transaction_id", trna_id);
            Call call = apiService.GetPayPnLink(map);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(getApplicationContext()).clearNonTouchableFlags(HelpDetailsActivity.this);
                    dialog.dismiss();
                    DetailsTicketResponseModel authResponse = (DetailsTicketResponseModel) response.body();
                    if (authResponse != null) {
                        Log.i("details::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            setInfo(authResponse.getData());
                            // Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(getApplicationContext()).showDialog(HelpDetailsActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(HelpDetailsActivity.this).clearNonTouchableFlags(HelpDetailsActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(getApplicationContext(), "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInfo(List<TicketDetailDataModel> data) {
        TicketDetailDataModel dataModel = data.get(0);
        txt_tran_id.setText("(Transaction ID - " + validString(dataModel.getTranid()));
        txt_from_user.setText(validString(dataModel.getFromUsername()));
        txt_to_user.setText(validString(dataModel.getToUsername()));
        txt_amount.setText(validString(dataModel.getAmount()));
        txt_contact.setText(validString(dataModel.getToMobile()));
        txt_google_pay.setText(validString(dataModel.getToTezNo()));
        txt_phone_pay.setText(validString(dataModel.getToPhonepeNo()));
        txtpaytm.setText(validString(dataModel.getToPaytmNo()));
        account_no.setText(validString(dataModel.getToAccountNo()));
        branch_name.setText(validString(dataModel.getToBranchName()));
        bank_name.setText(validString(dataModel.getToBankName()));
        ifsc_code.setText(validString(dataModel.getToIfscCode()));
        txtholder_name.setText(validString(dataModel.getToHolderName()));
    }

    private String validString(String data) {
        return data == null ? "--" : data;
    }
}
