package it.polito.mad.sportcamp.profileScreens



import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import it.polito.mad.sportcamp.common.BitmapConverter
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import it.polito.mad.sportcamp.common.SaveMessage
import it.polito.mad.sportcamp.common.ValidationMessage
import it.polito.mad.sportcamp.ui.theme.*


var usrName: String = ""
var usrNickname: String = ""
var usrCity: String = ""
var usrAge: String = ""
var usrGender: String = ""
var usrLevel: String = ""
var usrSports: String = ""
var usrBio: String = ""
var isEditedGender: Boolean = false
var isEditedLevel: Boolean = false
var isEditedSports :Boolean = false
var isEditedCity :Boolean = false




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(
    viewModel: AppViewModel = viewModel(factory = AppViewModel.factory),
    navController: NavController
) {

    val mContext = LocalContext.current
    val permission = android.Manifest.permission.CAMERA
   //=========================================================================================

    var userId = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    val user by viewModel.getUserById(userId.toInt()).observeAsState()

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    // True if the message about the edit feature is shown.
    var validationMessageShown by remember { mutableStateOf(false) }
    var saveMessageShown by remember { mutableStateOf(false) }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap1 = user?.image?.let { BitmapConverter.converterStringToBitmap(it) }

    var bitmap = remember {
        mutableStateOf(bitmap1)
    }

    var isEditedImage by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        isEditedImage = true
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap.value = it
        isEditedImage = true
    }
    val openCameraDialog = remember { mutableStateOf(false)  }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCameraDialog.value = true
        } else {

        }
    }

    fun checkAndRequestCameraPermission(
        context: Context,
        permission: String,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            openCameraDialog.value = true
        } else {
            // Request a permission
            launcher.launch(permission)
        }
    }

    clearAll()


    // Shows the validation message.
    suspend fun showEditMessage() {
        if (!validationMessageShown) {
            validationMessageShown = true
            delay(3000L)
            validationMessageShown = false
        }
    }

    // Shows the save message.
    suspend fun showSaveMessage() {
        if (!saveMessageShown) {
            saveMessageShown = true
            delay(3000L)
            saveMessageShown = false
        }
    }

    suspend fun refreshUser() {

    }

    val scrollState = rememberScrollState()
    var isEditedNickname by remember { mutableStateOf(false) }
    var isEditedName by remember { mutableStateOf(false) }
    var isEditedAge by remember { mutableStateOf(false) }
    var isEditedBio by remember { mutableStateOf(false) }


    //========================= Dialog on discard ===================================
    val openDialog = remember { mutableStateOf(false)  }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Text("Changes will be discarded permanently. Are you sure to discard all? ")
            },
            confirmButton = {
                Button(
                    onClick = {
                        usrName = user?.name.toString()
                        usrNickname = user?.nickname.toString()
                        usrCity = user?.city.toString()
                        usrAge = user?.age.toString()
                        usrGender = user?.city.toString()
                        usrLevel = user?.level.toString()
                        usrSports = user?.sports.toString()
                        usrBio = user?.bio.toString()
                        openDialog.value = false
                    }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Don't discard")
                }
            }
        )
    }

    //========================= Dialog Camera or gallery ===================================


    if (openCameraDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openCameraDialog.value = false
            },
            text = {
                Text("Choose an option to update your profile image:")
            },
            confirmButton = {
                Button(
                    onClick = {
                        cameraLauncher.launch()
                        openCameraDialog.value = false
                    }) {
                    Text("Camera")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        openCameraDialog.value = false
                    }) {
                    Text("Gallery")
                }
            }
        )
    }

    // ==================================  EDIT SCREEN ==============================================
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

                        if (bitmap1 != null) {
                            Image(
                                painter = BitmapPainter(bitmap1.asImageBitmap()),
                                contentDescription = "Profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    // Clip image to be shaped as a circle
                                    .clip(CircleShape)
                                    .size(200.dp)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colors.secondary,
                                        CircleShape
                                    )
                            )
                        }
                        bitmap.value?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colors.secondary,
                                        CircleShape
                                    )
                            )
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
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colors.secondary,
                                            CircleShape
                                        )
                                )
                            }
                        }

                        IconButton(
                            onClick = { /* Azione da eseguire quando il bottone viene cliccato */ },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    translationX = 250f,
                                    translationY = 250f
                                )
                                .border(
                                    2.dp,
                                    MaterialTheme.colors.secondary,
                                    CircleShape
                                )
                                .background(
                                    color = MaterialTheme.colors.secondary,
                                    shape = CircleShape
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Camera",
                                modifier = Modifier.clickable {
                                    checkAndRequestCameraPermission(mContext, permission, launcher)
                                },
                                tint = MaterialTheme.colors.background
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

                    user?.city?.let { dropDownMenu(it, "City") }

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

                    user?.gender?.let { dropDownMenu(it, "Gender") }
                    user?.level?.let { dropDownMenu(it, "Level") }
                    user?.sports?.let { dropDownMenu(it, "Sports") }

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
                    SaveMessage(saveMessageShown)
                    Row (
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier= Modifier.fillMaxWidth()
                    ){
                        Column() {
                            Button(onClick = {
                                openDialog.value = true
                            }) {
                                Text(
                                    text = "Discard",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Column() {
                            Button(onClick = {
                                if ( isEditedAge || isEditedBio || isEditedCity || isEditedGender || isEditedLevel
                                    || isEditedName || isEditedSports || isEditedNickname || isEditedImage) {
                                    val user = User(
                                        id_user =  userId.trim().toInt(),
                                        nickname = if (isEditedNickname) usrNickname else user?.nickname,
                                        name = if (isEditedName) usrName else user?.name,
                                        mail =  user?.mail,
                                        city = if (isEditedCity) usrCity else user?.city,
                                        age = if (isEditedAge) usrAge.toInt() else user?.age,
                                        gender = if (isEditedGender) usrGender else user?.gender,
                                        level = if (isEditedLevel) usrLevel else user?.level,
                                        sports = if(isEditedSports) usrSports else user?.sports,
                                        bio = if (isEditedBio) usrBio else user?.bio,
                                        image = if(bitmap.value != null) bitmap.value?.let {
                                            BitmapConverter.converterBitmapToString(
                                                it
                                            )
                                        } else user?.image)
                                    updateUserInDB(user, viewModel)
                                    coroutineScope.launch {
                                        showSaveMessage()
                                    }
                                    clearAll()
                                } else {
                                    //toast(mContext, "Please add or update something...")
                                    coroutineScope.launch {
                                        showEditMessage()
                                    }
                                }
                            }) {
                                Text(
                                    text = "Save",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    )
}

fun clearAll() {
    usrName = ""
    usrNickname = ""
    usrCity = ""
    usrAge = ""
    usrGender = ""
    usrLevel = ""
    usrSports = ""
    usrBio = ""
}

fun updateUserInDB(
    user: User,
    viewModel: AppViewModel
) {
    viewModel.updateUser(user.nickname!!, user.name!!,user.mail!!,user.city!!,
        user.age!!,user.gender!!,user.level!!,user.sports!!,user.bio!!,user.id_user!!, user.image!! )
}

@Composable
fun CustomToolbarWithBackArrow(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(text = title, fontFamily = fonts) },
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun dropDownMenu(userOption: String, type: String) {

    var isExpanded by remember { mutableStateOf(false) }
    var userInitialValue by remember { mutableStateOf(userOption) } //male
    var suggestions = emptyList<String>()

    if (type == "Gender")
        suggestions = listOf("Male","Female","Other")
    if (type == "Level")
        suggestions = listOf ("Beginner", "Intermediate", "Advanced")
    if (type == "Sports")
        suggestions = listOf("Tennis", "Basketball")
    if (type == "City")
        suggestions = listOf("Torino", "Milano", "Roma", "Venezia", "Verona", "Padova")

    val icon = if (isExpanded)
        Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown

    Box {

        OutlinedTextField(
            value = userInitialValue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp),
            onValueChange = { userInitialValue = it },
            readOnly = true,
            label = { type },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { isExpanded = !isExpanded })
            }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .padding(all = 10.dp)
                // .border(BorderStroke(1.dp, Color.Gray))
                .fillMaxWidth(),
            // .width(with(LocalDensity.current){textfieldSize.width.toDp()}),
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    userInitialValue = label
                    isExpanded = false
                    if(type == "Gender") {
                        isEditedGender = true
                        usrGender = label
                    }
                    if(type == "Level") {
                        isEditedLevel = true
                        usrLevel = label
                    }
                    if(type == "Sports") {
                        isEditedSports = true
                        usrSports = label
                    }
                    if(type == "City") {
                        isEditedCity = true
                        usrCity = label
                    }
                }) {
                    Text(text = label)
                }
            }
        }
        /*
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {
                    TextField(
                        value = gender,
                        onValueChange = {},
                        label = { Text("Gender") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        // modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = { gender = "Male"; isExpanded = false },
                            content = { Text(text = "Male") })
                        DropdownMenuItem(
                            onClick = { gender = "Female" },
                            content = { Text(text = "Female") })
                        DropdownMenuItem(
                            onClick = { gender = "Other" },
                            content = { Text(text = "Other") })

                    }
                }*/
    }


}







