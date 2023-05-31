package it.polito.mad.sportcamp.classes


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

