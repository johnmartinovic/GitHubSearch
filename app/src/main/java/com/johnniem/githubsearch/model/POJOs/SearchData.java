package com.johnniem.githubsearch.model.POJOs;

import java.util.ArrayList;

public class SearchData
{
    private String incomplete_results;

    private ArrayList<Items> items;

    private String total_count;

    public String getIncomplete_results ()
    {
        return incomplete_results;
    }

    public void setIncomplete_results (String incomplete_results)
    {
        this.incomplete_results = incomplete_results;
    }

    public ArrayList<Items> getItems ()
    {
        return items;
    }

    public void setItems (ArrayList<Items> items)
    {
        this.items = items;
    }

    public String getTotal_count ()
    {
        return total_count;
    }

    public void setTotal_count (String total_count)
    {
        this.total_count = total_count;
    }
}