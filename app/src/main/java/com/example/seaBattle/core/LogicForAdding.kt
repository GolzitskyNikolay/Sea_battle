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
        field: Field
    ): Boolean {

        val cells = field.cells

        if (kindOfShip == 1) return checkCellsAroundCell(id, field)

        // for horizontal orientation we will check horizontal neighbours (id + 1, id + 2, id + 3)
        // for vertical -> vertical (id + size, id + 2*size, id + 3*size)
        val needToAdd: Int? = if (orientation) {
            1
        } else {
            field.getSizeOfField()
        }

        //for ship_2, ship_3, ship_4
        if (!checkCellsAroundCell(id, field)) return false
        if (!checkCellsAroundCell(id + 1 * needToAdd!!, field)) return false

        if (kindOfShip == 3 || kindOfShip == 4) {
            if (!checkCellsAroundCell(id + 2 * needToAdd, field)) return false
        }

        if (kindOfShip == 4) {
            if (!checkCellsAroundCell(id + 3 * needToAdd, field)) return false
        }

        return true
    }

    /*
    If around cell stay other ships -> false, else -> true
    */
    private fun checkCellsAroundCell(
        idOfCurrentCell: Int,
        field: Field
    ): Boolean {

        val cells = field.cells
        val size = field.getSizeOfField()

        if (idOfCurrentCell !in 1..size * size) return false

        //above and left
        if ((idOfCurrentCell > size) && (idOfCurrentCell % size != 1)
            && (cells[idOfCurrentCell - size - 2].hasShip())
        ) return false

        //above
        if ((idOfCurrentCell > size) && (cells[idOfCurrentCell - size - 1].hasShip())
        ) return false

        //above and right
        if ((idOfCurrentCell > size) && (idOfCurrentCell % size != 0)
            && (cells[idOfCurrentCell - size].hasShip())
        ) return false

        //left
        if ((idOfCurrentCell % size != 1) && (cells[idOfCurrentCell - 2].hasShip())
        ) return false

        //right
        if ((idOfCurrentCell % size != 0) && (cells[idOfCurrentCell].hasShip())
        ) return false

        //under and left
        if ((idOfCurrentCell < size * size - size + 1) && (idOfCurrentCell % size != 1)
            && (cells[idOfCurrentCell + size - 2].hasShip())
        ) return false

        //under
        if ((idOfCurrentCell < size * size - size + 1) &&
            (cells[idOfCurrentCell + size - 1].hasShip())
        ) return false

        //under and right
        if ((idOfCurrentCell < size * size - size + 1) && (idOfCurrentCell % size != 0)
            && (cells[idOfCurrentCell + size].hasShip())
        ) return false

        return true
    }

}