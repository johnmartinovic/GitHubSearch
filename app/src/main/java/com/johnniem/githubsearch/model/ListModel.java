package com.johnniem.githubsearch.model;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.johnniem.githubsearch.MVP;
import com.johnniem.githubsearch.common.Utils;
import com.johnniem.githubsearch.model.POJOs.SearchData;
import com.johnniem.githubsearch.model.RetrofitUtils.RetrofitAPI;
import com.johnniem.githubsearch.presenter.ListPresenter;

import okhttp3.internal.framed.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericPresenter framework.
 */
public class ListModel implements MVP.ProvidedModelOps {

    private static final String gitHubURL = "https://api.github.com/";

    private RetrofitAPI mRetrofitAPI;

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredPresenterOps> mPresenter;

    /**
     * Hook method called when a new instance of ListModel is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter and initializing the sync and
     * async Services.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter = new WeakReference<>(presenter);

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(gitHubURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mRetrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangingConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // No-op.
    }

    @Override
    public void downloadSearchData(Context context, String url) {
        Call<SearchData> call = mRetrofitAPI.searchRepositories(url);

        call.enqueue(new RepositoriesCallback());
    }

    @Override
    public void downloadSearchData(Context context, String searchKeywords, String sortValue, String orderValue) {
        Call<SearchData> call = mRetrofitAPI.searchRepositories(searchKeywords, sortValue, orderValue);

        call.enqueue(new RepositoriesCallback());
    }

    private class RepositoriesCallback implements Callback<SearchData> {

        @Override
        public void onResponse(Call<SearchData> call, Response<SearchData> response) {
            SearchData searchData = response.body();

            String headerValue = response.headers().get("Link");

            String nextLink = Utils.getNextLinkFromHeaderLink(headerValue);

            mPresenter.get().onDownloadCompleted(searchData, nextLink);
        }

        @Override
        public void onFailure(Call<SearchData> call, Throwable t) {
            mPresenter.get().onDownloadCompleted(null, null);
        }
    }
}
