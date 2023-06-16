package com.example.sqlite

import SQLiteHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_items_dictionary)
        initView()
        initRecyclerView()
        sqliteHelper = SQLiteHelper(this)
        getItem()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter()
        recyclerView.adapter = adapter
    }

    private fun getItem() {
        val itemList = sqliteHelper.getAllItems()
        adapter.addItems(itemList)
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
    }


}
