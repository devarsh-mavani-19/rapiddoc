package com.jayshreegopalapps.imagetopdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.util.ArrayList;
import java.util.HashMap;

class DeletePageAdapter extends BaseAdapter {
    Context context;
    ArrayList<Integer> arrayList;
    private SparseBooleanArray selectedList = new SparseBooleanArray();
    Uri uri;
    DeletePageAdapter(Context context, ArrayList<Integer> arrayList, Uri uri) {
        this.context = context;
        this.arrayList = arrayList;
        this.uri = uri;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_delete_page_layout, parent, false);
        PDFView pdfView = v.findViewById(R.id.delete_pdf_view);
        try {
            pdfView.fromUri(uri).pages(arrayList.get(position)).enableSwipe(false).enableAntialiasing(false).enableDoubletap(false).enableAnnotationRendering(false).onPageError(new OnPageErrorListener() {
                @Override
                public void onPageError(int page, Throwable t) {

                }
            }).onError(new OnErrorListener() {
                @Override
                public void onError(Throwable t) {

                }
            }).load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ImageView expand = v.findViewById(R.id.image_delete_page);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(context).setView(R.layout.layout_pdf_page).create();
                dialog.show();
                PDFView pdfView1 = dialog.findViewById(R.id.pdf_view_view_page);
                pdfView1.fromUri(uri).pages(position).load();
                ImageView cross = dialog.findViewById(R.id.close_view_page);
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        final CheckBox check = v.findViewById(R.id.check_box_delete_page);
        check.setChecked(selectedList.get(position, false));
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedList.put(position, isChecked);
            }
        });
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.isChecked()) {
                    check.setChecked(false);
                    selectedList.put(position, false);
                }
                else {
                    check.setChecked(true);
                    selectedList.put(position, true);
                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.isChecked()) {
                    check.setChecked(false);
                    selectedList.put(position, false);
                }
                else {
                    check.setChecked(true);
                    selectedList.put(position, true);
                }
            }
        });

        return v;

    }
    SparseBooleanArray getSelectedList() {
        return selectedList;
    }
}
