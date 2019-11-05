package com.example.seaBattle.core

class BotLogic() {

    //consists id of cells, that can has ship (first cell to open we choose by random)
    val idOfCellsToOpen = mutableListOf((1..100).random())

    //null -> we don't know orientation, true -> horizontal, false -> vertical
    private var shipOrientation: Boolean? = null

    //remember direction from last cell to next cell, trying to know ship orientation
    // (id before = 80, next id = 81 => nextCellWasCalledFromHorizontal = true)
    private var nextCellWasCalledFromHorizontal: Boolean = true

    private val closedCells: MutableSet<Int> = mutableSetOf()

    init {
        for (i in 1..100) {
            closedCells.add(i)
        }
    }

    fun getOrientation(): Boolean? {
        return shipOrientation
    }

    fun getIdOfNextCell(): Int? {
        val result = idOfCellsToOpen[0]
        idOfCellsToOpen.remove(result)
        closedCells.remove(result)
        return result
    }

    fun chooseRandomCell() {
        idOfCellsToOpen.add(closedCells.random())
    }

    fun getCellsAroundWoundedCell(id: Int, cells: Array<Cell>) {

        //horizontal or null
        if (shipOrientation != false) {

            //right
            if (id % 10 in 1..9 && !cells[id + 1].hasShot()) {
                idOfCellsToOpen.add(id + 1)
                nextCellWasCalledFromHorizontal = true
            }

            //left
            if (id % 10 in 2..9 || id % 10 == 0 && !cells[id - 1].hasShot()) {
                idOfCellsToOpen.add(id - 1)
                nextCellWasCalledFromHorizontal = true
            }

            //if first -> then check only horizontal
            return
        }

        //vertical or null
        if (shipOrientation != true) {

            //above
            if (id !in 1..10 && !cells[id - 10].hasShot()) {
                idOfCellsToOpen.add(id - 10)
                nextCellWasCalledFromHorizontal = false
            }

            //under
            if (id !in 91..100 && !cells[id + 10].hasShot()) {
                idOfCellsToOpen.add(id + 10)
                nextCellWasCalledFromHorizontal = false
            }

            //if first -> then check only vertical
            return
        }
    }

    fun tryToKnowOrientation(cells: Array<Cell>) {
        if (cells[idOfCellsToOpen[0]].hasShip()) {
            shipOrientation = nextCellWasCalledFromHorizontal
        }
    }
}