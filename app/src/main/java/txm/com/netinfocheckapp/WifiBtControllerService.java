package txm.com.netinfocheckapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import static com.android.internal.R.id.status;

/**
 * Created by txm on 2016/08/07.
 */

public class WifiBtControllerService extends Service{

    static final String TAG = "txm_WifiBtController";
    private WifiManager mWifiManager = null;
    private WifiConfiguration mConfiguration = null;
    private WifiConfiguration m5GConfiguration = null;
    private BluetoothAdapter mBtAdapter;

    private RequestReceiver mRequestReceiver = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mRequestReceiver = new RequestReceiver();
        registerReceiver(mRequestReceiver, getFilter());

        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        telephonyInfo();
        //明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (null != mRequestReceiver) {
            unregisterReceiver(mRequestReceiver);
        }
        // Foreground終了
        stopForeground(true);
    }

    private class RequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if(action.equals(Constants.ON_WIFI_STA)) {
                        WIfiSta(true);
                    }
                    else if(action.equals(Constants.OFF_WIFI_STA)) {
                        WIfiSta(false);
                    }
                    else if(action.equals(Constants.ON_WIFI2G_AP)) {
                        WifiAp(true);
                    }
                    else if(action.equals(Constants.ON_WIFI5G_AP)) {
                        WifiAp5g(true);
                    }
                    else if(action.equals(Constants.OFF_WIFI2G_AP)) {
                        WifiAp(false);
                    }
                    else if(action.equals(Constants.OFF_WIFI5G_AP)) {
                        WifiAp5g(false);
                    }
                    else if(action.equals(Constants.ON_BT)) {
                        Bt(true);
                    }
                    else if(action.equals(Constants.OFF_BT)) {
                        Bt(false);
                    }
                }
            }
        }
    }
    private void WIfiSta(boolean status){
        mWifiManager.setWifiEnabled(status);
        if (status) {
            Log.d(TAG, "Wifi STA is on");
        } else{
            Log.d(TAG, "Wifi STA is off");
        }
    }

    private void WifiAp(boolean status){
        try {
            mConfiguration = mWifiManager.getWifiApConfiguration();
            if (status == false) {
                mWifiManager.setWifiApEnabled(null, status);
                Log.d(TAG, "Wifi AP 2.4G is off");
            } else {
                mWifiManager.setWifiApEnabled(mConfiguration, status);
                Log.d(TAG, "Wifi AP 2.4G is on");
            }
        } catch (NoSuchMethodError e){
            Log.e (TAG, "NoSuchMethodError : " + e.getMessage());
        }
    }

    private void WifiAp5g(boolean status){
        try {
            m5GConfiguration = mWifiManager.getWifi5gApConfiguration();
            if (status == false) {
                mWifiManager.setWifiApEnabled(null, status);
                Log.d(TAG, "Wifi AP 5G is off");
            } else {
                mWifiManager.setWifiApEnabled(m5GConfiguration, status);
                Log.d(TAG, "Wifi AP 5G is on");
            }
        } catch (NoSuchMethodError e){
            Log.e (TAG, "NoSuchMethodError : " + e.getMessage());
        }
    }
    private void Bt(boolean status){
        if(status) {
            mBtAdapter.enable();
            Log.d(TAG, "Bluetooth is on");
        }else{
            mBtAdapter.disable();
            Log.d(TAG, "Bluetooth is off");
        }
    }

    private IntentFilter getFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ON_WIFI_STA);
        filter.addAction(Constants.OFF_WIFI_STA);
        filter.addAction(Constants.ON_WIFI2G_AP);
        filter.addAction(Constants.OFF_WIFI2G_AP);
        filter.addAction(Constants.ON_WIFI5G_AP);
        filter.addAction(Constants.OFF_WIFI5G_AP);
        filter.addAction(Constants.ON_BT);
        filter.addAction(Constants.OFF_BT);
        return filter;
    }

    private void telephonyInfo(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Log.d(TAG, "Line1Number " + telephonyManager.getLine1Number());
        Log.d(TAG, "DeviceId " + telephonyManager.getDeviceId());
        Log.d(TAG, "SimCountryIso " + telephonyManager.getSimCountryIso());
        Log.d(TAG, "SimOperator " + telephonyManager.getSimOperator());
        Log.d(TAG, "SimOperatorName " + telephonyManager.getSimOperatorName());
        Log.d(TAG, "SimSerialNumber " + telephonyManager.getSimSerialNumber());
        Log.d(TAG, "SimState " + telephonyManager.getSimState());
    }

}
