package com.johnniem.githubsearch.model.RetrofitUtils;

import com.johnniem.githubsearch.model.POJOs.OwnerDetailed;
import com.johnniem.githubsearch.model.POJOs.RepoDetailed;
import com.johnniem.githubsearch.model.POJOs.SearchData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET("search/repositories")
    Call<SearchData> searchRepositories(
            @Query(value = "q", encoded = true) String q,
            @Query("sort") String sort,
            @Query("order") String order
    );

    @GET
    Call<SearchData> searchRepositories(
            @Url String url
    );

    @GET
    Call<RepoDetailed> getRepoDetailed(
            @Url String url
    );

    @GET
    Call<OwnerDetailed> getOwnerDetailed(
            @Url String url
    );
}