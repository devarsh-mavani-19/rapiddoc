package com.shortcontent.imagetopdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

class CustomarrayAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    public CustomarrayAdapter(Context applicationContext, ArrayList<String> arrayList) {
        this.context = applicationContext;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_gridview, null);
        ImageView imageView = convertView.findViewById(R.id.folder_image_custom_gridview);
        imageView.setImageResource(R.drawable.addfolder);
        return convertView;
    }
}
