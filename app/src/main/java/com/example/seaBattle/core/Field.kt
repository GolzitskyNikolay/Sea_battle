package com.example.seaBattle.core

import android.content.Context

class Field(context: Context) {

    var sizeOfField = 10
    var cells: Array<Cell> = emptyArray()

    init {
        cells = Array(sizeOfField * sizeOfField) { Cell(context) }
    }

    var countOfSingleDeck = 0
    var countOfDoubleDeck = 0
    var countOfThreeDeck = 0
    var countOfFourDeck = 0
}
