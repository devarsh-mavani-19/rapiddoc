package com.shortcontent.imagetopdf;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.core.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<String> createFile(final Context context) {
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                String imagesId;
                String rapidDocId;
                String pdfId;
                SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                if(!preferences.getBoolean("FolderCreatedOnDrive", false)) {
                    File fileMetadata = new File();
                    fileMetadata.setName("Rapid Doc");
                    fileMetadata.setMimeType("application/vnd.google-apps.folder");
                    File file1 = mDriveService.files().create(fileMetadata).execute();
                    preferences.edit().putString("RapidDocFolderId", file1.getId()).commit();
                    rapidDocId = file1.getId();

                    File fileMetadata2 = new File();
                    fileMetadata2.setName("Images");
                    fileMetadata2.setParents(Collections.singletonList(rapidDocId));
                    fileMetadata2.setMimeType("application/vnd.google-apps.folder");
                    File file2 = mDriveService.files().create(fileMetadata2).execute();
                    preferences.edit().putString("RapidDocImagesId", file2.getId()).commit();
                    imagesId = file2.getId();


                    File fileMetadata3 = new File();
                    fileMetadata.setName("PDFs");
                    fileMetadata.setParents(Collections.singletonList(rapidDocId));
                    fileMetadata.setMimeType("application/vnd.google-apps.folder");
                    File file3 = mDriveService.files().create(fileMetadata3).execute();
                    preferences.edit().putString("RapidDocPDFsId", file3.getId()).commit();
                    pdfId = file3.getId();

                    preferences.edit().putBoolean("FolderCreatedOnDrive", true).commit();
                }
                else {
                    imagesId = preferences.getString("RapidDocImagesId", "");
                    if(imagesId.equals("")) {
                        return null;
                    }
                }

                //check for parent folder
//                File rapidDoc = mDriveService.files().list().setSpaces

                SQLiteDatabase database = context.openOrCreateDatabase("FileInformationDB", Context.MODE_PRIVATE, null);
                String q = "select * from FileDetails where type = 'FILE'";
                Cursor cursor = database.rawQuery(q, null);
                java.io.File f;
                if(cursor.moveToNext()) {
                    do{

//                        File x = mDriveService.files().list()

                        f = new java.io.File(context.getExternalFilesDir(null) + "/" + cursor.getString(0));



                        File metadata = new File()
                                .setName(cursor.getString(0))
                                .setParents(Collections.singletonList(imagesId));

                        FileContent mediaContent = new FileContent("image/png", f);
                        File file = null;
                        try {
                            file=  mDriveService.files().create(metadata, mediaContent).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(file ==null) {
                            throw new IOException("Null result when requesting file");
                        }
                        String id =file.getId();
                        String update = "update FileDetails set synced = 'Y' and google_drive_id = '" + id + "' where name='" + cursor.getString(0) + "'";
                        database.execSQL(update);
                    } while(cursor.moveToNext()) ;
                    return "";
                } else{
                    System.out.println("Nothing found");
                }
                cursor.close();



               return null;
            }
        });
    }



    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, String>> readFile(final String fileId) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                // Retrieve the metadata as a File object.
                File metadata = mDriveService.files().get(fileId).execute();
                String name = metadata.getName();

                // Stream the file contents to a String.
                try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();

                    return Pair.create(name, contents);
                }
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(final String fileId, final String name, final String content) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Create a File containing any metadata changes.
                File metadata = new File().setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                return null;
            }
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, new Callable<FileList>() {
            @Override
            public FileList call() throws Exception {
                return mDriveService.files().list().setSpaces("drive").execute();
            }
        });
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            final ContentResolver contentResolver, final Uri uri) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                // Retrieve the document's display name from its metadata.
                String name;
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        name = cursor.getString(nameIndex);
                    } else {
                        throw new IOException("Empty cursor returned for file.");
                    }
                }

                // Read the document's contents as a String.
                String content;
                try (InputStream is = contentResolver.openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    content = stringBuilder.toString();
                }

                return Pair.create(name, content);
            }
        });
    }
}
