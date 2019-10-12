package com.example.seaBattle.core

import android.content.Context

import androidx.appcompat.widget.AppCompatButton

class Cell(context: Context) : AppCompatButton(context) {
    private var hasShip = false
    private var hasShot = false

    fun hasShip(): Boolean {
        return hasShip
    }

    fun setHasShip() {
        this.hasShip = !this.hasShip
    }

    fun hasShot(): Boolean {
        return hasShot
    }

    fun setHasShot() {
        this.hasShot = !this.hasShot
    }

}
