package org.example.expert.domain.user.entity

import jakarta.persistence.*
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.user.enums.UserRole

@Entity
@Table(name = "users")
class User (
    @Column(unique = true)
    var email: String,
    var nickname: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    var userRole: UserRole
): Timestamped() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var profileImage: String? = null

    private constructor(id: Long?, email: String, nickname: String, userRole: UserRole)
    : this(email, nickname, "", userRole) {
        this.id = id
    }

    companion object {
        @JvmStatic
        fun fromAuthUser(authUser: AuthUser): User {
            return User(authUser.id, authUser.email, authUser.nickname, authUser.userRole)
        }
    }

    fun changePassword(password: String) {
        this.password = password
    }

    fun updateRole(userRole: UserRole) {
        this.userRole = userRole
    }

    fun changeProfile(nickname: String, profileImage: String) {
        this.nickname = nickname;
        this.profileImage = profileImage
    }
}