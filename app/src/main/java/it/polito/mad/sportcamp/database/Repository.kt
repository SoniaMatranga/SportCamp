package it.polito.mad.sportcamp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.polito.mad.sportcamp.SportCampApplication

class AppViewModel(private val Dao: Dao) : ViewModel() {

    //======================================= users ==============================================
    fun getUserById(id_user:Int) : LiveData<User> = Dao.getUserById(id_user)
    fun getCourtById(id_court: Int): LiveData<Court> = Dao.getCourtById(id_court)

    fun getCourtReviewById(id_court: Int, id_user: Int): LiveData<Rating> = Dao.getCourtReviewById(id_court, id_user)

    fun updateReview(id: Int, rating: Float, review: String) = Dao.updateReview(id, rating, review)

    fun insertReview(id: Int?, id_user: Int, id_court: Int, rating: Float, review: String?)= Dao.insertReview(id, id_user, id_court, rating, review)

    fun getCourtReviewsById(id_court: Int): LiveData<List<Rating>> = Dao.getCourtReviewsById(id_court)

    fun deleteReviewById(id: Int) = Dao.deleteReviewById(id)

    fun updateCourtRatingById(id_court: Int) = Dao.updateCourtRatingById(id_court)

    fun getAllCourtsUserPlayed(id_user: Int, date: String): LiveData<List<Court>> = Dao.getAllCourtsUserPlayed(id_user, date)

    fun getFilteredCourtsUserPlayed(id_user: Int, date: String, sport: String): LiveData<List<Court>> = Dao.getFilteredCourtsUserPlayed(id_user, date, sport)

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SportCampApplication)
                AppViewModel(application.database.Dao())
            }
        }
    }
}



