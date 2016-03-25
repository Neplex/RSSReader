package com.bitoaster.rssreader.app;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class ChannelAdapter extends ArrayAdapter<Channel> {

    public ChannelAdapter(Context context, List<Channel> channels) {
        super(context, 0, channels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        }

        ChannelViewHolder viewHolder = (ChannelViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ChannelViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.activate = (Switch) convertView.findViewById(R.id.activate);
            convertView.setTag(viewHolder);
        }

        Channel channel = getItem(position);
        viewHolder.title.setText(channel.getTitle());
        viewHolder.description.setText(channel.getDescription());
        if (viewHolder.activate != null) {
            viewHolder.activate.setChecked(true);
            viewHolder.activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "unChecked", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return convertView;
    }

    private class ChannelViewHolder {
        public TextView title;
        public TextView description;
        public Switch activate;
    }
}
