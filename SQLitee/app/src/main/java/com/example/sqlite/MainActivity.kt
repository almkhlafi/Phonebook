package com.example.sqlite

import SQLiteHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initRecyclerView()
        sqliteHelper = SQLiteHelper(this)
        getItem()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, UserInputActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getItem() {
        val itemList = sqliteHelper.getAllItems()
        adapter.addItems(itemList)
    }



    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter()
        recyclerView.adapter = adapter

        adapter.setOnClickItem { item ->
            val intent = Intent(this, UpdateContact::class.java)
            intent.putExtra("itemId", item.id)
            intent.putExtra("itemName", item.name)
            intent.putExtra("itemNumber", item.phonenumber)
            intent.putExtra("itemImage", item.image)
            startActivity(intent)
        }

        adapter.setOnDeleteItemClick { item ->
            deleteItem(item.id)
        }
    }


    private fun deleteItem(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this contact?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { dialog, _ ->
            val deletedRows = sqliteHelper.deleteItemByID(id)
            if (deletedRows > 0) {
                Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
                getItem()
            } else {
                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}
