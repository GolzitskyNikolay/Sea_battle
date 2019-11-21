package com.example.seaBattle.core

import android.util.Log

class BotLogic {

    //consists id of cells, that can has ship (first cell to open we choose by random)
    val idOfCellsToOpen = mutableListOf<Int>(43)

    val closedCells: MutableSet<Int> = mutableSetOf()

    init {
        for (i in 1..100) {
            closedCells.add(i)
        }
    }

    //remember it to know ship orientation
    var lastIdWithShip: Int? = null

    //null -> we don't know orientation, true -> horizontal, false -> vertical
    var shipOrientation: Boolean? = null

    fun getIdOfNextCell(): Int? {
        if (idOfCellsToOpen.isEmpty()) return null
        val result = idOfCellsToOpen[0]
        idOfCellsToOpen.remove(result)
        closedCells.remove(result)
        return result
    }

    fun chooseRandomCell() {
        idOfCellsToOpen.add(closedCells.random())
    }

    fun getUnopenedCellsAroundWoundedCell(id: Int, cells: Array<Cell>) {

        //horizontal or null
        if (shipOrientation != false) {
            Log.d("getCellsAroundWounded", "horizontal or null")

            //right
            if (id % 10 in 1..9 && !cells[id].hasShot()) {
                Log.d("getCellsAroundWounded", "right")
                idOfCellsToOpen.add(id + 1)
            }

            //left
            if ((id % 10 in 2..9 || id % 10 == 0) && !cells[id - 2].hasShot()) {
                Log.d("getCellsAroundWounded", "left")
                idOfCellsToOpen.add(id - 1)
            }
        }

        //vertical or null
        if (shipOrientation != true) {
            Log.d("getCellsAroundWounded", "vertical or null")

            //above
            if (id !in 1..10 && !cells[id - 10].hasShot()) {
                Log.d("getCellsAroundWounded", "above")
                idOfCellsToOpen.add(id - 10)
            }

            //under
            if (id !in 91..100 && !cells[id + 10].hasShot()) {
                Log.d("getCellsAroundWounded", "under")
                idOfCellsToOpen.add(id + 10)
            }

            //if first -> then check only vertical
            return
        }
    }

    fun knowOrientation(cells: Array<Cell>, id: Int) {
        Log.d("knowOrientation", "lastIdWithShip = $lastIdWithShip, shipOrientation = ?")

        if (lastIdWithShip != null) {
            //if orientation is horizontal => true, else => false
            shipOrientation = (id == lastIdWithShip!! + 1) || (id == lastIdWithShip!! - 1)
            Log.d("knowOrientation", "shipOrientation = $shipOrientation")

            //remove empty cells, that we added in "getUnopenedCellsAroundWoundedCell"
            if (shipOrientation!!) {
                idOfCellsToOpen.remove(lastIdWithShip!! - 10)
                idOfCellsToOpen.remove(lastIdWithShip!! + 10)

            } else {
                idOfCellsToOpen.remove(lastIdWithShip!! - 1)
                idOfCellsToOpen.remove(lastIdWithShip!! + 1)
            }
            Log.d("openCell", "idOfCellsToOpen = $idOfCellsToOpen")
        }

    }
}