package it.polito.mad.sportcamp.database

import androidx.compose.runtime.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.polito.mad.sportcamp.SportCampApplication

class AppViewModel(private val Dao: Dao) : ViewModel() {

    //======================================= users ==============================================
    fun getUserById(id_user:Int) : LiveData<User> = Dao.getUserById(id_user)
    fun updateUserById( nickname: String, name:String, mail:String, city:String,
                        age:Int, gender:String, level:String, sports:String, bio:String, id_user:Int) =
        Dao.updateUserById(nickname,name,mail,city,age,gender,level,sports,bio,id_user)

    //=================================== reservations ==========================================
    fun getAllReservations(): LiveData<List<Reservation>> = Dao.getAllReservations()
    fun getReservationsByUse(id_user: Int): Reservation = Dao.getReservationByUser(id_user)
    suspend fun addReservation(reservation: Reservation)=Dao.addReservation(reservation)
    fun updateReservationById( id_reservation: Int, time_slot: String, equipments: String) =
        Dao.updateReservationById(id_reservation,time_slot,equipments)
    fun deleteReservationById(id_reservation: Int) =Dao.deleteReservationById(id_reservation)

    //====================================== courts =============================================
    fun getAllCourts(): LiveData<List<Court>> = Dao.getAllCourts()
    fun getCourtsBySport(sport: String): LiveData<List<Court>> = Dao.getCourtsBySport(sport)



    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SportCampApplication)
                AppViewModel(application.database.Dao())
            }
        }
    }
}


/*
class AppViewModelFactory(
    private val Dao: Dao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(Dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
