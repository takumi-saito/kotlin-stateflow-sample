package com.t_saito.kotlinflowsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SubActivity : AppCompatActivity() {

    private val user: User by lazy {
        intent.getSerializableExtra(KEY_USER) as User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        findViewById<TextView>(R.id.text_1).text = user.toString()
    }

    companion object {

        private const val KEY_USER = "key_user"

        fun toSub(activity: AppCompatActivity, user: User) {
            val intent = Intent(activity, SubActivity::class.java).also {
                it.putExtra(KEY_USER, user)
            }
            activity.startActivity(intent)
        }
    }
}