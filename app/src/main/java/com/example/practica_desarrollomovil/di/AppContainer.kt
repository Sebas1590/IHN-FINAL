package com.example.practica_desarrollomovil.di

import android.content.Context
import androidx.room.Room
import com.example.practica_desarrollomovil.data.local.MetamercaDatabase
import com.example.practica_desarrollomovil.data.local.preferences.AccessibilityPreferences
import com.example.practica_desarrollomovil.data.local.preferences.SessionPreferences
import com.example.practica_desarrollomovil.data.repository.ProductRepositoryImpl
import com.example.practica_desarrollomovil.data.repository.SaleRepositoryImpl
import com.example.practica_desarrollomovil.data.repository.SessionRepositoryImpl
import com.example.practica_desarrollomovil.data.repository.UserRepositoryImpl
import com.example.practica_desarrollomovil.domain.repository.ProductRepository
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.domain.repository.SessionRepository
import com.example.practica_desarrollomovil.domain.repository.UserRepository
import com.example.practica_desarrollomovil.presentation.accessibility.AccessibilityViewModel
import com.example.practica_desarrollomovil.presentation.login.LoginViewModel
import com.example.practica_desarrollomovil.presentation.login.RegisterViewModel
import com.example.practica_desarrollomovil.presentation.home.HomeViewModel
import com.example.practica_desarrollomovil.presentation.products.ProductFormViewModel
import com.example.practica_desarrollomovil.presentation.products.ProductsViewModel
import com.example.practica_desarrollomovil.presentation.earnings.EarningsViewModel
import com.example.practica_desarrollomovil.presentation.sales.RegisterSaleViewModel
import com.example.practica_desarrollomovil.presentation.sales.SalesListViewModel

class AppContainer(context: Context) {

    private val database: MetamercaDatabase = Room.databaseBuilder(
        context,
        MetamercaDatabase::class.java,
        "metamerca.db"
    )
        // Al ser una app de demostración, si cambia el esquema se recrea la base de datos
        // en lugar de exigir una migración manual.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    private val sessionPreferences = SessionPreferences(context)
    val accessibilityPreferences = AccessibilityPreferences(context)

    val sessionRepository: SessionRepository =
        SessionRepositoryImpl(sessionPreferences)

    val productRepository: ProductRepository =
        ProductRepositoryImpl(database.productDao())

    val saleRepository: SaleRepository =
        SaleRepositoryImpl(database.saleDao(), database.productDao())

    val userRepository: UserRepository =
        UserRepositoryImpl(database.userDao())

    fun loginViewModelFactory() = LoginViewModel.Factory(sessionRepository, userRepository)

    fun registerViewModelFactory() = RegisterViewModel.Factory(userRepository)

    fun homeViewModelFactory() = HomeViewModel.Factory(saleRepository = saleRepository)

    fun productsViewModelFactory() = ProductsViewModel.Factory(productRepository)

    fun productFormViewModelFactory(productId: Long?) = ProductFormViewModel.Factory(
        productRepository = productRepository,
        saleRepository = saleRepository,
        productId = productId
    )

    fun earningsViewModelFactory() = EarningsViewModel.Factory(saleRepository)

    fun registerSaleViewModelFactory(receiptId: Long? = null) = RegisterSaleViewModel.Factory(
        productRepository = productRepository,
        saleRepository = saleRepository,
        receiptId = receiptId
    )

    fun salesListViewModelFactory() = SalesListViewModel.Factory(saleRepository)

    fun accessibilityViewModelFactory() = AccessibilityViewModel.Factory(
        accessibilityPreferences = accessibilityPreferences,
        sessionRepository = sessionRepository
    )
}
