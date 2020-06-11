package com.mad.whosetreat.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.activities.ContactPickerActivity;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.utilClass.FlipAnimator;

import java.util.ArrayList;

import static android.R.color.transparent;

/**
 * NameAdapter for displaying names to the recyclerview.
 * user can selects each name for forming the group
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public static final String CONTACT_ADAPTER = "ContactAdapter";
    private static final String TAG = "ContactAdapter_TAG";
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private Context mContext;
    private OnContactClickListener mListener;
    private ArrayList<Person> mSelected = new ArrayList<>();
    private ArrayList<Person> mAllList = new ArrayList<>();
    private SparseBooleanArray selectedItems;
    private ActionMode actionMode;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;


    /**
     * constructor initiates the fields with parameters
     *
     * @param context
     * @param contList
     */
//    public ContactAdapter(ContactPickerActivity context, ArrayList<String> names, ArrayList<String> contacts) {
    public ContactAdapter(ContactPickerActivity context, ArrayList<Person> contList) {
        mContext = context;

        mAllList = contList;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    /**
     * inflates the layout for recyclerview
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_cont_pick, parent, false);
        return new ContactAdapter.ViewHolder(itemView);
    }

    /**
     * binding views and sets the contents accordingly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mNameTv.setText(mAllList.get(position).getName());
        holder.mInitialTv.setText("" + mAllList.get(position).getName().charAt(0));

        holder.mContTv.setText(mAllList.get(position).getPhone());

        // allocating random colors on initial circle
        GradientDrawable bgShape = (GradientDrawable) holder.mInitialIv.getBackground();
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + mContext.getString(R.string.material_color_code), "array", mContext.getPackageName());
        TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
        int index = (int) (Math.random() * colors.length());
        bgShape.setColor(colors.getColor(index, Color.RED));

        // applying animation on selection of the name
        if (mAllList.get(position).isSelected()) {
            holder.mContactLayout.setBackgroundColor(mContext.getResources().getColor(R.color.material_gray_300));
            holder.mInitial_f.setVisibility(View.GONE);
            resetIconYAxis(holder.mInitial_b);
            holder.mInitial_b.setVisibility(View.VISIBLE);
            holder.mInitial_b.setAlpha(1);
            resetCurrentIndex();


        } else {
            holder.mContactLayout.setBackgroundColor(mContext.getResources().getColor(transparent));
            holder.mInitial_b.setVisibility(View.GONE);
            resetIconYAxis(holder.mInitial_f);
            holder.mInitial_f.setVisibility(View.VISIBLE);
            holder.mInitial_f.setAlpha(1);
            resetCurrentIndex();

        }

        holder.mContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSelected.contains(mAllList.get(position))) {
                    mAllList.get(position).setSelected(true);
                    mSelected.add(mAllList.get(position));

                    holder.mContactLayout.setBackgroundColor(mContext.getResources().getColor(R.color.material_gray_300));

                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    holder.mInitial_f.setVisibility(View.GONE);
                    resetIconYAxis(holder.mInitial_b);
                    holder.mInitial_b.setVisibility(View.VISIBLE);
                    holder.mInitial_b.setAlpha(1);

                    FlipAnimator.flipView(mContext, holder.mInitial_b, holder.mInitial_f, true);
                    resetCurrentIndex();

                } else {
                    mAllList.get(position).setSelected(false);
                    mSelected.remove(mAllList.get(position));

                    holder.mContactLayout.setBackgroundColor(mContext.getResources().getColor(transparent));

                    holder.mInitial_b.setVisibility(View.GONE);
                    resetIconYAxis(holder.mInitial_f);
                    holder.mInitial_f.setVisibility(View.VISIBLE);
                    holder.mInitial_f.setAlpha(1);

                    FlipAnimator.flipView(mContext, holder.mInitial_b, holder.mInitial_f, false);
                    resetCurrentIndex();

                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
            }
        });


    }


    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }


    /**
     * registers the listener
     *
     * @param listener
     */
    public void setOnContactClickLister(OnContactClickListener listener) {
        this.mListener = listener;
    }

    /**
     * returns the size of the list the adapter currently have
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mAllList.size();
    }

    /**
     * allowing the parent to get the new members list
     *
     * @return
     */
    public ArrayList<Person> getSelected() {
        return mSelected;
    }


    /**
     * interface of onNameClickListener.
     */
    public interface OnContactClickListener {
        void onContactItemClick(View itemView, int position, String name, String contact);
    }

    /**
     * Inner class exteding ViewHolder to hold views
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mContactLayout;
        RelativeLayout mInitial_f;
        RelativeLayout mInitial_b;
        TextView mNameTv;
        TextView mContTv;
        TextView mInitialTv;
        ImageView mInitialIv;

        /**
         * finds and holds the togglebuttons representing each person's name
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mContactLayout = view.findViewById(R.id.cont_container);
            mInitial_f = view.findViewById(R.id.cont_initial_front);
            mInitial_b = view.findViewById(R.id.cont_initial_back);
            mNameTv = view.findViewById(R.id.cont_name_tv);
            mContTv = view.findViewById(R.id.cont_contact_tv);
            mInitialTv = view.findViewById(R.id.cont_initial_tv);
            mInitialIv = view.findViewById(R.id.cont_initial_iv);

        }
    }

}
