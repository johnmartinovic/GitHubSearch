package com.johnniem.githubsearch.presenter;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.johnniem.githubsearch.MVP;
import com.johnniem.githubsearch.common.GenericPresenter;
import com.johnniem.githubsearch.model.ListModel;
import com.johnniem.githubsearch.model.POJOs.Items;
import com.johnniem.githubsearch.model.POJOs.SearchData;


/**
 * This class defines all the image-related operations.  It implements
 * the various Ops interfaces so it can be created/managed by the
 * GenericActivity framework.  It plays the role of the "Abstraction"
 * in Bridge pattern and the role of the "Presenter" in the
 * Model-View-Presenter pattern.
 */
public class ListPresenter
        extends GenericPresenter<MVP.RequiredPresenterOps,
                MVP.ProvidedModelOps,
                ListModel>
        implements MVP.ProvidedPresenterOps,
                MVP.RequiredPresenterOps {

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<MVP.RequiredViewOps> mView;

    public static final boolean DOWNLOAD_SUCCESS = true;
    public static final boolean DOWNLOAD_FAIL = false;

    private String mSearchKeywords;
    private String mSearchQualifiers;
    private String mSortValue;
    private String mOrderValue;
    private String mSearchString;

    private ArrayList<Items> mItemsList;

    boolean isDownloadInProgress;

    // if "null", search is done
    String mNextLink;

    /**
     * Hook method called when a new instance of ListPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     */
    @Override
    public void onCreate(MVP.RequiredViewOps view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);

        // Initialize the list of items
        mItemsList = new ArrayList<>();

        mView.get().initAdapter(mItemsList);

        // Finish the initialization steps.
        resetFields();

        // Invoke the special onCreate() method in GenericPresenter,
        // passing in the ListModel class to instantiate/manage and
        // "this" to provide ListModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(ListModel.class,
                this);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ListPresenter object after a runtime
     * configuration change.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        // Reset the mView WeakReference.
        mView = new WeakReference<>(view);

        // Redisplay the repos data
        mView.get().initAdapter(mItemsList);
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }

    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mView.get().getApplicationContext();
    }

    /**
     * Reset all the data
     */
    private void resetFields() {
        mItemsList.clear();

        mSearchKeywords = "";
        mSearchQualifiers = "";
        mSortValue = "stars";
        mOrderValue = "desc";
        mSearchQualifiers = "+in:name";
        mSearchString = mSearchKeywords + mSearchQualifiers;

        mView.get().refreshRepoList(mItemsList);
    }

    /****************************************************************************************
     * Methods provided by the ListPresenter class to to the MainActivity.
     */
    @Override
    public void startDataDownload(String searchKeywords) {
        // skip this call if data is still being downloaded
        if (isDownloadInProgress)
            return;

        // return if string is empty
        if (searchKeywords.isEmpty()) {
            mView.get().reportStatus("Pls write something brah...");
            return;
        }

        mView.get().dismissSoftKeyboard();

        // reset list and adapter
        mItemsList = new ArrayList<>();
        mView.get().resetItemsAdapter();

        isDownloadInProgress = true;
        mView.get().displayProgressBar();
        this.mSearchKeywords = searchKeywords;

        mSearchString = searchKeywords + mSearchQualifiers;

        getModel().downloadSearchData(getApplicationContext(), mSearchString, mSortValue, mOrderValue);
    }

    @Override
    public void continueDataDownload() {
        if (mNextLink == null) {
            mView.get().reportStatus("You got all the data...");
        }

        getModel().downloadSearchData(getApplicationContext(), mNextLink);

    }

    @Override
    public void settingsButtonClicked() {
        mView.get().displaySearchSettings(mSearchQualifiers, mSortValue, mOrderValue);
    }

    @Override
    public void setSearchOpts(String sortValue, String orderValue, String searchQualifiers) {
        mSortValue = sortValue;
        mOrderValue = orderValue;
        mSearchQualifiers = searchQualifiers;
        mSearchString = mSearchKeywords + mSearchQualifiers;

        mView.get().dismissSearchSettings();
    }

    /****************************************************************************************
     * Methods needed by the ListModel class to interact with ListPresenter class.
     */
    @Override
    public void onDownloadCompleted(SearchData searchData, String nextLink) {
        mView.get().dismissProgressBar();

        if (searchData == null) {
            mView.get().reportStatus("Ups... Something went wrong");
            return;
        }

        // if results are good, update next link
        mNextLink = nextLink;

        mView.get().reportStatus("Results: " + searchData.getTotal_count());

        // refresh list and adapter
        mItemsList.addAll(searchData.getItems());
        mView.get().refreshRepoList(searchData.getItems());

        // reset downloading status
        isDownloadInProgress = false;
    }
}
