package it.polito.mad.sportcamp.classes

import com.google.firebase.Timestamp


data class User(
    val id_user: String? = null,
    val lastLogin: Timestamp? = null,
    val nickname: String? = null,
    val name: String? = null,
    val mail: String? = null,
    val city: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val sports: String? = null,
    val bio: String? = null,
    val image: String? = null,
    val tennis_level: String? = null,
    val football_level: String? = null,
    val volley_level: String? = null,
    val basket_level: String? = null,
)

