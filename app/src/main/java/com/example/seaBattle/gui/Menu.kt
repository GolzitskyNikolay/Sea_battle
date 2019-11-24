package com.example.seaBattle.gui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import kotlinx.android.synthetic.main.menu_activity.*


class Menu : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        play_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.play_button) {
            val intent = Intent(this, CreateField::class.java)
            startActivity(intent)
        }
    }

}