package com.mad.whosetreat.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.whosetreat.R;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Person;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.core.content.ContextCompat.getDrawable;
import static com.mad.whosetreat.activities.GroupsInfoActivity.REQUEST_GALLERY;

/**
 * GroupInfoAdapter is for showing the existing groups to user for further manipulation.
 * User can deletes or reset groups.
 */
public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ViewHolder> {

    public static final String DELETE = "option_delete";
    public static final String RESET = "option_reset";
    public static final String DETAIL = "option_detail";
    private static final String TAG = "GroupInfoAdapter_TAG";
    private Context mContext;
    private Activity mActivity;
    private List<Group> mGroupList;
    private GroupInfoAdapter.OnGroupInfoClickListener mListener;
    private ImageView mTargetIv;
    private Group mTargetGroup;
    private static int sIndex;


    /**
     * constructor instantiates the fields
     *
     * @param context
     * @param groups
     */
    public GroupInfoAdapter(Context context, List<Group> groups) {
        mContext = context;
        mActivity = (Activity) context;
        mGroupList = groups;

//        Log.d(TAG, "GroupInfoAdapter list size: " + mGroupList.size());
    }

    public void resetGroupList(List<Group> groups) {
        mGroupList = groups;
    }

    /**
     * sets the layout of the each Recyclerview
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_group_info, parent, false);
        return new GroupInfoAdapter.ViewHolder(itemView);
    }

    /**
     * links the view and sets the contents of them
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Group group = mGroupList.get(position);

        holder.mGroupNameTv.setText(group.getName());
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (group.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(group.getImage());
            holder.mProfileIV.setImageBitmap(bitmap);
            holder.mProfileIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.mProfileIV.setImageDrawable(getDrawable(mContext, R.drawable.ic_children));
            holder.mProfileIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // for generating table rows dynamically
        if (holder.mTable.getChildCount() < group.getPerson().size()) {

            // sorting list according to person's name
            Collections.sort(group.getPerson(), new Comparator<Person>() {
                public int compare(Person v1, Person v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });

            //dynamically generates the table rows
            for (int i = 0; i < group.getPerson().size(); i++) {
                TableRow row = (TableRow) inflater.inflate(R.layout.attribute_row, null);
                TextView nameTv = (TextView) row.getVirtualChildAt(0);
                TextView treatTv = (TextView) row.getVirtualChildAt(1);
                nameTv.setText(group.getPerson().get(i).getName());
                treatTv.setText("" + group.getPerson().get(i).getCount());

                holder.mTable.addView(row, i + 1);
            }
        } else {
            // sorting list according to person's name
            Collections.sort(group.getPerson(), new Comparator<Person>() {
                public int compare(Person v1, Person v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });
//            Log.d(TAG, "child count" + holder.mTable.getChildCount());

            //dynamically generates the table rows and deletes the existing rows
            for (int i = 1; i <= group.getPerson().size(); i++) {
                TableRow row = (TableRow) inflater.inflate(R.layout.attribute_row, null);
                TextView nameTv = (TextView) row.getVirtualChildAt(0);
                TextView treatTv = (TextView) row.getVirtualChildAt(1);
                nameTv.setText(group.getPerson().get(i - 1).getName());
                treatTv.setText("" + group.getPerson().get(i - 1).getCount());

                holder.mTable.removeViewAt(i);
                holder.mTable.addView(row, i);
            }
        }


        // set onclick listener to reset and delete option
        // when they are clicked, the dialog will pop up and ask user for confirmation
        final int pos = position;
        holder.mResetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mListener.onGroupClick(v, pos, group, RESET);
            }
        });

        holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mListener.onGroupClick(v, pos, group, DELETE);
            }
        });

        holder.mGroupInfoCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mListener.onGroupClick(v, pos, group, DETAIL);
            }
        });

        holder.mProfileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTargetIv = holder.mProfileIV;
                sIndex = pos;
                mTargetGroup = group;
                getImageFromGallery();
            }
        });


    }

    public void getImageFromGallery() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            ((Activity) mContext).startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_GALLERY);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

    }

    public void setGroupImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        mTargetIv.setImageBitmap(BitmapFactory.decodeFile(imagePath, options));
        mTargetIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mGroupList.get(sIndex).setImage(imagePath);
    }

    public static int getTargetIndex() {
        return sIndex;
    }

    /**
     * return the number of items currently is holding
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    /**
     * register the listener
     *
     * @param mListener
     */
    public void setOnGroupInfoClickListener(OnGroupInfoClickListener mListener) {
        this.mListener = mListener;
    }

    public void removeGroup(int index) {
        mGroupList.remove(mGroupList.get(index));
        notifyDataSetChanged();
    }

    public void resetGroup(int index) {

        Group group = mGroupList.get(index);
        for (int i = 0; i < group.getPerson().size(); i++) {
            mGroupList.get(mGroupList.indexOf(group)).getPerson().get(i).setCount(0);
        }
        notifyDataSetChanged();
    }


    /**
     * interface of OnGroupInfoClickListener.
     */
    public interface OnGroupInfoClickListener {
        void onGroupClick(View itemView, int position, Group group, String option);
    }

    /**
     * Inner class ViewHolder holding views
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        CardView mGroupInfoCv;
        TextView mGroupNameTv;
        TableLayout mTable;
        TextView mMemberTv;
        TextView mTreatTv;
        TextView mDeleteTv;
        TextView mResetTv;
        ImageView mProfileIV;

        /**
         * find and link view to the correct position
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mGroupInfoCv = view.findViewById(R.id.group_info_cv);
            mGroupNameTv = view.findViewById(R.id.group_info_name_tv);
            mTable = view.findViewById(R.id.group_info_table);
            mMemberTv = view.findViewById(R.id.member_name_tv);
            mTreatTv = view.findViewById(R.id.member_treat_tv);
            mDeleteTv = view.findViewById(R.id.group_option2);
            mResetTv = view.findViewById(R.id.group_option1);
            mProfileIV = view.findViewById(R.id.group_info_profile_iv);


        }
    }
}
