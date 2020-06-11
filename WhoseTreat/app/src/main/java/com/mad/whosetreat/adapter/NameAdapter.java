package com.mad.whosetreat.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.activities.AddMemberActivity;
import com.mad.whosetreat.dialog.ConfirmDialog;
import com.mad.whosetreat.model.Person;
import com.mad.whosetreat.utilClass.FlipAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.color.transparent;
import static com.mad.whosetreat.dialog.NewGroupDialogueFragment.CODE;

/**
 * NameAdapter for displaying names to the recyclerview.
 * user can selects each name for forming the group
 */
public class NameAdapter extends RecyclerView.Adapter<NameAdapter.ViewHolder> {

    public static final String NAME_ADAPTER = "name adapter";
    private static final String TAG = "NameAdapter_TAG";
    private static final String NAME_DELETION = "name deletion confirm dialog";
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private Context mContext;
    private onNameClickListener mListener;
    private List<Person> mAllMembers = new ArrayList<>();
    private List<Person> mNewGroupMembers = new ArrayList<>();
    private SparseBooleanArray selectedItems;
    private ActionMode actionMode;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private Person mDelPerson;
    private int mDelId;


    /**
     * constructor initiates the fields with parameters
     *
     * @param context
     * @param allPeople
     */
    public NameAdapter(AddMemberActivity context, List<Person> allPeople) {
        mContext = context;
        mAllMembers = allPeople;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        // sorting list according to person's name
        Collections.sort(mAllMembers, new Comparator<Person>() {
            public int compare(Person v1, Person v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });
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
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_name_pick, parent, false);
        return new NameAdapter.ViewHolder(itemView);
    }

    /**
     * binding views and sets the contents accordingly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mNameTv.setText(mAllMembers.get(position).getName());

        holder.mInitialTv.setText("" + mAllMembers.get(position).getName().charAt(0));

        GradientDrawable bgShape = (GradientDrawable) holder.mInitialIv.getBackground();
        bgShape.setColor(mAllMembers.get(position).getColor());

        if (mNewGroupMembers.contains(mAllMembers.get(position))) {
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
                if (!mNewGroupMembers.contains(mAllMembers.get(position))) {
                    mAllMembers.get(position).setSelected(true);
                    mNewGroupMembers.add(mAllMembers.get(position));

                    holder.mContactLayout.setBackgroundColor(mContext.getResources().getColor(R.color.material_gray_300));

                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    holder.mInitial_f.setVisibility(View.GONE);
                    resetIconYAxis(holder.mInitial_b);
                    holder.mInitial_b.setVisibility(View.VISIBLE);
                    holder.mInitial_b.setAlpha(1);

                    FlipAnimator.flipView(mContext, holder.mInitial_b, holder.mInitial_f, true);
                    resetCurrentIndex();

                } else {

                    mAllMembers.get(position).setSelected(false);
                    mNewGroupMembers.remove(mAllMembers.get(position));

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

        holder.mContactLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                ConfirmDialog delDialog = new ConfirmDialog();
                Bundle bdl1 = new Bundle();
                bdl1.putString(CODE, NAME_ADAPTER);
                delDialog.setArguments(bdl1);
                delDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), NAME_DELETION);
                Person person = mAllMembers.get(position);
                mDelPerson = person;
                mDelId = person.getId();

                return true;
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
    public void setOnNameClickLister(onNameClickListener listener) {
        this.mListener = listener;
    }

    /**
     * returns the size of the list the adapter currently have
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mAllMembers.size();
    }

    /**
     * allowing the parent to get the new members list
     *
     * @return
     */
    public List<Person> getNewMembers() {
        return mNewGroupMembers;
    }

    public void addPerson(Person person) {
        mAllMembers.add(person);
        mNewGroupMembers.add(person);
        notifyDataSetChanged();
    }

    public void removePerson(Person person) {
        mAllMembers.remove(person);
        notifyDataSetChanged();
    }

    public Person getDelPerson() {
        return mDelPerson;
    }


    public int getDelId() {
        return mDelId;
    }


    /**
     * interface of onNameClickListener.
     */
    public interface onNameClickListener {
        void onItemClick(View itemView, int position, Person person);
    }

    /**
     * Inner class exteding ViewHolder to hold views
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContactLayout;
        RelativeLayout mInitial_f;
        RelativeLayout mInitial_b;
        TextView mNameTv;
        TextView mInitialTv;
        ImageView mInitialIv;

        /**
         * finds and holds the togglebuttons representing each person's name
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mContactLayout = view.findViewById(R.id.name_container);
            mInitial_f = view.findViewById(R.id.initial_front);
            mInitial_b = view.findViewById(R.id.initial_back);
            mNameTv = view.findViewById(R.id.name_name_tv);
            mInitialTv = view.findViewById(R.id.initial_tv);
            mInitialIv = view.findViewById(R.id.initial_iv);
        }
    }

}
