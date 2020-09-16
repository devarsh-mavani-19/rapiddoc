package com.shortcontent.imagetopdf;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseManagment {
    SQLiteDatabase database;
    private Context context;
    private String tableName;

    StringBuilder sb = new StringBuilder();
    boolean isSorting = false;

    public DatabaseManagment(Context context) {
        this.context = context;
        database = context.openOrCreateDatabase("FileInformationDB", Context.MODE_PRIVATE, null);
    }

    public void useTable(String tableName) {
        this.tableName = tableName;
    }

    public void createTable(String tableName, ArrayList<String> fieldName, ArrayList<String> dataTypes) {
        this.tableName = tableName;
        StringBuilder restQuery = new StringBuilder();

        for(int i = 0;i < fieldName.size();i++) {
            String name = fieldName.get(i);
            String dt = dataTypes.get(i);
            restQuery = restQuery.append(name).append(" ").append(dt).append(",");
        }
        restQuery = restQuery.replace(restQuery.length() - 1, restQuery.length(), "");
        String query = "create table if not exists " + tableName + " ( " + restQuery + ");";
        database.execSQL(query);
    }

    public void deleteTable(String tableName) {
        String query = "DROP TABLE " + tableName;
        database.execSQL(query);
    }

    public void deleteRecords() {
        StringBuilder query = new StringBuilder(("DELETE FROM " + this.tableName + " WHERE "));
        query = query.append(sb).append(";");
        database.execSQL(new String(query));
    }

    public void insertRecord(ArrayList<String> values) {
        StringBuilder restQuery = new StringBuilder();
        //sample query = ('hello', 123, null)
        for (String value: values) {
            if(value == null || value.equals("")){
                //insert empty
                restQuery = restQuery.append("null").append(",");
            }
            else{
                restQuery = restQuery.append(value).append(",");
            }

        }
        //remove coma from end
        restQuery = restQuery.replace(restQuery.length() - 1, restQuery.length(),"");
        String query = "insert into " + this.tableName + " VALUES(" + restQuery + ");";
        database.execSQL(query);
    }

    public void updateRecords(ArrayList<String> setFieldName, ArrayList<String> setValues) {
        StringBuilder temp = new StringBuilder();
        temp.append("UPDATE " + this.tableName + " SET ");

        for(int i = 0;i < setFieldName.size();i++) {
            temp.append(setFieldName.get(i)).append("=").append(setValues.get(i)).append(",");
        }
        //remove coma
        temp = temp.replace(temp.length() - 1,temp.length(), "");

        temp = temp.append(" WHERE ").append(sb).append(";");
        database.execSQL(new String(temp), null);
    }

    public DatabaseManagment prepare() {

        sb = new StringBuilder();
        sb.append("");
        isSorting = false;
        return this;
    }

    public DatabaseManagment where(String field, String condition, String value) {
        sb.append(field).append(condition).append(value);
        return this;
    }

    public DatabaseManagment not() {
        sb.append(" NOT ");
        return this;
    }

    public DatabaseManagment or() {
        sb.append(" OR ");
        return this;
    }

    public DatabaseManagment and() {
        sb.append(" AND ");
        return this;
    }

    public DatabaseManagment sort(String field, String order) {
        if(!isSorting) {
            //append ORDER BY
            sb.append(" ORDER BY ");
        }
        sb.append(field).append(" ");
        if(order.equals("DESC")) {
            sb.append("DESC");
        }
        isSorting = true;
        return this;
    }

    public DatabaseManagment openBracket() {
        sb.append(" ( ");
        return this;
    }

    public DatabaseManagment closeBracket() {
        sb.append(" ) ");
        return this;
    }

    public Cursor select(String[] fieldsName) {
        StringBuilder temp = new StringBuilder();
        temp.append("SELECT ");
        for (int i = 0;i < fieldsName.length;i++) {
            temp = temp.append(fieldsName[i]).append(",");
        }
        temp = temp.replace(temp.length() - 1,temp.length(), "");
        temp = temp.append(" FROM ").append(this.tableName);

        temp = temp.append(" where ").append(sb).append(";");

        return database.rawQuery(new String(temp), null);
    }

    public void customQuery(String query) {
        database.execSQL(query);
    }

    public Cursor customSelect(String query) {
        return database.rawQuery(query, null);
    }
}