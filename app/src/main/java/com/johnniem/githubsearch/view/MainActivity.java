package com.johnniem.githubsearch.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.johnniem.githubsearch.MVP;
import com.johnniem.githubsearch.R;
import com.johnniem.githubsearch.model.POJOs.Items;
import com.johnniem.githubsearch.model.adapter.ItemsAdapter;
import com.johnniem.githubsearch.presenter.ListPresenter;
import com.johnniem.githubsearch.common.GenericActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Activity prompts the user for the search keyword which
 * will used for GitHub repository search.
 * It plays the role of the "View" in the
 * Model-View-Presenter (MVP) pattern.  It extends GenericActivity
 * that provides a framework to automatically handle runtime
 * configuration changes of an ImagePresenter object, which plays the
 * role of the "Presenter" in the MVP pattern.  The
 * MPV.RequiredViewOps and MVP.ProvidedPresenterOps interfaces are
 * used to minimize dependencies between the View and Presenter
 * layers.
 */
public class MainActivity
        extends GenericActivity<MVP.RequiredViewOps,
        MVP.ProvidedPresenterOps,
        ListPresenter>
        implements MVP.RequiredViewOps {

    public static final String RepUrlExtra = "repUrlString";

    // Settings dialog views
    RadioGroup mRadioSortByGroup;
    RadioButton mRadioStars;
    RadioButton mRadioForks;
    RadioButton mRadioUpdated;
    RadioGroup mRadioOrderGroup;
    RadioButton mRadioDesc;
    RadioButton mRadioAsc;
    CheckBox mCheckboxName;
    CheckBox mCheckboxDescription;
    CheckBox mCheckboxReadme;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.progressBar_loading)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.keywords)
    TextView keywordTextView;
    @BindView(R.id.repositoriesListView)
    ListView repositoriesListView;

    ItemsAdapter mItemsAdapter;

    // Dialog data
    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // (Re)initialize all the View fields.
        initializeViewFields();

        // Perform second part of initializing the super class,
        // passing in the ListPresenter class to instantiate/manage
        // and "this" to provide ListPresenter with the
        // MVP.RequiredViewOps instance.
        super.onCreate(ListPresenter.class,
                this);
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    @Override
    protected void onDestroy() {
        // Destroy the presenter layer, passing in whether this is
        // triggered by a runtime configuration or not.
        getPresenter().onDestroy(isChangingConfigurations());

        // Always call super class for necessary operations when
        // stopping.
        super.onDestroy();
    }

    /**
     * Initialize the View fields.
     */
    private void initializeViewFields() {
        ButterKnife.bind(this);

        repositoriesListView.setOnScrollListener(new RepoOnScrollListener());

        setSupportActionBar(toolbar);

        // set Floating Action Button to start data download on click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().startDataDownload(keywordTextView.getText().toString());
            }
        });

        // prevent floationg action button from being covered by keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getPresenter().settingsButtonClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dismissSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(repositoriesListView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Make the ProgressBar visible.
     */
    @Override
    public void displayProgressBar() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Make the ProgressBar invisible.
     */
    @Override
    public void dismissProgressBar() {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void refreshRepoList(ArrayList<Items> itemsList) {
        // refresh ListView
        mItemsAdapter.addAll(itemsList);
        mItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void resetItemsAdapter() {
        mItemsAdapter.clear();
    }

    @Override
    public void reportStatus(String status) {
        Snackbar.make(findViewById(R.id.content_main), status, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void displayRepoDetails(String url) {
        // Open an Activity for displaying the repository details.
        Intent intent = new Intent(this, RepDetailsActivity.class);
        intent.putExtra(RepUrlExtra, url);
        startActivity(intent);
    }

    @Override
    public void displaySearchSettings(String searchQualifiers, String sortValue, String orderValue) {
        mDialog = createDialog(searchQualifiers, sortValue, orderValue);

        mDialog.show();
    }

    @Override
    public void dismissSearchSettings() {
        mDialog.dismiss();
    }

    @Override
    public void initAdapter(ArrayList<Items> itemsList) {
        mItemsAdapter = new ItemsAdapter(this, itemsList);
        repositoriesListView.setAdapter(mItemsAdapter);
        mItemsAdapter.notifyDataSetChanged();
    }

    public Dialog createDialog(String searchQualifiers, String sortValue, String orderValue) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.settings_dialog);
        dialog.setTitle("Search options");

        mRadioSortByGroup = (RadioGroup) dialog.findViewById(R.id.radioSortBy);
        mRadioStars = (RadioButton) dialog.findViewById(R.id.radioStars);
        mRadioForks = (RadioButton) dialog.findViewById(R.id.radioForks);
        mRadioUpdated = (RadioButton) dialog.findViewById(R.id.radioUpdated);
        mRadioOrderGroup = (RadioGroup) dialog.findViewById(R.id.radioOrder);
        mRadioDesc = (RadioButton) dialog.findViewById(R.id.radioDesc);
        mRadioAsc = (RadioButton) dialog.findViewById(R.id.radioAsc);
        mCheckboxName = (CheckBox) dialog.findViewById(R.id.checkboxName);
        mCheckboxDescription = (CheckBox) dialog.findViewById(R.id.checkboxDescription);
        mCheckboxReadme = (CheckBox) dialog.findViewById(R.id.checkboxReadme);

        // if button is clicked, close the custom dialog
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new DialogButtonClickListener());

        // set preserved values
        switch (sortValue) {
            case "stars":
                mRadioStars.setChecked(true);
                break;
            case "forks":
                mRadioForks.setChecked(true);
                break;
            case "updated":
                mRadioUpdated.setChecked(true);
                break;
        }

        switch (orderValue) {
            case "desc":
                mRadioDesc.setChecked(true);
                break;
            case "asc":
                mRadioAsc.setChecked(true);
                break;
        }

        mCheckboxName.setChecked(searchQualifiers.contains("name"));
        mCheckboxDescription.setChecked(searchQualifiers.contains("description"));
        mCheckboxReadme.setChecked(searchQualifiers.contains("readme"));

        return dialog;
    }

    class DialogButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int selectedId;

            String sortValue = "";
            selectedId = mRadioSortByGroup.getCheckedRadioButtonId();
            switch (selectedId) {
                case R.id.radioStars:
                    sortValue = "stars";
                    break;
                case R.id.radioForks:
                    sortValue = "forks";
                    break;
                case R.id.radioUpdated:
                    sortValue = "updated";
                    break;
            }

            String orderValue = "";
            selectedId = mRadioOrderGroup.getCheckedRadioButtonId();
            switch (selectedId) {
                case R.id.radioDesc:
                    orderValue = "desc";
                    break;
                case R.id.radioAsc:
                    orderValue = "asc";
                    break;
            }

            String searchQualifiers = "";
            if (mCheckboxName.isChecked())
                searchQualifiers += "name,";
            if (mCheckboxDescription.isChecked())
                searchQualifiers += "description,";
            if (mCheckboxReadme.isChecked())
                searchQualifiers += "readme,";
            if (!searchQualifiers.isEmpty()){
                // add prefix and remove "," from the end
                searchQualifiers = "+in:" + searchQualifiers;
                searchQualifiers = searchQualifiers.substring(0, searchQualifiers.length() - 1);
            }

            getPresenter().setSearchOpts(sortValue, orderValue, searchQualifiers);
        }
    }

    class RepoOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            if (repositoriesListView.getLastVisiblePosition() == repositoriesListView.getAdapter().getCount() - 1
                    && repositoriesListView.getChildAt(repositoriesListView.getChildCount() - 1).getBottom() <= repositoriesListView.getHeight()) {
                getPresenter().continueDataDownload();
            }
        }
    }
}
