package com.example.cattletracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements CattleFragment.CattleFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    // key for storing cattle Uri in a Bundle passed to a fragment
    public static final String CATTLE_URI = "cattle_uri";

    private CattleFragment cattleFragment; // displays cattle list

    // display CattleFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if layout contains fragmentContainer, the phone layout is in use;
        // create and display a CattleFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create CattleFragment
            cattleFragment = new CattleFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, cattleFragment);
            transaction.commit(); // display CattleFragment
        }
        else {
            cattleFragment =
                    (CattleFragment) getSupportFragmentManager().
                            findFragmentById(R.id.cattleFragment);
        }
    }

    // display DetailFragment for selected cattle data
    @Override
    public void onCattleSelected(Uri cattleUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayCattle(cattleUri, R.id.fragmentContainer);
        else { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            displayCattle(cattleUri, R.id.rightPaneContainer);
        }
    }

    // display AddEditFragment to add new cattle data
    @Override
    public void onAddCattle() {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // display cattle data
    private void displayCattle(Uri cattleUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify cattle Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(CATTLE_URI, cattleUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }

    // display fragment for adding a new or editing an existing cattle data
    private void displayAddEditFragment(int viewID, Uri cattleUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing cattle data, provide cattleUri as an argument
        if (cattleUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CATTLE_URI, cattleUri);
            addEditFragment.setArguments(arguments);
        }

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // return to cattle list when displayed cattle data deleted
    @Override
    public void onCattleDeleted() {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        cattleFragment.updateCattleList(); // refresh cattle data
    }

    // display the AddEditFragment to edit an existing cattle
    @Override
    public void onEditCattle(Uri cattleUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, cattleUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, cattleUri);
    }

    // update GUI after new cattle data or updated cattle data saved
    @Override
    public void onAddEditCompleted(Uri cattleUri) {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        cattleFragment.updateCattleList(); // refresh cattle

        if (findViewById(R.id.fragmentContainer) == null) { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            // on tablet, display contact that was just added or edited
            displayCattle(cattleUri, R.id.rightPaneContainer);
        }
    }
}