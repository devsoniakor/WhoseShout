package com.mad.whosetreat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.model.PlaceDto;
import com.mad.whosetreat.utilClass.Distance;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mad.whosetreat.WhoseTreatApplication.API_KEY;


/**
 * ShopAdapter is used for holding individual shop items displayed on ShopList fragment
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private static final String TAG = "ShopAdapter_TAG";
    private Context mContext;
    private List<PlaceDto.Place> mShops;
    private Set<String> mShopNames = new HashSet<>();
    private OnItemClickListener mListener;
    private double mLat;
    private double mLng;


    /**
     * constructor initiate the fields
     *
     * @param context
     * @param places
     * @param lat
     * @param lng
     */
    public ShopAdapter(Context context, List<PlaceDto.Place> places, double lat, double lng) {
        mContext = context;
        mShops = places;
        mLat = lat;
        mLng = lng;
    }

    /**
     * This method will take OnItemClickListener as parameter.
     * The own data field (mListener) will be initialised by the parpam.
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * inflates the layout and return the view representing each shop
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_shops, parent, false);
        return new ShopAdapter.ViewHolder(itemView);
    }

    /**
     * binding view and the data of each shop item
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PlaceDto.Place shop = mShops.get(position);

        try {
            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxwidth=400" +
                    "&photoreference=" + shop.getPhotos().get(0).getPhoto_reference() +
                    "&key=" + API_KEY;
            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_title_icon).into(holder.mShopIv);

        } catch (NullPointerException e) {
            holder.mShopIv.setImageResource(R.drawable.ic_title_icon);
            holder.mShopIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.mShopIv.setPadding(0, 0, 0, 7);
        }

        // initiate the Distance object for calculating the distance
        Distance dist = new Distance(mLat, mLng, shop.getGeometry().getLocation().getLat(), shop.getGeometry().getLocation().getLng());

        // setting the price tag in accordance with the returned value from Google Places API
        String priceLv = "";
        switch (shop.getPrice_level()) {
            case 0:
                priceLv = "-";
                break;
            case 1:
                priceLv = "$";
                break;
            case 2:
                priceLv = "$$";
                break;
            case 3:
                priceLv = "$$$";
                break;
            case 4:
                priceLv = "$$$$";
                break;
        }

        holder.mShopNameTv.setText(shop.getName());
        holder.mRatingBar.setActivated(false);
        holder.mRatingBar.setRating((float) shop.getRating());
        holder.mBudgetTv.setText(priceLv);
        holder.mDistanceTv.setText("" + dist.getDistance() + "M");

        holder.mShopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(holder.itemView, holder.getAdapterPosition(), shop);
                    }
                }
            }
        });

        mShopNames.add(shop.getName());

    }

    /**
     * returning the name of the shop
     *
     * @param position
     * @return
     */
    public String getName(int position) {
        return mShops.get(position).getName();
    }


    @Override
    public int getItemCount() {
        return mShops.size();
    }

    /**
     * returning the names of all availble shops
     *
     * @return
     */
    public Set<String> getShopNames() {
        return mShopNames;
    }

    /**
     * interface of OnItemClickListener.
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, PlaceDto.Place shop);
    }

    /**
     * Inner class ViewHolder holds the view for each shop
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mShopLayout;
        ImageView mShopIv;
        TextView mShopNameTv;
        RatingBar mRatingBar;
        TextView mBudgetTv;
        TextView mDistanceTv;

        public ViewHolder(View view) {
            super(view);
            mShopLayout = view.findViewById(R.id.shop_container);
            mShopIv = view.findViewById(R.id.shop_img);
            mShopNameTv = view.findViewById(R.id.shop_name_tv);
            mRatingBar = view.findViewById(R.id.rating_bar);
            mBudgetTv = view.findViewById(R.id.budget_tv);
            mDistanceTv = view.findViewById(R.id.distance_tv);

        }
    }

}
