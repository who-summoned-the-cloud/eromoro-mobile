package com.who_summoned_the_cloud.eromoro.app.feature.report

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.data.model.ListableReport
import com.who_summoned_the_cloud.eromoro.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
): ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    val reports: MutableStateFlow<List<ListableReport>?> = MutableStateFlow(null)
    val isReportsFetchedAll: MutableStateFlow<Boolean> = MutableStateFlow(false)

    suspend fun loadReports() {
        val currentReports = reports.value ?: emptyList()
        val page = currentReports.size / PAGE_SIZE
        val newReports = reportRepository.getMyReportList(page = page, size = PAGE_SIZE)

        reports.value = currentReports + newReports
        if (newReports.isEmpty()) {
            isReportsFetchedAll.value = true
        }
    }
}