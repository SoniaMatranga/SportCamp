package it.polito.mad.sportcamp.classes



data class Reservation(
    var id_reservation: String? = null,
    val users: List<String>? = null,
    val id_user: String? = null,
    val id_court: String? = null,
    val id_time_slot: Int? = null,
    val date: String? = null,
    val equipments: String? = null,
    val options: String? = null,
    val players: String? = null,
    val state: String? = null
)