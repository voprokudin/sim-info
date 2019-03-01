package p.vasylprokudin.siminfo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import p.vasylprokudin.siminfo.R;

public class MyService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private ArrayList<String> listInfo;
    private TelephonyManager phoneMgr;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public MyService getServiceInstance(){
            return MyService.this;
        }
    }

    public ArrayList<String> getSimInfo() {
        listInfo = new ArrayList<>();
        phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String callState = "<b>" + getString(R.string.call_state) + " " + "</b>" + phoneMgr.getCallState();
        listInfo.add(callState);

        String mobileNumber = "<b>" + getString(R.string.mobile_number) + " " + "</b>" + phoneMgr.getLine1Number();
        listInfo.add(mobileNumber);

        String operatorName = "<b>" + getString(R.string.sim_operator_name) + " " + "</b>" + phoneMgr.getSimOperatorName();
        listInfo.add(operatorName);

        String simSerialNumber = "<b>" + getString(R.string.sim_serial_number) + " " + "</b>" + phoneMgr.getSimSerialNumber();
        listInfo.add(simSerialNumber);

        String simState = "<b>" + getString(R.string.sim_state) + " " + "</b>" + phoneMgr.getSimState();
        listInfo.add(simState);

        String countryIso = "<b>" + getString(R.string.country_iso) + " " + "</b>" + phoneMgr.getSimCountryIso().toUpperCase();
        listInfo.add(countryIso);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return getExtraSimInfo();
        } else {
            return listInfo;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private ArrayList<String> getExtraSimInfo() {
        SubscriptionManager subscriptionMgr = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        int number = subscriptionMgr.getActiveSubscriptionInfoCountMax();
        String numberOfSlots = "<b>" + getString(R.string.number_of_slots) + " " + "</b>" + number;
        listInfo.add(numberOfSlots);

        for (int i = 0; i < number; i ++){
            String imeiSlots = "<b>" + getString(R.string.imei_number_of_slot) + (i+1) + ": " + " " + "</b>" + phoneMgr.getImei(i);
            listInfo.add(imeiSlots);
        }

        List<SubscriptionInfo> lll = new ArrayList<>();
        lll = subscriptionMgr.getActiveSubscriptionInfoList();
        if (lll != null){
            for (int i = 0; i < lll.size(); i ++){
                String slotSimInserted = "<b>" + getString(R.string.slot_sim_inserted) + " " + "</b>" + (lll.get(i).getSimSlotIndex() + 1);
                listInfo.add(slotSimInserted);
            }
        }
        return listInfo;
    }
}
