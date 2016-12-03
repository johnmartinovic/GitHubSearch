package com.johnniem.githubsearch.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.johnniem.githubsearch.R;
import com.johnniem.githubsearch.common.Utils;
import com.johnniem.githubsearch.model.ListModel;
import com.johnniem.githubsearch.model.POJOs.OwnerDetailed;
import com.johnniem.githubsearch.model.POJOs.RepoDetailed;
import com.johnniem.githubsearch.model.RetrofitUtils.RetrofitAPI;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepoDetailsActivity extends AppCompatActivity {

    @BindView(R.id.author_image)
    ImageView authorImage;
    @BindView(R.id.repository_name)
    TextView repositoryName;
    @BindView(R.id.language)
    TextView language;
    @BindView(R.id.repo_created_at)
    TextView repoCreatedAt;
    @BindView(R.id.repo_updated_at)
    TextView repoUpdatedAt;
    @BindView(R.id.repo_pushed_at)
    TextView repoPushedAt;
    @BindView(R.id.stars)
    TextView stars;
    @BindView(R.id.watchers)
    TextView watchers;
    @BindView(R.id.forks)
    TextView forks;
    @BindView(R.id.issues)
    TextView issues;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.author_name)
    TextView authorName;
    @BindView(R.id.author_created_at)
    TextView authorCreatedAt;
    @BindView(R.id.author_updated_at)
    TextView authorUpdatedAt;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.main_linear_layout)
    LinearLayout mainLayout;

    ProgressDialog progressDialog;

    String repoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_details);

        ButterKnife.bind(this);

        mainLayout.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        dismissSoftKeyboard();

        progressDialog = getNewProgressDialog();
        progressDialog.show();

        // get url passed from previus activity
        repoUrl = getIntent().getStringExtra(MainActivity.REPO_URL);

        new DownloadDataAsyncTask().execute(repoUrl);
    }

    // If back is pressed, activity is destroyed and user is sent to previous activity.
    // This prevents only dialog window to be destroyed.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    ProgressDialog getNewProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Downloading data");
        progressDialog.setMessage("Please, wait...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }

    public void dismissSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(authorImage.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private class DownloadDataAsyncTask extends AsyncTask<String, Integer, DetailedInfo> {

        @Override
        protected DetailedInfo doInBackground(String... urls) {
            String repoUrl = urls[0];

            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ListModel.gitHubURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

            DetailedInfo detailedInfo;
            try {
                Call<RepoDetailed> callRepoDetailed = retrofitAPI.getRepoDetailed(repoUrl);
                RepoDetailed repoDetailed = callRepoDetailed.execute().body();

                String ownerUrl = repoDetailed.getOwner().getUrl();

                Call<OwnerDetailed> callOwnerDetailed = retrofitAPI.getOwnerDetailed(ownerUrl);
                OwnerDetailed ownerDetailed = callOwnerDetailed.execute().body();

                detailedInfo = new DetailedInfo(repoDetailed, ownerDetailed);

                Bitmap authorImageBmp = Picasso.with(getApplicationContext())
                        .load(detailedInfo.getOwnerDetailed().getAvatar_url())
                        .get();

                detailedInfo.setAuthorImageBmp(authorImageBmp);
            } catch (IOException e ){
                // set result to null
                detailedInfo = null;
            }

            return detailedInfo;
        }

        @Override
        protected void onPostExecute(DetailedInfo detailedInfo) {
            progressDialog.dismiss();

            if (detailedInfo == null) {
                Utils.showToast(RepoDetailsActivity.this, "Ups... Something went wrong.");
                return;
            }

            showData(detailedInfo);

            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    void showData(DetailedInfo detailedInfo) {
        authorImage.setImageBitmap(detailedInfo.getAuthorImageBmp());
        repositoryName.setText(detailedInfo.getRepoDetailed().getName());
        language.setText(detailedInfo.getRepoDetailed().getLanguage());

        // adapt date format to specific area
        DateFormat df = DateFormat.getDateInstance();
        repoCreatedAt.setText(df.format(detailedInfo.getRepoDetailed().getCreated_at()));
        repoUpdatedAt.setText(df.format(detailedInfo.getRepoDetailed().getUpdated_at()));
        repoPushedAt.setText(df.format(detailedInfo.getRepoDetailed().getPushed_at()));

        stars.setText(String.valueOf(detailedInfo.getRepoDetailed().getStargazers_count()));
        watchers.setText(String.valueOf(detailedInfo.getRepoDetailed().getWatchers_count()));
        forks.setText(String.valueOf(detailedInfo.getRepoDetailed().getForks_count()));
        issues.setText(String.valueOf(detailedInfo.getRepoDetailed().getOpen_issues_count()));
        description.setText(detailedInfo.getRepoDetailed().getDescription());
        login.setText(detailedInfo.getOwnerDetailed().getLogin());
        authorName.setText(detailedInfo.getOwnerDetailed().getName());
        authorCreatedAt.setText(detailedInfo.getOwnerDetailed().getCreated_at());
        authorUpdatedAt.setText(detailedInfo.getOwnerDetailed().getUpdated_at());

        // make button clickable and all data visible
        fab.setOnClickListener(new btnOnClickListener(detailedInfo.getRepoDetailed().getHtml_url()));
        mainLayout.setVisibility(View.GONE);
        progressDialog.dismiss();
    }

    private class btnOnClickListener implements View.OnClickListener {
        String url;

        btnOnClickListener(String url){
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            if (URLUtil.isValidUrl(this.url)) {
                Uri uri = Uri.parse(this.url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            else {
                Utils.showToast(RepoDetailsActivity.this, "Not a valid link, sorry.");
            }
        }
    }

    private class DetailedInfo {

        private RepoDetailed repoDetailed;
        private OwnerDetailed ownerDetailed;
        private Bitmap authorImageBmp;

        DetailedInfo(RepoDetailed repoDetailed, OwnerDetailed ownerDetailed) {
            this.repoDetailed = repoDetailed;
            this.ownerDetailed = ownerDetailed;
        }

        private RepoDetailed getRepoDetailed() {
            return repoDetailed;
        }

        private void setRepoDetailed(RepoDetailed repoDetailed) {
            this.repoDetailed = repoDetailed;
        }

        private OwnerDetailed getOwnerDetailed() {
            return ownerDetailed;
        }

        private void setOwnerDetailed(OwnerDetailed ownerDetailed) {
            this.ownerDetailed = ownerDetailed;
        }

        private Bitmap getAuthorImageBmp() {
            return authorImageBmp;
        }

        private void setAuthorImageBmp(Bitmap authorImageBmp) {
            this.authorImageBmp = authorImageBmp;
        }
    }
}
