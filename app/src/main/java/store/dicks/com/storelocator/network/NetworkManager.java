package store.dicks.com.storelocator.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import store.dicks.com.storelocator.StoreLocatorApplication;

public class NetworkManager {

    public static final String TAG = NetworkManager.class.getSimpleName();

    NetworkListeners networkListeners;

    public NetworkManager(NetworkListeners networkListeners) {
        this.networkListeners = networkListeners;
    }

    public void execute(String url) {
        if (isOnline()) {
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response.toString());
                    networkListeners.onSuccess(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkListeners.onError();
                }

            });

// Adding request to request queue
            StoreLocatorApplication.INSTANCE.addToRequestQueue(strReq, "venuesList");
        } else {
            networkListeners.onOffline();
        }

    }

    public static boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) StoreLocatorApplication
                .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


}
