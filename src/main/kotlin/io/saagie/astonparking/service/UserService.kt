package io.saagie.astonparking.service

import io.saagie.astonparking.dao.UserDao
import io.saagie.astonparking.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService(
        @Autowired val userDao: UserDao,
        @Autowired val emailService: EmailService) {

    fun registerUser(username: String, id: String): Boolean {
        if (!userDao.exists(id)) {
            userDao.save(User(id = id, username = username))
            return true
        }
        return false
    }


    fun updateUserInfo(map: Map<String, Any>) {
        val userMap = map.get("user") as Map<*, *>
        val id = userMap.get("id") as String
        if (userDao.exists(id)) {
            val user = userDao.findOne(id)
            val activatedUser = user.activated
            user.apply {
                email = userMap.get("email") as String
                image_24 = userMap.get("image_24") as String
                image_32 = userMap.get("image_32") as String
                image_48 = userMap.get("image_48") as String
                image_72 = userMap.get("image_72") as String
                image_192 = userMap.get("image_192") as String
                image_512 = userMap.get("image_512") as String
                activated = true

            }
            if (!activatedUser) {
                user.enable = true
                emailService.profileCreated(user)
            }
            userDao.save(user)
        }
    }

    fun get(id: String): User {
        return userDao.findOne(id)
    }

    fun getAll(): List<User> {
        return userDao.findAll() as List<User>
    }

    fun getAllActive(): List<User> {
        return userDao.findByEnable(true)
    }

    fun changeStatus(id: String) {
        val user = userDao.findOne(id)
        user.enable = !user.enable
        userDao.save(user)
        emailService.profileStatusChange(user)
    }

    fun changeStatus(id: String, status: Boolean) {
        val user = userDao.findOne(id)
        user.enable = status
        userDao.save(user)
        emailService.profileStatusChange(user)
    }

}