package com.example.seaBattle.core

import android.util.Log

class BotLogic {

    private val size: Int

    val closedCells: MutableSet<Int> = mutableSetOf()

    init {
        val field = Field()
        size = field.getSizeOfField()

        for (i in 1..size * size) {
            closedCells.add(i)
        }
    }

    //consists id of cells, that can has ship (first cell to open we choose by random)
    val idOfCellsToOpen = mutableListOf((1..size * size).random())

    //remember it to know ship orientation
    var lastIdWithShip: Int? = null

    //null -> we don't know orientation, true -> horizontal, false -> vertical
    var shipOrientation: Boolean? = null

    fun getIdOfNextCell(): Int? {
        if (idOfCellsToOpen.isEmpty()) return null
        val result = idOfCellsToOpen.random()
        idOfCellsToOpen.remove(result)
        closedCells.remove(result)
        return result
    }

    fun chooseRandomCell() {
        idOfCellsToOpen.add(closedCells.random())
    }

    /*
    Add unopened cells around wounded cell in idOfCellsToOpen ("id" - id of wounded cell)
    These cells can contain ship -> bot will open them later
     */
    fun rememberCellsThatCanHasShip(id: Int, field: Field) {

        val cells = field.cells
        val size = field.getSizeOfField()

        //horizontal or null
        if (shipOrientation != false) {
            Log.d("getCellsAroundWounded", "horizontal or null")

            //right
            if (id % size != 0 && !cells[id].hasShot()) {
                Log.d("getCellsAroundWounded", "right")
                idOfCellsToOpen.add(id + 1)
            }

            //left
            if ((id % size != 1) && !cells[id - 2].hasShot()) {
                Log.d("getCellsAroundWounded", "left")
                idOfCellsToOpen.add(id - 1)
            }
        }

        //vertical or null
        if (shipOrientation != true) {
            Log.d("getCellsAroundWounded", "vertical or null")

            //above
            if (id !in 1..size && !cells[id - size - 1].hasShot()) {
                Log.d("getCellsAroundWounded", "above")
                idOfCellsToOpen.add(id - size)
            }

            //under
            if (id !in size * size - size + 1..size * size && !cells[id + size - 1].hasShot()) {
                Log.d("getCellsAroundWounded", "under")
                idOfCellsToOpen.add(id + size)
            }

            //if first -> then check only vertical
            return
        }
    }

    fun knowOrientation(id: Int) {
        Log.d("knowOrientation", "lastIdWithShip = $lastIdWithShip, shipOrientation = ?")

        if (lastIdWithShip != null) {
            //if orientation is horizontal => true, else => false
            shipOrientation = (id == lastIdWithShip!! + 1) || (id == lastIdWithShip!! - 1)
            Log.d("knowOrientation", "shipOrientation = $shipOrientation")

            //remove empty cells, that we added in "getUnopenedCellsAroundWoundedCell"
            if (shipOrientation!!) {
                val field = Field()
                val size = field.getSizeOfField()
                idOfCellsToOpen.remove(lastIdWithShip!! - size)
                idOfCellsToOpen.remove(lastIdWithShip!! + size)

            } else {
                idOfCellsToOpen.remove(lastIdWithShip!! - 1)
                idOfCellsToOpen.remove(lastIdWithShip!! + 1)
            }
            Log.d("openCell", "idOfCellsToOpen = $idOfCellsToOpen")
        }

    }
}