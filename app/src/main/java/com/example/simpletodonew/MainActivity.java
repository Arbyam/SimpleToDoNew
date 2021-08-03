package com.example.simpletodonew;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button add;
    EditText ETitem;
    RecyclerView rvItems;
    itemsAdapter ItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        ETitem = findViewById(R.id.ETitem);
        //deleted something here find it and fix it




        loadItems();



        itemsAdapter.OnLongClickListener onLongClickListener = new itemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //notify the adapter
                ItemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        itemsAdapter.OnClickListener onClickListener = new itemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at position" + position);
                //Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        ItemsAdapter = new itemsAdapter(items, onLongClickListener,onClickListener);
        rvItems.setAdapter(ItemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = ETitem.getText().toString();
                //Add item to model
                items.add(todoItem);
                //notify adapter that we inserted the item
                ItemsAdapter.notifyItemInserted(items.size()-1);
                ETitem.setText("");
                Toast.makeText(getApplicationContext(),"item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });


    }
    //handle the result of the edit acitivity


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the editied item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //Update the model at the right position with the new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            itemsAdapter.
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT);
        } else {

            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }
    //This function will load items by reading every luine of the data file
    private void loadItems(){

        try {
            items = new ArrayList(org.apache.commons.io.FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error Reading items", e);
            items = new ArrayList();
        }
    }

    // this function saves items by writing them into the data file
    private void saveItems() {
        try {
            org.apache.commons.io.FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error with writing items", e);
        }
    }
}