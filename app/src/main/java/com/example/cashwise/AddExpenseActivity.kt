package com.example.cashwise

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import com.example.cashwise.R
import com.example.cashwise.Expense

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize views
        amountEditText = findViewById(R.id.amountEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        val saveExpenseButton = findViewById<Button>(R.id.saveExpenseButton)

        // Setup category spinner
        val categories = arrayOf("Food", "Transport", "Shopping", "Entertainment",
            "Bills", "Healthcare", "Other")
        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Save button click
        saveExpenseButton.setOnClickListener { saveExpense() }
    }

    private fun saveExpense() {
        val amountStr = amountEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()

        // Validation
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val amount = amountStr.toDouble()
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
                return
            }

            // Get current date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = sdf.format(Date())

            // Create expense
            val newExpense = Expense(amount, description, category, currentDate)

            // Load existing expenses
            val expensesList = loadExpenses()
            expensesList.add(newExpense)

            // Save expenses
            saveExpenses(expensesList)

            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish() // Close activity and return to main

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadExpenses(): ArrayList<Expense> {
        val sharedPreferences = getSharedPreferences("CashWise", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("expenses", null)
        val type = object : TypeToken<ArrayList<Expense>>() {}.type

        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            ArrayList()
        }
    }

    private fun saveExpenses(expensesList: ArrayList<Expense>) {
        val sharedPreferences = getSharedPreferences("CashWise", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(expensesList)
        editor.putString("expenses", json)
        editor.apply()
    }
}