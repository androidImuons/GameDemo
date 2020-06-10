package com.imuons.saddaadda.View;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.imuons.saddaadda.DataModel.CompleteSlotRecord;
import com.imuons.saddaadda.DataModel.TopupDatum;
import com.imuons.saddaadda.DataModel.WinningDateRecord;
import com.imuons.saddaadda.DataModel.WinningNumberData;
import com.imuons.saddaadda.EntityClass.CompleteSlotEntity;
import com.imuons.saddaadda.EntityClass.WinningNumberEntity;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.adapters.ShatakLeaderBoardAdapter;
import com.imuons.saddaadda.adapters.WonPriceAdapter;
import com.imuons.saddaadda.responseModel.CompleteSlotResponse;
import com.imuons.saddaadda.responseModel.WinningDateResponse;
import com.imuons.saddaadda.responseModel.WinningNumberResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaddaShatakLeaderBoardActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recycleView;

    @BindView(R.id.recycleViewWonNUmber)
    RecyclerView recycleViewWonNUmber;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.windate)
    EditText windate;
    @BindView(R.id.nxt)
    RelativeLayout nxt;

    @BindView(R.id.comingDate)
    TextView comingDate;
    @BindView(R.id.winnerBox)
    LinearLayout winnerBox;
    @BindView(R.id.wonNumber)
    TextView wonNumber;

    ShatakLeaderBoardAdapter leaderBoardAdapter;
    WonPriceAdapter wonPriceAdapter;
    ArrayList<CompleteSlotRecord> reportData;
    ArrayList<TopupDatum> topupData;
    ArrayList<WinningDateRecord> levelDataArrayList;

    String level, getdatecode, selectedDate;
    String strDate = "";

    ListPopupWindow datelistPopupWindow;
    List<String> listDateName = new ArrayList<>();
    private List<WinningDateRecord> datelist = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        ButterKnife.bind(this);
        datelistPopupWindow = new ListPopupWindow(SaddaShatakLeaderBoardActivity.this);
        reportData = new ArrayList<>();

        levelDataArrayList = new ArrayList<>();
        leaderBoardAdapter = new ShatakLeaderBoardAdapter(this, reportData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setAdapter(leaderBoardAdapter);


        topupData = new ArrayList<>();
        wonPriceAdapter = new WonPriceAdapter(this, topupData);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        recycleViewWonNUmber.setLayoutManager(mLayoutManager1);
        recycleViewWonNUmber.setNestedScrollingEnabled(false);
        recycleViewWonNUmber.setAdapter(wonPriceAdapter);

        datelistPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                windate.setText(datelist.get(i).getDate());

                getdatecode = datelist.get(i).getDate();

                datelistPopupWindow.dismiss();
            }
        });
        datelistPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                windate.setText(String.valueOf(datelist.get(i).getDate()));
                selectedDate = datelist.get(i).getDate();
                CallApiCompleteSlot(selectedDate);
                datelistPopupWindow.dismiss();
            }
        });
        windate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                datelistPopupWindow.setAdapter(new ArrayAdapter(SaddaShatakLeaderBoardActivity.this, R.layout.check_list_item, listDateName));
                datelistPopupWindow.setAnchorView(windate);
                datelistPopupWindow.getBackground();
                datelistPopupWindow.setModal(true);
                datelistPopupWindow.show();
            }
        });

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));
        CallWinningDate();
        CallApiCompleteSlot(String.valueOf(dateobj));

    }

    private void CallWinningDate() {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(SaddaShatakLeaderBoardActivity.this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Call call = apiService.ShatakWinningDate();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    dialog.dismiss();
                    WinningDateResponse authResponse = (WinningDateResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Winning date", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            datelist.addAll(authResponse.getData().getRecords());
                            listDateName.clear();
                            getDatebyname(authResponse.getData().getRecords());
                        } else {
                            Toast.makeText(SaddaShatakLeaderBoardActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).showDialog(SaddaShatakLeaderBoardActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(SaddaShatakLeaderBoardActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDatebyname(ArrayList<WinningDateRecord> dateRecords) {
        for (int i = 0; i < dateRecords.size(); i++) {
            listDateName.add(dateRecords.get(i).getDate());
        }
    }

    private void CallApiCompleteSlot(String d) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(SaddaShatakLeaderBoardActivity.this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Call call = apiService.ShatakCompleteSolts(new CompleteSlotEntity(d));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    dialog.dismiss();
                    CompleteSlotResponse authResponse = (CompleteSlotResponse) response.body();
                    if (authResponse != null) {
                        Log.i("complete solt::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            reportData = authResponse.getData().getRecords();

                            setAdapter(authResponse.getData().getRecords());
                            Toast.makeText(SaddaShatakLeaderBoardActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SaddaShatakLeaderBoardActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).showDialog(SaddaShatakLeaderBoardActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(SaddaShatakLeaderBoardActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter(ArrayList<CompleteSlotRecord> records) {
        ShatakLeaderBoardAdapter leaderBoardAdapter = new ShatakLeaderBoardAdapter(this, records);
        recycleView.setAdapter(leaderBoardAdapter);
    }


    //Winning Date And Number

    private void CallWinningNumber(String sNo) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(SaddaShatakLeaderBoardActivity.this);
            AppService apiService = ServiceGenerator.createService(AppService.class);
            Call call = apiService.ShatakLeadership(new WinningNumberEntity(sNo, "0", "10"));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    dialog.dismiss();
                    WinningNumberResponse authResponse = (WinningNumberResponse) response.body();
                    if (authResponse != null) {
                        Log.i("leadership::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            setWonNumber(authResponse.getData());
                            if (authResponse.getData().getTopupData() != null) {
                                setWonNoAdapter(authResponse.getData().getTopupData());
                            } else {
                                nxt.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(SaddaShatakLeaderBoardActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).showDialog(SaddaShatakLeaderBoardActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(SaddaShatakLeaderBoardActivity.this).clearNonTouchableFlags(SaddaShatakLeaderBoardActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(SaddaShatakLeaderBoardActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setWonNoAdapter(ArrayList<TopupDatum> topupData) {
        nxt.setVisibility(View.VISIBLE);
        WonPriceAdapter wonPriceAdapter = new WonPriceAdapter(this, topupData);
        recycleViewWonNUmber.setAdapter(wonPriceAdapter);
    }

    private void setWonNumber(WinningNumberData data) {
        if (data.getProductName() != null) {
            wonNumber.setText(String.valueOf(data.getProductName()));
        } else {
            wonNumber.setText("_ _");
        }
        if (data.getWinnerDate() != null) {
            comingDate.setText(String.valueOf(data.getWinnerDate()));

        } else {
            comingDate.setText("");
        }


    }

    public void selectedSlot(int adapterPosition) {
        if (reportData.get(adapterPosition).getSlotNo() != null) {
            String slotNo = String.valueOf(reportData.get(adapterPosition).getSlotNo());

            CallWinningNumber(slotNo);
        }
    }
}
