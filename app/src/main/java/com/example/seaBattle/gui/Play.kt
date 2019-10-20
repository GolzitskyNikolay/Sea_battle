package com.example.seaBattle.gui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.Field

class Play : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.play)

        createBotField()
    }

    private fun createBotField() {
        val botField = Field()

        botField.createEmptyField(botField, findViewById(R.id.secondField), this)

        val emptyCells = ArrayList<Int>()
        for (i in 1..100) {
            emptyCells.add(i)
        }

        while (botField.currentCountOfShips != botField.maxCountOfShips) {

            val newShip = botField.addRandomShip(emptyCells, botField)
            Log.d("createBotField", "new ship = $newShip")

            emptyCells.removeAll(newShip)
            emptyCells.removeAll(botField.getCellsAroundShip(newShip, botField))
                    }
    }

}
