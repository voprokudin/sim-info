package p.vasylprokudin.siminfo.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

import p.vasylprokudin.siminfo.R;
import p.vasylprokudin.siminfo.service.MyService;

public class SimPresenter {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private SimActivity mView;
    private BroadcastReceiver broadcastReceiver;
    private MyService myService;
    private ServiceConnection serviceConnection;
    private boolean mBound;
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    public SimPresenter(SimActivity mView) {
        this.mView = mView;
        mView.initViews();
        initPresenter();
    }

    private void initPresenter() {
        createServiceConnection();
    }

    private void createServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.LocalBinder binder = (MyService.LocalBinder) service;
                myService = binder.getServiceInstance();

                String wantPermission = Manifest.permission.READ_PHONE_STATE;
                if (!checkPermission(wantPermission)) {
                    requestPermission(wantPermission);
                } else if (isReadyForShowing()){
                    showData();
                    mView.showMessage(mView.getString(R.string.sim_detected));
                }
                else {
                    showData();
                    mView.showMessage(mView.getString(R.string.no_sim_detected));
                }
                createBroadcastReceiver();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent serviceIntent = new Intent(mView, MyService.class);
        mView.startSimService(serviceIntent);
        mBound = mView.bindSimService(serviceIntent, serviceConnection);
    }

    private void createBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    if (isReadyForShowing()){
                        showData();
                        mView.showMessage(mView.getString(R.string.sim_detected));
                    } else {
                        if (isCheckedSelfPermission()){
                            showData();
                            mView.showMessage(mView.getString(R.string.no_sim_detected));
                        }
                    }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(ACTION_SIM_STATE_CHANGED);
        mView.registerBroadcastReceiver(broadcastReceiver, filter);
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(mView, permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mView, permission)) {
            mView.showMessage(mView.getString(R.string.permission_info));
        }
        ActivityCompat.requestPermissions(mView, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissions(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isReadyForShowing()){
                        showData();
                    } else {
                        showData();
                        mView.showMessage(mView.getString(R.string.no_sim_detected));
                    }
                } else {
                    mView.showMessage(mView.getString(R.string.permission_denied));
                }
                break;
        }
    }

    private boolean isSimAvailable(){
        TelephonyManager phoneMgr = (TelephonyManager) mView.getSystemService(Context.TELEPHONY_SERVICE);
        return phoneMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
    }

    private boolean isCheckedSelfPermission(){
        String wantPermission = Manifest.permission.READ_PHONE_STATE;
        return ActivityCompat.checkSelfPermission(mView, wantPermission) == PackageManager.PERMISSION_GRANTED;
    }

    public void onUnregisterBroadcastReceiver() {
        mView.unregisterBroadcastReceiver(broadcastReceiver);
    }

    public void onUnbindSimService() {
        if (mBound){
            mView.unBindSimService(serviceConnection);
        }
    }

    private boolean isReadyForShowing(){
        return (myService != null && isSimAvailable() && isCheckedSelfPermission());
    }
    private void showData(){
        ArrayList<String> arrayList = myService.getSimInfo();
        mView.showSimInfo(arrayList);
    }
}
