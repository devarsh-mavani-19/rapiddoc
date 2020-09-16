package com.shortcontent.imagetopdf;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView pageNumber;
    CheckBox checkBox;


    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image_inside_folder);
        pageNumber = itemView.findViewById(R.id.image_page_number);
        checkBox = itemView.findViewById(R.id.inside_folder_check_box);
    }


    public void fillUI(FileDetailsModel fileDetailsModel, Context context, boolean isInMultiSelectMode) {
        if (fileDetailsModel.type.equals("FILE")) {
            pageNumber.setText((fileDetailsModel.orderno + ""));
            String imagepath = context.getExternalFilesDir(null) + "/" + fileDetailsModel.name;
            image.setImageURI(Uri.parse(imagepath));
//            Picasso.get()
//                    .load("file://" + imagepath)
//                    .placeholder(R.drawable.addfolder)
//                    .fit()
//                    .into(image, new Callback() {
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

            if (isInMultiSelectMode){
                checkBox.setVisibility(View.VISIBLE);
            }
            else{
                checkBox.setVisibility(View.INVISIBLE);
            }
        }
    }
}
