package com.example.seaBattle.gui

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.BotLogic
import com.example.seaBattle.core.Cell
import com.example.seaBattle.core.Field
import kotlinx.android.synthetic.main.start_game.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class Play : AppCompatActivity() {
    private val userField = Field()
    private val botField = Field()

    private val botLogic = BotLogic()

    private var isShipDestroyed = false

    private var openCells = true

    private var timer: Timer = Timer()

    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_game)

        createBotField()
        createUserField()
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
        for (id in ships!!) {
            val cell = userField.cells[id.toInt() - 1]
            cell.background = getDrawable(R.drawable.ship_1)
            cell.setHasShip()
        }
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
            Log.d("logic", "new ship in bot field= $newShip")

            emptyCells.removeAll(newShip)
            emptyCells.removeAll(botField.getCellsAroundShip(newShip, botField))
        }

        addListeners(botField)
    }

    private fun addListeners(field: Field) {
        for (cell in field.cells) {

            cell.setOnClickListener {
                cell.setOnClickListener(null)

                //if user opened empty cell -> bot starts his turn
                if (!openCell(field, cell, null)) {

                    //disable cells
                    botField.cells.forEach { cell ->
                        cell.isEnabled = false
                    }
                    turn.setImageResource(R.drawable.left)

                    openCells = true
                    timer = Timer()

                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {

                                while (openCells) {
                                    startBotLogic()
                                    if (userField.countOfDestroyedShips ==
                                        userField.maxCountOfShips
                                    ) {
                                        finishGame(false)
                                    }
                                }

                                if (!gameOver) {
                                    //bot finished his turn
                                    turn.setImageResource(R.drawable.right)
                                    botField.cells.forEach { cell ->
                                        cell.isEnabled = true
                                    }
                                }
                            }
                            cancel()
                        }
                    }, 1000, 1000)
                }
            }
        }
    }

    // true -> has ship, else -> false
    private fun openCell(field: Field, cell: Cell, botLogic: BotLogic?): Boolean {

        cell.setHasShot()

        //ship is wounded
        if (cell.hasShip()) {
            cell.background = getDrawable(R.drawable.ship_x)

            Log.d("openCell", "hasShip = ${cell.hasShip()}; hasShot = ${cell.hasShot()}")

            val cellsAroundShip = field.isShipDestroyed(cell.id, field)

            Log.d("logic", "cellsAroundShip = $cellsAroundShip")

            //if ship is destroyed
            if (cellsAroundShip.isNotEmpty()) {

                //open empty cells around destroyed ship
                for (id in cellsAroundShip) {
                    if (!field.cells[id - 1].hasShip()) {
                        field.cells[id - 1].background = getDrawable(R.drawable.sqr_point)
                        field.cells[id - 1].setHasShot()
                    }
                }

                //method was called from bot, remember opened cells
                if (botLogic != null) {
                    Log.d("logic", "removing cells from user field around ship from checkable")
                    botLogic.idOfCellsToOpen.removeAll(cellsAroundShip)
                    botLogic.closedCells.removeAll(cellsAroundShip)

                    isShipDestroyed = true
                    userField.countOfDestroyedShips++
                    Log.d("logic", "userField, destroyed ships=${userField.countOfDestroyedShips}")

                } else {
                    Log.d("logic", "removing listeners from bot field around destroyed ship")
                    cellsAroundShip.forEach { id ->
                        botField.cells[id - 1].setOnClickListener(null)
                    }
                    botField.countOfDestroyedShips++
                    Log.d("logic", "botField destroyed ships=${botField.countOfDestroyedShips}")

                    //user won
                    if (botField.countOfDestroyedShips == botField.maxCountOfShips) {
                        finishGame(true)
                    }
                }
            }
            return true

        } else {
            //change image of button
            cell.background = getDrawable(R.drawable.sqr_point)

            return false
        }
    }

    private fun startBotLogic() {

        val id = botLogic.getIdOfNextCell()
        Log.d("logic", "*start of moves*\n id = $id; cellsToOpen = ${botLogic.idOfCellsToOpen}")
        Log.d("logic", "orientation = ${botLogic.shipOrientation}")

        if (id != null) {

            //if cell has ship
            if (openCell(userField, userField.cells[id - 1], botLogic)) {

                if (!isShipDestroyed) {

                    //if we don't know ship orientation
                    if (botLogic.shipOrientation == null) {

                        //when we have 2 wounded cells ->  we can know ship orientation
                        if (botLogic.lastIdWithShip != null) {
                            botLogic.knowOrientation(userField.cells, id)
                            botLogic.getUnopenedCellsAroundWoundedCell(id, userField.cells)

                            Log.d("logic", "ship orientation = ${botLogic.shipOrientation}")

                        } else {
                            botLogic.getUnopenedCellsAroundWoundedCell(id, userField.cells)
                        }
                        //remember id of last wounded cell to know ship orientation
                        botLogic.lastIdWithShip = id

                    } else {
                        botLogic.getUnopenedCellsAroundWoundedCell(id, userField.cells)
                    }

                } else { //ship is destroyed
                    isShipDestroyed = false
                    botLogic.shipOrientation = null
                    botLogic.lastIdWithShip = null
                }
                Log.d("logic", "orientation = ${botLogic.shipOrientation}")
                Log.d("logic", "*end of moves*\n cellsToOpen = ${botLogic.idOfCellsToOpen}")

            } else {
                //cell is empty, stop timer, begin user turn
                openCells = false
            }

        } else {
            // bot doesn't know where can be ship -> takes random cell from unopened cells
            if (botLogic.closedCells.isNotEmpty()) {
                botLogic.chooseRandomCell()

            } else { // user field hasn't unopened cells
                openCells = false
            }
        }
    }

    private fun finishGame(isPlayerWon: Boolean) {
        if (isPlayerWon) {
            Toast.makeText(this, "Player won!!!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Bot won!!!", Toast.LENGTH_LONG).show()
        }

        botField.cells.forEach { cell ->
            if (cell.hasOnClickListeners()) {
                cell.setOnClickListener(null)
            }
        }
    }

}
