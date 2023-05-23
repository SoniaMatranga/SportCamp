package it.polito.mad.sportcamp.database

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
    fun updateUser(nickname: String, name:String, mail:String, city:String,
                   age:Int, gender:String, level:String, sports:String, bio:String, id_user:Int, image: String
    ) =
        Dao.updateUser(nickname,name,mail,city,age,gender,level,sports,bio,id_user,image)

    //=================================== reservations ==========================================
    //fun getAllReservations(): LiveData<List<ReservationTimed>> = Dao.getAllReservations()
    fun getReservationsByUser(id_user: Int): LiveData<List<ReservationTimed>> = Dao.getReservationsByUser(id_user)
    fun getReservationAndCourt(id_reservation: Int): LiveData<ReservationContent> = Dao.getReservationAndCourt(id_reservation)
    fun getReservationById(id_reservation: Int): LiveData<Reservation> = Dao.getReservationById(id_reservation)

    fun getReservationsByUserAndDate(id_user: Int, date: String): LiveData<List<ReservationContent>> = Dao.getReservationsByUserAndDate(id_user, date)

    fun updateReservationById(id_reservation: Int, time_slot: String, equipments: String) =
       Dao.updateReservationById(id_reservation, time_slot, equipments)

    fun addReservation(id_reservation: Int?, id_user: Int, id_court: Int, time_slot: String, date: String, equipments: String, options: String)=
        Dao.addReservation(id_reservation,id_user,id_court,time_slot,date,equipments, options)
    fun deleteReservationById(id_reservation: Int) =Dao.deleteReservationById(id_reservation)

    //fun getFreeTimeSlots(id_court: LiveData<List<String>>) = Dao.getFreeTimeSlots(id_court)

    //====================================== courts =============================================
    fun getAllCourts(): LiveData<List<Court>> = Dao.getAllCourts()
    fun getCourtsBySport(sport: String): LiveData<List<Court>> = Dao.getCourtsBySport(sport)
    fun getCourtByName(name: String): LiveData<Court> = Dao.getCourtByName(name)
    fun getCourtById(id_court: Int): LiveData<Court> = Dao.getCourtById(id_court)

    fun getAvailableTimeSlots(courtId: Int?, date: String?): LiveData<List<String>> = Dao.getAvailableTimeSlots(courtId, date)

    fun getCourtReviewById(id_court: Int, id_user: Int): LiveData<Rating> = Dao.getCourtReviewById(id_court, id_user)

    fun updateReview(id: Int, rating: Float, review: String) = Dao.updateReview(id, rating, review)

    fun insertReview(id: Int?, id_user: Int, id_court: Int, rating: Float, review: String?)= Dao.insertReview(id, id_user, id_court, rating, review)

    fun getCourtReviewsById(id_court: Int): LiveData<List<Rating>> = Dao.getCourtReviewsById(id_court)

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
