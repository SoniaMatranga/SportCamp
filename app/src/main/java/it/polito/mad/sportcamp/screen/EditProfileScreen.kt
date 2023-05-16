package it.polito.mad.sportcamp.screen


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import it.polito.mad.sportcamp.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import it.polito.mad.sportcamp.bottomnav.DETAIL_ARGUMENT_KEY
import it.polito.mad.sportcamp.database.AppViewModel
import it.polito.mad.sportcamp.database.User
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch


var usrName: String = ""
var usrNickname: String = ""
var usrMail: String = ""
var usrCity: String = ""
var usrAge: String = ""
var usrGender: String = ""
var usrLevel: String = ""
var usrSports: String = ""
var usrBio: String = ""




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory),
    navController: NavController
) {
    var isEdit:Boolean = false
    var userId = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    val user by viewModel.getUserById(userId.toInt()).observeAsState()


    lateinit var selectedUser: User
    val mContext = LocalContext.current
    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    // True if the message about the edit feature is shown.
    var validationMessageShown by remember { mutableStateOf(false) }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    clearAll()

    /*if (isEdit) {
        viewModel.getUserById(1)
        selectedUser = viewModel.getUserById(1).observeAsState().value!!
        usrName = selectedUser.name.toString()
        usrNickname = selectedUser.nickname.toString()
        usrMail = selectedUser.mail.toString()
        usrCity = selectedUser.city.toString()
        usrAge = selectedUser.age.toString()
        usrGender = selectedUser.gender.toString()
        usrLevel = selectedUser.level.toString()
        usrSports = selectedUser.sports.toString()
        usrBio = selectedUser.bio.toString()
    }*/

    // Shows the validation message.
    suspend fun showEditMessage() {
        if (!validationMessageShown) {
            validationMessageShown = true
            delay(3000L)
            validationMessageShown = false
        }
    }

    val scrollState = rememberScrollState()
    var isEdited by remember { mutableStateOf(false) }
    var isEditedNickname by remember { mutableStateOf(false) }
    var isEditedName by remember { mutableStateOf(false) }
    var isEditedMail by remember { mutableStateOf(false) }
    var isEditedCity by remember { mutableStateOf(false) }
    var isEditedAge by remember { mutableStateOf(false) }
    var isEditedGender by remember { mutableStateOf(false) }
    var isEditedLevel by remember { mutableStateOf(false) }
    var isEditedSports by remember { mutableStateOf(false) }
    var isEditedBio by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CustomToolbarWithBackArrow(
                title = "Edit Profile" ,
                navController = navController as NavHostController
            )
        },
        content = {  _ ->
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .verticalScroll(state = scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    // User's image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.user_image1),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                // Clip image to be shaped as a circle
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    MaterialTheme.colors.primary,
                                    CircleShape
                                )
                        )

                        IconButton(
                            onClick = { /* Azione da eseguire quando il bottone viene cliccato */ },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    translationX = 250f,
                                    translationY = 300f
                                )
                                .border(
                                    2.dp,
                                    MaterialTheme.colors.secondary,
                                    CircleShape
                                )
                                .background(
                                    color = MaterialTheme.colors.primary,
                                    shape = CircleShape
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colors.secondary
                            )
                        }
                    }
                    imageUri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap.value = MediaStore.Images
                                .Media.getBitmap(mContext.contentResolver, it)

                        } else {
                            val source = ImageDecoder
                                .createSource(mContext.contentResolver, it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }

                        bitmap.value?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                            )
                        }
                    }


                    user?.nickname?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Nickname,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 50,
                            maxLines = 1
                        ) {
                            isEditedNickname = true
                            usrNickname = it
                        }
                    }

                    user?.name?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Name,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 50,
                            maxLines = 1
                        ) {
                            isEditedName = true
                            usrName = it
                        }
                    }
                    user?.mail?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Mail,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 5,
                            maxLines = 1
                        ) {
                            isEditedMail = true
                            usrMail = it
                        }
                    }
                    user?.city?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.City,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 100,
                            maxLines = 1
                        ) {
                            isEditedCity = true
                            usrCity = it
                        }
                    }
                    user?.age?.toString().let {
                        if (it != null) {
                            CustomTextField(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .fillMaxWidth(),
                                labelResId = R.string.Age,
                                inputWrapper = it,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                maxLength = 3,
                                maxLines = 1
                            ) {
                                isEditedAge = true
                                usrAge = it
                            }
                        }
                    }
                    user?.gender?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Gender,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 100,
                            maxLines = 1
                        ) {
                            isEditedGender = true
                            usrGender = it
                        }
                    }
                    user?.level?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Level,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            maxLength = 10,
                            maxLines = 1
                        ) {
                            isEditedLevel = true
                            usrLevel = it
                        }
                    }
                    user?.sports?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Sports,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 100,
                            maxLines = 1
                        ) {
                            isEditedSports = true
                            usrSports = it
                        }
                    }
                    user?.bio?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Bio,
                            inputWrapper = it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 100,
                            maxLines = 1
                        ) {
                            isEditedBio = true
                            usrBio = it
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    ValidationMessage(validationMessageShown)
                    Button(onClick = {
                        if (isEdited || isEditedAge || isEditedBio || isEditedCity || isEditedGender || isEditedLevel || isEditedMail
                            || isEditedMail || isEditedName || isEditedNickname) {
                            val user = User(
                                id_user = if (isEdit) selectedUser.id_user else userId.trim().toInt(),
                                nickname = if (isEditedNickname) usrNickname else user?.nickname,
                                name = if (isEditedName) usrName else user?.name,
                                mail = if (isEditedMail) usrMail else user?.mail,
                                city = if (isEditedCity) usrCity else user?.city,
                                age = if (isEditedAge) usrAge.toInt() else user?.age,
                                gender = if (isEditedGender) usrGender else user?.gender,
                                level = if (isEditedLevel) usrLevel else user?.level,
                                sports = if(isEditedSports) usrSports else user?.sports,
                                bio = if (isEditedBio) usrBio else user?.bio
                            )

                                updateUserInDB(mContext,
                                    navController as NavHostController, user, viewModel)
                            clearAll()
                        } else {
//                            toast(mContext, "Please add or update something...")
                            coroutineScope.launch {
                                showEditMessage()
                            }
                        }
                    }) {
                        Text(
                            text = if (isEdit) "Update Details" else "Save",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    )
}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ValidationMessage(shown: Boolean) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = shown,
            enter = slideInVertically(
                // Enters by sliding in from offset -fullHeight to 0.
                initialOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
            ),
            exit = slideOutVertically(
                // Exits by sliding out from offset 0 to -fullHeight.
                targetOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colors.secondary,
                    elevation = 4.dp,

                    ) {
                    Text(
                        text = "Please update at least one field",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

        }
    }
}

fun clearAll() {
    usrName = ""
    usrNickname = ""
    usrMail = ""
    usrCity = ""
    usrAge = ""
    usrGender = ""
    usrLevel = ""
    usrSports = ""
    usrBio = ""
}

/*
fun addEmployeeInDB(
    context: Context,
    navController: NavHostController,
    employee: User,
    homeViewModel: HomeViewModel
) {
    homeViewModel.addEmployee(employee)
    navController.popBackStack()
}*/

fun updateUserInDB(
    context: Context,
    navController: NavHostController,
    user: User,
    viewModel: AppViewModel
) {
    viewModel.updateUser(user.nickname!!, user.name!!,user.mail!!,user.city!!,user.age!!,user.gender!!,user.level!!,user.sports!!,user.bio!!,user.id_user!! )
    //navController.popBackStack()
}

@Composable
fun CustomToolbarWithBackArrow(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.h6) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "arrowBack",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = remember { KeyboardOptions.Default },
    inputWrapper: String,
    @StringRes labelResId: Int,
    maxLength: Int,
    maxLines: Int,
    onTextChanged: (String) -> Unit
) {
    var fieldValue by remember { mutableStateOf(inputWrapper) }
    val focusManager = LocalFocusManager.current
    Column {
        OutlinedTextField(
            value = fieldValue,
            label = { Text(stringResource(labelResId), style = MaterialTheme.typography.caption) },
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            modifier = modifier,
            onValueChange = {
                if (it.length <= maxLength) {
                    fieldValue = it
                    //text = value.filter { it.isDigit() }
                    onTextChanged(it)
                }
            },
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDone = {
                    focusManager.clearFocus()
                }
            ),
        )

    }
}

