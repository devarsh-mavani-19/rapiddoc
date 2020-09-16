package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyHolder> implements Filterable {
    Context context;
    ArrayList<FileDetailsModel> arrayList;
    MultipleSelectionInterface multipleSelectionInterface;
    private boolean isInMultiSelectMode = false;
    HashMap<Integer, String> selectedItems = new HashMap<>();
    Boolean isEmpty=true;
    public RecycleAdapter(Context recyclerViewActivity, ArrayList<FileDetailsModel> arrayList, MultipleSelectionInterface multipleSelectionInterface, boolean isInMultiSelectMode) {
        this.context = recyclerViewActivity;
        this.arrayList = arrayList;
        this.multipleSelectionInterface = multipleSelectionInterface;
        this.isInMultiSelectMode = isInMultiSelectMode;
        if(arrayList.size()==0){
            isEmpty=true;
        }
        else{
            isEmpty=false;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_view, parent, false);
        return new RecycleAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isInMultiSelectMode = true;
                multipleSelectionInterface.updateUIToMultipleSelectMode();
                notifyDataSetChanged();
                return true;
            }
        });
        if(isInMultiSelectMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            selectedItems.clear();
        }
        holder.textView.setText(arrayList.get(position).name);
        holder.textView2.setText(arrayList.get(position).creationDate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInMultiSelectMode) {
                    if(FileDetailsModel.isAllChildImages(context, arrayList.get(position).name)) {
                        Intent intent = new Intent(context, InsideFolderActivtiy.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("folderPath", arrayList.get(position).name);
                        bundle.putInt("imageCount", FileDetailsModel.getImagesCountInsideDir(context, arrayList.get(position).name));
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("parentFolder", arrayList.get(position).name);
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    selectedItems.put(position, arrayList.get(position).name);
                    if(selectedItems.keySet().size() == 1) {
                        multipleSelectionInterface.enableRenameOptions();
                    }
                    else{
                        multipleSelectionInterface.disableRenameOptions();
                    }
                }
                else {
                    selectedItems.remove(position);
                    if(selectedItems.keySet().size() == 1) {
                        multipleSelectionInterface.enableRenameOptions();
                    }
                    else{
                        multipleSelectionInterface.disableRenameOptions();
                    }
                }
            }
        });

        holder.textView3.setText((FileDetailsModel.getImagesCountInsideDir(context, arrayList.get(position).name) + " pages"));

        if(FileDetailsModel.isAllChildImages(context, arrayList.get(position).name)) {
            String imagepath = context.getExternalFilesDir(null) + "/" + FileDetailsModel.getDisplayImage(context, arrayList.get(position).name);
            holder.imageView.setImageURI(Uri.parse(imagepath));
//            Picasso.get()
//                    .load("file://" + imagepath)
//                    .placeholder(R.drawable.addfolder)
//                    .fit()
//                    .into(holder.imageView, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
        }
        else{
            holder.imageView.setImageResource(R.drawable.addfolder);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                  /*  contactListFiltered = contactList;*/
                } else {
                   /* List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;*/
                }

                FilterResults filterResults = new FilterResults();
/*
                filterResults.values = contactListFiltered;
*/
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
/*
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
*/
                notifyDataSetChanged();
            }
        };



    }

    public void updateDataSet(ArrayList<FileDetailsModel> tempList) {
        arrayList = tempList;
        notifyDataSetChanged();
    }

    public HashMap<Integer, String> getSelectedItems() {
        return selectedItems;
    }


    public int getImagesCountInsideDir(String parentFolderName) {
        DatabaseManagment databaseManagment = new DatabaseManagment(context);
        Cursor cursor = databaseManagment.prepare().where("parent", "=", parentFolderName).and().where("type","=","'FILE'").select(new String[]{"*"});
        if(cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView textView, textView2, textView3;
        ImageView imageView;
        CheckBox checkBox;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_recycler_customview);
            textView2 = itemView.findViewById(R.id.txt_recycler_customview_date);
            textView3 = itemView.findViewById(R.id.txt_recycler_customview_no_of_pages);
            imageView = itemView.findViewById(R.id.image_recycler_customview);
            checkBox = itemView.findViewById(R.id.check_multi_select);

        }

        @Override
        public boolean onLongClick(View v) {
            checkBox.setVisibility(View.VISIBLE);
            return true;
        }
    }
}


