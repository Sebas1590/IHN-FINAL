package com.example.practica_desarrollomovil.data.repository

import com.example.practica_desarrollomovil.data.local.dao.UserDao
import com.example.practica_desarrollomovil.data.local.entity.UserEntity
import com.example.practica_desarrollomovil.domain.model.User
import com.example.practica_desarrollomovil.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun emailExists(email: String): Boolean =
        userDao.countByEmail(email.normalize()) > 0

    override suspend fun register(name: String, email: String, password: String): User {
        val entity = UserEntity(
            name = name.trim(),
            email = email.normalize(),
            password = password
        )
        val id = userDao.insert(entity)
        return User(id = id, name = entity.name, email = entity.email)
    }

    override suspend fun authenticate(email: String, password: String): User? {
        val entity = userDao.getByEmail(email.normalize()) ?: return null
        return if (entity.password == password) {
            User(id = entity.id, name = entity.name, email = entity.email)
        } else {
            null
        }
    }

    // El correo se guarda y compara siempre en minúsculas y sin espacios.
    private fun String.normalize(): String = trim().lowercase()
}
