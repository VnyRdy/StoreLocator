package store.dicks.com.storelocator.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import store.dicks.com.storelocator.R;
import store.dicks.com.storelocator.model.Venue;

/**
 * Created by vinay on 12/22/17.
 */

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder>{

    List<Venue> mVenueList;
    onClickStoreVenue storeVenueListener;

    public StoreListAdapter(List<Venue> mVenueList, onClickStoreVenue storeVenueListener) {
        this.mVenueList = mVenueList;
        this.storeVenueListener = storeVenueListener;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_adapter_row, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, final int position) {

        DecimalFormat distanceDecimalFormat = new DecimalFormat("#.00");
        holder.city.setText(mVenueList.get(position).getLocation().getCity());
        holder.address.setText(mVenueList.get(position).getLocation().getAddress());
        holder.rating.setText("Customer rating  "+mVenueList.get(position).getRating());
        holder.distance.setText("Ditance "+distanceDecimalFormat.format(mVenueList.get(position).getDistance()));
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeVenueListener.venueRowInformation(mVenueList.get(position));
            }
        });

        Log.i("Adapter",mVenueList.get(position).getDistance()+" ****** "+mVenueList.get(position).getRating());

    }

    @Override
    public int getItemCount() {
        return mVenueList.size();
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {

        TextView city;
        TextView address;
        TextView distance;
        TextView rating;
        ConstraintLayout constraintLayout;

        public StoreViewHolder(View itemView) {
            super(itemView);
            city = (TextView) itemView.findViewById(R.id.city);
            address = (TextView) itemView.findViewById(R.id.address);
            rating = (TextView) itemView.findViewById(R.id.rating);
            distance = (TextView) itemView.findViewById(R.id.distance);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.store_content_row);
        }
    }

   public interface onClickStoreVenue{
       void venueRowInformation(Venue venue);
    }
}
