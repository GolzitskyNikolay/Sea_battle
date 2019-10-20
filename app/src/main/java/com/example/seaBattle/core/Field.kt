package com.example.seaBattle.core

import android.content.Context
import android.widget.GridLayout
import com.example.seaBattle.R

class Field {

    private var sizeOfField = 10
    var cells: Array<Cell> = emptyArray()
    var currentCountOfShips = 0
    var ships: Array<HashSet<Int>> = emptyArray()

    var countOfSingleDeck = 0
    var countOfDoubleDeck = 0
    var countOfThreeDeck = 0
    var countOfFourDeck = 0

    var maxCountOfSingleDeck = 4
    var maxCountOfDoubleDeck = 3
    var maxCountOfThreeDeck = 2
    var maxCountOfFourDeck = 1

    val maxCountOfShips = 10

    fun createEmptyField(field: Field, layout: GridLayout, context: Context) {

        val size = field.sizeOfField

        field.cells = Array(size * size) { Cell(context) }
        field.ships = Array(field.maxCountOfShips) { HashSet<Int>() }

        for (i in 1..size * size) {

            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            )
            params.height = 0
            params.width = 0

            val cell = Cell(context)
            cell.setBackgroundResource(R.drawable.button_background)
            cell.layoutParams = params
            cell.id = i

            field.cells[i - 1] = cell
            layout.addView(cell)
        }
    }

    // return true, if we can add ship, else - false
    fun addInListCellsToRedraw(
        id: Int, logic: LogicForAdding, ship: Int, orientation: Boolean, field: Field,
        isRandomAdding: Boolean, cellsWithGoodMove: MutableList<Int>,
        cellsWithBadMove: MutableList<Int>?
    ): Boolean {

        val size = field.sizeOfField

        // horizontal ship
        if (orientation) {
            // if there is enough space, then we place the ship
            // from the current cell to the right
            if (ship == 1 || (id % size != 0 && ship == 2)
                || (id % size != 0 && id % size != size - 1 && ship == 3)
                || (id % size != 0 && id % size != size - 1 && id % size != size - 2 && ship == 4)
            ) {
                knowBadOrGoodMove(
                    id, logic, ship, orientation, field, isRandomAdding,
                    cellsWithGoodMove, cellsWithBadMove
                )

            } else {
                //place the ship to the left
                if (id % size == 0 && ship in 2..4) {
                    knowBadOrGoodMove(
                        id - ship + 1, logic, ship, orientation, field, isRandomAdding,
                        cellsWithGoodMove, cellsWithBadMove
                    )

                } else if (id % size == size - 1 && ship in 3..4) {
                    knowBadOrGoodMove(
                        id - ship + 2, logic, ship, orientation, field, isRandomAdding,
                        cellsWithGoodMove, cellsWithBadMove
                    )

                } else if (ship == 4) {
                    knowBadOrGoodMove(
                        id - ship + 3, logic, ship, orientation, field, isRandomAdding,
                        cellsWithGoodMove, cellsWithBadMove
                    )
                }
            }
        } else {

            // if there is enough space, then we place the ship
            // from the current cell to the bottom
            if ((ship == 2 && ((id / size !in size - 1..size) ||
                        (id / size == size - 1 && id % size == 0))) ||
                (ship == 3 && ((id / size !in size - 2..size) ||
                        (id / size == size - 2 && id % size == 0))) ||
                (ship == 4 && ((id / size !in size - 3..size) ||
                        (id / size == size - 3 && id % size == 0)))
            ) {
                knowBadOrGoodMove(
                    id, logic, ship, orientation, field,
                    isRandomAdding, cellsWithGoodMove, cellsWithBadMove
                )


            } else if ((id / size == size - 1 || id == size * size) &&
                ship in 2..4 && id != size * size - size
            ) {
                knowBadOrGoodMove(
                    id - (ship - 1) * size,
                    logic, ship, orientation, field,
                    isRandomAdding, cellsWithGoodMove, cellsWithBadMove
                )


            } else if ((id / size == size - 2 || id == size * size - size) &&
                ship in 3..4 && id != size * size - 2 * size
            ) {
                knowBadOrGoodMove(
                    id - (ship - 2) * size,
                    logic, ship, orientation, field,
                    isRandomAdding, cellsWithGoodMove, cellsWithBadMove
                )


            } else if (((id / size == size - 3) || (id == size * size - 2 * size)) &&
                id != size * size - 3 * size && ship == 4
            ) {
                knowBadOrGoodMove(
                    id - (ship - 3) * size, logic, ship, orientation, field,
                    isRandomAdding, cellsWithGoodMove, cellsWithBadMove
                )
            }

        }

        return cellsWithGoodMove.isNotEmpty()
    }

    /*
    If we can add ship, then it's good move -> green cells,
    else bad move -> red cells.
    */
    private fun knowBadOrGoodMove(
        id: Int, logic: LogicForAdding, ship: Int, orientation: Boolean,
        field: Field, isRandomAdding: Boolean, cellsWithGoodMove: MutableList<Int>,
        cellsWithBadMove: MutableList<Int>?
    ) {
        if (!field.cells[id - 1].hasShip() &&
            logic.checkBeforeAdding(ship, id, orientation, field.cells)
        ) {
            addCells(id - 1, cellsWithGoodMove, ship, orientation, field)

            // draw bad move only for user adding, but not for random
        } else if (!isRandomAdding) {
            addCells(id - 1, cellsWithBadMove!!, ship, orientation, field)
        }

    }

    /*
    Add cells in list of bad or good move
    */
    private fun addCells(
        id: Int, list: MutableList<Int>, ship: Int,
        orientation: Boolean, field: Field
    ) {

        //redraw cells horizontally
        if (orientation) {
            for (e in id..id - 1 + ship) {
                list.add(e + 1)
            }
        } else {
            list.add(id + 1)
            list.add(id + 1 * field.sizeOfField + 1)
            if (ship == 3 || ship == 4) {
                list.add(id + 2 * field.sizeOfField + 1)
            }
            if (ship == 4) {
                list.add(id + 3 * field.sizeOfField + 1)
            }

        }
    }

    /*
    Return id of cells with new ship, it's for generating of bot field
    */
    fun addRandomShip(emptyCells: ArrayList<Int>, botField: Field): MutableList<Int> {

        val orientation: ArrayList<Boolean> = ArrayList()
        orientation.add(true) // horizontal orientation
        orientation.add(false) // vertical orientation

        val newShip = mutableListOf<Int>()

        val logic = LogicForAdding()

        var shipIsAdded = false

        while (!shipIsAdded) {

            val randomId = emptyCells.random()
            val randomOrientation = orientation.random()

            for (ship in 4 downTo 1) {
                if ((ship == 4 &&
                            botField.countOfFourDeck != botField.maxCountOfFourDeck)
                    || (ship == 3 &&
                            botField.countOfThreeDeck != botField.maxCountOfThreeDeck)
                    || (ship == 2 &&
                            botField.countOfDoubleDeck != botField.maxCountOfDoubleDeck)
                    || (ship == 1 &&
                            botField.countOfSingleDeck != botField.maxCountOfSingleDeck)
                ) {

                    // if we can add ship here
                    if (addInListCellsToRedraw(
                            randomId, logic, ship, randomOrientation,
                            botField, true, newShip, null
                        )
                    ) {

                        botField.currentCountOfShips++

                        when (ship) {
                            4 -> botField.countOfFourDeck++
                            3 -> botField.countOfThreeDeck++
                            2 -> botField.countOfDoubleDeck++
                            1 -> botField.countOfSingleDeck++
                        }

                        for (id in newShip) {
                            botField.cells[id - 1].setHasShip()
                        }

                        shipIsAdded = true
                        break
                    }
                }
            }
            if (shipIsAdded) break
        }
        return newShip
    }

    fun getCellsAroundShip(ship: MutableList<Int>, botField: Field): ArrayList<Int> {
        val cellsAroundShip = ArrayList<Int>()

        for (i in 1..ship.size) {
            if (i == 1) {
                cellsAroundShip.addAll(getCellsAroundCell(ship[i - 1], null, botField))
            } else {
                cellsAroundShip.addAll(getCellsAroundCell(ship[i - 1], ship[i - 2], botField))
            }
        }

        return cellsAroundShip
    }

    private fun getCellsAroundCell(
        currentCell: Int, cellBefore: Int?, botField: Field
    ): ArrayList<Int> {
        val size = botField.sizeOfField

        val result = ArrayList<Int>()

        //above and left
        if (currentCell !in 1..size && currentCell % size != 1 &&
            (cellBefore == null || currentCell - size - 1 != cellBefore)
        ) {
            result.add(currentCell - size - 1)
        }

        //above
        if (currentCell !in 1..size && (cellBefore == null || currentCell - size != cellBefore)) {
            result.add(currentCell - size)
        }

        //above and right
        if (currentCell !in 1..size && currentCell % size != 0 &&
            (cellBefore == null || currentCell - size + 1 != cellBefore)
        ) {
            result.add(currentCell - size + 1)
        }

        //left
        if (currentCell % size != 1 && (cellBefore == null || currentCell - 1 != cellBefore)) {
            result.add(currentCell - 1)
        }

        //right
        if (currentCell % size != 0 && (cellBefore == null || currentCell + 1 != cellBefore)) {
            result.add(currentCell + 1)
        }

        //under and left
        if (currentCell % size != 1 && currentCell !in size * size - size + 1..size * size &&
            (cellBefore == null || currentCell + size - 1 != cellBefore)
        ) {
            result.add(currentCell + size - 1)
        }

        //under
        if (currentCell !in size * size - size + 1..size * size &&
            (cellBefore == null || currentCell + size != cellBefore)
        ) {
            result.add(currentCell + size)
        }

        //under and right
        if (currentCell !in size * size - size + 1..size * size && currentCell % size != 0 &&
            (cellBefore == null || currentCell + size + 1 != cellBefore)
        ) {
            result.add(currentCell + size + 1)
        }

        return result
    }

}
