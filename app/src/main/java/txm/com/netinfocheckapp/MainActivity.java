package txm.com.netinfocheckapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.spinnersShown;

public class MainActivity extends AppCompatActivity {

    private String TAG = "txm_NetInfoCheckApp";
    private WifiManager wiman;
    private WifiConfiguration wifiConfig = null;
    private WifiInfo wifiInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBuildInfo();
        //telephonyInfo();

        wiman = (WifiManager)getApplication().getSystemService(WIFI_SERVICE);

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), WifiBtControllerService.class);
        startService(intent);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiInfo = wiman.getConnectionInfo();
                String BSSID = wifiInfo.getBSSID();
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

                Log.v(TAG, String.format("NetworkID : %s", wifiInfo.getNetworkId()));
                Log.v(TAG, String.format("LinkSpeed : %s", wifiInfo.getLinkSpeed()));
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    wifiConfig = wiman.getWifi5gApConfiguration();
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
            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalIpv4Address();
            }
        });

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> listServices = am.getRunningServices(Integer.MAX_VALUE);
                PackageManager pm = getPackageManager();


                for (ActivityManager.RunningServiceInfo curr : listServices) {
                    if(curr.service.getPackageName().toString().startsWith("com.kddi") ||
                            curr.service.getPackageName().toString().startsWith("txm.com") ||
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
            }
        });

        Button button_wifi_sta_off = (Button) findViewById(R.id.button_wifi_sta_off);
        button_wifi_sta_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Constants.OFF_WIFI_STA);
                    sendBroadcast(intent);
            }
        });

        Button button_wifi_sta_on = (Button) findViewById(R.id.button_wifi_sta_on);
        button_wifi_sta_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.ON_WIFI_STA);
                sendBroadcast(intent);
            }
        });

        Button button_wifi_24ap_off = (Button) findViewById(R.id.button_wifi_24ap_off);
        button_wifi_24ap_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.OFF_WIFI2G_AP);
                sendBroadcast(intent);
            }
        });

        Button button_wifi_24ap_on = (Button) findViewById(R.id.button_wifi_24ap_on);
        button_wifi_24ap_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.ON_WIFI2G_AP);
                sendBroadcast(intent);
            }
        });

        Button button_wifi_5ap_off = (Button) findViewById(R.id.button_wifi_5ap_off);
        button_wifi_5ap_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.OFF_WIFI5G_AP);
                sendBroadcast(intent);
            }
        });

        Button button_wifi_5ap_on = (Button) findViewById(R.id.button_wifi_5ap_on);
        button_wifi_5ap_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.ON_WIFI5G_AP);
                sendBroadcast(intent);
            }
        });

        Button button_bt_off = (Button) findViewById(R.id.button_bt_off);
        button_bt_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.OFF_BT);
                sendBroadcast(intent);
            }
        });
        Button button_bt_on = (Button) findViewById(R.id.button_bt_on);
        button_bt_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.ON_BT);
                sendBroadcast(intent);
            }
        });
        Button button_connectivity = (Button) findViewById(R.id.button_connectivity);
        button_connectivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startConnectivityTest230Over();
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startConnectivityTest();
                }
            }
        });
    }

    private void startConnectivityTest() {
        boolean waitNextTest;
        TestConnectivityManagerApiLv21Over test = new TestConnectivityManagerApiLv21Over(getApplicationContext());

        test.dotest1();
        test.dotest2();
        waitNextTest = test.dotest3();
        // onAvailableがコールされたら先へ進める
        // 但し、dotest3で失敗していた場合は待たない
        int count = 0;
        try {
            while (waitNextTest) {
                Thread.sleep(1000);
                waitNextTest = test.getWaitFlag();
                count++;
                if(count > 60) {
                    count = -1;
                    break;
                }
            }
        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        test.dotest4(true);
        test.dotest5();
        test.dotest6();
    }
    private void startConnectivityTest230Over() {
        boolean waitNextTest;
        TestConnectivityManagerApiLv23Over test = new TestConnectivityManagerApiLv23Over(getApplicationContext());

        test.dotest1();
        test.dotest2();
        waitNextTest = test.dotest3();
        // onAvailableがコールされたら先へ進める
        // 但し、dotest3で失敗していた場合は待たない
        int count = 0;
        try {
            while (waitNextTest) {
                Thread.sleep(1000);
                waitNextTest = test.getWaitFlag();
                count++;
                if(count > 60) {
                    count = -1;
                    break;
                }
            }
        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        test.dotest4(true);
        test.dotest5();
        test.dotest6();
    }

    private void getLocalIpv4Address(){
        Enumeration<NetworkInterface> networkInterfaceEnum;
        Log.d(TAG, "IPv4 ----------------------------------");

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
//                        if (ipAddr.getHostName() != null) {
//                            Log.d(TAG, "ipAddr_name : " + ipAddr.getHostName());
//                        }

                    }
                }
            }
        }catch (SocketException ex) {
            Log.e(TAG, "error : " + ex.toString());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.d(TAG, "Uri : " + uri.toString());
            }
        }
    }

    private void showBuildInfo(){
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
