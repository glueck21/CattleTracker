package com.example.cattletracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.cattletracker.DatabaseDescription.Cattle;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // callback methods implemented by MainActivity
    public interface DetailFragmentListener {
        void onCattleDeleted(); // called when cattle data is deleted

        // pass Uri of cattle to edit to the DetailFragmentListener
        void onEditCattle(Uri cattleUri);
    }

    private static final int CATTLE_LOADER = 0; // identifies the Loader

    private DetailFragmentListener listener; // MainActivity
    private Uri cattleUri; // Uri of selected cattle data

    private TextView cowIdTextView; // displays cow's ID
    private TextView calfIdTextView; // displays calf's ID
    private TextView sireIdTextView; // displays sire' ID
    private TextView birthDateTextView; // displays calf' birth date
    private TextView weightTextView; // displays calf's weight
    private TextView sexTextView; // displays calf's sex
    private TextView additionalNotesTextView; // displays additional notes

    // set DetailFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // remove DetailFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when DetailFragmentListener's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // this fragment has menu items to display

        // get Bundle of arguments then extract the cattle Uri
        Bundle arguments = getArguments();

        if (arguments != null)
            cattleUri = arguments.getParcelable(MainActivity.CATTLE_URI);

        // inflate DetailFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_detail, container, false);

        // get the EditTexts
        cowIdTextView = (TextView) view.findViewById(R.id.cowIdTextView);
        calfIdTextView = (TextView) view.findViewById(R.id.calfIdTextView);
        sireIdTextView = (TextView) view.findViewById(R.id.sireIdTextView);
        birthDateTextView = (TextView) view.findViewById(R.id.birthDateTextView);
        weightTextView = (TextView) view.findViewById(R.id.weightTextView);
        sexTextView = (TextView) view.findViewById(R.id.sexTextView);
        additionalNotesTextView = (TextView) view.findViewById(R.id.additionalNotesTextView);

        // load the catte data
        getLoaderManager().initLoader(CATTLE_LOADER, null, this);
        return view;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditCattle(cattleUri); // pass Uri to listener
                return true;
            case R.id.action_delete:
                deleteCattle();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // delete cattle
    private void deleteCattle() {
        // use FragmentManager to display the confirmDelete DialogFragment
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    // DialogFragment to confirm deletion of cattle
    private final DialogFragment confirmDelete =
            new DialogFragment() {
                // create an AlertDialog and return it
                @Override
                public Dialog onCreateDialog(Bundle bundle) {
                    // create a new AlertDialog Builder
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.confirm_title);
                    builder.setMessage(R.string.confirm_delete_message);

                    // provide an OK button that simply dismisses the dialog
                    builder.setPositiveButton(R.string.button_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int button) {

                                    // use Activity's ContentResolver to invoke
                                    // delete on the AddressBookContentProvider
                                    getActivity().getContentResolver().delete(
                                            cattleUri, null, null);
                                    listener.onCattleDeleted(); // notify listener
                                }
                            }
                    );

                    builder.setNegativeButton(R.string.button_cancel, null);
                    return builder.create(); // return the AlertDialog
                }
            };

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        CursorLoader cursorLoader;

        switch (id) {
            case CATTLE_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        cattleUri, // Uri of cattle to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the cattle exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int cowIndex = data.getColumnIndex(Cattle.COLUMN_COW_ID);
            int calfIndex = data.getColumnIndex(Cattle.COLUMN_CALF_ID);
            int sireIndex = data.getColumnIndex(Cattle.COLUMN_SIRE_ID);
            int birthdateIndex = data.getColumnIndex(Cattle.COLUMN_BIRTH_DATE);
            int weightIndex = data.getColumnIndex(Cattle.COLUMN_WEIGHT);
            int sexIndex = data.getColumnIndex(Cattle.COLUMN_SEX);
            int additionalNotesIndex = data.getColumnIndex(Cattle.COLUMN_ADDITIONAL_NOTES);

            // fill TextViews with the retrieved data
            cowIdTextView.setText(data.getString(cowIndex));
            calfIdTextView.setText(data.getString(calfIndex));
            sireIdTextView.setText(data.getString(sireIndex));
            birthDateTextView.setText(data.getString(birthdateIndex));
            weightTextView.setText(data.getString(weightIndex));
            sexTextView.setText(data.getString(sexIndex));
            additionalNotesTextView.setText(data.getString(additionalNotesIndex));
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}