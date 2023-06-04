package it.polito.mad.sportcamp.profileScreens



import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import it.polito.mad.sportcamp.classes.User
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import it.polito.mad.sportcamp.common.BitmapConverter
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.common.SaveMessage
import it.polito.mad.sportcamp.common.ValidationMessage
import it.polito.mad.sportcamp.ui.theme.*
import it.polito.mad.sportcamp.common.CustomToolbarWithBackArrow


class EditProfileViewModel : ViewModel() {

    var usrName by mutableStateOf("")
    var usrNickname by mutableStateOf("")
    var usrCity by mutableStateOf("")
    var usrAge by mutableStateOf("")
    var usrGender by mutableStateOf("")
    var usrTennisLevel by mutableStateOf("")
    var usrFootballLevel by mutableStateOf("")
    var usrBasketLevel by mutableStateOf("")
    var usrVolleyLevel by mutableStateOf("")
    var usrSports by mutableStateOf("")
    var usrBio by mutableStateOf("")
    var usrImage by mutableStateOf("")
    var isEditedGender by mutableStateOf(false)
    var isEditedTennisLevel by mutableStateOf(false)
    var isEditedFootballLevel by mutableStateOf(false)
    var isEditedBasketLevel by mutableStateOf(false)
    var isEditedVolleyLevel by mutableStateOf(false)
    var isEditedSports by mutableStateOf(false)
    var isEditedCity by mutableStateOf(false)
    var isEditedNickname by  mutableStateOf(false)
    var isEditedName by  mutableStateOf(false)
    var isEditedAge by  mutableStateOf(false)
    var isEditedBio by  mutableStateOf(false)

    private val db = Firebase.firestore
    private val user = MutableLiveData<User>()
    private var fuser: FirebaseUser = Firebase.auth.currentUser!!


    fun getUserDocument() : MutableLiveData<User> {
        db
            .collection("users")
            .document(getUserUID())
            .addSnapshotListener { value, error ->
                if(error != null) Log.w(ContentValues.TAG, "Error getting documents.")
                if(value != null) user.value = value.toObject(User::class.java)
            }
        return user
    }

    private fun getUserUID(): String{
        return fuser.uid
    }


    init {
            val userLiveData = getUserDocument()
            userLiveData.observeForever { user ->
                usrName = user?.name ?: ""
                usrNickname = user?.nickname ?: ""
                usrAge = user?.age?.toString() ?: ""
                usrBio = user?.bio ?: ""
                usrCity = user?.city ?: ""
                usrTennisLevel = user?.tennis_level ?: ""
                usrFootballLevel = user?.football_level ?: ""
                usrBasketLevel = user?.basket_level ?: ""
                usrVolleyLevel = user?.volley_level ?: ""
                usrGender = user?.gender ?: ""
                usrSports = user?.sports ?: ""
                usrImage = user?.image ?: ""
            }

    }


    fun updateUser(
        nickname: String,
        name: String,
        mail: String,
        city: String,
        age: Int,
        gender: String,
        tennis_level: String,
        basket_level: String,
        football_level: String,
        volley_level: String,
        sports: String,
        bio: String,
        id_user: String,
        image: String
    ) {
        val userRef = db.collection("users").document(getUserUID())

        val updateData = hashMapOf(
            "nickname" to nickname,
            "name" to name,
            "mail" to mail,
            "city" to city,
            "age" to age,
            "gender" to gender,
            "tennis_level" to tennis_level,
            "football_level" to football_level,
            "volley_level" to volley_level,
            "basket_level" to basket_level,
            "sports" to sports,
            "bio" to bio,
            "id_user" to id_user,
            "image" to image
        )

        userRef
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("UpdateUser", "User data updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("UpdateUser", "Error updating user data.", e)
            }
    }
    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EditProfileViewModel()
            }
        }
    }
}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(
    vm: EditProfileViewModel = viewModel(factory = EditProfileViewModel.factory),
    navController: NavController
) {

    val mContext = LocalContext.current
    val permission = android.Manifest.permission.CAMERA

    //=========================================================================================

    val userId = navController.currentBackStackEntry?.arguments?.getInt(DETAIL_ARGUMENT_KEY).toString()
    val user by vm.getUserDocument().observeAsState()



    // The coroutine scope for event handlers calling suspend functions.
    //val coroutineScope = rememberCoroutineScope()
    // True if the message is shown.
    val validationMessageShown by remember { mutableStateOf(false) }
    val saveMessageShown by remember { mutableStateOf(false) }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap1 = user?.image?.let { BitmapConverter.converterStringToBitmap(it) }

    val bitmap = remember {
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




    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(false) }

    //========================= Dialog on save ===================================
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Text("Your data will be permanently changed. Are you sure to save all changes anyway? ")

            },
            confirmButton = {
                Button(
                    onClick = {
                        val usr = User(
                            id_user =  userId.trim(),
                            nickname = if (vm.isEditedNickname) vm.usrNickname else user?.nickname,
                            name = if (vm.isEditedName) vm.usrName else user?.name,
                            mail =  user?.mail,
                            city = if (vm.isEditedCity) vm.usrCity else user?.city,
                            age = if (vm.isEditedAge) vm.usrAge.toInt() else user?.age,
                            gender = if (vm.isEditedGender) vm.usrGender else user?.gender,
                            tennis_level = if (vm.isEditedTennisLevel) vm.usrTennisLevel else user?.tennis_level,
                            football_level =  if (vm.isEditedFootballLevel) vm.usrFootballLevel else user?.football_level,
                            basket_level = if (vm.isEditedBasketLevel) vm.usrBasketLevel else user?.basket_level,
                            volley_level =  if (vm.isEditedVolleyLevel) vm.usrVolleyLevel else user?.volley_level,
                            sports = if(vm.isEditedSports) vm.usrSports else user?.sports,
                            bio = if (vm.isEditedBio) vm.usrBio else user?.bio,
                            image = if(bitmap.value != null) bitmap.value?.let {
                                BitmapConverter.converterBitmapToString(
                                    it
                                )
                            } else user?.image)
                        updateUserInDB(usr, vm)
                        Toast.makeText(context, "Profile successfully updated!", Toast.LENGTH_SHORT).show()
                        openDialog.value = false
                        //navController.navigate(route = Screen.Reservations.route)

                    }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Cancel")
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
                            onClick = { },
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
                            inputWrapper = if(vm.usrNickname != "") vm.usrNickname else it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 50,
                            maxLines = 1,
                            onTextChanged = { newText ->
                                vm.usrNickname = newText
                                vm.isEditedNickname = true
                            }

                        )
                    }



                    user?.name?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Name,
                            inputWrapper = if(vm.usrName != "") vm.usrName else it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 50,
                            maxLines = 1,
                            onTextChanged = { newText ->
                                vm.usrName = newText
                                vm.isEditedName = true
                            }
                        )
                    }

                    user?.city?.let { DropDownMenu(if(vm.usrCity != "") vm.usrCity else it, "City") }

                    user?.age?.toString().let {
                        if (it != null) {
                            CustomTextField(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .fillMaxWidth(),
                                labelResId = R.string.Age,
                                inputWrapper = if(vm.usrAge != "") vm.usrAge else it,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                maxLength = 3,
                                maxLines = 1,
                                onTextChanged = { newText ->
                                    vm.usrAge = newText
                                    vm.isEditedAge = true
                                }
                            )
                        }
                    }

                    user?.gender?.let { DropDownMenu(if(vm.usrGender != "") vm.usrGender else it, "Gender") }
                    user?.sports?.let { DropDownMenuSports(if (vm.usrSports != "") vm.usrSports else it) }
                    user?.tennis_level?.let { DropDownMenu(if(vm.usrTennisLevel != "") vm.usrTennisLevel else it, "Tennis level") }
                    user?.football_level?.let { DropDownMenu(if(vm.usrFootballLevel != "") vm.usrFootballLevel else it, "Football level") }
                    user?.basket_level?.let { DropDownMenu(if(vm.usrBasketLevel != "") vm.usrBasketLevel else it, "Basketball level") }
                    user?.volley_level?.let { DropDownMenu(if(vm.usrVolleyLevel != "") vm.usrVolleyLevel else it, "Volleyball level") }


                    user?.bio?.let {
                        CustomTextField(
                            modifier = Modifier
                                .padding(all = 10.dp)
                                .fillMaxWidth(),
                            labelResId = R.string.Bio,
                            inputWrapper = if(vm.usrBio != "") vm.usrBio else it,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            maxLength = 100,
                            maxLines = 1,
                            onTextChanged = { newText ->
                                vm.usrBio = newText
                                vm.isEditedBio = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    ValidationMessage(validationMessageShown)
                    SaveMessage(saveMessageShown)
                    Row (
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier= Modifier.fillMaxWidth()
                    ){
                        Column {
                            Button(onClick = {
                                if ( vm.isEditedAge || vm.isEditedBio || vm.isEditedCity || vm.isEditedGender || vm.isEditedTennisLevel
                                    || vm.isEditedFootballLevel || vm.isEditedBasketLevel || vm.isEditedVolleyLevel ||
                                     vm.isEditedName || vm.isEditedSports || vm.isEditedNickname || isEditedImage) {
                                    //openDialog.value=true
                                    val usr = User(
                                        id_user =  userId.trim(),
                                        nickname = if (vm.isEditedNickname) vm.usrNickname else user?.nickname,
                                        name = if (vm.isEditedName) vm.usrName else user?.name,
                                        mail =  user?.mail,
                                        city = if (vm.isEditedCity) vm.usrCity else user?.city,
                                        age = if (vm.isEditedAge) vm.usrAge.toInt() else user?.age,
                                        gender = if (vm.isEditedGender) vm.usrGender else user?.gender,
                                        tennis_level = if (vm.isEditedTennisLevel) vm.usrTennisLevel else user?.tennis_level,
                                        football_level =  if (vm.isEditedFootballLevel) vm.usrFootballLevel else user?.football_level,
                                        basket_level = if (vm.isEditedBasketLevel) vm.usrBasketLevel else user?.basket_level,
                                        volley_level =  if (vm.isEditedVolleyLevel) vm.usrVolleyLevel else user?.volley_level,
                                        sports = if(vm.isEditedSports) vm.usrSports else user?.sports,
                                        bio = if (vm.isEditedBio) vm.usrBio else user?.bio,
                                        image = if(bitmap.value != null) bitmap.value?.let {
                                            BitmapConverter.converterBitmapToString(
                                                it
                                            )
                                        } else user?.image)
                                    updateUserInDB(usr, vm)
                                    Toast.makeText(context, "Profile successfully updated!", Toast.LENGTH_SHORT).show()
                                    //openDialog.value = false
                                } else {
                                    Toast.makeText(context, "Please add or update something...", Toast.LENGTH_SHORT).show()
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


fun updateUserInDB(
    user: User,
    viewModel: EditProfileViewModel
) {
    viewModel.updateUser(user.nickname!!, user.name!!,user.mail!!,user.city!!,
        user.age!!,user.gender!!,user.tennis_level!!, user.basket_level!!, user.football_level!!, user.volley_level!!,user.sports!!,user.bio!!,user.id_user!!, user.image!! )
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


@Composable
fun DropDownMenu(userOption: String, type: String) {
    val vm: EditProfileViewModel = viewModel()

    var isExpanded by remember { mutableStateOf(false) }
    var userInitialValue by remember { mutableStateOf(userOption) } //male
    var suggestions = emptyList<String>()

    if (type == "Gender")
        suggestions = listOf("Male","Female","Other")
    if (type == "Tennis level" || type == "Football level" || type == "Basketball level" || type == "Volleyball level")
        suggestions = listOf ("Beginner", "Intermediate", "Advanced")
    if (type == "Sports")
        suggestions = listOf("Basketball", "Football", "Tennis", "Volleyball" )
    if (type == "City")
        suggestions = listOf("Turin", "Milan", "Rome", "Venice", "Naples", "Padua","Genoa")

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
            label = { Text("$type") },
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
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    userInitialValue = label
                    isExpanded = false
                    if(type == "Gender") {
                        vm.isEditedGender = true
                        vm.usrGender = label
                    }
                    if(type == "Tennis level") {
                        vm.isEditedTennisLevel = true
                        vm.usrTennisLevel = label
                    }
                    if(type == "Football level") {
                        vm.isEditedFootballLevel = true
                        vm.usrFootballLevel = label
                    }
                    if(type == "Basketball level") {
                        vm.isEditedBasketLevel = true
                        vm.usrBasketLevel = label
                    }
                    if(type == "Volleyball level") {
                        vm.isEditedVolleyLevel = true
                        vm.usrVolleyLevel = label
                    }
                    if(type == "City") {
                        vm.isEditedCity = true
                        vm.usrCity = label
                    }
                }) {
                    Text(text = label)
                }
            }
        }
    }

}


@Composable
fun DropDownMenuSports(userOption: String) {

    val vm: EditProfileViewModel = viewModel()
    var isExpanded by remember { mutableStateOf(false) }
    var userInitialValue by remember { mutableStateOf(userOption) }
    val suggestions = listOf("Basketball", "Football", "Tennis", "Volleyball")

    val icon = if (isExpanded)
        Icons.Filled.ArrowDropUp
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
            label = { Text("Sports") },
            trailingIcon = {
                Icon(
                    icon, "contentDescription",
                    Modifier.clickable { isExpanded = !isExpanded }
                )
            }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false
                vm.isEditedSports = true
                vm.usrSports = userInitialValue},
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth(),
        ) {
            val selectedCheckboxes = remember { mutableStateListOf<String>() }

            suggestions.forEach { labelRow ->
                val isChecked = remember { mutableStateOf(false) }

                DropdownMenuItem(onClick = {
                    isChecked.value = !isChecked.value

                    if (isChecked.value) {
                        selectedCheckboxes.add(labelRow)
                    } else {
                        selectedCheckboxes.remove(labelRow)
                    }

                    userInitialValue = selectedCheckboxes.joinToString(", ")
                }) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked.value,
                            onCheckedChange = null, // Leave this null to handle the click in DropdownMenuItem
                            enabled = true,
                            colors = CheckboxDefaults.colors(Color.Blue)
                        )
                        Text(text = labelRow)
                    }
                }
            }
        }
    }
}

@Composable
fun TriStateToggleEdit(sport: String) {
    val states = listOf(
        "Beginner",
        "Intermediate",
        "Advanced",
    )
    var selectedOption by remember {
        mutableStateOf(states[1])
    }
    val onSelectionChange = { text: String ->
        selectedOption = text
    }

    Column( modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "$sport level" ,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(5.dp))
    }


    Surface(
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        modifier = Modifier
            .wrapContentSize()
    ) {

        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(Color.LightGray)
        ) {
            states.forEach { text->
                Text(
                    text = text,
                    color = Color.White,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            onSelectionChange(text)
                        }
                        .background(
                            if (text == selectedOption) {
                                MaterialTheme.colors.primary
                            } else {
                                Color.LightGray
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
}








