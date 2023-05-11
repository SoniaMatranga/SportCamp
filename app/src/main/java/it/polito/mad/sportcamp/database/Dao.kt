package it.polito.mad.sportcamp.database


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface Dao {

    //=============== USERS ==============

    //TODO: check if works

    @Query("SELECT * FROM users_table WHERE id_user=:id_user")
    fun getUserById(id_user: Int) :User

    @Query("UPDATE users_table SET nickname=:nickname, name=:name, surname =:surname, mail=:mail," +
            "city=:city, age=:age, gender=:gender, level=:level, sports=:sports, bio=:bio WHERE id_user=:id_user")
    fun updateUserById( nickname: String, name:String, surname:String, mail:String, city:String,
                        age:Int, gender:String, level:String, sports:String, bio:String, id_user:Int)



    //============ RESERVATIONS ===============

    @Query("SELECT * FROM reservations_table")
    fun getAllReservations(): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations_table WHERE id_user=:id_user")
    fun getReservationByUser(id_user: Int): Reservation

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReservation(reservation: Reservation)

    @Query("UPDATE reservations_table SET time_slot=:time_slot, equipments=:equipments WHERE id_reservation=:id_reservation")
    fun updateReservationById( id_reservation: Int, time_slot: String, equipments: String)

    @Query("DELETE FROM reservations_table WHERE id_reservation=:id_reservation")
    fun deleteReservationById(id_reservation: Int)




    //============== COURTS ==================
    @Query("SELECT * FROM courts_table")
    fun getAllCourts(): Flow<List<Court>>

    @Query("SELECT * FROM courts_table WHERE sport=:sport") //sport filter
    fun getCourtsBySport(sport: String): Flow<List<Court>>

   /* @Query("SELECT * FROM courts_table, reservations_table WHERE date=:date AND sport=:sport ORDER BY court_rating DESC") //calendar date filter
    fun getCourtsByDateAndSport(date: String): Flow<List<Court>>*/

}