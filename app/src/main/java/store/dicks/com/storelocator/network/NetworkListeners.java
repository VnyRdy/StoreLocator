package store.dicks.com.storelocator.network;


public interface NetworkListeners {
    void onSuccess(String response);
    void onError();
    void onOffline();
}
