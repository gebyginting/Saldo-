package com.geby.saldo.data.local.item

import com.geby.saldo.R
import com.geby.saldo.data.model.Category
import com.geby.saldo.data.model.TransactionCategory
import com.geby.saldo.data.model.TransactionType

object DummyItems {
    val dummyCategoryItems = listOf(
        Category(name = "Food", iconResId = R.drawable.ic_food, type = TransactionType.EXPENSE, category = TransactionCategory.MAKANAN),
        Category(name = "Transportation", iconResId = R.drawable.ic_transport, type = TransactionType.EXPENSE, category = TransactionCategory.TRANSPORTASI),
        Category(name = "Education", iconResId = R.drawable.ic_education, type = TransactionType.EXPENSE, category = TransactionCategory.PENDIDIKAN),
        Category(name = "Health", iconResId = R.drawable.ic_medical, type = TransactionType.EXPENSE, category = TransactionCategory.KESEHATAN),
        Category(name = "Other Expense", iconResId = R.drawable.ic_other_expense, type = TransactionType.EXPENSE, category = TransactionCategory.OTHER_EXPENSE, isOtherCategory = true),
        Category(name = "Work", iconResId = R.drawable.ic_work, type = TransactionType.INCOME, category = TransactionCategory.KERJA),
        Category(name = "Gift", iconResId = R.drawable.ic_gift, type = TransactionType.INCOME, category = TransactionCategory.HADIAH),
        Category(name = "Other Income", iconResId = R.drawable.ic_other_income, type = TransactionType.INCOME, category = TransactionCategory.OTHER_INCOME, isOtherCategory = true),
        )

    val dummyOptionCategoryItems = listOf(
        Category(name = "Housing", iconResId = R.drawable.ic_housing, type = TransactionType.EXPENSE),
        Category(name = "Utilities", iconResId = R.drawable.ic_electrical_services, type = TransactionType.EXPENSE),
        Category(name = "Debt Repayment", iconResId = R.drawable.ic_credit_card, type = TransactionType.EXPENSE),
        Category(name = "Entertainment", iconResId = R.drawable.ic_entertainment, type = TransactionType.EXPENSE),
        Category(name = "Donation", iconResId = R.drawable.ic_donation, type = TransactionType.EXPENSE),
        Category(name = "Body Care", iconResId = R.drawable.ic_body_care, type = TransactionType.EXPENSE),
        Category(name = "Laundry", iconResId = R.drawable.ic_laundry, type = TransactionType.EXPENSE),
        Category(name = "Parking", iconResId = R.drawable.ic_parking, type = TransactionType.EXPENSE),
        Category(name = "Pet Care", iconResId = R.drawable.ic_pet_care, type = TransactionType.EXPENSE),
        Category(name = "Vocation", iconResId = R.drawable.ic_vocation, type = TransactionType.EXPENSE),
        Category(name = "Allowance", iconResId = R.drawable.ic_allowance, type = TransactionType.INCOME),
        Category(name = "Freelance", iconResId = R.drawable.ic_freelance, type = TransactionType.INCOME),
        Category(name = "Royalties", iconResId = R.drawable.ic_royalties, type = TransactionType.INCOME),
        Category(name = "Scholarship", iconResId = R.drawable.ic_scholarship, type = TransactionType.INCOME),
        Category(name = "Lottery", iconResId = R.drawable.ic_lottery, type = TransactionType.INCOME),
        )
}