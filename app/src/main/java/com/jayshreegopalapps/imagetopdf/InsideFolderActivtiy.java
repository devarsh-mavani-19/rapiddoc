package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class InsideFolderActivtiy extends AppCompatActivity implements updateUIFromAdapter, RenameBottomModalSheet.BottomSheetListener {
    RecyclerView recyclerView;
    ArrayList<FileDetailsModel> arrayList = new ArrayList<>();
    RecycleAdapterInsideFolder recycleAdapter;
    LinearLayout bottomNavigationView;
    FloatingActionButton fab, fab2, fab3;
    String path;
    int imageCountOfCurrentFolder;
    DatabaseManagment fileTable;
    boolean isInMultiSelecMode = false;
    private static int REQUEST_CUSTOM_CAMERA = 100;
//    private InterstitialAd interstitialAd;

    @Override
    public void save(String name, String newName) {
        if(name.equals("") || newName.equals("")) {
            Toast.makeText(this, "Failed to rename", Toast.LENGTH_SHORT).show();
        }
        else{
            Cursor cursor = fileTable.customSelect("select count(*) from FileDetails where name = '" + newName + "'");
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    Toast.makeText(this, "Folder already exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    fileTable.customQuery("update FileDetails set name = '" + newName + "' where name = '" + name + "'");
                    fileTable.customQuery("update FileDetails set parent = '" + newName + "' where parent = '" + name + "'");
                }
            }
            cursor.close();
            path = newName;
            refreshPage();

        }
    }

    enum multiSelectOperations  {
            NO_OPERATION,
            SHARE_IMAGE,
            SHARE_PDF,
            COPY_IMAGES,
            CUT_IMAGES,
            DELETE_IMAGES,
            EXPORT_TO_GALLERY
    }
    multiSelectOperations operation = multiSelectOperations.NO_OPERATION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_folder_activtiy);
        PDFBoxResourceLoader.init(getApplicationContext());






        initViews();

        initTable();

        //extract bundle
        extractBundle();

        recycleAdapter = new RecycleAdapterInsideFolder(getApplicationContext(), arrayList, path, new updateUIFromAdapter() {
            @Override
            public void updateUI() {
                refreshPage();
            }
        });
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recycleAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);



        refreshPage();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                startActivityForResult(intent, 1);
                if(!askPermissions()) {
                    return;
                }

                Intent i = new Intent(InsideFolderActivtiy.this, CustomCameraActivity.class);
                startActivityForResult(i, REQUEST_CUSTOM_CAMERA);




//                interstitialAd.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(int i) {
//                        super.onAdFailedToLoad(i);
//                        int REQUEST_CODE = 1;
//                        int preference = ScanConstants.OPEN_CAMERA;
//                        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
//                        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
//                        startActivityForResult(intent, REQUEST_CODE);
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        super.onAdClosed();
//                        int REQUEST_CODE = 1;
//                        int preference = ScanConstants.OPEN_CAMERA;
//                        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
//                        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
//                        startActivityForResult(intent, REQUEST_CODE);
//                    }
//                });



            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!askPermissions()){
               }
                else{
                    ConvertToPDFTask task = new ConvertToPDFTask(InsideFolderActivtiy.this);
                    task.execute(path);
                }


            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operation!=multiSelectOperations.NO_OPERATION) {
                    if(operation == multiSelectOperations.SHARE_IMAGE) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");

                        HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                        if(selectedItems.size() == 0) {
                            Toast.makeText(InsideFolderActivtiy.this, "Please Select at least 1 image", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ArrayList<Uri> files = new ArrayList<Uri>();
                            for (Integer i : selectedItems.keySet()) {
                                String fname = selectedItems.get(i);
                                files.add(Uri.parse(getExternalFilesDir(null) + "/" + fname));
                            }
                            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                            startActivity(Intent.createChooser(share, "Share Image"));
                            setToSingleSelectMode();
                        }
                    }
                    if(operation == multiSelectOperations.COPY_IMAGES) {
                        Intent navIntent = new Intent(getApplicationContext(), NavigateAllFolders.class);
                        startActivityForResult(navIntent, 3);
                    }
                    if(operation == multiSelectOperations.CUT_IMAGES) {
                        Intent navIntent = new Intent(getApplicationContext(), NavigateAllFolders.class);
                        startActivityForResult(navIntent, 4);
                    }
                    if(operation == multiSelectOperations.DELETE_IMAGES) {
                        HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                        if(selectedItems.size() == 0) {
                            Toast.makeText(InsideFolderActivtiy.this, "Please Select at least 1 image", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for (Integer i : selectedItems.keySet()) {
                                fileTable.customQuery("delete from FileDetails where name = '" + selectedItems.get(i) + "'");
                            }
                            Toast.makeText(getApplicationContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
                            imageCountOfCurrentFolder--;
                            refreshPage();
                        }
                    }
                    if(operation == multiSelectOperations.EXPORT_TO_GALLERY) {
                        if(!askPermissions()) {
                            return;
                        }

                        HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                        if(selectedItems.size() == 0) {
                            Toast.makeText(InsideFolderActivtiy.this, "Please select at least 1 image", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for (Integer i : selectedItems.keySet()) {
//                            ContentValues values = new ContentValues();
//                            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//                            values.put(MediaStore.MediaColumns.DATA, (getExternalFilesDir(null) + "/" + selectedItems.get(i)));
//                            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                downloadImage(selectedItems.get(i));

                            }
                            Toast.makeText(InsideFolderActivtiy.this, "Saved In Gallery", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    private boolean askPermissions() {
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
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
        if (prefs.getBoolean("insidefolder8", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("insidefolder8", false).commit();


            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"+
                            "Capture Image")

                    .build();



            FancyShowCaseView fancyShowCaseView2 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab2)
                    .title("Convert to pdf")
                    .build()
                    ;
           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView2)
                    .show();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }
        }
        recycleAdapter.notifyDataSetChanged();
    }

    private void downloadImage(String imageName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis() + "");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + "");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to pdf");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Bitmap source = BitmapFactory.decodeFile(getExternalFilesDir(null) + "/" + imageName);
            if (source != null) {
                OutputStream imageOut = getContentResolver().openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
            } else {
                getContentResolver().delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                getContentResolver().delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }
    }



    @Override
    public void onBackPressed() {
        if(isInMultiSelecMode) {
            setToSingleSelectMode();
        }
        else{
            super.onBackPressed();
        }
    }

    private void setToSingleSelectMode() {
        isInMultiSelecMode = false;
        recycleAdapter.disableCheckBoxes();
        fab.show();
        fab2.show();
        fab3.hide();
        refreshPage();
    }

//    private void initializeBottomMenu() {
//        final ImageView shareMenu, copyMenu, cutMenu, deleteMenu, moreMenu;
//        shareMenu = findViewById(R.id.share_bottom_nav);
//        copyMenu = findViewById(R.id.copy_bottom_nav);
//        cutMenu = findViewById(R.id.cut_bottom_nav);
//        deleteMenu = findViewById(R.id.delete_bottom_nav);
//        moreMenu = findViewById(R.id.more_bottom_nav);
//
//        shareMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        copyMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        cutMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        moreMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), moreMenu);
//                popupMenu.getMenuInflater().inflate(R.menu.more_pop_up_menu, popupMenu.getMenu());
//                popupMenu.show();
//            }
//        });
//
//    }


    private void extractBundle() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            path = bundle.getString("folderPath");
            imageCountOfCurrentFolder = bundle.getInt("imageCount");
            getSupportActionBar().setTitle(path);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inside_folder_top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();



        if(id == R.id.inside_folder_share_images) {
            setToMultiSelectMode();
            operation = multiSelectOperations.SHARE_IMAGE;
        }
        if(id == R.id.inside_folder_share_as_pdf) {
            if(!askPermissions()) {
                return false;
            }
            operation = multiSelectOperations.SHARE_PDF;
            ConvertToPDFTaskShare task = new ConvertToPDFTaskShare(InsideFolderActivtiy.this);
            task.execute(path);

//            String pdfName = PdfConverterBox.convertFrom(getApplicationContext(), path);
//            Intent share = new Intent(Intent.ACTION_SEND);
//            share.setType("application/*");
//            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Constants.PDF_STORAGE_PATH + pdfName + ".pdf"));
//            startActivity(Intent.createChooser(share, "Share pdf ..."));
        }
        if(id == R.id.inside_folder_import_from_gallery) {

            int REQUEST_CODE = 2;
            int preference = ScanConstants.OPEN_MEDIA;
            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 88);
        }
//        if (id == R.id.inside_folder_copy) {
//            setToMultiSelectMode();
//            operation = multiSelectOperations.COPY_IMAGES;
//        }
//
//        if (id == R.id.inside_folder_move) {
//            setToMultiSelectMode();
//            operation = multiSelectOperations.CUT_IMAGES;
//        }

        if(id == R.id.inside_folder_delete) {
            setToMultiSelectMode();
            operation = multiSelectOperations.DELETE_IMAGES;
        }

        if(id == R.id.inside_folder_rename_dir) {
            RenameBottomModalSheet renameBottomModalSheet = new RenameBottomModalSheet(path);
            renameBottomModalSheet.show(getSupportFragmentManager(), "rename bottom modal sheet");
        }
        if(id == R.id.inside_folder_export_to_gallery) {
            setToMultiSelectMode();
            operation = multiSelectOperations.EXPORT_TO_GALLERY;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToMultiSelectMode() {
        isInMultiSelecMode = true;
        recycleAdapter.enableCheckBoxes();
        fab.hide();
        fab2.hide();
        fab3.show();
        getSupportActionBar().setTitle("select images to share");
    }

    private void initViews() {
        fab = findViewById(R.id.fab_inside_folder);
        fab2 = findViewById(R.id.fab_pdf);
        fab3 = findViewById(R.id.done_inside_folder);
        fab3.hide();

        recyclerView = findViewById(R.id.images_view_inside_folder);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
    }


    private void initTable() {
        fileTable = new DatabaseManagment(getApplicationContext());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }
        if(requestCode == REQUEST_CUSTOM_CAMERA && resultCode==RESULT_OK) {
            String location = null;
            if (data != null) {
                location = data.getStringExtra("location");
                int REQUEST_CODE = 1;
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                intent.putExtra("location", location);
                startActivityForResult(intent, REQUEST_CODE);
            }

        }
        if (requestCode == 1) {
            Bitmap image = null;
            if (data != null) {
                if(data.getExtras()!=null) {
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    String fileName = "" + System.currentTimeMillis();
                    try {
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png", false);
                        image.compress(Bitmap.CompressFormat.PNG, 100, fos);


                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        Date d2 = new Date();
                        d2.setTime(System.currentTimeMillis());
                        String creationTime = sdf2.format(d2);

                        //insert into database
                        //insert image as child
                        ArrayList<String> insertImageArrayList = new ArrayList<>();
                        insertImageArrayList.add("'" + fileName + ".png'"); //name
                        insertImageArrayList.add("'" + "CHILD" + "'"); //category
                        insertImageArrayList.add("'" + path + "'"); //parent
                        insertImageArrayList.add("'" + "FILE" + "'"); //type
                        insertImageArrayList.add("" + ++imageCountOfCurrentFolder); //order number
                        insertImageArrayList.add("'" + "PNG" + "'"); //extension
                        insertImageArrayList.add("'" + creationTime + "'"); //creation
                        insertImageArrayList.add("'" + creationTime + "'"); //modified
                        insertImageArrayList.add("'" + "'"); //
                        insertImageArrayList.add("'N" + "'"); //
                        fileTable.insertRecord(insertImageArrayList); //save


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    return;
                }
            }
            else{
                return;
            }
            //store
            /*catch (IOException e) {
                e.printStackTrace();
            }*/
            refreshPage();
        }
        if(requestCode == 2) {
            if(resultCode == RESULT_OK) {

                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);
//                    scannedImageView.setImageBitmap(bitmap);
                    String fileName = System.currentTimeMillis() + "";
//                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    OutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    // Copy the bits from instream to outstream
//                    byte[] buf = new byte[1024];
//                    int len;
//
//                    while ((len = inputStream.read(buf)) > 0) {
//                        out.write(buf, 0, len);
//                    }
//
//                    inputStream.close();
//                    out.close();

                    String creationDate = DateTimeUtils.getDate();
                    ArrayList<String> a = new ArrayList<>();
                    a.add("'" + fileName + ".png'");
                    a.add("'CHILD'");
                    a.add("'" + path + "'");
                    a.add("'FILE'");
                    a.add("" + (++imageCountOfCurrentFolder));
                    a.add("'PNG'");
                    a.add("'" + creationDate + "'");
                    a.add("'" + creationDate + "'");
                    a.add("'" + "'"); //
                    a.add("'N" + "'"); //
                    fileTable.insertRecord(a);
                    refreshPage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for(int i = 0; i < count; i++) {
                        try {
//                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            String fileName = System.currentTimeMillis() + "";
                            InputStream inputStream = getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());
                            OutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png");

                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;

                            while ((len = inputStream.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            inputStream.close();
                            out.close();

                            String creationDate = DateTimeUtils.getDate();
                            ArrayList<String> a = new ArrayList<>();
                            a.add("'" + fileName + ".png'");
                            a.add("'CHILD'");
                            a.add("'" + path + "'");
                            a.add("'FILE'");
                            a.add("" + (++imageCountOfCurrentFolder));
                            a.add("'PNG'");
                            a.add("'" + creationDate + "'");
                            a.add("'" + creationDate + "'");
                            a.add("'" + "'"); //
                            a.add("'N" + "'"); //
                            fileTable.insertRecord(a);
                            refreshPage();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(data.getData() != null) {

                    try {
//                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        String fileName = System.currentTimeMillis() + "";
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        OutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png");

                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = inputStream.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        inputStream.close();
                        out.close();

                        String creationDate = DateTimeUtils.getDate();
                        ArrayList<String> a = new ArrayList<>();
                        a.add("'" + fileName + ".png'");
                        a.add("'CHILD'");
                        a.add("'" + path + "'");
                        a.add("'FILE'");
                        a.add("" + (++imageCountOfCurrentFolder));
                        a.add("'PNG'");
                        a.add("'" + creationDate + "'");
                        a.add("'" + creationDate + "'");
                        a.add("'" + "'"); //
                        a.add("'N" + "'"); //
                        fileTable.insertRecord(a);
                        refreshPage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            }
        }
        if (requestCode == 3) {
            if (data != null) {
                if (data.getStringExtra("result") != null) {
                    if (FileDetailsModel.isAllChildImages(getApplicationContext(), (data.getStringExtra("result")))) {
                        //copy images
                        String folderName = data.getStringExtra("result");
                        HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                        for (Integer i : selectedItems.keySet()) {
                            ArrayList<String> a = new ArrayList<>();
                            String folder = selectedItems.get(i);

                            Cursor cursor = fileTable.prepare().where("name", "=", "'" + folder + "'").select(new String[]{"*"});

                            if (cursor.moveToNext()) {
                                String fn = System.currentTimeMillis() + "";
                                String creationTime = DateTimeUtils.getDate();

                                InputStream in = null;
                                try {
                                    in = new FileInputStream((getExternalFilesDir(null) + "/" + cursor.getString(0)));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                OutputStream out = null;
                                try {
                                    out = new FileOutputStream((getExternalFilesDir(null) + "/" + fn));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                // Copy the bits from instream to outstream
                                byte[] buf = new byte[1024];
                                int len;

                                try {
                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                a.add("'" + fn + "'");
                                a.add("'" + "CHILD" + "'");
                                a.add("'" + folderName + "'");
                                a.add("'" + cursor.getString(3) + "'");
                                a.add("" + cursor.getInt(4));
                                a.add("'" + cursor.getString(5) + "'");
                                a.add("'" + creationTime + "'");
                                a.add("'" + creationTime + "'");
                                fileTable.insertRecord(a);
                            }
                        }
                    }
                    refreshPage();
                } else {
                    Toast.makeText(this, "Failed To Copy to " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == 88 && resultCode == RESULT_OK) {

        }
    if(requestCode == 4) {
            if(data!=null) {

                if(data.getStringExtra("result")!=null) {
                    if(FileDetailsModel.isAllChildImages(getApplicationContext(), (data.getStringExtra("result")))) {
                        //cut images
                        String folderName = data.getStringExtra("result");
                        HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                        for (Integer i : selectedItems.keySet()) {

                            String file = selectedItems.get(i);
                            fileTable.customQuery("update FileDetails set parent = '" + folderName + "', category = 'CHILD', orderno = "+ (FileDetailsModel.getImagesCountInsideDir(getApplicationContext(), folderName) + 1) +" where name = '" + file + "';");
                        }
                        refreshPage();
                    }
                    else{
                        Toast.makeText(this, "Failed To Move to " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void storeImage(Uri imageUri) {

    }

    private void refreshPage() {
        getSupportActionBar().setTitle(path);
        arrayList.clear();
        Cursor cursor = fileTable.prepare().where("parent", "=", "'" + path + "'").sort("orderno", "ASC").select(new String[]{"*"});

        if(cursor.moveToNext()) {
            do{
                FileDetailsModel recycleListInsideFolder = new FileDetailsModel();
                recycleListInsideFolder.name = (cursor.getString(0));
                recycleListInsideFolder.category = cursor.getString(1);
                recycleListInsideFolder.parent = cursor.getString(2);
                recycleListInsideFolder.type = (cursor.getString(3));
                recycleListInsideFolder.orderno = "" + (cursor.getInt(4));
                recycleListInsideFolder.extension = (cursor.getString(5));
                recycleListInsideFolder.creationDate = cursor.getString(6);
                recycleListInsideFolder.modifiedDate = cursor.getString(7);

                arrayList.add(recycleListInsideFolder);
            }
            while(cursor.moveToNext());
        }

        cursor.close();

        recyclerView.setAdapter(recycleAdapter);
    }

    @Override
    public void updateUI() {
        refreshPage();
    }

    public class ConvertToPDFTask extends AsyncTask<String, String, String> {
        private InsideFolderActivtiy context;
        AlertDialog dialog;

        ConvertToPDFTask(InsideFolderActivtiy context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.activity_main, null);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return PdfConverterBox.convertFrom(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            dialog.dismiss();
            Toast.makeText(context, "Saved in " + (Constants.PDF_PATH + aVoid + ".pdf"), Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, OpenPDFActivity.class);
            i.setDataAndType(Uri.parse("file:///" + Constants.PDF_PATH + aVoid + ".pdf"), "application/pdf");
            startActivity(i);
        }

    }
    public class ConvertToPDFTaskShare extends AsyncTask<String, String, String> {
        private InsideFolderActivtiy context;
        AlertDialog dialog;

        ConvertToPDFTaskShare(InsideFolderActivtiy context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.activity_main, null);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return PdfConverterBox.convertFrom(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(final String aVoid) {
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Constants.PDF_PATH + aVoid + ".pdf"));
                    startActivity(Intent.createChooser(share, "Share pdf ..."));
                }
            });

        }

    }

}