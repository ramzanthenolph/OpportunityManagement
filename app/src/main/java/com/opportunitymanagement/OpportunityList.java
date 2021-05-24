package com.opportunitymanagement;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OpportunityList extends ArrayAdapter<Opportunity> {
    private Activity context;
    List<Opportunity> opportunities;

    public OpportunityList(Activity context, List<Opportunity> opportunities) {
        super(context, R.layout.layout_account_list, opportunities);
        this.context = context;
        this.opportunities = opportunities;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_account_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = (TextView) listViewItem.findViewById(R.id.textViewAddress);

        Opportunity opportunity = opportunities.get(position);
        textViewName.setText(opportunity.getOpportunityName());
        textViewRating.setText(String.valueOf(opportunity.getStage()));

        return listViewItem;
    }
}
