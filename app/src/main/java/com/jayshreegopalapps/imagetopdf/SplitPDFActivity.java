package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_pdf);
        final AdView mAdView = findViewById(R.id.ad_banner_split);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        initViews();

//        extractBundle();

        refreshPage();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.add(new RangeModel());
                adapter.notifyDataSetChanged();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitPdf();

            }
        });

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("opening intent");
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_PDF_SPLIT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_PDF_SPLIT) {
            if(data!=null) {
                if(data.getData()!=null) {
                    pdfUri = data.getData();
                    if(pdfUri!=null) {
                        PdfReader reader = null;
                        try {
                            reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
                            maxPageNumber = reader.getNumberOfPages();
                            textView.setText("PDF selected");
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
                PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
                maxPageNumber = reader.getNumberOfPages();
                System.out.println("page number = " + maxPageNumber);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void splitPdf() {
        if(pdfUri!=null) {
            if (adapter.areAllRangeValid()) {
                ArrayList<RangeModel> list = adapter.getArrayList();
                try {
                    PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
                    for (int i = 0; i < list.size(); i++) {
                        Document document = new Document();
                        PdfCopy writer = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf"));
                        document.open();
                        for (int j = list.get(i).fromRange; j <= list.get(i).toRange; j++) {
                            PdfImportedPage page = writer.getImportedPage(reader, j);
                            writer.addPage(page);
                        }
                        document.close();
                        writer.close();
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "PDF saved to " + Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf", Snackbar.LENGTH_LONG).show();
                    }
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
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
            holder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return true;
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
}
