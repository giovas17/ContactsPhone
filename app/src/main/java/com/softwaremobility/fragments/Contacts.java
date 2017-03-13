package com.softwaremobility.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softwaremobility.adapters.ContactsAdapter;
import com.softwaremobility.contactsphone.R;
import com.softwaremobility.custom.EmptyRecyclerView;
import com.softwaremobility.data.ContactsContract;

/**
 * Created by darkgeat on 3/12/17.
 */

public class Contacts extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID = 2342;
    private ProgressBar progress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmptyRecyclerView list;
    private ContactsAdapter adapter;
    private String[] projection = new String[]{
            ContactsContract.ContactsEntry.Key_Name,
            ContactsContract.ContactsEntry.Key_Group,
            ContactsContract.ContactsEntry.Key_PhotoContact,
            ContactsContract.ContactsEntry.Key_Phone
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);

        list = (EmptyRecyclerView) view.findViewById(R.id.listContacts);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshingLayoutContacts);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.green_color,R.color.orange_color,R.color.purple_color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MakeAnUpdate();
            }
        });

        progress = (ProgressBar) view.findViewById(R.id.progressBarContacts);
        TextView emptyTextView = (TextView) view.findViewById(R.id.noData);
        emptyTextView.setText(getString(R.string.no_data));
        list.setProgressView(progress);
        list.setEmptyTextView(emptyTextView);
        list.setSwipeRefreshLayout(swipeRefreshLayout);

        LinearLayoutManager laman = new LinearLayoutManager(getContext());
        list.setLayoutManager(laman);
        list.setHasFixedSize(true);

        adapter = new ContactsAdapter(getContext());
        list.setAdapter(adapter);

        return view;
    }

    private void MakeAnUpdate() {
        getLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ContactsContract.ContactsEntry.Key_Name + " ASC";
        Uri uri = ContactsContract.ContactsEntry.buildContactsUriParams(null,null);
        return new CursorLoader(getContext(),uri,projection,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
