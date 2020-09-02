package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SharedMemory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.multipdf.Splitter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

//import com.itextpdf.text.pdf.PdfReader;

//import org.apache.pdfbox.multipdf.Splitter;
//import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class SplitPDFActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RangeModel> arrayList = new ArrayList<>();
    PdfRangeAdapter adapter;
    ItemTouchHelper touchHelper;
    FloatingActionButton button, done, addPdf;
    TextView textView;
    Uri pdfUri;
    int maxPageNumber;
    private int RESULT_PDF_SPLIT = 0;
    RelativeLayout empty_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_pdf);
//        final AdView mAdView = findViewById(R.id.ad_banner_split);
////        AdRequest adRequest = new AdRequest.Builder().build();
////        mAdView.loadAd(adRequest);

        initViews();

//        extractBundle();

        refreshPage();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.add(new RangeModel());
                adapter.notifyDataSetChanged();
                if(!arrayList.isEmpty()) {
                    empty_image.setVisibility(View.GONE);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermissionGranted()) {
                    return;
                }
                SplitPdfTask task = new SplitPdfTask(SplitPDFActivity.this);
                task.execute();
            }
        });

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermissionGranted()) {
                    return;
                }
                System.out.println("opening intent");
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_PDF_SPLIT);
            }
        });

    }

    private boolean isPermissionGranted() {
        if ((ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            }
            return false;
        }
        else{
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            createDirs();
        }
    }

    private void createDirs() {
        isPermissionGranted();
        File f = new File(Constants.PDF_MERGE_PATH);
        File f2 = new File(Constants.PDF_PATH);
        File f3 = new File(Constants.PDF_SPLIT_PATH);
        File f4 = new File(Constants.PDF_WATERMARK_PATH);
        File f5 = new File(Constants.PDF_STORAGE_PATH);

        if(!f.exists()) {
            f.mkdirs();
        }
        if(!f2.exists()) {
            f2.mkdirs();
        }
        if(!f3.exists()) {
            f3.mkdirs();
        }
        if(!f4.exists()) {
            f4.mkdirs();
        }
        if(!f5.exists()) {
            f5.mkdirs();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
        if (prefs.getBoolean("split8", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("split8", false).commit();

            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(addPdf)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Select a PDF")

                    .build();

            FancyShowCaseView fancyShowCaseView3 =new FancyShowCaseView.Builder(this)
                    .focusOn(button)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "add Custom Range")
                    .build();

            FancyShowCaseView fancyShowCaseView2 =new FancyShowCaseView.Builder(this)
                    .focusOn(done)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Split PDF")
                    .build();

           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView3)
                    .add(fancyShowCaseView2)
                    .show();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_PDF_SPLIT) {
            if(data!=null) {
                if(data.getData()!=null) {
                    pdfUri = data.getData();
                    if(pdfUri!=null) {
                        PDDocument reader = null;
                        try {
                            reader = PDDocument.load(getContentResolver().openInputStream(pdfUri));
                            maxPageNumber = reader.getNumberOfPages();
                            textView.setText("PDF selected");
                            Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    private void extractBundle() {
        if(getIntent().getExtras()!=null) {
            pdfUri = Uri.parse(getIntent().getExtras().getString("PDF"));
            try {
                PDDocument reader = PDDocument.load(getContentResolver().openInputStream(pdfUri));
                maxPageNumber = reader.getNumberOfPages();
                System.out.println("page number = " + maxPageNumber);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void splitPdf() {
//        if(pdfUri!=null) {
//            if (adapter.areAllRangeValid()) {
//                ArrayList<RangeModel> list = adapter.getArrayList();
//                try {
//                    PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
//                    for (int i = 0; i < list.size(); i++) {
//                        Document document = new Document();
//                        PdfCopy writer = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf"));
//                        document.open();
//                        for (int j = list.get(i).fromRange; j <= list.get(i).toRange; j++) {
//                            PdfImportedPage page = writer.getImportedPage(reader, j);
//                            writer.addPage(page);
//                        }
//                        document.close();
//                        writer.close();
//                        View parentLayout = findViewById(android.R.id.content);
//                        Snackbar.make(parentLayout, "PDF saved to " + Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf", Snackbar.LENGTH_LONG).show();
//                    }
//                    reader.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                View parentLayout = findViewById(android.R.id.content);
//                Snackbar.make(parentLayout, "Invalid Ranges To proceed", Snackbar.LENGTH_LONG).show();
//            }
//        }
//        else{
//            View parentLayout = findViewById(android.R.id.content);
//            Snackbar.make(parentLayout, "Please Select a PDF", Snackbar.LENGTH_LONG).show();
//        }
//    }

    private void refreshPage() {
        adapter = new PdfRangeAdapter(getApplicationContext(), arrayList, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }
        });

        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallbackList(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_split);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        button = findViewById(R.id.fab_add_range);
        done = findViewById(R.id.fab_done_split);
        textView = findViewById(R.id.selected_pdf);
        addPdf = findViewById(R.id.fab_operation_split_pdf);
        empty_image = findViewById(R.id.empty_image_split);
    }

    private class RangeModel {
        int fromRange, toRange;
    }

    private class PdfRangeAdapter extends RecyclerView.Adapter<MyHolder> implements ItemTouchHelperAdapterList{
        ArrayList<RangeModel> arrayList;
        Context context;
        private final OnStartDragListener mDragStartListener;


        public PdfRangeAdapter(Context application, ArrayList<RangeModel> arrayList, OnStartDragListener dragStartListener) {
            this.context = application;
            this.arrayList = arrayList;
            this.mDragStartListener = dragStartListener;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_view_split_pdf, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
            arrayList.get(position).fromRange = 0;
            arrayList.get(position).toRange = 0;
            holder.dragHandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(position);
                    refreshPage();
                }
            });

            holder.textView.setText(("Range " + (position + 1)));
            holder.from.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().equals("")) {
                        arrayList.get(position).fromRange = 0;
                    }
                    else {
                        arrayList.get(position).fromRange = Integer.parseInt(s.toString());
                    }
//                    int toPageNumber = Integer.parseInt(holder.from.getText().toString());
//                    int fromPageNumber = Integer.parseInt(s.toString());
//
//                    if(fromPageNumber>maxPageNumber) {
//                        holder.from.setText("");
//                        holder.from.setError("Maximum is " + maxPageNumber);
//                    }
//                    else if(fromPageNumber<1) {
//                        holder.from.setText("");
//                        holder.from.setError("Must be greater than 0");
//                    }
//                    else if(toPageNumber > fromPageNumber) {
//                        holder.from.setText("");
//                        holder.from.setError("Invalid range");
//                    }
//                    else{
//                        arrayList.get(position).fromRange = Integer.parseInt(s.toString());
//                    }
                }
            });

            holder.to.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().equals("")) {
                        arrayList.get(position).toRange = 0;
                    }
                    else {
                        arrayList.get(position).toRange = Integer.parseInt(s.toString());
                    }
//                    int toPageNumber = Integer.parseInt(s.toString());
//                    int fromPageNumber = Integer.parseInt(holder.from.getText().toString());
//
//                    if(toPageNumber>maxPageNumber) {
//                        holder.to.setText("");
//                        holder.to.setError("Maximum is " + maxPageNumber);
//                    }
//                    else if(toPageNumber<1) {
//                        holder.to.setText("");
//                        holder.to.setError("Must be greater than 0");
//                    }
//                    else if(toPageNumber > fromPageNumber) {
//                        holder.to.setText("");
//                        holder.to.setError("Invalid range");
//                    }
//                    else{
//                        arrayList.get(position).fromRange = Integer.parseInt(s.toString());
//                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(arrayList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(arrayList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);

            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }

        boolean areAllRangeValid() {
            for(int i =0;i<arrayList.size();i++) {
                if(arrayList.get(i).fromRange > arrayList.get(i).toRange) {
                    System.out.println("from  > to");
                    System.out.println("from = " + arrayList.get(i).fromRange);
                    System.out.println("to = " + arrayList.get(i).toRange);
                    return false;
                }
                if(arrayList.get(i).fromRange < 1 || arrayList.get(i).toRange < 1) {
                    System.out.println("from  ||  to < 1");
                    System.out.println("from = " + arrayList.get(i).fromRange);
                    System.out.println("to = " + arrayList.get(i).toRange);
                    return false;
                }
                if(arrayList.get(i).toRange > maxPageNumber || arrayList.get(i).fromRange > maxPageNumber) {
                    System.out.println("from  ||  to > 1");

                    return false;
                }
            }
            return true;
        }

        void splitPdf() {

        }

        public ArrayList<RangeModel> getArrayList() {
            return this.arrayList;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView dragHandle;
        TextView textView;
        EditText from, to;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            dragHandle = itemView.findViewById(R.id.drag_handle_split_pdf);
            textView = itemView.findViewById(R.id.range_no_split);
            from = itemView.findViewById(R.id.from_split);
            to = itemView.findViewById(R.id.to_split);
        }
    }

    public class SimpleItemTouchHelperCallbackList extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapterList mAdapter;

        SimpleItemTouchHelperCallbackList(ItemTouchHelperAdapterList adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    public interface ItemTouchHelperAdapterList {
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    public class SplitPdfTask extends AsyncTask<Void, Void, Void> {
        AlertDialog dialog;
        Context context;
        SplitPdfTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new AlertDialog.Builder(context).setCancelable(false).setView(R.layout.layout_loading_dialog).create();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            splitPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }

        private void splitPDF() {
            if(pdfUri!=null) {
                if (adapter.areAllRangeValid()) {
                    ArrayList<RangeModel> list = adapter.getArrayList();
                    try {
                        PDDocument doc = PDDocument.load(getContentResolver().openInputStream(pdfUri));

                        final String foldername = DateTimeUtils.getDateTime() + "_" + System.currentTimeMillis() + "/";
                        File f = new File(Constants.PDF_SPLIT_PATH + foldername);
                        if(!f.exists()) f.mkdirs();
                        System.out.println(f.getAbsolutePath());
                        for (int i = 0; i < list.size(); i++) {
                            String pdfName = "Split_" + System.currentTimeMillis() + ".pdf";
                            Splitter splitter = new Splitter();

                            splitter.setStartPage(list.get(i).fromRange);
                            splitter.setEndPage(list.get(i).toRange);
                            splitter.setSplitAtPage(list.get(i).toRange);

                            List<PDDocument> docs = splitter.split(doc);
                            System.out.println("List size = " + docs.size());
                            docs.get(0).save(Constants.PDF_SPLIT_PATH + foldername + pdfName);
                            docs.get(0).close();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "PDF Saved in " + foldername, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Failed To Split", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Invalid Ranges To proceed", Snackbar.LENGTH_LONG).show();
                }
            }
            else{
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Please Select a PDF", Snackbar.LENGTH_LONG).show();
            }
        }



    }

}
