package com.example.seaBattle.gui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.view.View;
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R


class Menu : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        val play = findViewById<Button>(R.id.play_button)
        play.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.play_button) {
            startActivity(Intent(this, CreateField::class.java))
        }
    }

}