package com.example.seaBattle.gui

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
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

    //when false -> change turn from bot to user
    private var openCells = true

    private var gameOver = false

    private var timer: Timer = Timer()

    //when user is pressing back button -> show dialog
    var dialog: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_game)

        createBotField()
        createUserField()

        createDialog()
    }

    private fun createDialog() {

        dialog = AlertDialog.Builder(this)

        dialog!!.setTitle(R.string.attention)
        dialog!!.setMessage(R.string.message)

        dialog!!.setPositiveButton(R.string.yes, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })

        // change color of android warning image
        val unwrappedDrawable =
            AppCompatResources.getDrawable(this, android.R.drawable.stat_sys_warning)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.RED)

        dialog!!.setIcon(wrappedDrawable)

    }

    /*
    If game isn't finished -> show dialog, else go back
     */
    override fun onBackPressed() {
        if (!gameOver) {
            dialog!!.show()
        } else super.onBackPressed()
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

    private fun createUserField() {

        userField.createEmptyField(userField, findViewById(R.id.firstField), this)

        //read remembered user ships
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val ships = preferences.getStringSet(
            "cells with ship", HashSet<String>(listOf("1", "2", "3", "4", "23", "33", "87"))
        )
        Log.d("createUserField", " ships = $ships")
        for (id in ships!!) {
            val cell = userField.cells[id.toInt() - 1]
            cell.background = getDrawable(R.drawable.ship_1)
            cell.setHasShip()
        }
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
                    turn_text.text = getString(R.string.bot_turn)

                    openCells = true

                    startTimer()
                }
            }
        }
    }

    private fun startTimer() {
        timer = Timer()

        //repeat bot steps while cells aren't empty
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {

                    if (openCells) {
                        startBotLogic()

                        if (userField.countOfDestroyedShips ==
                            userField.maxCountOfShips
                        ) {
                            finishGame(false)
                        }

                    } else {
                        if (!gameOver) {
                            //bot finished his turn
                            turn.setImageResource(R.drawable.right)
                            turn_text.text = getString(R.string.user_turn)
                            botField.cells.forEach { cell ->
                                cell.isEnabled = true
                            }
                        }
                        //cancel timer
                        cancel()
                    }
                }
            }
        }, 700, 700)
    }

    // true -> has ship, else -> false
    private fun openCell(field: Field, cell: Cell, botLogic: BotLogic?): Boolean {

        cell.setHasShot()

        //ship is wounded
        if (cell.hasShip()) {
            cell.background = getDrawable(R.drawable.ship_x)

            Log.d("openCell", "hasShip = ${cell.hasShip()}; hasShot = ${cell.hasShot()}")

            val cellsAroundShip = field.isShipDestroyed(cell.id, field)

            Log.d("openCell", "cellsAroundShip = $cellsAroundShip")

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
                    Log.d(
                        "botLogic",
                        "removing cells from user field around ship from checkable"
                    )
                    botLogic.idOfCellsToOpen.removeAll(cellsAroundShip)
                    botLogic.closedCells.removeAll(cellsAroundShip)

                    isShipDestroyed = true
                    userField.countOfDestroyedShips++
                    Log.d(
                        "botLogic",
                        "userField,destroyedShips=${userField.countOfDestroyedShips}"
                    )

                } else {
                    Log.d("openCell", "removing listeners from bot field around destroyed ship")
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
        Log.d("botLogic", "*start of moves*; id = $id; cellsToOpen = ${botLogic.idOfCellsToOpen}")
        Log.d("botLogic", "orientation = ${botLogic.shipOrientation}")

        if (id != null) {

            //if cell has ship
            if (openCell(userField, userField.cells[id - 1], botLogic)) {

                if (!isShipDestroyed) {

                    //if we don't know ship orientation
                    if (botLogic.shipOrientation == null) {

                        //when we have 2 wounded cells ->  we can know ship orientation
                        if (botLogic.lastIdWithShip != null) {
                            botLogic.knowOrientation(id)
                            botLogic.rememberCellsThatCanHasShip(id, userField)

                            Log.d("botLogic", "ship orientation = ${botLogic.shipOrientation}")

                        } else {
                            botLogic.rememberCellsThatCanHasShip(id, userField)
                        }
                        //remember id of last wounded cell to know ship orientation
                        botLogic.lastIdWithShip = id

                    } else {
                        botLogic.rememberCellsThatCanHasShip(id, userField)
                    }

                } else { //ship is destroyed
                    isShipDestroyed = false
                    botLogic.shipOrientation = null
                    botLogic.lastIdWithShip = null
                }
                Log.d("botLogic", "orientation = ${botLogic.shipOrientation}")
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
        Log.d("botLogic", "*end of moves*; cellsToOpen = ${botLogic.idOfCellsToOpen}")
    }

    private fun finishGame(isPlayerWon: Boolean) {
        gameOver = true
        openCells = false

        if (isPlayerWon) {
            Toast.makeText(this, R.string.user_won, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.bot_won, Toast.LENGTH_LONG).show()
        }

        botField.cells.forEach { cell ->
            if (cell.hasOnClickListeners()) {
                if (cell.hasShip()) {
                    cell.background = getDrawable(R.drawable.ship_1)
                }
                cell.setOnClickListener(null)
            }
        }
    }

}
