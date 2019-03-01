package p.vasylprokudin.siminfo.ui.interfaces;

import android.content.Intent;
import android.content.ServiceConnection;

public interface ISimViewService {

    void startSimService(Intent serviceIntent);
    boolean bindSimService(Intent serviceIntent, ServiceConnection serviceConnection);
    void unBindSimService(ServiceConnection serviceConnection);
}
