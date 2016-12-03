package com.johnniem.githubsearch;

import android.content.Context;

import com.johnniem.githubsearch.common.ContextView;
import com.johnniem.githubsearch.common.ModelOps;
import com.johnniem.githubsearch.common.PresenterOps;
import com.johnniem.githubsearch.model.POJOs.SearchData;
import com.johnniem.githubsearch.model.POJOs.SearchData.Items;
import com.johnniem.githubsearch.view.MainActivity;

import java.util.ArrayList;

/**
 * Defines the interfaces for the application
 * that are required and provided by the layers in the
 * Model-View-Presenter (MVP) pattern.  This design ensures loose
 * coupling between the layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * ListPresenter class in the Presenter layer to interact with
     * MainActivity in the View layer.  It extends the
     * ContextView interface so the Model layer can access Context's
     * defined in the View layer.
     */
    interface RequiredViewOps
            extends ContextView {
        /**
         * Remove the keyboard
         */
        void dismissSoftKeyboard();

        /**
         * Make the ProgressBar visible.
         */
        void displayProgressBar();

        /**
         * Make the ProgressBar invisible.
         */
        void dismissProgressBar();

        /**
         * Display the repositories data provided so far
         */
        void refreshRepoList(ArrayList<Items> itemsList);

        /**
         * Reset Items Adapter
         */
        void resetItemsAdapter();

        /**
         * Handle failure to download the data.
         */
        void reportStatus(MainActivity.InfoDialog infoDialog, String status);

        /**
         * Start the DisplayDetailsActivity to display the results of
         * the download to the user.
         */
        void displayRepoDetails(String url);

        /**
         * Display search settings
         */
        void displaySearchSettings(String searchQualifiers, String sortValue, String orderValue);

        /**
         * Dismiss search settings
         */
        void dismissSearchSettings();

        /**
         * Init adapter and connect it with repo list
         */
        void initAdapter(ArrayList<Items> itemsList);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ListPresenter class in the Presenter layer to the
     * MainActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    interface ProvidedPresenterOps
            extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * Start the repository data download
         */
        void startDataDownload(String searchKeywords);

        /**
         * Continue the repostiory data download (get the next page)
         */
        void continueDataDownload();

        /**
         * Inform that settings button has been clicked
         */
        void settingsButtonClicked();

        /**
         * Set search options
         */
        void setSearchOpts(String sortValue, String orderValue, String searchQualifiers);

        /**
         * Item in repo list was selected
         */
        void repoItemSelected(Items item);
    }

    /**
     * This interface defines the minimum API needed by the ListModel
     * class in the Model layer to interact with ListPresenter class
     * in the Presenter layer.  It extends the ContextView interface
     * so the Model layer can access Context's defined in the View
     * layer.
     */
    interface RequiredPresenterOps
            extends ContextView {
        /**
         * Interact with the Presenter layer to display the
         * downloaded data when downloading is done
         */
        void onDownloadCompleted(SearchData searchData, String nextLink);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ListModel class in the Model layer to the ListPresenter class
     * in the Presenter layer.  It extends the ModelOps interface,
     * which is parameterized by the MVP.RequiredPresenterOps
     * interface used to define the argument passed to the
     * onConfigurationChange() method.
     */
    interface ProvidedModelOps
            extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Download repositories data only with url value
         */
        void downloadSearchData(Context context, String url);

        /**
         * Download repositories data with query values
         */
        void downloadSearchData(Context context, String searchKeywords, String sortValue, String orderValue);
    }
}
