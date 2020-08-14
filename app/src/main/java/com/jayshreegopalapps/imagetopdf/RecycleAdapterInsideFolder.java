package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class RecycleAdapterInsideFolder extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    updateUIFromAdapter updateUIFromAdapter;
    Context context;
    ArrayList<FileDetailsModel> arrayList;
    String folderPath;
    DatabaseManagment fileTable;
    boolean isInMultiSelectMode = false;
    HashMap<Integer, String> selectedItems = new HashMap<>();
    public RecycleAdapterInsideFolder(Context applicationContext, ArrayList<FileDetailsModel> arrayList, String folderPath, updateUIFromAdapter updateUIFromAdapter) {
        this.context = applicationContext;
        this.arrayList = arrayList;
        this.folderPath = folderPath;
        this.updateUIFromAdapter = updateUIFromAdapter;
        initTable();
    }

    private void initTable() {
        fileTable = new DatabaseManagment(context);
        ArrayList<String> tableFields = new ArrayList<>();
        ArrayList<String> tableDatatypes = new ArrayList<>();
        tableFields.add("name");
        tableDatatypes.add("varchar(30)");
        tableFields.add("category");
        tableDatatypes.add("varchar(5)"); //ROOT / CHILD / LEAF
        tableFields.add("parent"); //FILE/DIRECTORY path
        tableDatatypes.add("varchar(30)");
        tableFields.add("type"); //FILE / DIRECTORY
        tableDatatypes.add("varchar(4)");
        tableFields.add("orderno");
        tableDatatypes.add("number");
        tableFields.add("extension");
        tableDatatypes.add("varchar(5)");
        tableFields.add("creation");
        tableDatatypes.add("varchar(10)");
        tableFields.add("modified");
        tableDatatypes.add("varchar(10)");

        fileTable.createTable("FileDetails", tableFields, tableDatatypes);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0) {
            view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_view_relative_layout, parent, false);
            return new MyHolder(view);
        }
        else {
            view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycle_for_image_list, parent, false);
            return new ItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {

            case 0:
                ((MyHolder)holder).fillUI(arrayList.get(position));

                break;

            case 1:

                ((ItemViewHolder)holder).fillUI(arrayList.get(position), context, isInMultiSelectMode);
                ((ItemViewHolder)holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isInMultiSelectMode) {
                            if (isChecked) {
                                selectedItems.put(position, arrayList.get(position).name);
                            } else {
                                selectedItems.remove(position);
                            }
                        }
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isInMultiSelectMode) {
                            //true check box

                        }
                        else{
                            Intent intent = new Intent(context, OpenImageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("image", arrayList.get(position).name);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }

                    }
                });

                break;


        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibe != null) {
                    vibe.vibrate(50);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position).type.equals("DIR")) {
            return 0;
        }
        else{
            return 1;
        }
    }


    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        int oldFromContent = arrayList.get(fromPosition).pageNumber;
//        int oldToContent = arrayList.get(toPosition).pageNumber;
//
//        if (fromPosition < toPosition) {
//
//            //store primary key in temp variable
//            String tempName;
//            Cursor cursor = fileTable.prepare().where("orderno","=",oldFromContent+"").and().where("parent", "=","'"+folderPath+"'").and().where("type","=","'FILE'").select(new String[]{"name"});
//            if(cursor.moveToNext()){
//                tempName = cursor.getString(0);
//                cursor.close();
//                fileTable.customQuery("update FileDetails set orderno = (orderno - 1) " + "where orderno>=" + oldFromContent + " AND " + " orderno <= " + oldToContent + " AND parent = '" + folderPath + "' AND type = 'FILE';");
//                fileTable.customQuery("update FileDetails set orderno = " + oldToContent + " where name = '" + tempName + "';");
//            }
//
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(arrayList, i, i + 1);
//            }
//        } else {
//
//
//            //store primary key in temp variable
//            String tempName;
//            Cursor cursor = fileTable.prepare().where("orderno","=",oldFromContent+"").and().where("parent", "=","'"+folderPath+"'").and().where("type","=","'FILE'").select(new String[]{"name"});
//            if(cursor.moveToNext()){
//                tempName = cursor.getString(0);
//                cursor.close();
//                fileTable.customQuery("update FileDetails set orderno = (orderno + 1) " + "where orderno>=" + oldToContent + " AND " + " orderno <= " + oldFromContent + " AND parent = '" + folderPath + "' AND type = 'FILE';");
//                fileTable.customQuery("update FileDetails set orderno = " + oldToContent + " where name = '" + tempName + "';");
//            }
//
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(arrayList, i, i - 1);
//            }
//        }

//        Toast.makeText(context, fromPosition + " " + toPosition, Toast.LENGTH_SHORT).show();
//        if(fromPosition != toPosition) {
//            Collections.swap(arrayList, fromPosition, toPosition);
//        }
//
//        notifyItemMoved(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        int oldFromContent = Integer.parseInt(arrayList.get(fromPosition).orderno);
        int oldToContent = Integer.parseInt(arrayList.get(toPosition).orderno);

        if (fromPosition < toPosition) {
            //store primary key in temp variable
            String tempName;
            Cursor cursor = fileTable.prepare().where("orderno","=",oldFromContent+"").and().where("parent", "=","'"+folderPath+"'").and().where("type","=","'FILE'").select(new String[]{"name"});
            if(cursor.moveToNext()){
                tempName = cursor.getString(0);
                cursor.close();
                fileTable.customQuery("update FileDetails set orderno = (orderno - 1) " + "where orderno>" + oldFromContent + " AND " + " orderno <= " + oldToContent + " AND parent = '" + folderPath + "' AND type = 'FILE';");
                fileTable.customQuery("update FileDetails set orderno = " + oldToContent + " where name = '" + tempName + "';");
            }

            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(arrayList, i, i + 1);
            }
        } else {
            //store primary key in temp variable
            String tempName;
            Cursor cursor = fileTable.prepare().where("orderno","=",oldFromContent+"").and().where("parent", "=","'"+folderPath+"'").and().where("type","=","'FILE'").select(new String[]{"name"});
            if(cursor.moveToNext()){
                tempName = cursor.getString(0);
                cursor.close();
                fileTable.customQuery("update FileDetails set orderno = (orderno + 1) " + "where orderno>=" + oldToContent + " AND " + " orderno < " + oldFromContent + " AND parent = '" + folderPath + "' AND type = 'FILE';");
                fileTable.customQuery("update FileDetails set orderno = " + oldToContent + " where name = '" + tempName + "';");
            }

            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(arrayList, i, i - 1);
            }
        }
        updateUIFromAdapter.updateUI();
    }

    public void updateDataSet(ArrayList<RecycleListInsideFolder> tempList) {

    }

    public void enableCheckBoxes() {
        isInMultiSelectMode = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public HashMap<Integer, String> getSelectedItems() {
        return selectedItems;
    }

    public void disableCheckBoxes() {
        isInMultiSelectMode = false;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
//        ImageView image;
//        TextView pageNumber;


        //for custom_recycler_view
        ImageView imageView;
        TextView textView1, textView2, textView3;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
//            image = itemView.findViewById(R.id.image_inside_folder);
//            pageNumber = itemView.findViewById(R.id.image_page_number);


            imageView = itemView.findViewById(R.id.image_recycler_customview1);
            textView1 = itemView.findViewById(R.id.txt_recycler_customview1);
            textView2 = itemView.findViewById(R.id.txt_recycler_customview_date1);
            textView3 = itemView.findViewById(R.id.txt_recycler_customview_no_of_pages1);

        }

        public void fillUI(FileDetailsModel fileDetailsModel) {
            textView1.setText(fileDetailsModel.name);
            textView2.setText(fileDetailsModel.creationDate);
            if(FileDetailsModel.isAllChildImages(context, fileDetailsModel.name)) {
                Picasso.get()
                        .load("file://" + context.getExternalFilesDir(null) + "/" +FileDetailsModel.getDisplayImage(context, fileDetailsModel.name))
                        .placeholder(R.drawable.addfolder)
                        .fit()
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        });
                textView3.setText((FileDetailsModel.getImagesCountInsideDir(context, fileDetailsModel.name) + ""));
            }

            else {
                imageView.setImageResource(R.drawable.addfolder);
                textView1.setText(fileDetailsModel.name);
                textView2.setText(fileDetailsModel.creationDate);
                textView3.setVisibility(View.INVISIBLE);
            }
        }
    }
}
