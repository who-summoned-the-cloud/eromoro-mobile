package com.who_summoned_the_cloud.eromoro.app.feature.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.util.createImageUri
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.app.util.uriToFile
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.data.model.SignUpRequest
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.modal.PictureSelectModalPopup
import com.who_summoned_the_cloud.eromoro.presentation.model.SignUpScreenField
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.SignUpFormScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SignUpSuccessScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addSignUpRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    navigation(
        route = "/sign-up",
        startDestination = "/sign-up/form",
    ) {
        composable(
            route = "/sign-up/form"
        ) {
            val context = LocalContext.current
            val viewModel = hiltViewModel<SignUpViewModel>()

            val nickname = rememberTextFieldState()
            val id = rememberTextFieldState()
            val password = rememberTextFieldState()
            val passwordCheck = rememberTextFieldState()
            var userType: UserType? by remember { mutableStateOf(null) }

            val passwordCheckValidation by snapshotFlow {
                if (passwordCheck.text.isEmpty()) null
                else if (password.text == passwordCheck.text) SignUpScreenField.Validation.PASS
                else SignUpScreenField.Validation.ERROR
            }.collectAsState(null)

            val isSignUpButtonEnabled by snapshotFlow {
                listOf(
                    nickname.text.isNotEmpty(),
                    id.text.isNotEmpty(),
                    password.text.isNotEmpty(),
                    passwordCheckValidation == SignUpScreenField.Validation.PASS,
                    userType != null,
                ).all { it }
            }.collectAsState(false)

            var tempImage: Uri? by remember { mutableStateOf(null) }
            var image: Uri? by remember { mutableStateOf(null) }

            var showPictureSelectPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            val camera = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
            ) {
                if (it) image = tempImage
            }

            val photoPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                image = uri
            }

            SignUpFormScreen(
                profileImage = image,
                nickname = SignUpScreenField(
                    state = nickname,
                    underText = null,  // TODO
                    validation = null,  // TODO
                ),
                id = SignUpScreenField(
                    state = id,
                    underText = null,  // TODO
                    validation = null,  // TODO
                ),
                password = SignUpScreenField(
                    state = password,
                    underText = null,  // TODO
                    validation = null,  // TODO
                ),
                passwordCheck = SignUpScreenField(
                    state = passwordCheck,
                    underText = null,  // TODO
                    validation = passwordCheckValidation,
                ),
                userType = userType,
                isSignUpButtonEnabled = isSignUpButtonEnabled,
                onBackButtonClicked = { navController.popBackStack() },
                onProfilePictureClicked = { showPictureSelectPopup = true },
                onUserTypeButtonClicked = { userType = it },
                onSignUpButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching {
                            val id = id.text.toString()
                            val nickname = nickname.text.toString()
                            val password = password.text.toString()
                            val userType = userType ?: return@launch

                            val isIdAvailable = checkIdAvailable(id = id)
                            if (!isIdAvailable) {
                                showToast("이미 존재하는 아이디입니다.", ToastType.ERROR)
                                return@runCatching
                            }

                            val isSucceed = signUp(
                                request = SignUpRequest(
                                    id = id,
                                    nickname = nickname,
                                    password = password,
                                    userType = userType,
                                    profileImage = image?.let { uriToFile(context, it) },
                                ),
                            )

                            if (isSucceed) {
                                MainScope().launch {
                                    navController.navigate(route = "/sign-up/success/${nickname}") {
                                        popUpTo("/sign-up/form") { inclusive = true }
                                    }
                                }
                            } else {
                                showToast("회원가입에 실패하였습니다.", ToastType.ERROR)
                            }
                        }

                        showLoading = false
                    }
                },
            )

            if (showPictureSelectPopup) {
                PictureSelectModalPopup(
                    onDismissRequest = { showPictureSelectPopup = false },
                    onCameraButtonClicked = {
                        val uri = createImageUri(context)
                        tempImage = uri
                        camera.launch(uri)
                        showPictureSelectPopup = false
                    },
                    onGalleryButtonClicked = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        showPictureSelectPopup = false
                    },
                )
            }

            if (showLoading) LoadingModal()
        }

        composable(
            route = "/sign-up/success/{nickname}",
            arguments = listOf(navArgument("nickname") { type = NavType.StringType }),
        ) { backStackEntry ->
            val nickname = backStackEntry.arguments?.getString("nickname") ?: "회원"

            SignUpSuccessScreen(
                nickname = nickname,
                onBackButtonClicked = { navController.popBackStack() },
                onStartButtonClicked = { navController.popBackStack() },
            )
        }
    }
}