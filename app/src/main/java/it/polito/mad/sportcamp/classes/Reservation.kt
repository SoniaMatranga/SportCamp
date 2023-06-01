package it.polito.mad.sportcamp.classes



data class Reservation(
    val id_reservation: Int? = null,
    val id_user: String? = null,
    val id_court: Int? = null,
    val id_time_slot: Int? = null,
    val date: String? = null,
    val equipments: String? = null,
    val options: String? = null
)