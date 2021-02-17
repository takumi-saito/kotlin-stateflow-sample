package com.t_saito.kotlinflowsample

import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlin.random.Random

sealed class User : Serializable {
    abstract val id: Long
    abstract val firstName: String
    abstract val lastName: String
    abstract val age: Int
    abstract val gender: Int

    @JsonClass(generateAdapter = true)
    data class Normal(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val age: Int,
        override val gender: Int,
    ): User() {
        companion object {
            val EMPTY = Normal(
                id = -1,
                firstName = "",
                lastName = "",
                age = 0,
                gender = -1
            )

            fun randomUser(): Normal {
                return Random.nextInt(100).let {
                    Normal(
                        id = it.toLong(),
                        firstName = "firstName$it",
                        lastName = "lastName$it",
                        age = it,
                        gender = it
                    )
                }
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class Special(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val age: Int,
        override val gender: Int,
    ): User() {
        companion object {
            val EMPTY = Special(
                id = -1,
                firstName = "",
                lastName = "",
                age = 0,
                gender = -1
            )

            fun randomUser(): Special {
                return Random(100).nextInt().let {
                    Special(
                        id = it.toLong(),
                        firstName = "firstName$it",
                        lastName = "lastName$it",
                        age = it,
                        gender = it
                    )
                }
            }
        }
    }
}