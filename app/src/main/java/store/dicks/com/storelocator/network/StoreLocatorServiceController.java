package store.dicks.com.storelocator.network;


import android.text.TextUtils;

import java.io.Serializable;

import store.dicks.com.storelocator.model.VenueDTO;

public class StoreLocatorServiceController implements NetworkListeners{

    ControllerViewListeners controllerViewListeners;

    public StoreLocatorServiceController(ControllerViewListeners controllerViewListeners) {
        this.controllerViewListeners = controllerViewListeners;
    }

    public void retrieveVenue(){
        new NetworkManager(this).execute("https://movesync-qa.dcsg.com/dsglabs/mobile/api/venue/");
    }


    @Override
    public void onSuccess(String response) {
        if(response != null && !response.equals("")){
            Serializable venueSerializable = NetworkResponseParser.getInstance().parse(response, VenueDTO.class);
            parseNetworkResponse(venueSerializable != null ? (VenueDTO)venueSerializable : null);
            return;
        }
        controllerViewListeners.onError();
    }

    private void parseNetworkResponse(VenueDTO venueDTO) {

        controllerViewListeners.onSuccess(new NetworkResponseObject(venueDTO));

    }

    @Override
    public void onError() {
        controllerViewListeners.onError();
    }

    @Override
    public void onOffline() {
        controllerViewListeners.onOffline();
    }
}
