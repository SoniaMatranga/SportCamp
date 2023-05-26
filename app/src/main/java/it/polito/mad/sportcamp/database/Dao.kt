package it.polito.mad.sportcamp.database


import androidx.lifecycle.LiveData
import androidx.room.*



data class ReservationContent(
    val id_reservation: Int?,
    val id_court: Int?,
    val equipments: String?,
    val court_name: String?,
    val address: String?,
    val city: String?,
    val sport: String?,
    val time_slot: String?,
    val date: String?,
    val image: String?,
    val court_rating: Float?
    )


data class ReservationTimed(
    val  id_reservation: Int?,
    val id_user: Int?,
    val id_court: Int?,
    val time_slot: String?,
    val date: String?,
    val equipments: String?,
    val options: String?
)


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

    /*@RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM reservations_table, time_slots_table WHERE reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getAllReservations(): LiveData<List<ReservationTimed>>*/

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM reservations_table, time_slots_table WHERE id_user=:id_user AND reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getReservationsByUser(id_user: Int): LiveData<List<ReservationTimed>>

    @Query("SELECT * FROM reservations_table  WHERE id_reservation=:id_reservation")
    fun getReservationById(id_reservation: Int): LiveData<Reservation>


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM reservations_table, courts_table, time_slots_table WHERE id_reservation=:id_reservation AND reservations_table.id_court=courts_table.id_court AND reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getReservationAndCourt(id_reservation: Int): LiveData<ReservationContent>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM reservations_table, courts_table, time_slots_table WHERE id_user=:id_user AND date=:date AND reservations_table.id_court=courts_table.id_court AND reservations_table.id_time_slot == time_slots_table.id_time_slot")
    fun getReservationsByUserAndDate(id_user: Int, date:String): LiveData<List<ReservationContent>>




    @Query("INSERT INTO reservations_table (id_reservation, id_user, id_court, id_time_slot, date, equipments, options) " +
            "SELECT :id_reservation , :id_user, :id_court, time_slots_table.id_time_slot, :date, :equipments, :options " +
            "FROM time_slots_table WHERE time_slots_table.time_slot = :time_slot")
    fun addReservation(id_reservation: Int?, id_user: Int, id_court: Int, time_slot: String, date: String, equipments: String, options: String)
    @Query("UPDATE reservations_table SET id_time_slot =" +
            " (SELECT id_time_slot FROM time_slots_table WHERE time_slot = :time_slot) " +
            ", equipments = :equipments WHERE id_reservation = :id_reservation")
    fun updateReservationById( id_reservation: Int, time_slot: String, equipments: String)

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

    @Query("SELECT * FROM courts_table WHERE court_name=:court_name")
    fun getCourtByName(court_name: String?): LiveData<Court>

    @Query("SELECT * FROM courts_table WHERE id_court=:id_court")
    fun getCourtById(id_court: Int): LiveData<Court>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM courts_table, reservations_table, time_slots_table" +
            " WHERE courts_table.sport=:sport " +
            "AND reservations_table.date!=:date " +
            "AND reservations_table.id_time_slot != time_slots_table.id_time_slot") //sport and date filter
    fun getCourtsBySportAndDate(sport: String, date:String): LiveData<List<CourtContent>>


    @Query("SELECT time_slot FROM time_slots_table WHERE" +
            " id_time_slot NOT IN (" +
            "SELECT id_time_slot FROM reservations_table WHERE id_court = :courtId AND date = :date)")
    fun getAvailableTimeSlots(courtId: Int?, date: String?): LiveData<List<String>>
   /* @Query("SELECT * FROM courts_table, reservations_table WHERE date=:date AND sport=:sport ORDER BY court_rating DESC") //calendar date filter
    fun getCourtsByDateAndSport(date: String): LiveData<List<Court>>*/

    @Query("SELECT * from ratings_table WHERE id_user=:id_user AND id_court=:id_court")
    fun getCourtReviewById(id_court: Int, id_user: Int): LiveData<Rating>

    @Query("UPDATE ratings_table SET rating=:rating, review=:review WHERE id=:id")
    fun updateReview(id: Int, rating: Float, review: String)

    @Query("INSERT INTO ratings_table (id, id_user, id_court, rating, review) VALUES (:id, :id_user, :id_court, :rating, :review) ")
    fun insertReview(id: Int?, id_user: Int, id_court: Int, rating: Float, review: String?)

    @Query("SELECT * from ratings_table WHERE id_court=:id_court")
    fun getCourtReviewsById(id_court: Int): LiveData<List<Rating>>

    @Query("DELETE FROM ratings_table WHERE id=:id")
    fun deleteReviewById(id: Int)

    @Query("UPDATE courts_table SET court_rating = (SELECT AVG(rating)" +
            " FROM ratings_table WHERE ratings_table.id_court = courts_table.id_court) " +
            "WHERE id_court=:id_court")
    fun updateCourtRatingById(id_court: Int)

    @Query("SELECT ct.id_court, ct.court_name, ct.address, ct.city, ct.sport, ct.court_rating, ct.image" +
            " FROM courts_table ct JOIN reservations_table rt ON ct.id_court = rt.id_court" +
            " WHERE rt.id_user = :id_user" +
            "  AND rt.date < :date")
    fun getAllCourtsUserPlayed(id_user: Int, date: String): LiveData<List<Court>>

    @Query("SELECT ct.id_court, ct.court_name, ct.address, ct.city, ct.sport, ct.court_rating, ct.image" +
            " FROM courts_table ct JOIN reservations_table rt ON ct.id_court = rt.id_court" +
            " WHERE rt.id_user = :id_user" +
            "  AND rt.date < :date AND ct.sport = :sport")
    fun getFilteredCourtsUserPlayed(id_user: Int, date: String, sport: String): LiveData<List<Court>>

}