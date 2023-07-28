package com.example.wi_fid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class WiFiInfoAdapter extends ArrayAdapter<WiFiInfo> {
    private final LayoutInflater inflater;
    private final int layout;
    private final ArrayList<WiFiInfo> productList;

    WiFiInfoAdapter(Context context, int resource, ArrayList<WiFiInfo> products) {
        super(context, resource, products);

        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final WiFiInfo wiFiInfo = productList.get(position);

        viewHolder.TargetBSSIDTextView.setText(wiFiInfo.getBSSID());
        viewHolder.TargetSSIDTextView.setText(wiFiInfo.getSSID());

        viewHolder.TargetCheckBox.setOnClickListener(v -> wiFiInfo.setStatus(!wiFiInfo.getStatus()));

        return convertView;
    }

    private static class ViewHolder {
        TextView TargetBSSIDTextView;
        TextView TargetSSIDTextView;
        CheckBox TargetCheckBox;

        ViewHolder(View view) {
            TargetBSSIDTextView = view.findViewById(R.id.TargetBSSIDTextView);
            TargetSSIDTextView = view.findViewById(R.id.TargetSSIDTextView);
            TargetCheckBox = view.findViewById(R.id.TargetCheckBox);
        }
    }
}
