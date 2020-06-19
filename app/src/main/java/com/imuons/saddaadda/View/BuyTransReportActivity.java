package com.imuons.saddaadda.View;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.imuons.saddaadda.DataModel.TransRecord;
import com.imuons.saddaadda.DataModel.TransReportData;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.adapters.BuyTransAdapter;
import com.imuons.saddaadda.responseModel.TransReportResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTransReportActivity extends AppCompatActivity {
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //SellReportAdapter reportAdapter;
    BuyTransAdapter reportAdapter;
    ArrayList<TransRecord> reportData;
    @BindView(R.id.glowingText)
    TextView glowingText;
    int offsetLevel = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_history_report);
        ButterKnife.bind(this);
        glowingText.setText(getString(R.string.buy_transaction_report));
        reportData = new ArrayList<>();
        reportAdapter = new BuyTransAdapter(this, reportData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setAdapter(reportAdapter);
        CallApiForReport(0);
    }

    private void CallApiForReport(int start) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = null;
            if (start == 0)
                dialog = ViewUtils.getProgressBar(BuyTransReportActivity.this);
            else
                dialog = ViewUtils.getBottomProgress(BuyTransReportActivity.this);
            AppService apiService = ServiceGenerator.createService(AppService.class, AppCommon.getInstance(this).getToken());
            Map<String, String> roiMap = new HashMap<>();

            roiMap.put("table_name", "buy_sell");
            roiMap.put("tr_type", "Credit");
            roiMap.put("start", String.valueOf(start));
            roiMap.put("length", "20");
            Call call = apiService.BuyTransREPORT_CALL(roiMap);
            Dialog finalDialog = dialog;
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(BuyTransReportActivity.this).clearNonTouchableFlags(BuyTransReportActivity.this);
                    finalDialog.dismiss();
                    TransReportResponse authResponse = (TransReportResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            setData(authResponse.getData());
                        } else {
                            Toast.makeText(BuyTransReportActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(BuyTransReportActivity.this).showDialog(BuyTransReportActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    finalDialog.dismiss();
                    AppCommon.getInstance(BuyTransReportActivity.this).clearNonTouchableFlags(BuyTransReportActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(BuyTransReportActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(TransReportData data) {
        if (reportData.size() != 0)
            reportData.addAll(data.getRecords());
        else {
            reportData = data.getRecords();
            reportData.add(0, new TransRecord());
        }
        reportAdapter.updateList(reportData, offsetLevel);
    }

    public void checkOpen(int adapterPosition) {
        if (reportData.get(adapterPosition).isOpen()) {
            reportData.get(adapterPosition).setOpen(false);
        } else {
            reportData.get(adapterPosition).setOpen(true);
        }
        reportAdapter.update(reportData, adapterPosition);
    }

    public void callapi(int position) {
        offsetLevel = offsetLevel + 1;
        CallApiForReport(reportData.size());
    }
}
