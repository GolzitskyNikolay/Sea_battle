package com.example.seaBattle.core

import android.util.Log

class LogicForAdding {

    /*
    "kindOfShip" is type of ship (1, 2, 3 or 4);
    "id" is id of Cell in our field;
    "orientation" is ship orientation (horizontal -> true, vertical -> false).
    */
    fun checkBeforeAdding(
        kindOfShip: Int, id: Int,
        orientation: Boolean,
        cells: Array<Cell>
    ): Boolean {

        if (kindOfShip == 1) return checkCellsAroundCell(id, cells)

        // for horizontal orientation we will check horizontal neighbours (id + 1, id + 2, id + 3)
        // for vertical -> vertical (id + 10, id + 20, id + 30)
        val needToAdd: Int? = if (orientation) {
            1
        } else {
            10
        }

        //for ship_2, ship_3, ship_4
        if (!checkCellsAroundCell(id, cells)) return false
        if (!checkCellsAroundCell(id + 1 * needToAdd!!, cells)) return false

        if (kindOfShip == 3 || kindOfShip == 4) {
            if (!checkCellsAroundCell(id + 2 * needToAdd, cells)) return false
        }

        if (kindOfShip == 4) {
            if (!checkCellsAroundCell(id + 3 * needToAdd, cells)) return false
        }

        return true
    }

    /*
    If around cell stay other ships -> false, else -> true
    */
    private fun checkCellsAroundCell(
        idOfCurrentCell: Int,
        cells: Array<Cell>
    ): Boolean {

        if (idOfCurrentCell !in 1..100) return false

        //above and left
        if ((idOfCurrentCell > 10) && (idOfCurrentCell % 10 != 1)
            && (cells[idOfCurrentCell - 12].hasShip())
        ) return false

        //above
        if ((idOfCurrentCell > 10) && (cells[idOfCurrentCell - 11].hasShip())
        ) return false

        //above and right
        if ((idOfCurrentCell > 10) && (idOfCurrentCell % 10 != 0)
            && (cells[idOfCurrentCell - 10].hasShip())
        ) return false

        //left
        if ((idOfCurrentCell % 10 != 1) && (cells[idOfCurrentCell - 2].hasShip())
        ) return false

        //right
        if ((idOfCurrentCell % 10 != 0) && (cells[idOfCurrentCell].hasShip())
        ) return false

        //under and left
        if ((idOfCurrentCell < 91) && (idOfCurrentCell % 10 != 1)
            && (cells[idOfCurrentCell + 8].hasShip())
        ) return false

        //under
        if ((idOfCurrentCell < 91) && (cells[idOfCurrentCell + 9].hasShip())
        ) return false

        //under and right
        if ((idOfCurrentCell < 91) && (idOfCurrentCell % 10 != 0)
            && (cells[idOfCurrentCell + 10].hasShip())
        ) return false

        return true
    }

}