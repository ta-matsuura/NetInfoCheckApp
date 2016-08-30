package txm.com.netinfocheckapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by txm on 2016/08/07.
 */

public class NetInfoCheckReceiver extends BroadcastReceiver {
    private static final String TAG = "txm_NetInfoCheckReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Constants.ON_WIFI_STA)) {
                context.sendBroadcast(createIntent(context, action));
            }
            else if (action.equals(Constants.OFF_WIFI_STA)) {
                context.sendBroadcast(createIntent(context, action));
            }
            else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                context.startService(createIntent(context, action));
            }
        }
    }

    private Intent createIntent(Context context, String action){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(context.getApplicationContext(), WifiBtControllerService.class);
        return intent;
    }
}
