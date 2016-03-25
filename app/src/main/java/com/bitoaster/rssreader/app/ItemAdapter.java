package com.bitoaster.rssreader.app;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        }

        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ItemViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.enclosure = (ImageView) convertView.findViewById(R.id.enclosure);
            convertView.setTag(viewHolder);
        }

        Item item = getItem(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.enclosure.setImageDrawable(new ColorDrawable(23));

        return convertView;
    }

    private class ItemViewHolder {
        public TextView title;
        public TextView description;
        public ImageView enclosure;
    }
}
