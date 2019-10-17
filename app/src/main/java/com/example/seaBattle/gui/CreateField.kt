package com.example.seaBattle.gui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.Cell
import com.example.seaBattle.core.Field
import com.example.seaBattle.core.LogicForAdding

class CreateField : AppCompatActivity(),
    View.OnDragListener, View.OnTouchListener, View.OnClickListener {

    private var field: Field? = null
    private var cellsWithGoodMove = mutableListOf<Int>()
    private var cellsWithBadMove = mutableListOf<Int>()
    private var ship2IsHorizontal = true
    private var ship3IsHorizontal = true
    private var ship4IsHorizontal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid)

        fillField()

        addListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListeners() {

        val ship1 = findViewById<ImageView>(R.id.ship_1)
        val ship2 = findViewById<ImageView>(R.id.ship_2)
        val ship3 = findViewById<ImageView>(R.id.ship_3)
        val ship4 = findViewById<ImageView>(R.id.ship_4)

        // we can move ships from field to these ones
        ship1.setOnDragListener(this)
        ship2.setOnDragListener(this)
        ship3.setOnDragListener(this)
        ship4.setOnDragListener(this)

        // we can move ships from these ones to field
        ship1.setOnTouchListener(this)
        ship2.setOnTouchListener(this)
        ship3.setOnTouchListener(this)
        ship4.setOnTouchListener(this)

        val button2 = findViewById<Button>(R.id.rotate_2)
        val button3 = findViewById<Button>(R.id.rotate_3)
        val button4 = findViewById<Button>(R.id.rotate_4)

        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)
    }

    private fun fillField(){
        field = Field(this)
        val size = field!!.sizeOfField
        val layout = findViewById<GridLayout>(R.id.gridLayout_field)

        for (i in 1..size * size) {

            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            )
            params.height = 0
            params.width = 0


            val cell = Cell(this)
            cell.setBackgroundResource(R.drawable.button_background)
            cell.layoutParams = params
            cell.id = i
            cell.setOnDragListener(this)

            field!!.cells[i - 1] = cell
            layout.addView(cell)

        }
    }

    // changeShipOrientation ship
    override fun onClick(v: View?) {
        if (v != null) {
            when {

                v.id == R.id.rotate_2 -> {
                    if (findViewById<ImageView>(R.id.ship_2) != null) {
                        ship2IsHorizontal = changeShipOrientation(
                            R.id.ship_2, R.drawable.ship_2,
                            R.drawable.ship_2_vertical, 2
                        )
                    }
                }

                v.id == R.id.rotate_3 -> {
                    if (findViewById<ImageView>(R.id.ship_3) != null) {
                        ship3IsHorizontal = changeShipOrientation(
                            R.id.ship_3, R.drawable.ship_3,
                            R.drawable.ship_3_vertical, 3
                        )
                    }
                }

                v.id == R.id.rotate_4 -> {
                    if (findViewById<ImageView>(R.id.ship_4) != null) {
                        ship4IsHorizontal = changeShipOrientation(
                            R.id.ship_4, R.drawable.ship_4,
                            R.drawable.ship_4_vertical, 4
                        )
                    }
                }
            }
        }
    }

    private fun changeShipOrientation(
        id: Int, horizontal: Int,
        vertical: Int, ship: Int
    ): Boolean {

        val view = findViewById<ImageView>(id)
        var shipIsHorizontal: Boolean? = null

        when (ship) {
            2 -> {
                shipIsHorizontal = ship2IsHorizontal
            }
            3 -> {
                shipIsHorizontal = ship3IsHorizontal
            }
            4 -> {
                shipIsHorizontal = ship4IsHorizontal
            }
        }

        return if (shipIsHorizontal!!) {
            view.setImageResource(vertical)
            false
        } else {
            view.setImageResource(horizontal)
            true
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        return if (motionEvent!!.action == MotionEvent.ACTION_DOWN) {
            val dsb = View.DragShadowBuilder(view)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view!!.startDragAndDrop(null, dsb, view, 0)
            } else {
                view!!.startDrag(null, dsb, view, 0)
            }
            view.visibility = View.INVISIBLE
            true
        } else {
            false
        }
    }

    private fun redrawFromTo(
        id: Int, list: MutableList<Int>,
        isGoodMove: Boolean, ship: Int, orientation: Boolean
    ) {

        //redraw cells horizontally
        if (orientation) {
            for (e in id..id - 1 + ship) {
                list.add(e + 1)
            }
        } else {
            list.add(id + 1)
            list.add(id + 11)
            if (ship == 3 || ship == 4) {
                list.add(id + 21)
            }
            if (ship == 4) {
                list.add(id + 31)
            }

        }
        for (e in list) {
            if (isGoodMove) {
                field!!.cells[e - 1].background =
                    getDrawable(R.drawable.button_good_move)
            } else {
                field!!.cells[e - 1].background =
                    getDrawable(R.drawable.button_bad_move)
            }
        }
    }

    /*
    If we can add ship, then it's good move -> green cells,
    else bad move -> red cells.
    */
    private fun knowBadOrGoodMoveAndRedrawCells(
        id: Int, logic: LogicForAdding,
        ship: Int, orientation: Boolean
    ) {
        if (!field!!.cells[id - 1].hasShip() &&
            logic.checkBeforeAdding(ship, id, orientation, field!!.cells)
        ) {
            redrawFromTo(id - 1, cellsWithGoodMove, true, ship, orientation)

        } else {
            redrawFromTo(id - 1, cellsWithBadMove, false, ship, orientation)
        }

    }

    private fun redrawCells(id: Int, logic: LogicForAdding, ship: Int, orientation: Boolean) {

        // horizontal ship
        if (orientation) {
            // if there is enough space, then we place the ship
            // from the current cell to the right
            if (ship == 1 || (id % 10 != 0 && ship == 2)
                || (id % 10 != 0 && id % 10 != 9 && ship == 3)
                || (id % 10 != 0 && id % 10 != 9 && id % 10 != 8 && ship == 4)
            ) {
                knowBadOrGoodMoveAndRedrawCells(id, logic, ship, orientation)

            } else {
                //place the ship to the left
                if (id % 10 == 0 && ship in 2..4) {
                    knowBadOrGoodMoveAndRedrawCells(id - ship + 1, logic, ship, orientation)

                } else if (id % 10 == 9 && ship in 3..4) {
                    knowBadOrGoodMoveAndRedrawCells(id - ship + 2, logic, ship, orientation)

                } else if (ship == 4) {
                    knowBadOrGoodMoveAndRedrawCells(id - ship + 3, logic, ship, orientation)
                }
            }
        } else {

            // if there is enough space, then we place the ship
            // from the current cell to the bottom
            if ((ship == 2 && ((id / 10 !in 9..10) || (id / 10 == 9 && id % 10 == 0))) ||
                (ship == 3 && ((id / 10 !in 8..10) || (id / 10 == 8 && id % 10 == 0))) ||
                (ship == 4 && ((id / 10 !in 7..10) || (id / 10 == 7 && id % 10 == 0)))
            ) {
                knowBadOrGoodMoveAndRedrawCells(id, logic, ship, orientation)
            } else if ((id / 10 == 9 || id == 100) && ship in 2..4 && id != 90) {
                knowBadOrGoodMoveAndRedrawCells(id - (ship - 1) * 10, logic, ship, orientation)
            } else if ((id / 10 == 8 || id == 90) && ship in 3..4 && id != 80) {
                knowBadOrGoodMoveAndRedrawCells(id - (ship - 2) * 10, logic, ship, orientation)
            } else if (((id / 10 == 7) || (id == 80)) && id != 70 && ship == 4) {
                knowBadOrGoodMoveAndRedrawCells(id - (ship - 3) * 10, logic, ship, orientation)
            }

        }

    }


    override fun onDrag(container: View?, dragEvent: DragEvent?): Boolean {
        val view = dragEvent!!.localState as View
        val logicForAdding = LogicForAdding()

        when (dragEvent.action) {

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("onDrag", "ACTION_DRAG_ENTERED")
                when {
                    view.id == R.id.ship_1 -> {
                        redrawCells(container!!.id, logicForAdding, 1, true)
                    }

                    view.id == R.id.ship_2 -> {
                        redrawCells(container!!.id, logicForAdding, 2, ship2IsHorizontal)
                    }

                    view.id == R.id.ship_3 -> {
                        redrawCells(container!!.id, logicForAdding, 3, ship3IsHorizontal)
                    }

                    view.id == R.id.ship_4 -> {
                        redrawCells(container!!.id, logicForAdding, 4, ship4IsHorizontal)
                    }
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("onDrag", "ACTION_DRAG_EXITED")

                val allSelectedCells = cellsWithGoodMove
                allSelectedCells.addAll(cellsWithBadMove)
                for (id in allSelectedCells) {
                    if (field!!.cells[id - 1].hasShip()) {
                        field!!.cells[id - 1].background = getDrawable(R.drawable.ship_1)
                    } else {
                        field!!.cells[id - 1].background =
                            getDrawable(R.drawable.button_background)
                    }
                }
                cellsWithBadMove.clear()
                cellsWithGoodMove.clear()
            }

            DragEvent.ACTION_DROP -> {
                Log.d("onDrag", "ACTION_DROP")

                view.visibility = View.VISIBLE
                val owner = view.parent as ViewGroup
                owner.removeView(view)

                if (cellsWithGoodMove.isNotEmpty()) {
                    updateText(view)

                    for (id in cellsWithGoodMove) {
                        field!!.cells[id - 1].background = getDrawable(R.drawable.ship_1)
                        field!!.cells[id - 1].setHasShip()
                    }
                }

                for (id in cellsWithBadMove) {
                    if (field!!.cells[id - 1].hasShip()) {
                        field!!.cells[id - 1].background = getDrawable(R.drawable.ship_1)
                    } else {
                        field!!.cells[id - 1].background =
                            getDrawable(R.drawable.button_background)
                    }

                }

                cellsWithBadMove.clear()
                cellsWithGoodMove.clear()

                if ((view.id == R.id.ship_1 &&
                            field!!.countOfSingleDeck < field!!.maxCountOfSingleDeck) ||
                    (view.id == R.id.ship_2 &&
                            field!!.countOfDoubleDeck < field!!.maxCountOfDoubleDeck) ||
                    (view.id == R.id.ship_3 &&
                            field!!.countOfThreeDeck < field!!.maxCountOfThreeDeck) ||
                    (view.id == R.id.ship_4 &&
                            field!!.countOfFourDeck < field!!.maxCountOfFourDeck)
                ) {
                    owner.addView(view)
                }
            }
        }
        return true
    }

    //update count of ships
    private fun updateText(view: View?) {
        var textView: TextView? = null
        val newText = StringBuilder()

        if (view != null) {
            when {
                view.id == R.id.ship_1 -> {
                    field!!.countOfSingleDeck++
                    textView = findViewById(R.id.text_1)
                    newText.append(field!!.countOfSingleDeck)
                }

                view.id == R.id.ship_2 -> {
                    field!!.countOfDoubleDeck++
                    textView = findViewById(R.id.text_2)
                    newText.append(field!!.countOfDoubleDeck)
                }

                view.id == R.id.ship_3 -> {
                    field!!.countOfThreeDeck++
                    textView = findViewById(R.id.text_3)
                    newText.append(field!!.countOfThreeDeck)
                }

                view.id == R.id.ship_4 -> {
                    field!!.countOfFourDeck++
                    textView = findViewById(R.id.text_4)
                    newText.append(field!!.countOfFourDeck)
                }
            }
        }

        val list = textView!!.text.split(" ")
        newText.append(" ").append(list[1]).append(" ").append(list[2])
        textView.text = newText
    }
}