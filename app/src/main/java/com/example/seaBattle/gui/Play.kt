package com.example.seaBattle.gui

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.Field

class Play : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.play)

        createUserField()

        createBotField()
    }

    private fun createBotField() {
        val botField = Field()

        botField.createEmptyField(botField, findViewById(R.id.secondField), this)

        val emptyCells = ArrayList<Int>()
        for (i in 1..100) {
            emptyCells.add(i)
        }

        // add ships
        while (botField.currentCountOfShips != botField.maxCountOfShips) {

            val newShip = botField.addRandomShip(emptyCells, botField)
            Log.d("createBotField", "new ship = $newShip")

            emptyCells.removeAll(newShip)
            emptyCells.removeAll(botField.getCellsAroundShip(newShip, botField))
        }

        addListeners(botField)
    }

    private fun addListeners(field: Field) {
        for (cell in field.cells) {

            cell.setOnClickListener {
                cell.setHasShot()
                cell.setOnClickListener(null)

                if (cell.hasShip()) {
                    cell.background = getDrawable(R.drawable.ship_x)

                    val ship = mutableListOf<Int>()
                    ship.add(cell.id)

                    if (field.isShipDestroyed(cell.id, field, null, ship) != null) {
                        Log.d("addListeners", "ship = $ship")
                        for (id in field.getCellsAroundShip(ship, field)) {
                            if (!field.cells[id - 1].hasShip()) {
                                field.cells[id - 1].background = getDrawable(R.drawable.sqr_point)
                            }
                        }
                    }

                } else {
                    cell.background = getDrawable(R.drawable.sqr_point)
                }
            }
        }
    }

    private fun createUserField() {
        val userField = Field()

        userField.createEmptyField(userField, findViewById(R.id.firstField), this)

        //read remembered user ships
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val ships = preferences.getStringSet(
            "cells with ship",
            HashSet<String>(listOf("1", "2", "3", "4", "23", "33", "87"))
        )
        Log.d("createUserField", " ships = $ships")
        for (id in ships) {
            val cell = userField.cells[id.toInt() - 1]
            cell.background = getDrawable(R.drawable.ship_1)
            cell.setHasShip()
        }

        addListeners(userField)
    }
}
