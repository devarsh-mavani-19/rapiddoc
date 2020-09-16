package com.shortcontent.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NavigateAllFolders extends AppCompatActivity implements CustomBottomModalSheetFragment.BottomSheetListener {
    RecyclerView recyclerView;
    String path;
    ArrayList<FileDetailsModel> arrayList = new ArrayList<>();
    NavigateAdapter navigateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_all_folders);
        extractBundle();

        recyclerView = findViewById(R.id.recycler_view_navigate);
        navigateAdapter = new NavigateAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(navigateAdapter);
        resetLayoutToDefault();
        FloatingActionButton fab = findViewById(R.id.done_nav);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                if(path!=null) {
                    returnIntent.putExtra("result", path);

                }
                else{
                    returnIntent.putExtra("result", "root");
                }
                Bundle b = new Bundle();
                b.putString("shouldRetain", "true");
                returnIntent.putExtras(b);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 0) {
//            if (data != null) {
//                if(data.getData()!=null) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result", data.getStringExtra("result"));
//                    setResult(RESULT_OK,returnIntent);
//                    finish();
//                }
//
//            else{
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", "root");
//                setResult(RESULT_OK,returnIntent);
//                finish();
//            }
//            }
//        }
    }

    private void extractBundle() {
        if(getIntent().getExtras()!=null) {
            path = getIntent().getExtras().getString("folderPath");
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }
        else{
            path = null;
        }
    }

    void resetLayoutToDefault() {
        arrayList.clear();
        fetchRootFolders(path);
        navigateAdapter = new NavigateAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(navigateAdapter);
        navigateAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    void fetchRootFolders(@Nullable String parentFolder) {
        //required fields
        //name, modified date, thumbnail, page count
        DatabaseManagment filesTable = new DatabaseManagment(getApplicationContext());
        filesTable.useTable("FileDetails");
        Cursor cursor;
        if(parentFolder==null) {
            cursor = filesTable.prepare().where("category", "=", "'ROOT'").select(new String[]{"*"});
        }
        else{
            cursor = filesTable.prepare().where("parent", "=", "'" + parentFolder + "'").select(new String[]{"*"});
        }

//        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToNext()) {
            do{
                String name = cursor.getString(0);
                String category = cursor.getString(1);
                String parent = cursor.getString(2);
                String type = cursor.getString(3);
                String orderno = "" + cursor.getInt(4);
                String extension = cursor.getString(5);
                String creationTime = cursor.getString(6);
                String modifiedTime = cursor.getString(7);

                FileDetailsModel recycleList = new FileDetailsModel();
                recycleList.name = name;
                recycleList.creationDate = (creationTime);

//                Cursor cursor1 = filesTable.prepare().where("parent", "=", "'" + name + "'").and().where("type","=","'FILE'").select(new String[]{"count(*)"});
//                if(cursor1.moveToNext()) {
//                    recycleList.imageCount = (cursor1.getInt(0));
//                }
//                cursor1.close();

//                Cursor cursor2 = filesTable.prepare().where("type", "=", "'FILE'").and().where("parent", "=", "'" + name + "'").select(new String[]{"name"});
//                if(cursor2.moveToNext()) {
//                    recycleList.name = (cursor2.getString(0));
//                }
//                cursor2.close();

                arrayList.add(recycleList);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void yes(String name) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", name);
        setResult(RESULT_OK,returnIntent);
        finish();
    }


    private class NavigateAdapter extends RecyclerView.Adapter<MyHolder>{
        ArrayList<FileDetailsModel> arrayList;
        Context context;
        public NavigateAdapter(Context applicationContext, ArrayList<FileDetailsModel> arrayList) {
            this.arrayList = arrayList;
            this.context = applicationContext;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_view, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
            holder.textView.setText(arrayList.get(position).name);
            holder.textView2.setText(arrayList.get(position).creationDate);
            holder.textView3.setText((FileDetailsModel.getImagesCountInsideDir(context, arrayList.get(position).name) + ""));
            if(FileDetailsModel.isAllChildImages(context, arrayList.get(position).name)) {
                String imagepath = context.getExternalFilesDir(null) + "/" + FileDetailsModel.getDisplayImage(context, arrayList.get(position).name);
                Picasso.get()
                        .load("file://" + imagepath)
                        .placeholder(R.drawable.addfolder)
                        .fit()
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
            else{
                holder.imageView.setImageResource(R.drawable.addfolder);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!FileDetailsModel.isAllChildImages(getApplicationContext(), arrayList.get(position).name)) {
                        //start Activity for result
                        Intent start = new Intent(context, NavigateAllFolders.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        Bundle bundle = new Bundle();
                        bundle.putString("folderPath", arrayList.get(position).name);
                        start.putExtras(bundle);
                        startActivity(start);
                        finish();
                    }
                    else{
                       CustomBottomModalSheetFragment customBottomModalSheetFragment = new CustomBottomModalSheetFragment(arrayList.get(position).name);
                       customBottomModalSheetFragment.show(getSupportFragmentManager(),"confirm copy");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
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
    }
}
