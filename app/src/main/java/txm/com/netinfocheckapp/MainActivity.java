package txm.com.netinfocheckapp;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    private String TAG = "NetInfoCheckApp";
    private WifiManager wiman;
    private WifiConfiguration wifiConfig = null;
    private WifiInfo wifiInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wiman = (WifiManager)getApplication().getSystemService(WIFI_SERVICE);

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
                Log.v(TAG, String.format("MAC Address : %s", wifiInfo.getMacAddress()));
                // 受信信号強度&信号レベルを取得
                int rssi = wifiInfo.getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 5);
                Log.v(TAG, String.format("RSSI : %d / Level : %d/4", rssi, level));
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiInfo = wiman.getConnectionInfo();
                try {
                    wifiConfig = wiman.getWifiApConfiguration();
                    if (wifiConfig != null) {
                        //Log.v(TAG, "wifiConfig : " + wifiConfig.toString());
                        if (wifiConfig.BSSID != null) {
                            Log.v(TAG, "BSSID : " + wifiConfig.BSSID);
                        } else Log.v(TAG, "BSSID is null.");
                        if (wifiConfig.BSSID != null) {
                            Log.v(TAG, "FQDN : " + wifiConfig.FQDN);
                        } else Log.v(TAG, "FQDN is null.");
                        if (wifiConfig.SSID != null) {
                            Log.v(TAG, "SSID : " + wifiConfig.SSID);
                        }
                        if (wifiConfig.preSharedKey != null) {
                            Log.v(TAG, "preSharedKey : " + wifiConfig.preSharedKey);
                        }
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

                wifiInfo = wiman.getConnectionInfo();
                try {
                    wifiConfig = wiman.getWifi5gApConfiguration();
                    if (wifiConfig != null) {
                        //Log.v(TAG, "wifiConfig : " + wifiConfig.toString());
                        if (wifiConfig.BSSID != null) {
                            Log.v(TAG, "BSSID : " + wifiConfig.BSSID);
                        } else Log.v(TAG, "BSSID is null.");
                        if (wifiConfig.BSSID != null) {
                            Log.v(TAG, "FQDN : " + wifiConfig.FQDN);
                        } else Log.v(TAG, "FQDN is null.");
                        if (wifiConfig.SSID != null) {
                            Log.v(TAG, "SSID : " + wifiConfig.SSID);
                        }
                        if (wifiConfig.preSharedKey != null) {
                            Log.v(TAG, "preSharedKey : " + wifiConfig.preSharedKey);
                        }
                    } else {
                        Log.v(TAG, "wifiConfig is null.");
                    }
                } catch(NoSuchMethodError e) {
                    Log.e (TAG, "NoSuchMethodError : " + e.getMessage());
                }
            }
        });
    }

    private void getLocalIpv4Address(){
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
                        Log.d(TAG, "ipAddr_name : " + ipAddr.getHostName());
                        Log.d(TAG, "ipAddr : " + ipAddr.getHostAddress());

                    }
                }
            }
        }catch (SocketException ex) {
            Log.e(TAG, "error : " + ex.toString());
        }
    }
}
