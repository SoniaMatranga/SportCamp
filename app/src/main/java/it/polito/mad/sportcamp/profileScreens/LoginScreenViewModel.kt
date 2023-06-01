package it.polito.mad.sportcamp.profileScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.sportcamp.common.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class LoginScreenViewModel : ViewModel() {

    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    //val googleIdToken = ""
    private val db = Firebase.firestore
    val data = hashMapOf(
        "lastLogin" to com.google.firebase.Timestamp(Date(System.currentTimeMillis()))
    )

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInWithCredential(credential).await()
            loadingState.emit(LoadingState.LOADED)
            auth = Firebase.auth
            user = auth.currentUser!!
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signInAnonymously() = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInAnonymously().await()
            loadingState.emit(LoadingState.LOADED)
            auth = Firebase.auth
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun linkAccount(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.currentUser!!.linkWithCredential(credential).await()
            loadingState.emit(LoadingState.LOADED)
            auth = Firebase.auth
            user = auth.currentUser!!
            db.collection("users").document(auth.uid!!).set(data, SetOptions.merge())

        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }

    }


     fun signOut() = viewModelScope.launch {
         try {
             loadingState.emit(LoadingState.LOADING)
             Firebase.auth.signOut()
             loadingState.emit(LoadingState.LOADED)
         } catch (e: Exception) {
             loadingState.emit(LoadingState.error(e.localizedMessage))
         }

    }
}