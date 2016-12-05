package com.johnniem.githubsearch.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnniem.githubsearch.R;
import com.johnniem.githubsearch.model.POJOs.SearchData.Items;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsAdapter extends ArrayAdapter<Items> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<Items> items;

    public ItemsAdapter(Context context, ArrayList<Items> newItems) {
        super(context, R.layout.repository_list_view_item, newItems);

        items = newItems;
        mContext = context;
    }

    // View lookup cache
    static class ViewHolder {
        @BindView(R.id.author_image)
        ImageView authorImage;
        @BindView(R.id.repository_name)
        TextView repositoryName;
        @BindView(R.id.author_name)
        TextView authorName;
        @BindView(R.id.stars)
        TextView stars;
        @BindView(R.id.watchers)
        TextView watchers;
        @BindView(R.id.forks)
        TextView forks;
        @BindView(R.id.issues)
        TextView issues;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if ((convertView == null) || (convertView.getTag() == null)) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.repository_list_view_item, parent, false);

            viewHolder = new ViewHolder(convertView);
            ButterKnife.bind(this, convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        Items items = getItem(position);

        String imgUrl = items.getOwner().getAvatar_url();
        Picasso.with(mContext)
                .load(imgUrl)
                .into(viewHolder.authorImage);
        viewHolder.repositoryName.setText(items.getName());
        viewHolder.authorName.setText(items.getOwner().getLogin());
        viewHolder.stars.setText(String.valueOf(items.getStargazers_count()));
        viewHolder.watchers.setText(String.valueOf(items.getWatchers_count()));
        viewHolder.forks.setText(String.valueOf(items.getForks_count()));
        viewHolder.issues.setText(String.valueOf(items.getOpen_issues_count()));

        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        // do nothing
    }
}
