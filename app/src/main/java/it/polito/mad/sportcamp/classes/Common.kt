package it.polito.mad.sportcamp.classes


data class ReservationContent(
    val id_reservation: String?,
    val id_court: String?,
    val equipments: String?,
    val court_name: String?,
    val address: String?,
    val city: String?,
    val sport: String?,
    val time_slot: String?,
    val date: String?,
    val image: String?,
    val court_rating: Float?,
    val users: List<String>? = null,
    val players: String? = null,
    val state: String? = null
    )


data class ReservationTimed(
    val id_reservation: String?,
    val users: List<String>? = null,
    val id_user: String?,
    val id_court: String?,
    val time_slot: String?,
    val date: String?,
    val equipments: String?,
    val options: String?
)


