package com.contact.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.contact.R;
import com.contact.pojo.Contact;
import com.contact.utils.FlipAnimator;
import com.contact.utils.imageloader.CircleTransform;
import com.contact.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    private Context mContext;
    private List<Contact> mList;


    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    private ContactAdapterListener listener;

    public ContactListAdapter(Context mContext, List<Contact> callHistory, ContactAdapterListener listener) {
        this.mContext = mContext;
        this.mList = callHistory;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Contact contact = mList.get(position);

        holder.textContactName.setText(contact.getDisplayName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.textContactNumber.setText(PhoneNumberUtils.formatNumber(contact.getNumber(), Locale.getDefault().getCountry()));
        } else {
            holder.textContactNumber.setText(PhoneNumberUtils.formatNumber(contact.getNumber()));
        }

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // handle icon animation
        applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, contact);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.rlImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.rlContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRowClicked(position);
            }
        });

        holder.rlContactContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(MyViewHolder holder, Contact contact) {
        String picturePath = contact.getUserThumbnail();
        if (!TextUtils.isEmpty(picturePath)) {
            ImageLoader.loadImageWithCircleTransform(mContext,holder.imageProfile, Uri.parse(picturePath),R.mipmap.ic_launcher);
            holder.imageProfile.setColorFilter(null);
            holder.textProfile.setVisibility(View.GONE);
        } else {
            // displaying the first letter of From in textProfile
            if (contact.getDisplayName() != null && contact.getDisplayName().length() > 0) {
                holder.textProfile.setText(contact.getDisplayName().substring(0, 1));
                holder.imageProfile.setImageResource(R.drawable.bg_circle);
                holder.imageProfile.setColorFilter(contact.getColor());
                holder.textProfile.setVisibility(View.VISIBLE);
            } else {
                holder.imageProfile.setBackgroundResource(R.drawable.bg_circle);
                holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                holder.imageProfile.setColorFilter(null);
                holder.textProfile.setVisibility(View.GONE);
            }
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.rlImageFront.setVisibility(View.GONE);
            resetIconYAxis(holder.rlImageBack);
            holder.rlImageBack.setVisibility(View.VISIBLE);
            holder.rlImageBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.rlImageBack, holder.rlImageFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.rlImageBack.setVisibility(View.GONE);
            resetIconYAxis(holder.rlImageFront);
            holder.rlImageFront.setVisibility(View.VISIBLE);
            holder.rlImageFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.rlImageBack, holder.rlImageFront, false);
                resetCurrentIndex();
            }
        }
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

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).get_id());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        mList.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface ContactAdapterListener {
        void onIconClicked(int position);

        void onRowClicked(int position);

        void onRowLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textProfile, textContactName, textContactNumber;
        ImageView imageProfile;
        RelativeLayout rlContactContainer, rlImageContainer, rlImageBack, rlImageFront;

        MyViewHolder(View view) {
            super(view);

            rlContactContainer = view.findViewById(R.id.rl_contact_container);

            imageProfile = view.findViewById(R.id.iv_profile);
            textProfile = view.findViewById(R.id.tv_profile);
            rlImageBack = view.findViewById(R.id.rl_iv_back);
            rlImageFront = view.findViewById(R.id.rl_iv_front);
            rlImageContainer = view.findViewById(R.id.rl_image_container);


            textContactName = view.findViewById(R.id.tv_contact_name);
            textContactNumber = view.findViewById(R.id.tv_contact_number);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }
}