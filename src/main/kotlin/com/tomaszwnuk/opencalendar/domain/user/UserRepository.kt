package com.tomaszwnuk.opencalendar.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * The repository for managing user data.
 */
interface UserRepository : JpaRepository<User, UUID> {

    /**
     * Checks if a user with the given username exists.
     *
     * @param username The username to check
     *
     * @return true if a user with the given username exists, false otherwise
     */
    fun existsByUsername(username: String): Boolean

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check
     *
     * @return true if a user with the given email exists, false otherwise
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Finds a user by their username.
     *
     * @param username The username of the user to find
     *
     * @return The user if found, or null if not found
     */
    fun findByUsername(username: String): User?

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user to find
     *
     * @return The user if found, or null if not found
     */
    fun findByEmail(email: String): User?

}
