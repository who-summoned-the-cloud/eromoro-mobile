package com.who_summoned_the_cloud.eromoro.app.feature.report

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.app.util.uriToFile
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.ListableReport
import com.who_summoned_the_cloud.eromoro.data.model.Report
import com.who_summoned_the_cloud.eromoro.data.model.ReportRequest
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val reportRepository: ReportRepository,
    private val geolocationRepository: GeolocationRepository,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    val myReportList: MutableStateFlow<List<List<ListableReport>>?> = MutableStateFlow(null)
    val isReportsFetchedAll: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val reportingImage: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val title = TextFieldState()
    val content = TextFieldState()
    val obstacleType: MutableStateFlow<ObstacleType?> = MutableStateFlow(null)
    val currentPosition: MutableStateFlow<Position?> = MutableStateFlow(null)
    val address: MutableStateFlow<String?> = MutableStateFlow(null)
    val isForLocalGovernance: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val report = MutableStateFlow<Report?>(null)

    suspend fun loadMyReports() {
        val currentReports = myReportList.value ?: emptyList()
        val newReports = reportRepository.getMyReportList(
            page = currentReports.size,
            size = PAGE_SIZE
        )

        myReportList.value = currentReports.plus<List<ListableReport>>(newReports)
        if (newReports.size < PAGE_SIZE) {
            isReportsFetchedAll.value = true
        }
    }

    suspend fun createReport() {
        val image = reportingImage.value?.let { uriToFile(context, it) }
        val position = currentPosition.value
        val type = obstacleType.value
        val address = address.value

        if (image == null || position == null || type == null || address == null) {
            throw IllegalStateException("image or position is null")
        }

        reportRepository.report(
            request = ReportRequest(
                image = image,
                position = position,
                title = title.text.toString(),
                content = content.text.toString(),
                type = type,
                address = address,
                isForLocalGovernance = isForLocalGovernance.value
            )
        )
    }

    suspend fun loadReport(reportId: Long) {
        report.value = null
        report.value = reportRepository.getReport(reportId = reportId)
    }

    suspend fun modifyReportLikeFromList(reportId: Long) {
        val report = myReportList.value
            ?.flatten()
            ?.find { it.id == reportId } ?: return

        val likeCount =
            reportRepository.modifyReportLike(reportId = reportId, isLike = !report.isLiked)

        myReportList.value = myReportList.value?.map { reports ->
            reports.map {
                if (it.id == reportId) it.copy(
                    isLiked = !it.isLiked,
                    like = likeCount
                ) else it
            }
        }
    }

    suspend fun modifyCurrentReportLike() {
        val currentReport = report.value ?: return

        val likeCount = reportRepository.modifyReportLike(
            reportId = currentReport.id,
            isLike = !currentReport.isLiked
        )

        report.value = currentReport.copy(isLiked = !currentReport.isLiked, like = likeCount)
    }

    suspend fun getAddress(position: Position): String {
        return geolocationRepository.getAddressFromPosition(position = position)
    }

    suspend fun deleteReport() {
        report.value?.let { reportRepository.deleteReport(it.id) }
    }
}