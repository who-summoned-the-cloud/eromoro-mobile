package com.who_summoned_the_cloud.eromoro.presentation.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ReportCategory
import java.time.LocalDate

sealed class ReportListScreenTab {

    data class MyReports(
        val category: ReportCategory?,
        val sort: Sort,
        val reports: Fetch<List<Report>, Unit>,
        val showLoadingAtBottom: Boolean,
        val menuExpandedReportId: Long?,
        val onCategoryChipClicked: (ReportCategory?) -> Unit,
        val onNewPageRequest: () -> Unit,
    ) : ReportListScreenTab() {

        enum class Sort {
            NEWEST,
            DICTIONARY,
        }

        data class Report(
            val id: Long,
            val imageUri: Uri?,
            val category: ReportCategory,
            val state: State,
            val title: String,
            val address: String,
            val type: String,
            val date: LocalDate,
            val like: Int,
            val dislike: Int,
            val onMenuButtonClicked: () -> Unit,
            val onLikeButtonClicked: () -> Unit,
            val onDislikeButtonClicked: () -> Unit,
            val onClick: () -> Unit,
        ) {
            enum class State {
                BEFORE_APPROVAL,
                APPROVED,
                REJECTED,
            }
        }
    }

    data class Ranking(
        val data: Unit,
    ) : ReportListScreenTab()
}