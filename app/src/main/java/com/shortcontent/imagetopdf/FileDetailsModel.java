package com.shortcontent.imagetopdf;

import android.content.Context;
import android.database.Cursor;

public class FileDetailsModel {
    String name;
    String category;
    String parent;
    String type;
    String orderno;
    String extension;
    String creationDate;
    String modifiedDate;


    public static int getImagesCountInsideDir(Context context, String parentFolderName) {
        DatabaseManagment databaseManagment = new DatabaseManagment(context);
        databaseManagment.useTable("FileDetails");
        Cursor cursor = databaseManagment.prepare().where("parent", "=", "'" + parentFolderName + "'").and().where("type","=","'FILE'").select(new String[]{"count(*)"});
        if(cursor.moveToNext()) {
            int yo = cursor.getInt(0);
            cursor.close();
            return yo;
        }
        cursor.close();

        return 0;
    }

    public static String getDisplayImage(Context context, String parentFolderName) {
        DatabaseManagment databaseManagment = new DatabaseManagment(context);
        databaseManagment.useTable("FileDetails");
        Cursor cursor = databaseManagment.prepare().where("parent", "=", "'" + parentFolderName + "'").and().where("type", "=", "'FILE'").and().where("orderno", "=", "1").select(new String[]{"name"});
        if(cursor.moveToNext()) {
            String yo = cursor.getString(0);
            cursor.close();
            return yo;
        }
        cursor.close();
        return "";
    }

    public static boolean isAllChildImages(Context context, String dirName) {
        DatabaseManagment databaseManagment = new DatabaseManagment(context);
        databaseManagment.useTable("FileDetails");
        Cursor cursor = databaseManagment.prepare().where("parent", "=", "'" + dirName + "'").select(new String[]{"type"});
        if(cursor.moveToNext()) {
            do{
                if(!cursor.getString(0).equals("FILE")) {
                    return false;
                }
            }
            while(cursor.moveToNext());
        }
        else{
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
