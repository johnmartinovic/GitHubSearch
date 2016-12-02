package com.johnniem.githubsearch.model.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnniem.githubsearch.R;
import com.johnniem.githubsearch.model.POJOs.Items;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<Items> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<Items> items;

    public ItemsAdapter(Context context, ArrayList<Items> newItems) {
        super(context, R.layout.repository_list_view_item, newItems);

        items = newItems;
        mContext = context;
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView authorImage;
        TextView repositoryName;
        TextView authorName;
        TextView watchers;
        TextView forks;
        TextView issues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if ((convertView == null) || (convertView.getTag() == null)) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.repository_list_view_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.authorImage      = (ImageView) convertView.findViewById(R.id.author_image);
            viewHolder.repositoryName   = (TextView) convertView.findViewById(R.id.repository_name);
            viewHolder.authorName       = (TextView) convertView.findViewById(R.id.author_name);
            viewHolder.watchers         = (TextView) convertView.findViewById(R.id.watchers);
            viewHolder.forks            = (TextView) convertView.findViewById(R.id.forks);
            viewHolder.issues           = (TextView) convertView.findViewById(R.id.issues);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        Items items = getItem(position);

        String imgUrl = items.getOwner().getAvatar_url();
        Picasso.with(mContext).load(imgUrl).into(viewHolder.authorImage);
        viewHolder.repositoryName.setText(items.getName());
        viewHolder.authorName.setText(items.getOwner().getLogin());
        viewHolder.watchers.setText(String.valueOf(items.getWatchers_count()));
        viewHolder.forks.setText(String.valueOf(items.getForks_count()));
        viewHolder.issues.setText(String.valueOf(items.getOpen_issues_count()));

        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public void onClick(View view) {

    }
}
