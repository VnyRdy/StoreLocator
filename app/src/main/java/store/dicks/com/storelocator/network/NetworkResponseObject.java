package store.dicks.com.storelocator.network;

public class NetworkResponseObject {


    private Object mResponseDataObject;

    public NetworkResponseObject(Object responseDataObject)
    {

        mResponseDataObject = responseDataObject;
    }

    public Object getResponseDataObject()
    {
        return mResponseDataObject;
    }

    public void setResponseDataObject(Object responseDataObject)
    {
        mResponseDataObject = responseDataObject;
    }
}
