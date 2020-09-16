package com.shortcontent.imagetopdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FolderNavigationListViewAdapter extends BaseAdapter {
    ArrayList<FileDetailsModel> arrayList;
    Context context;

    public FolderNavigationListViewAdapter(Context context, ArrayList<FileDetailsModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position).name;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.navigation_folder_list_view_layout, null);
        TextView textView = convertView.findViewById(R.id.folder_navigation_custom_list_view_textbox);
        textView.setText(arrayList.get(position).name);
        return convertView;
    }
}
