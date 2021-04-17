package com.example.cattletracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.example.cattletracker.DatabaseDescription.Cattle;


public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // defines callback method implemented by MainActivity
    public interface AddEditFragmentListener {
        // called when cattle is saved
        void onAddEditCompleted(Uri contactUri);
    }

    // constant used to identify the Loader
    private static final int CATTLE_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri cattleUri; // Uri of selected cattle
    private boolean addingNewCattle = true; // adding (true) or editing

    // EditTexts for cattle information
    private TextInputLayout cowIdTextInputLayout;
    private TextInputLayout calfIdTextInputLayout;
    private TextInputLayout sireIdTextInputLayout;
    private TextInputLayout birthDateTextInputLayout;
    private TextInputLayout weightTextInputLayout;
    private TextInputLayout sexTextInputLayout;
    private TextInputLayout additionalNotesInputLayout;
    private FloatingActionButton saveCattleFAB;

    private CoordinatorLayout coordinatorLayout; // used with SnackBars

    // set AddEditFragmentListener when Fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // remove AddEditFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // inflate GUI and get references to EditTexts
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        cowIdTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.cowIdTextInputLayout);
        cowIdTextInputLayout.getEditText().addTextChangedListener(
                nameChangedListener);
        calfIdTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.calfIdTextInputLayout);
        sireIdTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.sireIdTextInputLayout);
        birthDateTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.birthDateTextInputLayout);
        weightTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.weightTextInputLayout);
        sexTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.sexTextInputLayout);
        additionalNotesInputLayout =
                (TextInputLayout) view.findViewById(R.id.additionalNotesTextInputLayout);

        // set FloatingActionButton's event listener
        saveCattleFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveCattleFAB.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB();

        // used to display SnackBars with brief messages
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null if creating new cattle

        if (arguments != null) {
            addingNewCattle = false;
            cattleUri = arguments.getParcelable(MainActivity.CATTLE_URI);
        }

        // if editing an existing cattle, create Loader to get the cattle
        if (cattleUri != null)
            getLoaderManager().initLoader(CATTLE_LOADER, null, this);

        return view;
    }

    // detects when the text in the cowIdTextInputLayout's EditText changes
    // to hide or show saveButtonFAB
    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {}

        // called when the text in cowIdTextInputLayout changes
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    // shows saveButtonFAB only if the cowId is not empty
    private void updateSaveButtonFAB() {
        String input =
                cowIdTextInputLayout.getEditText().getText().toString();

        // if there is a name for the contact, show the FloatingActionButton
        if (input.trim().length() != 0)
            saveCattleFAB.show();
        else
            saveCattleFAB.hide();
    }

    // responds to event generated when user saves cattle
    private final View.OnClickListener saveContactButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the virtual keyboard
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveCattle(); // save contact to the database
                }
            };

    // saves cattle information to the database
    private void saveCattle() {
        // create ContentValues object containing cattle's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cattle.COLUMN_COW_ID,
                cowIdTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_CALF_ID,
                calfIdTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_SIRE_ID,
                sireIdTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_BIRTH_DATE,
                birthDateTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_WEIGHT,
                weightTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_SEX,
                sexTextInputLayout.getEditText().getText().toString());
        contentValues.put(Cattle.COLUMN_ADDITIONAL_NOTES,
                additionalNotesInputLayout.getEditText().getText().toString());

        if (addingNewCattle) {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            Uri newCattleUri = getActivity().getContentResolver().insert(
                    Cattle.CONTENT_URI, contentValues);

            if (newCattleUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.cattle_data_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newCattleUri);
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.cattle_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    cattleUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(cattleUri);
                Snackbar.make(coordinatorLayout,
                        R.string.cattle_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.cattle_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case CATTLE_LOADER:
                return new CursorLoader(getActivity(),
                        cattleUri, // Uri of cattle to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the cattle exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int cowIdIndex = data.getColumnIndex(Cattle.COLUMN_COW_ID);
            int calfIdIndex = data.getColumnIndex(Cattle.COLUMN_CALF_ID);
            int sireIdIndex = data.getColumnIndex(Cattle.COLUMN_SIRE_ID);
            int birthDateIndex = data.getColumnIndex(Cattle.COLUMN_BIRTH_DATE);
            int weightIndex = data.getColumnIndex(Cattle.COLUMN_WEIGHT);
            int sexIndex = data.getColumnIndex(Cattle.COLUMN_SEX);
            int additionalNotesIndex = data.getColumnIndex(Cattle.COLUMN_ADDITIONAL_NOTES);

            // fill EditTexts with the retrieved data
            cowIdTextInputLayout.getEditText().setText(
                    data.getString(cowIdIndex));
            calfIdTextInputLayout.getEditText().setText(
                    data.getString(calfIdIndex));
            sireIdTextInputLayout.getEditText().setText(
                    data.getString(sireIdIndex));
            birthDateTextInputLayout.getEditText().setText(
                    data.getString(birthDateIndex));
            weightTextInputLayout.getEditText().setText(
                    data.getString(weightIndex));
            sexTextInputLayout.getEditText().setText(
                    data.getString(sexIndex));
            additionalNotesInputLayout.getEditText().setText(
                    data.getString(additionalNotesIndex));

            updateSaveButtonFAB();
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}