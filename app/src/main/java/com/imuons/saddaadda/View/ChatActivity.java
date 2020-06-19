package com.imuons.saddaadda.View;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.imuons.saddaadda.DataModel.FetchChatDataModel;
import com.imuons.saddaadda.EntityClass.ChatEntity;
import com.imuons.saddaadda.EntityClass.SendMessage;
import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;
import com.imuons.saddaadda.Utils.ViewUtils;
import com.imuons.saddaadda.adapters.ChatRoomAdapter;
import com.imuons.saddaadda.responseModel.FetchChatResponse;
import com.imuons.saddaadda.responseModel.PinResponse;
import com.imuons.saddaadda.retrofit.AppService;
import com.imuons.saddaadda.retrofit.ServiceGenerator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.rvChat)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    ChatRoomAdapter chatRoomAdapter;
    ArrayList<FetchChatDataModel> chatDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chatDataArrayList = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(this, chatDataArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatRoomAdapter);
        callChatApi();
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.lightgreen) ,
                getResources().getColor(R.color.pink) ,
                getResources().getColor(R.color.darkGry)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callChatApi();
            }
        });
    }

    private void callChatApi() {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = null;
            if(!swipeRefreshLayout.isRefreshing())
             dialog = ViewUtils.getProgressBar(ChatActivity.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class, AppCommon.getInstance(this).getToken());
            Call call = apiService.getChatList(new ChatEntity("1"));
            Dialog finalDialog = dialog;
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(ChatActivity.this).clearNonTouchableFlags(ChatActivity.this);
                    if(finalDialog != null)
                     finalDialog.dismiss();
                    else
                        swipeRefreshLayout.setRefreshing(false);
                    FetchChatResponse authResponse = (FetchChatResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            setAdapter(authResponse.getData());
                            //Toast.makeText(ChatActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChatActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(ChatActivity.this).showDialog(ChatActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if(finalDialog != null)
                        finalDialog.dismiss();
                    else
                        swipeRefreshLayout.setRefreshing(false);
                    AppCommon.getInstance(ChatActivity.this).clearNonTouchableFlags(ChatActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void setAdapter(ArrayList<FetchChatDataModel> data) {
        chatDataArrayList = data;
        chatRoomAdapter.updateList(chatDataArrayList);
        recyclerView.smoothScrollToPosition(chatDataArrayList.size() - 1);
    }

    @OnClick(R.id.btSend)
    void sendMessage(){
        String msg = etMessage.getText().toString().trim();
        if(msg.isEmpty()){
            Toast.makeText(this, getString(R.string.pls_enter_message), Toast.LENGTH_SHORT).show();
        }else {
            etMessage.setText("");
            callSendMessage(msg);
        }
    }

    private void callSendMessage(String msg) {
        AppCommon.getInstance(this).onHideKeyBoard(this);
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            Dialog dialog = ViewUtils.getProgressBar(ChatActivity.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppService apiService = ServiceGenerator.createService(AppService.class, AppCommon.getInstance(this).getToken());
            Call call = apiService.sendMessage(new SendMessage(msg));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(ChatActivity.this).clearNonTouchableFlags(ChatActivity.this);
                    dialog.dismiss();
                    PinResponse authResponse = (PinResponse) response.body();
                    if (authResponse != null) {
                        Log.i("SendResponse::", new Gson().toJson(authResponse));
                        if (authResponse.getCode() == 200) {
                            callChatApi();
                            Toast.makeText(ChatActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(ChatActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppCommon.getInstance(ChatActivity.this).showDialog(ChatActivity.this, "Server Error");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(ChatActivity.this).clearNonTouchableFlags(ChatActivity.this);
                    // loaderView.setVisibility(View.GONE);
                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // no internet
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }
    }


}
