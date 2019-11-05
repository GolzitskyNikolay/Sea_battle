package com.example.seaBattle.gui

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.BotLogic
import com.example.seaBattle.core.Cell
import com.example.seaBattle.core.Field
import kotlinx.android.synthetic.main.play.*

class Play : AppCompatActivity() {
    private val userField = Field()
    private val botField = Field()

    private val botLogic = BotLogic()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.play)

        createUserField()

        createBotField()
    }

    private fun createBotField() {

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
                cell.setOnClickListener(null)
                cell.setHasShot()

                openCell(field, cell)
            }
        }
    }

    // true -> has ship, else -> false
    private fun openCell(field: Field, cell: Cell): Boolean {

        //ship is wounded
        if (cell.hasShip()) {
            cell.background = getDrawable(R.drawable.ship_x)

            val ship = mutableListOf<Int>()
            ship.add(cell.id)

            if (field.isShipDestroyed(cell.id, field, null, ship) != null) {
                Log.d("addListeners", "ship = $ship")

                //mark empty cells around ship
                for (id in field.getCellsAroundShip(ship, field)) {
                    if (!field.cells[id - 1].hasShip()) {
                        field.cells[id - 1].background = getDrawable(R.drawable.sqr_point)
                    }
                }
            }
            return true

        } else {
            //change image of button
            cell.background = getDrawable(R.drawable.sqr_point)

            if (field == userField) {
                changeTurn(userField)

            } else {
                changeTurn(botField)
            }

            return false
        }
    }

    private fun createUserField() {

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
    }

    private fun changeTurn(currentField: Field) {

        if (currentField == botField) {

            //disable cells
            botField.cells.forEach { cell ->
                cell.isEnabled = false
            }

            turn.setImageResource(R.drawable.left)

//            Log.d("changeTurn", "cells = ${botLogic.idOfCellsToOpen}")
//            var id = botLogic.getIdOfNextCell()
//
//            if (id != null) {
//                while (openCell(userField, userField.cells[id!! - 1])) {
//                    Log.d("changeTurn", "id = $id")
//                    Log.d("changeTurn", "cells = ${botLogic.idOfCellsToOpen}")
//
//                    botLogic.getCellsAroundWoundedCell(id, userField.cells)
//
//                    id = botLogic.getIdOfNextCell()
//
//                    if (botLogic.getOrientation() == null) {
//                        botLogic.tryToKnowOrientation(userField.cells)
//                    }
//
//                    Thread.sleep(10)
//                }
//
//                //bot finished his moves
//            } else {
//                botLogic.chooseRandomCell()
//
//                turn.setImageResource(R.drawable.right)
//
//                botField.cells.forEach { cell ->
//                    cell.isEnabled = true
                }
//            }
//        }
    }
}
