package feedapp.app.com.feedapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import feedapp.app.com.feedapp.NoInternetFragment;

/**
 * Created by ADMIN on 08-10-2016.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {

        Log.d("aaa","Network connectivity change");
        Toast.makeText(context, "Network connectivity change", Toast.LENGTH_SHORT).show();
        if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                Log.i("app","Network "+ni.getTypeName()+" connected");
                Toast.makeText(context, "Network"+ ni.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        }
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            Log.d("app","There's no network connectivity");
            Toast.makeText(context, "There's no network connectivity", Toast.LENGTH_SHORT).show();
            Intent mIntentNoConnection=new Intent(context, NoInternetFragment.class);
            mIntentNoConnection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntentNoConnection);

        }
    }
}
