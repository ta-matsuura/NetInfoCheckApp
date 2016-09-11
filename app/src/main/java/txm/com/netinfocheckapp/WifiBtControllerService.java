package txm.com.netinfocheckapp;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

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
    private WifiManager wiman;
    private WifiConfiguration wifiConfig;
    private WifiConfiguration wifiConfig5g;


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
                    else if(action.equals(Constants.GETALLINFO)) {
                        getAllInfo();
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
        filter.addAction(Constants.GETALLINFO);

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

    private void getAllInfo(){

        //telephonyInfo();

        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        Log.v(TAG, "---------- Settings ----------------");
        Log.d(TAG, "Bluetooth Setting : " + mBtAdapter.isEnabled());
        Log.d(TAG, "isWifiEnabled : " + wifi.isWifiEnabled());
        Log.d(TAG, "isWifiApEnabled : " + wifi.isWifiApEnabled());
        try {
            Log.d(TAG, "isWifi5gApEnabled : " + wifi.isWifi5gApEnabled());
        }catch (NoSuchMethodError e) {
            Log.d(TAG, e.toString());
        }
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> listServices = am.getRunningServices(Integer.MAX_VALUE);
        PackageManager pm = getPackageManager();
        Log.v(TAG, "---------- Running KDDI Service ----------------");

        // List running KDDI service
        for (ActivityManager.RunningServiceInfo curr : listServices) {
            if(curr.service.getPackageName().toString().startsWith("com.kddi") ||
                    curr.service.getPackageName().toString().startsWith("txm.com") ||
                    curr.service.getPackageName().toString().startsWith("com.redbend") ||
                    curr.service.getPackageName().toString().startsWith("com.ktec")) {
                Log.d(TAG, curr.service.getPackageName() + " + " + curr.service.getClassName());
                try {
                    ApplicationInfo appInfo = pm.getApplicationInfo(curr.service.getPackageName(), 0);
                    Log.d(TAG , "APP Label : " + appInfo.loadLabel(pm));
                }catch (PackageManager.NameNotFoundException e) {
                    Log.d(TAG, e.toString());
                }

            }
        }

        wiman = (WifiManager)getApplication().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wiman.getConnectionInfo();
        String BSSID = wifiInfo.getBSSID();
        Log.v(TAG, "---------- Wifi STA ----------------");
        Log.v(TAG, "BSSID : " + BSSID);
        //　SSIDを取得
        Log.v(TAG, String.format("SSID : %s", wifiInfo.getSSID()));
        // IPアドレスを取得
        int ipAdr = wifiInfo.getIpAddress();
        Log.v(TAG, String.format("IP Adrress : %02d.%02d.%02d.%02d",
                (ipAdr>>0)&0xff, (ipAdr>>8)&0xff, (ipAdr>>16)&0xff, (ipAdr>>24)&0xff));
        // MACアドレスを取得
        Log.v(TAG, String.format("MAC Address of this device : %s", wifiInfo.getMacAddress()));
        // 受信信号強度&信号レベルを取得
        int rssi = wifiInfo.getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 5);
        Log.v(TAG, String.format("RSSI : %d / Level : %d/4", rssi, level));
        Log.v(TAG, "");

        Log.v(TAG, String.format("NetworkID : %s", wifiInfo.getNetworkId()));
        Log.v(TAG, String.format("LinkSpeed : %s", wifiInfo.getLinkSpeed()));


        Log.v(TAG, "---------- Wifi AP(2.4G) ----------------");
        try {
            wifiConfig = wiman.getWifiApConfiguration();

            if (wifiConfig != null) {
                //Log.v(TAG, "wifiConfig : " + wifiConfig.toString());
                if (wifiConfig.BSSID != null) {
                    Log.v(TAG, "BSSID : " + wifiConfig.BSSID);
                } else Log.v(TAG, "BSSID is null.");
                if (wifiConfig.FQDN != null) {
                    Log.v(TAG, "FQDN : " + wifiConfig.FQDN);
                } else Log.v(TAG, "FQDN is null.");
                if (wifiConfig.SSID != null) {
                    Log.v(TAG, "SSID : " + wifiConfig.SSID);
                } else Log.v(TAG, "SSID is null.");
                if (wifiConfig.preSharedKey != null) {
                    Log.v(TAG, "preSharedKey : " + wifiConfig.preSharedKey);
                }else Log.v(TAG, "preSharedKey is null.");
            } else {
                Log.v(TAG, "wifiConfig is null.");
            }
        } catch(NoSuchMethodError e) {
            Log.e (TAG, "NoSuchMethodError : " + e.getMessage());
        }

        Log.v(TAG, "---------- Wifi AP(5G) ----------------");
        try {
            wifiConfig5g = wiman.getWifi5gApConfiguration();

            if (wifiConfig5g != null) {
                if (wifiConfig5g.BSSID != null) {
                    Log.v(TAG, "BSSID(5G) : " + wifiConfig5g.BSSID);
                } else Log.v(TAG, "BSSID(5G) is null.");
                if (wifiConfig5g.FQDN != null) {
                    Log.v(TAG, "FQDN(5G) : " + wifiConfig5g.FQDN);
                } else Log.v(TAG, "FQDN(5G) is null.");
                if (wifiConfig5g.SSID != null) {
                    Log.v(TAG, "SSID(5G) : " + wifiConfig5g.SSID);
                } else Log.v(TAG, "SSID(5G) is null.");
                if (wifiConfig5g.preSharedKey != null) {
                    Log.v(TAG, "preSharedKey(5G) : " + wifiConfig5g.preSharedKey);
                }else Log.v(TAG, "preSharedKey(5G) is null.");
            } else {
                Log.v(TAG, "wifiConfig(5G) is null.");
            }
        } catch(NoSuchMethodError e) {
            Log.e (TAG, "NoSuchMethodError : " + e.getMessage());
        }


        Log.v(TAG, "---------- Build Info ----------------");

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            Log.d(TAG, "VERSION is Android M");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            Log.d(TAG, "VERSION is Android Lollipop");
        }
        Log.d(TAG, "BOARD : " + Build.BOARD);
        Log.d(TAG, "BOOTLOADER : " + Build.BOOTLOADER);
        Log.d(TAG, "DEVICE : " + Build.DEVICE);
        Log.d(TAG, "HARDWARE : " + Build.HARDWARE);
        Log.d(TAG, "MANUFACTURER : " + Build.MANUFACTURER);
        Log.d(TAG, "MODEL : " + Build.MODEL);
        Log.d(TAG, "PRODUCT : " + Build.PRODUCT);
        Log.d(TAG, "TIME : " + Build.TIME);
        Log.d(TAG, "TYPE : " + Build.TYPE);
        Log.d(TAG, "USER : " + Build.USER);
        Log.d(TAG, "DISPLAY : " + Build.DISPLAY);
        Log.d(TAG, "FINGERPRINT : " + Build.FINGERPRINT);
        Log.d(TAG, "ID : " + Build.ID);
        Log.d(TAG, "SERIAL : " + Build.SERIAL);


        Log.v(TAG, "---------- IP Address ----------------");
        Enumeration<NetworkInterface> networkInterfaceEnum;
        try {
            networkInterfaceEnum = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnum.hasMoreElements()) {
                NetworkInterface netIf = networkInterfaceEnum.nextElement();
                Enumeration<InetAddress> ipAddresses = netIf.getInetAddresses();

                while (ipAddresses.hasMoreElements()) {
                    InetAddress ipAddr = ipAddresses.nextElement();
                    if (!ipAddr.isLoopbackAddress() && netIf.isUp()) {
                        String networkInterfaceName = netIf.getName();
                        Log.d(TAG, "networkInterfaceName : " + networkInterfaceName);
                        if (ipAddr.getHostAddress() != null) {
                            Log.d(TAG, "ipAddr : " + ipAddr.getHostAddress());
                        }
                    }
                }
            }
        }catch (SocketException ex) {
            Log.e(TAG, "error : " + ex.toString());
        }

    }

}
