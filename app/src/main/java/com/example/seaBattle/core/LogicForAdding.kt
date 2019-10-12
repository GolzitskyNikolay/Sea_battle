package com.example.seaBattle.core

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

        //horizontal orientation
        if (orientation) {
            if (kindOfShip == 2) {
                if (!checkCellsAroundCell(id, cells)) return false
                if (!checkCellsAroundCell(id + 1, cells)) return false
            } else if (kindOfShip == 3) {
                if (!checkCellsAroundCell(id, cells)) return false
                if (!checkCellsAroundCell(id + 1, cells)) return false
                if (!checkCellsAroundCell(id + 2, cells)) return false
            } else if (kindOfShip == 4) {
                if (!checkCellsAroundCell(id, cells)) return false
                if (!checkCellsAroundCell(id + 1, cells)) return false
                if (!checkCellsAroundCell(id + 2, cells)) return false
                if (!checkCellsAroundCell(id + 3, cells)) return false
            }
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