package com.contact.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.contact.R;
import com.contact.adapters.ContactListAdapter;
import com.contact.adapters.ContactLoader;
import com.contact.pojo.Contact;
import com.contact.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<Contact>>, SwipeRefreshLayout.OnRefreshListener, ContactListAdapter.ContactAdapterListener {

    private static final int CONTACTS_LOADER_ID = 1;
    private Context mContext;

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView contactList;

    private List<Contact> mListContact = new ArrayList<>();
    private ContactListAdapter adapter;

    private static final int REQUEST_PERMISSION_CONTACT = 100;


    @Override
    public int getActivityView() {
        return R.layout.activity_main;
    }

    @Override
    public void initializeComponents() {
        mContext = MainActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionModeCallback = new ActionModeCallback();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        contactList = findViewById(R.id.contact_list);
        adapter = new ContactListAdapter(mContext,mListContact,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        contactList.setLayoutManager(mLayoutManager);
        contactList.setItemAnimator(new DefaultItemAnimator());
        contactList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        contactList.setAdapter(adapter);

        onRefresh();

    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", mContext.getPackageName());
        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = this.startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onRefresh() {
        String readContact = Manifest.permission.READ_CONTACTS;
        if (CommonUtil.checkForPermission(mContext, readContact)) {
            getLoaderManager().initLoader(CONTACTS_LOADER_ID,null,this).forceLoad();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{readContact}, REQUEST_PERMISSION_CONTACT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(CONTACTS_LOADER_ID,null,this).forceLoad();
                } else {
                    Toast.makeText(mContext,"Permission is required for further process.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = this.startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (adapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.recycle_action_mode, menu);
            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    // deleting the contact from list
                    adapter.resetAnimationIndex();
                    List<Integer> selectedItemPositions = adapter.getSelectedItems();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        adapter.removeData(selectedItemPositions.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            contactList.post(new Runnable() {
                @Override
                public void run() {
                    adapter.resetAnimationIndex();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }




    @Override
    public Loader<List<Contact>> onCreateLoader(int i, Bundle bundle) {
        return new ContactLoader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> contacts) {
        swipeRefreshLayout.setRefreshing(false);
        if (contacts != null) {

            for(Contact object : contacts)
                object.setColor(getRandomMaterialColor("400"));

            mListContact.clear();
            mListContact.addAll(contacts);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> loader) {

    }
}
