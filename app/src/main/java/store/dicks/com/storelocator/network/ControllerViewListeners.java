package store.dicks.com.storelocator.network;

/**
 * Created by vinay on 12/22/17.
 */

public interface ControllerViewListeners {

    void onSuccess(NetworkResponseObject networkResponse);

    void onError();

    void onOffline();

}
