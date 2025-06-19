package com.example.cashwise

data class Expense(
    val amount: Double,
    val description: String,
    val category: String,
    val date: String
) {
    override fun toString(): String {
        return "$description - ${String.format("%.2f", amount)} ($category) $date"
    }
}