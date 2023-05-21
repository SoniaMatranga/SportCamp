package it.polito.mad.sportcamp.database


import android.media.Image
import androidx.lifecycle.LiveData
import androidx.room.*


data class ReservationContent(
    val id_reservation: Int?,
    val equipments: String?,
    val court_name: String?,
    val address: String?,
    val city: String?,
    val sport: String?,
    val time_slot: String?,
    val date: String?,
    val image: String?
    )

data class ReservationTimed(
    val  id_reservation: Int?,
    val id_user: Int?,
    val id_court: Int?,
    val time_slot: String?,
    val date: String?,
    val equipments: String?,
    val options: String?
) {
}
data class CourtContent(
    val id_court: Int?,
    val court_name: String?,
    val address: String?,
    val city: String?,
    val sport: String?,
    val time_slot: String?,
    val image: String?
)

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

    @Query("SELECT * FROM reservations_table, time_slots_table WHERE reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getAllReservations(): LiveData<List<ReservationTimed>>

    @Query("SELECT * FROM reservations_table, time_slots_table WHERE id_user=:id_user AND reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getReservationsByUser(id_user: Int): LiveData<List<ReservationTimed>>

    @Query("SELECT * FROM reservations_table, courts_table, time_slots_table WHERE id_user=:id_user AND date=:date AND reservations_table.id_court=courts_table.id_court AND reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getReservationsByUserAndDate(id_user: Int, date:String): LiveData<List<ReservationContent>>




    @Query("INSERT INTO reservations_table (id_reservation, id_user, id_court, id_time_slot, date, equipments, options) " +
            "SELECT :id_reservation , :id_user, :id_court, time_slots_table.id_time_slot, :date, :equipments, :options " +
            "FROM time_slots_table WHERE time_slots_table.time_slot = :time_slot")
    fun addReservation(id_reservation: Int?, id_user: Int, id_court: Int, time_slot: String, date: String, equipments: String, options: String)
    @Query("UPDATE reservations_table SET id_time_slot=:id_time_slot, equipments=:equipments WHERE id_reservation=:id_reservation")
    fun updateReservationById( id_reservation: Int, id_time_slot: String, equipments: String)

    @Query("DELETE FROM reservations_table WHERE id_reservation=:id_reservation")
    fun deleteReservationById(id_reservation: Int)

    /*
    @Query("SELECT time_slot FROM reservations_table, time_slots_table" +
            " WHERE reservations_table.id_time_slot != time_slots_table.id_time_slot")
    fun getFreeTimeSlots(time_slots: LiveData<List<String>>)*/



    //============== COURTS ==================
    @Query("SELECT * FROM courts_table")
    fun getAllCourts(): LiveData<List<Court>>

    @Query("SELECT * FROM courts_table WHERE sport=:sport") //sport filter
    fun getCourtsBySport(sport: String): LiveData<List<Court>>

    @Query("SELECT * FROM courts_table WHERE id_court=:id_court") //sport filter
    fun getCourtById(id_court: Int): LiveData<Court>

    @Query("SELECT * FROM courts_table, reservations_table, time_slots_table" +
            " WHERE courts_table.sport=:sport " +
            "AND reservations_table.date!=:date " +
            "AND reservations_table.id_time_slot != time_slots_table.id_time_slot") //sport and date filter
    fun getCourtsBySportAndDate(sport: String, date:String): LiveData<List<CourtContent>>


    @Query("SELECT time_slot FROM time_slots_table WHERE" +
            " id_time_slot NOT IN (" +
            "SELECT id_time_slot FROM reservations_table WHERE id_court = :courtId AND date = :date)")
    fun getAvailableTimeSlots(courtId: Int, date: String): LiveData<List<String>>
   /* @Query("SELECT * FROM courts_table, reservations_table WHERE date=:date AND sport=:sport ORDER BY court_rating DESC") //calendar date filter
    fun getCourtsByDateAndSport(date: String): LiveData<List<Court>>*/

}