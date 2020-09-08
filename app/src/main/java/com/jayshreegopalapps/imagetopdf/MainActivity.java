package com.jayshreegopalapps.imagetopdf;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.pdf.PdfCopy;
//import com.itextpdf.text.pdf.PdfImportedPage;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
//import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class MainActivity extends AppCompatActivity implements CustomBottomModalSheetFragment.BottomSheetListener, RenameBottomModalSheet.BottomSheetListener {
    private static final int RESULT_PDF_TO_IMAGE = 6;
    private static final int RESULT_PDF_TO_WORD = 7;
    private static final int RESULT_PDF_SPLIT = 8;
    private static final int RESULT_COMPRESS_PDF = 9;
    private static final int RESULT_ADD_PAGE = 10;
    private static final int REQUEST_CUSTOM_CAMERA = 11;

    enum recycler_mode {
            MODE_GRID,
            MODE_LIST
    };
    recycler_mode r_mode = recycler_mode.MODE_GRID;
    String parent;
    Toolbar toolbar;
    CheckBox check;
    ImageView empty_image;
    private RecyclerView recyclerView;
    private FloatingActionButton fab, fab2, fab3, fab_opener;
    LinearLayout bottomNavigationView;
    ArrayList<FileDetailsModel> arrayList = new ArrayList<>();
    RecycleAdapter recycleAdapter;
    DatabaseManagment filesTable;
    private boolean isInMultiSelectMode = false;
    SQLiteDatabase database;
    HashMap<Integer, String> selectedItems;
    SharedPreferences prefs = null;
    int paddingBottomRecyclerView;
    private boolean isOpen = false;
//    private InterstitialAd interstitialAd;
    MenuItem renameItem;
    RelativeLayout imageView_empty;
    private Animation fab_clock, fab_anticlock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PDFBoxResourceLoader.init(getApplicationContext());
        prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });

//        MobileAds.initialize(getApplicationContext());

//        prepareAd();

        createDirs();


//        final AdView mAdView = findViewById(R.id.adView_banner_main);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//            }
//        });

        //ask permissions
        askPermissions();

        //init views
        initViews();


        //set ActionBar
        setSupportActionBar(toolbar);

        //initialize Menu Items
        initializeBottomMenu();

        //init Horizontal
        initHorizontalBar();

        //initialize database
        initTable();

        extractBundle();
        //reset to defaults
        resetLayoutToDefault();
        fab_clock = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate);
        fab_anticlock = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate_anticlock);
        //on click handlers
        fab_opener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(isOpen) {
                    //close
                    fab_opener.startAnimation(fab_anticlock);
                    fab.animate().translationY(0);
                    fab2.animate().translationY(0);
                    fab3.animate().translationY(0);
                    fab.setClickable(false);
                    fab2.setClickable(false);
                    fab3.setClickable(false);
                    isOpen = false;
                }
                else  {
                    //open

                    fab_opener.startAnimation(fab_clock);
                    fab.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                    fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
                    fab.setClickable(true);
                    fab2.setClickable(true);
                    fab3.setClickable(true);
                    isOpen = true;

                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                startActivityForResult(intent, 0);
                //launch interstitial ads

                if(!askPermissions()) {
                    return;
                }

                if ((ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED)) {

                    Intent customCamera = new Intent(MainActivity.this, CustomCameraActivity.class);
                    startActivityForResult(customCamera, REQUEST_CUSTOM_CAMERA);


                }
                else {
                    askPermissions();
                }



            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setView(R.layout.pdftools);
                final AlertDialog a = alertDialog.create();
                a.show();
                Chip pdf_to_image_ = a.findViewById(R.id.pdf_to_image);
                Chip pdf_merge_ = a.findViewById(R.id.pdf_merge);
                Chip pdf_split_ = a.findViewById(R.id.pdf_split);

                //Chip pdf_watermark_  = a.findViewById(R.id.pdf_watermark);
                final Chip split_by_fixed_range_ = a.findViewById(R.id.split_by_fixed_range);
                final Chip compress_pdf_ = a.findViewById(R.id.compress_pdf);
//                Chip image_in_pdf_ = a.findViewById(R.id.image_in_pdf);

//                image_in_pdf_.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        a.dismiss();
//                        Intent i = new Intent(getApplicationContext(), ImageInPDFActivity.class);
//                        startActivity(i);
//                    }
//                });



                pdf_merge_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        pdf_merge(v);
                    }
                });
                pdf_to_image_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        if(!askPermissions()) {
                            return;
                        }
                        pdf_image(v);
                    }
                });

              /*  pdf_watermark_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        pdf_watermark(v);
                    }
                });*/
                pdf_split_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        pdf_split(v);
                    }
                });
                split_by_fixed_range_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        split_by_fixed_range();
                    }
                });

                compress_pdf_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
//                        compress_pdf();
                        Intent i = new Intent(MainActivity.this, AddWatermarkActivity.class);
                        startActivity(i);
                    }
                });
            }
        });
        fab2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "PDF Tools", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!askPermissions()) {
                    return;
                }
                Intent i = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length >= 1) {
            if (permissions.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    createDirs();
                }
            }
        }
    }

    private void createDirs() {
        askPermissions();
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


    public void prepareAd() {
//    interstitialAd = new InterstitialAd(getApplicationContext());
//    interstitialAd.setAdUnitId("ca-app-pub-4411531601838575/9330661235");
//    interstitialAd.loadAd(new AdRequest.Builder().build());
}

    private void initHorizontalBar() {
//        Chip pdf2Image = findViewById(R.id.pdf_to_image);
//        pdf2Image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getApplicationContext(), ViewPDF.class);
////                Bundle bundle = new Bundle();
////                bundle.putString("result", ViewPdfStartConstants.PDF_TO_IMAGE);
////                intent.putExtras(bundle);
////                startActivityForResult(intent, RESULT_PDF_TO_IMAGE);
//
//                Intent intent = new Intent();
//                intent.setType("application/pdf");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_PDF_TO_IMAGE);
//
//            }
//        });

    }

    private void extractBundle() {
            if(getIntent().getExtras()!=null) {
                parent = getIntent().getExtras().getString("parentFolder");
            }
            else{
                parent = null;
            }
    }

    private void initTable() {
        filesTable = new DatabaseManagment(getApplicationContext());
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

        filesTable.createTable("FileDetails", tableFields, tableDatatypes);

    }

    private void initViews() {
        recyclerView = findViewById(R.id.folders_grid_content_main);
        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab_pdf);
        fab3 = findViewById(R.id.fab_qr);
        fab_opener = findViewById(R.id.open_main);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        paddingBottomRecyclerView = recyclerView.getPaddingBottom();
        imageView_empty = findViewById(R.id.empty_image);
    }

    private void initializeBottomMenu() {
        final ImageView  copyMenu, cutMenu, deleteMenu;
        copyMenu = findViewById(R.id.copy_bottom_nav);
        cutMenu = findViewById(R.id.cut_bottom_nav);
        deleteMenu = findViewById(R.id.delete_bottom_nav);

        copyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recycleAdapter.getSelectedItems().size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Please Select a Folder", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                Intent navIntent = new Intent(getApplicationContext(), NavigateAllFolders.class);
                startActivityForResult(navIntent, 3);
//                CustomBottomModalSheetFragment customBottomModalSheetFragment = new CustomBottomModalSheetFragment(selectedItems, 1);
//                customBottomModalSheetFragment.show(getSupportFragmentManager(), null);
            }
        });

        cutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> selectedItems = recycleAdapter.getSelectedItems();
                if(selectedItems.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Please Select a Folder", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
//                CustomBottomModalSheetFragment customBottomModalSheetFragment = new CustomBottomModalSheetFragment(selectedItems, 0);
//                customBottomModalSheetFragment.show(getSupportFragmentManager(), null);
                Intent navIntent = new Intent(getApplicationContext(), NavigateAllFolders.class);
                startActivityForResult(navIntent, 4);
            }
        });

        deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedItems = recycleAdapter.getSelectedItems();
                if(selectedItems.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Please Select a Folder", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                for(Integer i : selectedItems.keySet()) {

                    String query = "select * from FileDetails where parent = '" + selectedItems.get(i) + "'";
                    Cursor cursor = filesTable.customSelect(query);
                    if(cursor.moveToNext()) {
                        do{
                            if(cursor.getString(3).equals("FILE")) {
                                filesTable.customQuery("delete from FileDetails where name = '" + cursor.getString(0) + "'");
                            }
                            else{
                                recursionDelete(cursor.getString(0));
                                filesTable.customQuery("delete from FileDetails where name = '" + cursor.getString(0) + "'");
                            }
                        }
                        while (cursor.moveToNext());
                    }
                    cursor.close();
                    filesTable.customQuery("delete from FileDetails where name = '" + selectedItems.get(i) + "'");
                }
                resetLayoutToDefault();
//              deleteSelectedFolders();
            }
        });

    }


    private void recursionDelete(String parentFolderName) {
        Cursor cursor = filesTable.customSelect("select * from FileDetails where parent = '" + parentFolderName + "';");

        if(cursor.moveToNext()) {
            do{
                if(cursor.getString(3).equals("FILE")) {

                    filesTable.customQuery("delete from FileDetails where name = '" + cursor.getString(0) + "'");
                }
                else{
                    recursionDelete(cursor.getString(0));
                    filesTable.customQuery("delete from FileDetails where name = '" + parentFolderName + "'");
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private void deleteSelectedFolders() {
        filesTable.prepare().where("parent","","").select(new String[]{"*"});
        getAllFilesAndFolders("temp");
    }

    private ArrayList<String> getAllFilesAndFolders (String rootPath) {
        ArrayList<String> listOfFilesAndFolders = new ArrayList<>();

        Cursor cursor = filesTable.prepare().where("parent", "=", rootPath).select(new String[]{"name", "type"});
        if(cursor.moveToNext()) {
            do{
                //store in arraylist
                listOfFilesAndFolders.add(cursor.getString(0));
                if(cursor.getString(1).equals("'DIR'")) {
                    Cursor cursor1 = filesTable.prepare().where("parent", "=", rootPath).select(new String[]{"name", "type"});
                }
            }
            while(cursor.moveToNext());
        }
        return listOfFilesAndFolders;
    }

    private boolean askPermissions() {
        //permission
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
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isInMultiSelectMode) {
            resetLayoutToDefault();
        }
        else {
            super.onBackPressed();
        }
    }


    void updateUIToMultiSelectModeFunc() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), (recyclerView.getPaddingBottom() + 60));
        fab.hide();
        fab2.hide();
        fab3.hide();
        fab_opener.hide();
        //change action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isInMultiSelectMode = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }

        prepareAd();
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getExtras() != null) {
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        Date d = new Date();
                        d.setTime(System.currentTimeMillis());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        Date d2 = new Date();
                        d2.setTime(System.currentTimeMillis());

                        String creationTime = sdf2.format(d2);

                        String directoryName = sdf.format(d);
                        String fileName = "" + System.currentTimeMillis();

                        try {
                            //save in local storage
                            FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png", false);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                            //insert into database
                            //insert directory as root
                            ArrayList<String> insertDirArrayList = new ArrayList<>();
                            insertDirArrayList.add("'" + directoryName + "'"); //name
                            if(parent==null){
                                insertDirArrayList.add("'" + "ROOT" + "'"); //category
                                insertDirArrayList.add(""); //parent
                            }
                            else{
                                insertDirArrayList.add("'" + "CHILD" + "'"); //category
                                insertDirArrayList.add("'" + parent + "'"); //parent
                            }

                            insertDirArrayList.add("'" + "DIR" + "'"); //type
                            insertDirArrayList.add(""); //order number
                            insertDirArrayList.add(""); //extension
                            insertDirArrayList.add("'" + creationTime + "'"); //creation
                            insertDirArrayList.add("'" + creationTime + "'"); //modified
                            filesTable.insertRecord(insertDirArrayList); //save


                            //insert image as child
                            ArrayList<String> insertImageArrayList = new ArrayList<>();
                            insertImageArrayList.add("'" + fileName + ".png'"); //name
                            insertImageArrayList.add("'" + "CHILD" + "'"); //category
                            insertImageArrayList.add("'" + directoryName + "'"); //parent
                            insertImageArrayList.add("'" + "FILE" + "'"); //type
                            insertImageArrayList.add("1"); //order number
                            insertImageArrayList.add("'" + "PNG" + "'"); //extension
                            insertImageArrayList.add("'" + creationTime + "'"); //creation
                            insertImageArrayList.add("'" + creationTime + "'"); //modified
                            filesTable.insertRecord(insertImageArrayList); //save

                            //open next activity
                            Intent nextIntent = new Intent(getApplicationContext(), InsideFolderActivtiy.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("folderPath", directoryName);
                            bundle.putInt("imageCount", 1);
                            nextIntent.putExtras(bundle);
                            startActivity(nextIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                        getContentResolver().delete(uri, null, null);
//                        scannedImageView.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    return;
                }
            } else {
                //nothing captured
                // do nothing
                return;
            }
            //store

        }
        if(requestCode == 3) {
            if (data != null) {
                String name = data.getStringExtra("result");
                if(name.equals("root")) {
                    Toast.makeText(getApplicationContext(), "already present at screen", Toast.LENGTH_LONG).show();
                }
                else {
                    selectedItems = recycleAdapter.getSelectedItems();
                    for (Integer i : selectedItems.keySet()) {
                        ArrayList<String> a = new ArrayList<>();
                        String folder = selectedItems.get(i);
                        if (!name.equals(folder)) {
                            Cursor cursor = filesTable.prepare().where("name", "=", "'" + folder + "'").select(new String[]{"*"});

                            if (cursor.moveToNext()) {
                                String fn = DateTimeUtils.getDateTime();
                                String creationTime = DateTimeUtils.getDate();
                                a.add("'" + fn + "'");
                                a.add("'" + "CHILD" + "'");
                                a.add("'" + name + "'");
                                a.add("'" + cursor.getString(3) + "'");
                                a.add("" + cursor.getInt(4));
                                a.add("'" + cursor.getString(5) + "'");
                                a.add("'" + creationTime + "'");
                                a.add("'" + creationTime + "'");
                                filesTable.insertRecord(a);

                                if (FileDetailsModel.isAllChildImages(getApplicationContext(), folder)) {
                                    //duplicate images
                                    duplicateImages(folder, fn);
                                } else {
                                    recursionCopy(folder, fn);
                                }
                            }
                        }
                    }
                    resetLayoutToDefault();
                }
            }
        }
        if(requestCode == 4) {
            if(data!=null) {
                String folderName = data.getStringExtra("result");
                if (folderName.equals("root")) {
                    Toast.makeText(getApplicationContext(), "already present at screen", Toast.LENGTH_LONG).show();
                } else {
                    selectedItems = recycleAdapter.getSelectedItems();
                    for (Integer i : selectedItems.keySet()) {
                        String folder = selectedItems.get(i);
                        if (!folderName.equals(folder)) {
                            filesTable.customQuery("update FileDetails set parent = '" + folderName + "', category = 'CHILD' where name = '" + folder + "';");
                        }
                    }
                    resetLayoutToDefault();
                }
            }
        }
        if(requestCode == 5) {
            //import from gallery
            if(resultCode == RESULT_OK) {

                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;

                //create dir in current dir
                ArrayList<String> b = new ArrayList<>();
                String dirName = DateTimeUtils.getDateTime();
                String creationTime = DateTimeUtils.getDate();

                b.add("'" + dirName + "'"); //name
                if(parent==null){
                    b.add("'" + "ROOT" + "'"); //category
                    b.add(""); //parent
                }
                else{
                    b.add("'" + "CHILD" + "'"); //category
                    b.add("'" + parent + "'"); //parent
                }

                b.add("'" + "DIR" + "'"); //type
                b.add(""); //order number
                b.add(""); //extension
                b.add("'" + creationTime + "'"); //creation
                b.add("'" + creationTime + "'"); //modified
                filesTable.insertRecord(b); //save

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);

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
                    a.add("'" + dirName + "'");
                    a.add("'FILE'");
                    a.add("" + 1);
                    a.add("'PNG'");
                    a.add("'" + creationDate + "'");
                    a.add("'" + creationDate + "'");
                    filesTable.insertRecord(a);
                    resetLayoutToDefault();

//                    scannedImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                if(data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
//                    for(int i = 0; i < count; i++) {
//                        try {
//                            String fileName = System.currentTimeMillis() + "";
//                            InputStream inputStream = getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());
//                            OutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png");
//
//                            // Copy the bits from instream to outstream
//                            byte[] buf = new byte[1024];
//                            int len;
//
//                            while ((len = inputStream.read(buf)) > 0) {
//                                out.write(buf, 0, len);
//                            }
//
//                            inputStream.close();
//                            out.close();
//
//                            String creationDate = DateTimeUtils.getDate();
//                            ArrayList<String> a = new ArrayList<>();
//                            a.add("'" + fileName + ".png'");
//                            a.add("'CHILD'");
//                            a.add("'" + dirName + "'");
//                            a.add("'FILE'");
//                            a.add("" + (i + 1));
//                            a.add("'PNG'");
//                            a.add("'" + creationDate + "'");
//                            a.add("'" + creationDate + "'");
//                            filesTable.insertRecord(a);
//                            resetLayoutToDefault();
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                else if(data.getData() != null) {
//
//                    try {
////                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//                        String fileName = System.currentTimeMillis() + "";
//                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                        OutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName + ".png");
//
//                        // Copy the bits from instream to outstream
//                        byte[] buf = new byte[1024];
//                        int len;
//
//                        while ((len = inputStream.read(buf)) > 0) {
//                            out.write(buf, 0, len);
//                        }
//
//                        inputStream.close();
//                        out.close();
//
//                        String creationDate = DateTimeUtils.getDate();
//                        ArrayList<String> a = new ArrayList<>();
//                        a.add("'" + fileName + ".png'");
//                        a.add("'CHILD'");
//                        a.add("'" + dirName + "'");
//                        a.add("'FILE'");
//                        a.add("" + 1);
//                        a.add("'PNG'");
//                        a.add("'" + creationDate + "'");
//                        a.add("'" + creationDate + "'");
//                        filesTable.insertRecord(a);
//                        resetLayoutToDefault();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //do something with the image (save it to some directory or whatever you need to do with it here)
//                }
            }
        }

        if(requestCode == RESULT_PDF_TO_IMAGE) {

            if(data!=null) {
                if(data.getData()!=null) {
                    Uri uri = data.getData();
                    convertToImages(uri);


                }
            }
        }

        if (requestCode == RESULT_PDF_TO_WORD) {
            if(data!=null) {
                if(data.getData()!=null) {
//                    Uri uri = data.getData();
//                    try {
//                        PdfReader reader = new PdfReader(getContentResolver().openInputStream(uri));
//                        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
//                        SimpleTextExtractionStrategy strategy = parser.processContent(1,new SimpleTextExtractionStrategy());
//                        String text = strategy.getResultantText();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        if(requestCode == REQUEST_CUSTOM_CAMERA && resultCode == RESULT_OK) {
            if(data!=null) {

                String s = data.getStringExtra("location");

                int REQUEST_CODE = 0;
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("location" , s);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, REQUEST_CODE);

            }
        }
        if(requestCode == RESULT_COMPRESS_PDF) {
            if(data!=null) {
                if (data.getData()!=null) {
//                    PdfReader reader = null;
//                    try {
//                        reader = new PdfReader(getContentResolver().openInputStream(data.getData()));
//                        Document document = new Document();
//                        PdfCopy writer = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH+ System.currentTimeMillis() + ".pdf"));
//                        document.open();
//                        for(int i = 1;i <= reader.getNumberOfPages();i++) {
//                            PdfImportedPage page = writer.getImportedPage(reader, i);
//                            writer.addPage(page);
//                        }
//                        document.close();
//                        writer.setFullCompression();
//                        writer.close();
//                        Toast.makeText(this, "Compressed file stored at " + Constants.PDF_STORAGE_PATH+ System.currentTimeMillis() + ".pdf", Toast.LENGTH_LONG).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    private void convertToImages(Uri uri) {
        LoadingInBackground loadingInBackground = new LoadingInBackground(MainActivity.this, this);
        loadingInBackground.execute(uri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetLayoutToDefault();
        if (prefs.getBoolean("fab_opener", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("fab_opener", false).commit();


            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab_opener)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"+
                            "Show PDF tools")

                    .build();

           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .show();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docsFolder.exists()) {
               docsFolder.mkdir();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        renameItem = menu.findItem(R.id.action_rename_folder);
//        if(parent == null) {
//            renameItem.setEnabled(false);
//        }
//        else{
//            renameItem.setEnabled(true);
//        }
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<FileDetailsModel> tempList = new ArrayList<>();

                for(FileDetailsModel r : arrayList){
                    if(r.name.contains(query)) {
                        tempList.add(r);
                    }
                }
                if(tempList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Empty List", Toast.LENGTH_SHORT).show();
                }
                else{
                    recycleAdapter.updateDataSet(tempList);
                }
                
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                recycleAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            resetLayoutToDefault();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_folder) {
            //create new Folder as root since it is Main Activity
            ArrayList<String> values = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Date d = new Date();
            d.setTime(System.currentTimeMillis());
            String folderName = sdf.format(d);

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            Date d2 = new Date();
            d2.setTime(System.currentTimeMillis());
            String creationTime = sdf2.format(d2);


            values.add("'" + folderName + "'"); //name
            if(parent==null) {
                values.add("'ROOT'"); //category
                values.add(""); //parent
            }
            else{
                values.add("'CHILD'"); //category
                values.add("'" + parent + "'"); //parent
            }
            values.add("'DIR'"); //type
            values.add(""); //orderno
            values.add(""); //extension
            values.add("'" + creationTime + "'"); //creation date
            values.add("'" + creationTime + "'"); //modified date

            filesTable.insertRecord(values);
            resetLayoutToDefault();
            return true;
        }

        if(id == R.id.action_import_gallery) {
            askPermissions();
            int REQUEST_CODE = 5;
            int preference = ScanConstants.OPEN_MEDIA;
            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 5);
        }

        if(id == R.id.action_rename_folder) {
            RenameBottomModalSheet renameBottomModalSheet;
            if(parent == null) {
                HashMap<Integer, String> selected = recycleAdapter.getSelectedItems();
                if(selected.size() == 1) {
                    for(Integer i : selected.keySet()) {
                        renameBottomModalSheet = new RenameBottomModalSheet(selected.get(i));
                        renameBottomModalSheet.show(getSupportFragmentManager(), "rename");
                        break;
                    }
                }
                else {
                    renameBottomModalSheet = new RenameBottomModalSheet("");
                    renameBottomModalSheet.show(getSupportFragmentManager(), "rename");
                }
            }
            else{
                renameBottomModalSheet = new RenameBottomModalSheet(parent);
                renameBottomModalSheet.show(getSupportFragmentManager(), "rename");
            }

        }
        if(id == R.id.action_recycler_mode) {
            if(r_mode == recycler_mode.MODE_GRID) {
                item.setTitle("List View");
                r_mode = recycler_mode.MODE_LIST;
            }
            else{
                item.setTitle("Grid View");
                r_mode = recycler_mode.MODE_GRID;
            }
            resetLayoutToDefault();
        }

        return super.onOptionsItemSelected(item);
    }

    void resetLayoutToDefault() {
        if(parent == null) {
            getSupportActionBar().setTitle("ImageToPDF");
        }
        else {
            getSupportActionBar().setTitle(parent);
        }
        if(isInMultiSelectMode) {
            check = findViewById(R.id.check_multi_select);
            check.setVisibility(View.INVISIBLE);
            renameItem.setEnabled(false);
        }

        arrayList.clear();
        fetchRootFolders(parent);

        if(arrayList.isEmpty()) {
            imageView_empty.setVisibility(View.VISIBLE);
        }
        else {
            imageView_empty.setVisibility(View.GONE);
        }

        recycleAdapter = new RecycleAdapter(getApplicationContext(), arrayList, new MultipleSelectionInterface() {
            @Override
            public void updateUIToMultipleSelectMode() {
                updateUIToMultiSelectModeFunc();
            }

            @Override
            public void upadteUIToNormalMode() {

            }

            @Override
            public void disableRenameOptions() {
                renameItem.setEnabled(false);
            }

            @Override
            public void enableRenameOptions() {
                renameItem.setEnabled(true);
            }
        }, false);
        recyclerView.setAdapter(recycleAdapter);
        recycleAdapter.notifyDataSetChanged();
        if(r_mode == recycler_mode.MODE_GRID) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), paddingBottomRecyclerView);
        fab.show();
        fab2.show();
        fab3.show();
        fab_opener.show();
        bottomNavigationView.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        isInMultiSelectMode = false;
    }

    void fetchRootFolders(@Nullable String parentFolder) {
        //required fields
        //name, modified date, thumbnail, page count
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

    public void cutItem (String folderName, HashMap<Integer, String> selectedItems) {

    }

    public void copyItem(String name, HashMap<Integer, String> selectedItems) {

    }

    private void recursionCopy(String folder, String fn) {
        Cursor cursor = filesTable.prepare().where("parent", "=", "'" + folder + "'").select(new String[] {"*"});
        if(cursor.moveToNext()) {

            do{
                ArrayList<String> a = new ArrayList<>();
                String creationTime = DateTimeUtils.getDate();
                String fnew = DateTimeUtils.getDateTime();

                a.add("'" + fnew + "'");
                a.add("'" + "CH" +
                        "ILD" + "'");
                a.add("'" + fn + "'");
                a.add("'" + cursor.getString(3) + "'");
                a.add("" + cursor.getInt(4));
                a.add("'" + cursor.getString(5) + "'");
                a.add("'" + creationTime + "'");
                a.add("'" + creationTime + "'");
                filesTable.insertRecord(a);


                if(FileDetailsModel.isAllChildImages(getApplicationContext(), cursor.getString(0))) {
                    //duplicate images
                    duplicateImages(cursor.getString(0), fnew);
                }
                else{
                    recursionCopy(folder, fn);
                }

            }
            while(cursor.moveToNext());
        }
    }

    private void duplicateImages(String oldParentFolder, String newParentFolder) {
        Cursor cursor1;
//        if(oldParentFolder.equals("")) {
//            Toast.makeText(this, "old parent root", Toast.LENGTH_SHORT).show();
//            cursor1 = filesTable.customSelect("select * from FileDetails where parent = null;");
//        }
//        else{
            cursor1 = filesTable.customSelect("select * from FileDetails where parent = '"+ oldParentFolder +"';");
//        }


        if(cursor1.moveToNext()) {
            do{



                try {
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    String creationTime = DateTimeUtils.getDate();

                    String fName = System.currentTimeMillis() + "";

                    FileInputStream fis = new FileInputStream(getExternalFilesDir(null) + "/" + cursor1.getString(0));
                    FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/" + fName + ".png");

                    int x;
                    byte[] buf=new byte[1024];
                    while ((x = fis.read(buf))!=-1) {
                        fos.write(buf,0,x);
                    }

                    fos.close();
                    fis.close();

                    stringArrayList.add("'" + fName + ".png'");
                    stringArrayList.add("'" + cursor1.getString(1) + "'");
                    stringArrayList.add("'" + newParentFolder + "'");
                    stringArrayList.add("'" + cursor1.getString(3) + "'");
                    stringArrayList.add("" + cursor1.getInt(4));
                    stringArrayList.add("'" + cursor1.getString(5) + "'");
                    stringArrayList.add("'" + creationTime + "'");
                    stringArrayList.add("'" + creationTime + "'");
                    filesTable.insertRecord(stringArrayList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            while(cursor1.moveToNext());
        }
        else{

        }

    }

    @Override
    public void yes(String name) {

    }

    @Override
    public void save(String name, String newName) {
        if(name.equals("") || newName.equals("")) {
            Toast.makeText(this, "Failed to rename", Toast.LENGTH_SHORT).show();
        }
        else{
            Cursor cursor = filesTable.customSelect("select count(*) from FileDetails where name = '" + newName + "'");
            if(cursor.moveToNext()) {
                if(cursor.getInt(0) > 0) {
                    Toast.makeText(this, "Folder already exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    filesTable.customQuery("update FileDetails set name = '" + newName + "' where name = '" + name + "'");
                    filesTable.customQuery("update FileDetails set parent = '" + newName + "' where parent = '" + name + "'");
                }
            }
            cursor.close();
            if(parent == null) {
                parent = null;
            }
            else{
                parent = newName;
            }

            resetLayoutToDefault();

        }
    }

    public void pdf_merge(View v) {
        Intent intent = new Intent(getApplicationContext(), PDFMergeActivity.class);
        startActivity(intent);
    }

    public void pdf_split(View v) {
        Intent intent = new Intent(getApplicationContext(), SplitPDFActivity.class);
        startActivity(intent);
    }
    public void pdf_watermark(View v) {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }
    public void pdf_image(View v) {
//        Intent intent = new Intent(getApplicationContext(), ViewPDF.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("result", ViewPdfStartConstants.PDF_TO_IMAGE);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, RESULT_PDF_TO_IMAGE);

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_PDF_TO_IMAGE);
    }
    public void pdf_word(View v) {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_PDF_TO_WORD);
    }
    public void word_pdf(View v) {

    }
    private void split_by_fixed_range() {
        Intent intent = new Intent(getApplicationContext(), SplitByFixedRangeActivity.class);
        startActivity(intent);
    }

    private void compress_pdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_COMPRESS_PDF);
    }

    private void add_page_numbers() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a PDF"), RESULT_ADD_PAGE);
    }

}
