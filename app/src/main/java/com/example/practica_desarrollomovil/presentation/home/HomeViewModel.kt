package com.example.practica_desarrollomovil.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practica_desarrollomovil.domain.model.DashboardSummary
import com.example.practica_desarrollomovil.domain.model.RecentActivity
import com.example.practica_desarrollomovil.domain.repository.SaleRepository
import com.example.practica_desarrollomovil.util.DateTimeUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val headerDate: String = DateTimeUtils.formatHeaderDate(),
    val summary: DashboardSummary = DashboardSummary(),
    val recentActivity: List<RecentActivity> = emptyList()
)

class HomeViewModel(
    saleRepository: SaleRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = kotlinx.coroutines.flow.combine(
        saleRepository.observeDashboardSummary(),
        saleRepository.observeRecentActivity(3)
    ) { summary, activity ->
        HomeUiState(
            summary = summary,
            recentActivity = activity
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState()
    )

    class Factory(
        private val saleRepository: SaleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(saleRepository) as T
        }
    }
}
