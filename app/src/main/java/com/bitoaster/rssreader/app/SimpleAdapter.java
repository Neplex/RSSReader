package com.bitoaster.rssreader.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SimpleAdapter extends ArrayAdapter<Object>{

    public SimpleAdapter(Context context, List<Object> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_simple, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        }

        Object object = getItem(position);
        String str = object.toString();
        if (object.toString().length()>50)
            str = object.toString().substring(0,47)+"...";
        viewHolder.title.setText(str);

        return convertView;
    }

    private class ViewHolder {
        public TextView title;
    }
}
