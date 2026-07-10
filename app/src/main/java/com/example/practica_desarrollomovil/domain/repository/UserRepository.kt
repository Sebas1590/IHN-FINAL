package com.example.practica_desarrollomovil.domain.repository

import com.example.practica_desarrollomovil.domain.model.User

interface UserRepository {
    /** True si ya existe una cuenta con ese correo. */
    suspend fun emailExists(email: String): Boolean

    /** Crea un usuario nuevo. Lanza excepción si el correo ya está registrado. */
    suspend fun register(name: String, email: String, password: String): User

    /** Devuelve el usuario si el correo y la contraseña coinciden; null en caso contrario. */
    suspend fun authenticate(email: String, password: String): User?
}
