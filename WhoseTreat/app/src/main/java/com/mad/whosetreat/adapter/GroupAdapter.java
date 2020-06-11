package com.mad.whosetreat.adapter;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupAdapter is used for showing user existing groups they can select for random draw.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private static final String TAG = "GroupAdapter_TAG";
    private Context mContext;
    private List<Group> mGroupList = new ArrayList<>();
    private OnGroupClickListener mListener;

    /**
     * constructor initiate own fields with parameters
     *
     * @param context
     * @param groups
     */
    public GroupAdapter(Context context, List<Group> groups) {

        mContext = context;
        mGroupList = groups;
    }


    /**
     * register the listener
     *
     * @param listener
     */
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.mListener = listener;
    }


    /**
     * inflates the view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.ViewHolder(itemView);
    }

    /**
     * links views and sets the contents of them
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // setting the group name and members into the view
        holder.mGroupNameTv.setText(mGroupList.get(position).getName());
        String members = "";
        for (int i = 0; i < mGroupList.get(position).getPerson().size(); i++) {
            members += mGroupList.get(position).getPerson().get(i).getName();
            if (i != mGroupList.get(position).getPerson().size() - 1) {
                members += ", ";
            }
        }
        holder.mGroupMemberTv.setText(members);


        holder.mGroupContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGroupClicks(v, mGroupList.get(position), position);
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });

        holder.mGroupInitialTv.setText(mGroupList.get(position).getName().substring(0, 1));


    }

    /**
     * returns the number of list
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    public void addGroup(Group group) {
        mGroupList.add(group);
        notifyDataSetChanged();
    }

    public void removeGroup(String groupName) {
        for (int i = 0; i < mGroupList.size(); i++) {
            if (mGroupList.get(i).getName().equals(groupName)) {
                mGroupList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public void changeList(List<Group> groups) {
        mGroupList = groups;
        notifyDataSetChanged();
    }

    /**
     * interface of OnGroupClickListener.
     */
    public interface OnGroupClickListener {
        void onGroupClicks(View itemView, Group group, int position);
    }

    /**
     * Inner class ViewHolder holds the view for each shop
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        //        CardView mGroupCv;
        LinearLayout mGroupContainer;
        TextView mGroupNameTv;
        TextView mGroupMemberTv;
        ImageView mInitialIv;
        TextView mGroupInitialTv;


        /**
         * links the view and hold the place
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mGroupContainer = view.findViewById(R.id.group_container);
            mGroupNameTv = view.findViewById(R.id.group_pref_name_tv);
            mGroupMemberTv = view.findViewById(R.id.group_member_tv);
            mInitialIv = view.findViewById(R.id.g_initial_iv);
            mGroupInitialTv = view.findViewById(R.id.g_initial_tv);
        }
    }
}
