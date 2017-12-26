package store.dicks.com.storelocator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import store.dicks.com.storelocator.model.Venue;

public class StoreDetailsActivity extends Activity {

    ImageView storeImageView;
    TextView address;
    TextView city;
    TextView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        Venue venue;
        if(getIntent() != null){
            venue = (Venue) getIntent().getSerializableExtra(StoreListActivity.STORE_KEY);
            initializeViews();
            setData(venue);
        }

    }

    private void initializeViews() {

        contact = (TextView) findViewById(R.id.contact);
        address = (TextView) findViewById(R.id.address);
        city = (TextView) findViewById(R.id.City);
        storeImageView = (ImageView) findViewById(R.id.store_image);
        storeImageView.setImageDrawable(getResources().getDrawable(R.drawable.noimage));
    }


    private void setData(Venue venue){
        if(venue.getLocation() != null){
            city.setText(venue.getLocation().getCity());
            address.setText(venue.getLocation().getAddress());
        }
        if(venue.getContacts() != null && venue.getContacts().size() > 0){
            contact.setText(venue.getContacts().get(0).getPhone());
        }
        if(venue.getPhotos() != null && venue.getPhotos().size() > 0){
            ImageRequest mImageRequest = new ImageRequest(venue.getPhotos().get(0).getUrl(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    storeImageView.setImageBitmap(bitmap);
                }
            }, 0, 0, null, null);
            StoreLocatorApplication.INSTANCE.addToRequestQueue(mImageRequest, "StoreImage");
        }
    }
}
