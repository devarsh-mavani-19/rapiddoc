package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class PdfViewAdapter extends  RecyclerView.Adapter<PdfViewAdapter.MyHolder> {
    ArrayList<PDFModel> arrayList;
    Context context;
    private String mode;
    PDFviewChainInterfface mChain;

    public PdfViewAdapter(Context applicationContext, ArrayList<PDFModel> arrayList, String callingActivityType, PDFviewChainInterfface pdFviewChainInterfface) {
        this.context = applicationContext;
        this.arrayList = arrayList;
        this.mode = callingActivityType;
        this.mChain = pdFviewChainInterfface;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pdf_view_recycler, parent, false);
        return new PdfViewAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.imageView.setImageResource(R.drawable.pdflogo);
        holder.textView.setText((arrayList.get(position).name));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ViewPdfStartConstants.PDF_TO_IMAGE.equals(mode)) {
                    mChain.setRes(arrayList.get(position));
                }
                else{

                }

            }
        });
    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_pdf_view);
            textView = itemView.findViewById(R.id.text_view_pdf_view);
        }
    }

}
