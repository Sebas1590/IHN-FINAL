package com.example.practica_desarrollomovil.presentation.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.Sale
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

enum class EarningsTab { SEMANA, MES, FECHAS }

data class BarData(
    val label: String,
    val profit: Double,
    val investment: Double
)

data class EarningsUiState(
    val selectedTab: EarningsTab = EarningsTab.SEMANA,
    val dateFromMillis: Long? = null,
    val dateToMillis: Long? = null,
    val chartData: List<BarData> = emptyList(),
    val totalSales: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val totalProfit: Double = 0.0
)

class EarningsViewModel(
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(EarningsTab.SEMANA)
    private val _customDateFrom = MutableStateFlow<Long?>(DateTimeUtils.startOfDayMillis())
    private val _customDateTo = MutableStateFlow<Long?>(DateTimeUtils.startOfDayMillis())

    val uiState: StateFlow<EarningsUiState> = combine(
        saleRepository.observeAllSales(),
        _selectedTab,
        _customDateFrom,
        _customDateTo
    ) { sales, tab, customFrom, customTo ->
        val (start, end) = when (tab) {
            EarningsTab.SEMANA -> {
                DateTimeUtils.startOfWeekMillis() to System.currentTimeMillis()
            }
            EarningsTab.MES -> {
                DateTimeUtils.startOfMonthMillis() to System.currentTimeMillis()
            }
            EarningsTab.FECHAS -> {
                val s = customFrom ?: 0L
                val e = DateTimeUtils.endOfDayMillisFor(customTo ?: System.currentTimeMillis())
                s to e
            }
        }

        val filteredSales = sales.filter { it.soldAtMillis in start..end }
        
        val totalSales = filteredSales.sumOf { it.totalAmount }
        val totalProfit = filteredSales.sumOf { it.profitAmount }
        val totalInvestment = totalSales - totalProfit

        val chartData = when (tab) {
            EarningsTab.SEMANA -> calculateWeeklyChartData(filteredSales, start)
            EarningsTab.MES -> calculateMonthlyChartData(filteredSales)
            EarningsTab.FECHAS -> calculateCustomChartData(filteredSales, start, end)
        }

        EarningsUiState(
            selectedTab = tab,
            dateFromMillis = customFrom,
            dateToMillis = customTo,
            chartData = chartData,
            totalSales = totalSales,
            totalInvestment = totalInvestment,
            totalProfit = totalProfit
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EarningsUiState())

    fun onTabSelected(tab: EarningsTab) {
        _selectedTab.value = tab
    }

    fun onDateRangeSelected(from: Long?, to: Long?) {
        _customDateFrom.value = from
        _customDateTo.value = to
    }

    private fun calculateWeeklyChartData(sales: List<Sale>, weekStart: Long): List<BarData> {
        val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        val calendar = Calendar.getInstance()
        
        return days.mapIndexed { index, label ->
            calendar.timeInMillis = weekStart
            calendar.add(Calendar.DAY_OF_YEAR, index)
            val dayStart = DateTimeUtils.startOfDayMillisFor(calendar.timeInMillis)
            val dayEnd = DateTimeUtils.endOfDayMillisFor(calendar.timeInMillis)
            
            val daySales = sales.filter { it.soldAtMillis in dayStart..dayEnd }
            val p = daySales.sumOf { it.profitAmount }
            val i = daySales.sumOf { it.totalAmount } - p
            
            BarData(label, p, i)
        }
    }

    private fun calculateMonthlyChartData(sales: List<Sale>): List<BarData> {
        val p = sales.sumOf { it.profitAmount }
        val i = sales.sumOf { it.totalAmount } - p
        val monthName = DateTimeUtils.formatMonthName(System.currentTimeMillis())
        return listOf(BarData(monthName, p, i))
    }

    private fun calculateCustomChartData(sales: List<Sale>, start: Long, end: Long): List<BarData> {
        val p = sales.sumOf { it.profitAmount }
        val i = sales.sumOf { it.totalAmount } - p
        return listOf(BarData("Rango", p, i))
    }

    class Factory(
        private val saleRepository: SaleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarningsViewModel(saleRepository) as T
        }
    }
}
