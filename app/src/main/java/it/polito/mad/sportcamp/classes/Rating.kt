package it.polito.mad.sportcamp.classes


data class Rating(
    var id: String? = null,
    val id_user: String? = null,
    val id_court: String? = null,
    val rating: Float? = null,
    val review: String? = null
)