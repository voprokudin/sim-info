package p.vasylprokudin.siminfo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import p.vasylprokudin.siminfo.ui.interfaces.ISimInfo;
import p.vasylprokudin.siminfo.ui.interfaces.ISimView;
import p.vasylprokudin.siminfo.ui.interfaces.ISimViewBroadcastReceiver;
import p.vasylprokudin.siminfo.ui.interfaces.ISimViewService;
import p.vasylprokudin.siminfo.R;
import p.vasylprokudin.siminfo.ui.adapter.SimAdapter;

public class SimActivity extends AppCompatActivity implements ISimView, ISimViewBroadcastReceiver, ISimViewService,
        ISimInfo {

    private SimPresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new SimPresenter(this);
    }

    @Override
    public void initViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult) {
        mPresenter.onRequestPermissions(requestCode, permissions, grantResult);
    }

    @Override
    public void showMessage(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registerBroadcastReceiver(BroadcastReceiver broadcastReceiver, IntentFilter filter) {
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void unregisterBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void showSimInfo(ArrayList<String> arrayList) {
        SimAdapter adapter = new SimAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startSimService(Intent serviceIntent) {
        startService(serviceIntent);
    }

    @Override
    public boolean bindSimService(Intent serviceIntent, ServiceConnection serviceConnection) {
        return bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBindSimService(ServiceConnection serviceConnection) {
        unbindService(serviceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onUnbindSimService();
        mPresenter.onUnregisterBroadcastReceiver();
    }
}