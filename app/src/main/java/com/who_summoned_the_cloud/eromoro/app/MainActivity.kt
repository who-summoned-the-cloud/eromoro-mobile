package com.who_summoned_the_cloud.eromoro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import com.who_summoned_the_cloud.eromoro.presentation.screen.PreviewMapCourseViewerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box {
                PreviewMapCourseViewerScreen()
            }
        }
    }
}
