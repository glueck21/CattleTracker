package com.example.cattletracker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.cattletracker.DatabaseDescription.Cattle;


public class CattleFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // callback method implemented by MainActivity
    public interface CattleFragmentListener {
        // called when cattle selected
        void onCattleSelected(Uri cattleUri);

        // called when add button is pressed
        void onAddCattle();
    }

    private static final int CATTLE_LOADER = 0; // identifies Loader

    // used to inform the MainActivity when a cattle is selected
    private CattleFragmentListener listener;

    private CattleAdapter cattleAdapter; // adapter for recyclerView

    // configures this fragment's GUI
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // inflate GUI and get reference to the RecyclerView
        View view = inflater.inflate(
                R.layout.fragment_cattle, container, false);
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView should display items in a vertical list
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        // create recyclerView's adapter and item click listener
        cattleAdapter = new CattleAdapter(
                new CattleAdapter.CattleClickListener() {
                    @Override
                    public void onClick(Uri cattleUri) {
                        listener.onCattleSelected(cattleUri);
                    }
                }
        );
        recyclerView.setAdapter(cattleAdapter); // set the adapter

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes
        recyclerView.setHasFixedSize(true);

        // get the FloatingActionButton and configure its listener
        FloatingActionButton addButton =
                (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // displays the AddEditFragment when FAB is touched
                    @Override
                    public void onClick(View view) {
                        listener.onAddCattle();
                    }
                }
        );

        return view;
    }

    // set CattleFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (CattleFragmentListener) context;
    }

    // remove CattleFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // initialize a Loader when this fragment's activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CATTLE_LOADER, null, this);
    }

    // called from MainActivity when other Fragment's update database
    public void updateCattleList() {
        cattleAdapter.notifyDataSetChanged();
    }

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case CATTLE_LOADER:
                return new CursorLoader(getActivity(),
                        Cattle.CONTENT_URI, // Uri of cattle table
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        Cattle.COLUMN_COW_ID + " COLLATE NOCASE ASC"); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cattleAdapter.swapCursor(data);
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cattleAdapter.swapCursor(null);
    }
}