package p.vasylprokudin.siminfo.ui.interfaces;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public interface ISimViewBroadcastReceiver {
    void registerBroadcastReceiver(BroadcastReceiver broadcastReceiver, IntentFilter filter);
    void unregisterBroadcastReceiver(BroadcastReceiver broadcastReceiver);
}
