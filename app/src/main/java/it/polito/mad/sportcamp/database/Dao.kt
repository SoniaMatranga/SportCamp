package it.polito.mad.sportcamp.database


import android.media.Image
import androidx.lifecycle.LiveData
import androidx.room.*


data class ReservationContent(val court_name: String, val address: String,  val city: String, val sport: String,
                   val time_slot: String, val date: String)
@androidx.room.Dao
interface Dao {

    //=============== USERS ==============

    //TODO: check if works

    @Query("SELECT * FROM users_table WHERE id_user=:id_user")
    fun getUserById(id_user: Int) : LiveData<User>

    @Query("UPDATE users_table SET nickname=:nickname, name=:name, mail=:mail," +
            "city=:city, age=:age, gender=:gender, level=:level, sports=:sports, bio=:bio, image=:image WHERE id_user=:id_user")
    fun updateUser( nickname: String, name:String,mail:String, city:String,
                        age:Int, gender:String, level:String, sports:String, bio:String, id_user:Int, image: String)



    //============ RESERVATIONS ===============

    @Query("SELECT * FROM reservations_table")
    fun getAllReservations(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations_table WHERE id_user=:id_user")
    fun getReservationsByUser(id_user: Int): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations_table, courts_table WHERE id_user=:id_user AND date=:date AND reservations_table.id_court=courts_table.id_court")
    fun getReservationsByUserAndDate(id_user: Int, date:String): LiveData<List<ReservationContent>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReservation(reservation: Reservation)

    @Query("UPDATE reservations_table SET time_slot=:time_slot, equipments=:equipments WHERE id_reservation=:id_reservation")
    fun updateReservationById( id_reservation: Int, time_slot: String, equipments: String)

    @Query("DELETE FROM reservations_table WHERE id_reservation=:id_reservation")
    fun deleteReservationById(id_reservation: Int)




    //============== COURTS ==================
    @Query("SELECT * FROM courts_table")
    fun getAllCourts(): LiveData<List<Court>>

    @Query("SELECT * FROM courts_table WHERE sport=:sport") //sport filter
    fun getCourtsBySport(sport: String): LiveData<List<Court>>

   /* @Query("SELECT * FROM courts_table, reservations_table WHERE date=:date AND sport=:sport ORDER BY court_rating DESC") //calendar date filter
    fun getCourtsByDateAndSport(date: String): LiveData<List<Court>>*/

}