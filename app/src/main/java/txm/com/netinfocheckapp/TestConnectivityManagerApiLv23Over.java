package txm.com.netinfocheckapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

class TestConnectivityManagerApiLv23Over {
	private static final String TAG = "TestCMApiLv23Over";
	private Context context = null;
	ConnectivityManager cm = null;
	private NetworkCallback mNetWorkCallback = null;
	private Network mNetwork = null;
	private boolean waitNextTest = true;


	public TestConnectivityManagerApiLv23Over(Context context) {
		this.context = context;
	}

	// boolean true:成功 / false:失敗(null)
	public boolean dotest1() {
        Log.d(TAG, "dotest1 : ");
        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Log.d(TAG, "instance is " + cm.toString());
		if(cm == null) {
			return false;
		}
		return true;
	}

	public void dotest2() {
        Log.d(TAG, "dotest2 : ");
        getState();
        return;
	}


	public boolean dotest3() {
        Log.d(TAG, "dotest3 : ");
        return requestNetwork();
	}

	public boolean dotest4(boolean flag) {
        Log.d(TAG, "dotest4 : ");
        boolean ret = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (flag) {
				ret = cm.bindProcessToNetwork(mNetwork);
			} else {
				ret = cm.bindProcessToNetwork(null);
			}
			return ret;
		}
        getState();
		return ret;
	}


	public void dotest5() {
        Network nt = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nt = cm.getBoundNetworkForProcess();
        }
        if (nt != null) {
            Log.d(TAG, "dotest5 is pass");
        }else{
            Log.d(TAG, "dotest5 is fail");
        }
    }

	public void dotest6() {
        Log.d(TAG, "dotest6 : ");
        if ((cm != null) && (mNetWorkCallback != null)) {
			cm.unregisterNetworkCallback(mNetWorkCallback);
		}

		/* 3G/4Gが切断されるのを少し待つ */
		int count = 0;
		while (true) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			count++;
			if(count > 3) {
				count = -1;
				break;
			}
		}
        getState();
		return;
	}

	private void getState() {
		if(cm == null) {
			Log.d(TAG, "ConnectivityManager is null");
		}

		NetworkInfo.State state = NetworkInfo.State.DISCONNECTED;
		Network[] networks = cm.getAllNetworks();
		for(Network network : networks){
			NetworkInfo ni = cm.getNetworkInfo(network);

			switch(ni.getType()){
			case ConnectivityManager.TYPE_MOBILE:	Log.d(TAG, "TYPE_MOBILE(0) : " + ni.getState());
                break;
			case ConnectivityManager.TYPE_WIFI:		Log.d(TAG, "TYPE_WIFI(1)   : " + ni.getState());
                break;
			}

			NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
			if(capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
				state = ni.getState();
                Log.d(TAG, "CELLULAR & INTERNET is " + state);
			}
		}

		return;
	}

	public boolean getWaitFlag() {
		return waitNextTest;
	}

	private boolean requestNetwork() {
		boolean ret = true;
		if(cm == null) {
			Log.d(TAG, "ConnectivityManager is null");
			ret = false;
		} else {
			NetworkInfo ni = null;
			Network[] networks = cm.getAllNetworks();
			for(Network network : networks){
				NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
				if(capabilities != null
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
					ni = cm.getNetworkInfo(network);
					break;
				}
			}

			if(ni != null) {
				Log.d(TAG, "NET_CAPABILITY_INTERNET and TRANSPORT_CELLULAR is already conntected.");
			} else {
				NetworkRequest.Builder netReqBuilder = new  NetworkRequest.Builder();
				netReqBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
				netReqBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
				NetworkRequest networkRequest = netReqBuilder.build();

				mNetWorkCallback = new ConnectivityManager.NetworkCallback() {
					@Override
					public void onAvailable(Network network) {

						if (waitNextTest != false) {
							mNetwork = network;
                            waitNextTest = false;
						}
					}
				};

				cm.requestNetwork(networkRequest, mNetWorkCallback);
			}
		}
		return ret;
	}
}
