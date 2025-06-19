package com.example.cashwise

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.cashwise.R
import com.example.cashwise.Expense

class MainActivity : AppCompatActivity() {
    private lateinit var expensesList: ArrayList<Expense>
    private lateinit var adapter: ArrayAdapter<Expense>
    private lateinit var totalAmountText: TextView
    private lateinit var expensesListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        totalAmountText = findViewById(R.id.totalAmountText)
        expensesListView = findViewById(R.id.expensesListView)
        val addExpenseButton = findViewById<Button>(R.id.addExpenseButton)

        // Initialize expenses list
        expensesList = ArrayList()
        loadExpenses()

        // Setup ListView adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expensesList)
        expensesListView.adapter = adapter

        // Update total display
        updateTotal()

        // Add expense button click
        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        // Delete expense on long click
        expensesListView.setOnItemLongClickListener { _, _, position, _ ->
            expensesList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveExpenses()
            updateTotal()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
        adapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateTotal() {
        val total = expensesList.sumOf { it.amount }
        totalAmountText.text = "Total Spent: ${String.format("%.2f", total)}"
    }

    private fun saveExpenses() {
        val sharedPreferences = getSharedPreferences("CashWise", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(expensesList)
        editor.putString("expenses", json)
        editor.apply()
    }

    private fun loadExpenses() {
        val sharedPreferences = getSharedPreferences("CashWise", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("expenses", null)
        val type = object : TypeToken<ArrayList<Expense>>() {}.type

        expensesList = if (json != null) {
            gson.fromJson(json, type)
        } else {
            ArrayList()
        }
    }
}