package txm.com.netinfocheckapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

class TestConnectivityManagerApiLv21Over {
	private static final String TAG = "TestCMApiLv21Over";
	private Context context = null;
	ConnectivityManager cm = null;
	private NetworkCallback mNetWorkCallback = null;
	private Network mNetwork = null;
	private boolean waitNextTest = true;

	
	public TestConnectivityManagerApiLv21Over(Context context) {
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
        getState(ConnectivityManager.TYPE_MOBILE_HIPRI);
	}
	

	public boolean dotest3() {
        Log.d(TAG, "dotest3 : ");
        return requestNetwork();
    }

	public void dotest4(boolean flag) {
        boolean ret;
		if (flag ) {
            ret = ConnectivityManager.setProcessDefaultNetwork(mNetwork);
            Log.d(TAG, "dotest4 : " + ret);
            getState(ConnectivityManager.TYPE_MOBILE_HIPRI);
        }
		else {
			ConnectivityManager.setProcessDefaultNetwork(null);
		}
	}
	

	public void dotest5() {
        Network nt = ConnectivityManager.getProcessDefaultNetwork();
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
		getState(ConnectivityManager.TYPE_MOBILE_HIPRI);
	}

    public boolean getWaitFlag(){
        return waitNextTest;
    }

	private void getState(int networkType) {

		NetworkInfo ni = cm.getNetworkInfo(networkType);
		if(ni == null) {
			Log.d(TAG, "getNetworkInfo(" + networkType + ") is null");
		}
		Log.d(TAG,"TYPE_MOBILE(0)       : "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState());
		Log.d(TAG,"TYPE_WIFI(1)         : "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState());
        Log.d(TAG,"TYPE_MOBILE_HIPRI(5) : "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI).getState());
    }

	private boolean requestNetwork() {
		boolean ret = true;
		if(cm == null) {
			Log.d(TAG, "ConnectivityManager is null");
			ret = false;
		} else {
			NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI);
			if(ni == null) {
				Log.d(TAG, "getNetworkInfo(TYPE_MOBILE_HIPRI) is null");
			} else {
				NetworkRequest.Builder netReqBuilder = new  NetworkRequest.Builder();
				netReqBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
				netReqBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
				NetworkRequest networkRequest = netReqBuilder.build();
				
				mNetWorkCallback = new ConnectivityManager.NetworkCallback() {
					@Override
					public void onAvailable(Network network) {
                        Log.d(TAG, "NetworkCallback : onAvailable");
                        if (waitNextTest){
                            mNetwork = network;
                            waitNextTest = false;
                        }
					}

                    @Override
                    public void onLost(Network network) {
                        // Networkを失った時、そもそも失敗した時に呼ばれる
                        super.onLost(network);
                        // 必要な処理をします。実装によっては特に継承する必要はないかもしれません。
                    }
				};
				
				cm.requestNetwork(networkRequest, mNetWorkCallback);
			}
		}
		return ret;
	}
}
