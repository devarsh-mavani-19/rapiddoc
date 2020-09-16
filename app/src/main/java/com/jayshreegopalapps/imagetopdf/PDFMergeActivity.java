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
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.pdf.PdfCopy;
//import com.itextpdf.text.pdf.PdfReader;


//import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class PDFMergeActivity extends AppCompatActivity {
    private static final int ADD_PDF_MERGE = 0;
    RecyclerView recyclerView;
    ArrayList<PdfModelMerge> arrayList = new ArrayList<>();
    MergePdfAdapter arrayAdapter;
    FloatingActionButton fab, fab2;
    ItemTouchHelper touchHelper;
    SharedPreferences prefs;
    RelativeLayout image_empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfmerge);
//        final AdView mAdView = findViewById(R.id.ad_banner_merge);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        initViews();
        refreshPage();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermissionGranted()) {
                    return;
                }
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select a PDF"), ADD_PDF_MERGE);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mergePDFs(1);
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
        prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);

        if (prefs.getBoolean("merge8", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("merge8", false).commit();

            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Add Pdf to merge list")

                    .build();

            FancyShowCaseView fancyShowCaseView3 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab2)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Merge PDF from here")
                    .build();

           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView3)
                    .show();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }
        }
    }

    private void mergePDFs(int x) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final AlertDialog[] dialog = new AlertDialog[1];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        dialog[0] = new AlertDialog.Builder(PDFMergeActivity.this).setCancelable(false).setView(R.layout.layout_loading_dialog).create();
                        dialog[0].show();
                    }
                });
                if(!arrayList.isEmpty()) {
                    String fileName = "Merge_" + System.currentTimeMillis() + "";
                    PDFMergerUtility mergerUtility = new PDFMergerUtility();
                    mergerUtility.setDestinationFileName(Constants.PDF_MERGE_PATH + fileName + ".pdf");

                    for (int i = 0; i < arrayList.size(); i++) {
                        // Create pdf reader object to read each input pdf file
                        try {
                            mergerUtility.addSource(getContentResolver().openInputStream(arrayList.get(i).uri));
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PDFMergeActivity.this, "Failed to merge pdf.. Maybe one of the pdf is corrupted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    }
                    try {
                        mergerUtility.mergeDocuments(true);
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "PDF saved to " + Constants.PDF_MERGE_PATH + fileName + ".pdf", Snackbar.LENGTH_LONG).show();
                        Intent i = new Intent(PDFMergeActivity.this, OpenPDFActivity.class);
                        i.setDataAndType(Uri.parse("file:///" + Constants.PDF_MERGE_PATH + fileName + ".pdf"), "application/pdf");
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Failed to save PDF", Snackbar.LENGTH_LONG).show();

                    }

                }
                else{
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please Select at least 1 PDF " , Snackbar.LENGTH_LONG).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog[0].dismiss();
                    }
                });
            }
        }).start();

    }

//    private void mergePDFs() {
//        if(!arrayList.isEmpty()) {
//            String fileName = System.currentTimeMillis() + "";
//            Document document = new Document();
//            // Create pdf copy object to copy current document to the output mergedresult file
//            PdfCopy copy = null;
//            try {
//                copy = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + fileName + ".pdf"));
//            } catch (DocumentException ex) {
//                ex.printStackTrace();
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            }
//            // Open the document
//            document.open();
//            PdfReader pr;
//            int n;
//            for (int i = 0; i < arrayList.size(); i++) {
//                // Create pdf reader object to read each input pdf file
//                try {
//                    pr = new PdfReader(getContentResolver().openInputStream(arrayList.get(i).uri));
//                    n = pr.getNumberOfPages();
//                    for (int page = 0; page < n; ) {
//                        // Import all pages from the file to PdfCopy
//                        copy.addPage(copy.getImportedPage(pr, ++page));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Failed to merge pdf.. Maybe one of the pdf is corrupted", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // Get the number of pages of the pdf file
//
//            }
//            document.close(); // close the document
//            View parentLayout = findViewById(android.R.id.content);
//            Snackbar.make(parentLayout, "PDF saved to " + Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf", Snackbar.LENGTH_LONG).show();
//        }
//        else{
//            View parentLayout = findViewById(android.R.id.content);
//            Snackbar.make(parentLayout, "Please Select at least 1 PDF " , Snackbar.LENGTH_LONG).show();
//        }
//
//    }

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse(getExternalFilesDir(null)+"/" + fileName + ".pdf"), "application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arrayAdapter.notifyDataSetChanged();
        if(requestCode == ADD_PDF_MERGE) {
            if(data!=null) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.

                    for(int i = 0; i < count; i++) {
                        try {
                            Uri uri_path = data.getClipData().getItemAt(i).getUri();
                            PdfModelMerge pdfModel = new PdfModelMerge();
                            pdfModel.uri = uri_path;
                            arrayList.add(pdfModel);
                            image_empty.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(data.getData() != null) {

                    try {
                        Uri uri_path = data.getData();
                        PdfModelMerge pdfModelMerge = new PdfModelMerge();
                        pdfModelMerge.uri = uri_path;
                        arrayList.add(pdfModelMerge);
                        image_empty.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                refreshPage();

            }
        }
    }

    private void refreshPage() {
        arrayAdapter = new MergePdfAdapter(getApplicationContext(), arrayList, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(arrayAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallbackList(arrayAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_merge);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);

        fab = findViewById(R.id.fab_add_pdf_merge);
        fab2 = findViewById(R.id.fab_done_pdf_merge);
        image_empty = findViewById(R.id.empty_image_merge);
    }

    private class PdfModelMerge {
        Uri uri;
    }

    private class MergePdfAdapter extends RecyclerView.Adapter<PDFMergeActivity.MyHolder>  implements ItemTouchHelperAdapterList {
        ArrayList<PdfModelMerge> arrayList;
        Context context;
        private final OnStartDragListener mDragStartListener;


        MergePdfAdapter(Context applicationContext, ArrayList<PdfModelMerge> arrayList, OnStartDragListener dragStartListener) {
            this.context = applicationContext;
            this.arrayList = arrayList;
            mDragStartListener = dragStartListener;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_merge_custom_recycler_view, parent, false);
            return new PDFMergeActivity.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
            holder.t.setText(getFileName(arrayList.get(position).uri));
            holder.i.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return true;
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
            refreshPage();
        }

        public String getFileName(Uri uri) {
            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result;
        }


    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView t;
        ImageView i;
        MyHolder(@NonNull View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.text_pdf_merge_custom_recycler);
            i = itemView.findViewById(R.id.drag_handle);
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

}
