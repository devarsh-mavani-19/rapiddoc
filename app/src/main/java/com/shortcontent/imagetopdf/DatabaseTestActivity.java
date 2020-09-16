package com.shortcontent.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DatabaseTestActivity extends AppCompatActivity {
    DatabaseManagment tableTest;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        listView = findViewById(R.id.list_view_test);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);

        tableTest = new DatabaseManagment(getApplicationContext());
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


//        fieldName.add("row1");
//        fieldName.add("row2");
//        dataTypes.add("varchar(10)");
//        dataTypes.add("number");
        tableTest.createTable("FileDetails", tableFields, tableDatatypes);

//        ArrayList<String> insertValues = new ArrayList<>();
//        insertValues.add("'hello'");
//        insertValues.add("123");
//        tableTest.insertRecord(insertValues);
//
//        insertValues.clear();
//        insertValues.add("'hello'");
//        insertValues.add("456");
//        tableTest.insertRecord(insertValues);

        loadListView();
    }

    private void loadListView() {
        String[] a = {"*"};
        Cursor c = tableTest.prepare().select(a);
        if(c.moveToNext()){
            do{
                arrayList.add(c.getString(0));
            }
            while(c.moveToNext());
        }
        listView.setAdapter(adapter);
    }
}
