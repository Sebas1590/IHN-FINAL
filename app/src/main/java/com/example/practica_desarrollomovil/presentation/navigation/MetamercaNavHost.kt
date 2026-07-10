package com.example.practica_desarrollomovil.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practica_desarrollomovil.di.AppContainer
import com.example.practica_desarrollomovil.presentation.accessibility.AccessibilityScreen
import com.example.practica_desarrollomovil.presentation.accessibility.AccessibilityViewModel
import com.example.practica_desarrollomovil.presentation.components.MetamercaBottomBar
import com.example.practica_desarrollomovil.presentation.earnings.EarningsScreen
import com.example.practica_desarrollomovil.presentation.earnings.EarningsViewModel
import com.example.practica_desarrollomovil.presentation.home.HomeScreen
import com.example.practica_desarrollomovil.presentation.home.HomeViewModel
import com.example.practica_desarrollomovil.presentation.login.LoginScreen
import com.example.practica_desarrollomovil.presentation.login.LoginViewModel
import com.example.practica_desarrollomovil.presentation.login.RegisterScreen
import com.example.practica_desarrollomovil.presentation.products.ProductFormScreen
import com.example.practica_desarrollomovil.presentation.products.ProductFormViewModel
import com.example.practica_desarrollomovil.presentation.products.ProductsScreen
import com.example.practica_desarrollomovil.presentation.products.ProductsViewModel
import com.example.practica_desarrollomovil.presentation.sales.RegisterSaleScreen
import com.example.practica_desarrollomovil.presentation.sales.RegisterSaleViewModel
import com.example.practica_desarrollomovil.presentation.sales.SalesScreen
import com.example.practica_desarrollomovil.presentation.theme.CreamBackground

private val bottomBarRoutes = setOf(
    Routes.HOME,
    Routes.PRODUCTS,
    Routes.SALES,
    Routes.EARNINGS,
    Routes.ACCESSIBILITY
)

@Composable
fun MetamercaNavHost(container: AppContainer) {
    val loginViewModel = viewModel<LoginViewModel>(factory = container.loginViewModelFactory())
    val isGuestActive by loginViewModel.isGuestSessionActive.collectAsStateWithLifecycle()

    var showRegister by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        if (isGuestActive) {
            MainScaffold(container = container)
        } else if (showRegister) {
            RegisterScreen(
                onBack = { showRegister = false }
            )
        } else {
            LoginScreen(
                onLoginSuccess = {
                    loginViewModel.continueWithoutSession { }
                },
                onContinueWithoutSession = {
                    loginViewModel.continueWithoutSession { }
                },
                onGoToRegister = { showRegister = true }
            )
        }
    }
}

@Composable
private fun MainScaffold(container: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route?.substringBefore("/") ?: Routes.HOME

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = CreamBackground,
        bottomBar = {
            if (showBottomBar) {
                MetamercaBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                val vm = viewModel<HomeViewModel>(factory = container.homeViewModelFactory())
                HomeScreen(
                    viewModel = vm,
                    onRegisterSale = { navController.navigate(Routes.REGISTER_SALE) },
                    onAddProduct = { navController.navigate(Routes.PRODUCT_ADD) },
                    onNavigateToEarnings = {
                        navController.navigate(Routes.EARNINGS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Routes.PRODUCTS) {
                val vm = viewModel<ProductsViewModel>(factory = container.productsViewModelFactory())
                ProductsScreen(
                    viewModel = vm,
                    onAddProduct = { navController.navigate(Routes.PRODUCT_ADD) },
                    onEditProduct = { id -> navController.navigate(Routes.productEdit(id)) }
                )
            }
            composable(Routes.PRODUCT_ADD) {
                val vm = viewModel<ProductFormViewModel>(factory = container.productFormViewModelFactory(null))
                ProductFormScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.PRODUCT_EDIT,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { entry ->
                val productId = entry.arguments?.getLong("productId")
                val vm = viewModel<ProductFormViewModel>(factory = container.productFormViewModelFactory(productId))
                ProductFormScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SALES) {
                SalesScreen(
                    container = container,
                    onRegisterSale = { navController.navigate(Routes.REGISTER_SALE) },
                    onEditReceipt = { id -> navController.navigate(Routes.receiptEdit(id)) }
                )
            }
            composable(Routes.REGISTER_SALE) {
                val vm = viewModel<RegisterSaleViewModel>(factory = container.registerSaleViewModelFactory(null))
                RegisterSaleScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.RECEIPT_EDIT,
                arguments = listOf(navArgument("receiptId") { type = NavType.LongType })
            ) { entry ->
                val receiptId = entry.arguments?.getLong("receiptId")
                val vm = viewModel<RegisterSaleViewModel>(factory = container.registerSaleViewModelFactory(receiptId))
                RegisterSaleScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.EARNINGS) {
                val vm = viewModel<EarningsViewModel>(factory = container.earningsViewModelFactory())
                EarningsScreen(viewModel = vm)
            }
            composable(Routes.ACCESSIBILITY) {
                val vm = viewModel<AccessibilityViewModel>(factory = container.accessibilityViewModelFactory())
                AccessibilityScreen(
                    viewModel = vm,
                    onLogout = { /* Session flow handles navigation via isGuestActive */ }
                )
            }
        }
    }
}
