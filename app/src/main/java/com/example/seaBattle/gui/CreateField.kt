package com.example.seaBattle.gui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.Cell
import com.example.seaBattle.core.Field
import com.example.seaBattle.core.LogicForAdding

class CreateField : AppCompatActivity(),
    View.OnDragListener, View.OnTouchListener {

    private var field: Field? = null
    private var cellsWithGoodMove = mutableListOf<Int>()
    private var cellsWithBadMove = mutableListOf<Int>()

    @SuppressLint("ResourceType", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid)

        val layout = findViewById<GridLayout>(R.id.gridLayout_field)

        field = Field(this)
        val size = field!!.sizeOfField

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

    private fun redrawFromTo(from: Int, to: Int, list: MutableList<Int>, isGoodMove: Boolean) {

        //redraw cells horizontally
        for (id in from..to) {
            list.add(id + 1)
            if (isGoodMove) {
                field!!.cells[id].background =
                    getDrawable(R.drawable.button_good_move)
            } else {
                field!!.cells[id].background =
                    getDrawable(R.drawable.button_bad_move)
            }
        }
    }

    /*
    If we can add ship, then it's good move -> green cells,
    else bad move -> red cells.
    */
    private fun knowBadOrGoodMoveAndRedrawCells(id: Int, logic: LogicForAdding, ship: Int) {
        if (!field!!.cells[id - 1].hasShip() &&
            logic.checkBeforeAdding(ship, id, true, field!!.cells)
        ) {
            redrawFromTo(id - 1, id - 2 + ship, cellsWithGoodMove, true)

        } else {
            redrawFromTo(id - 1, id - 2 + ship, cellsWithBadMove, false)
        }
    }

    private fun redrawCells(id: Int, logic: LogicForAdding, ship: Int) {

        // if there is enough space, then we place the ship
        // from the current cell to the right
        if (ship == 1 || (id % 10 != 0 && ship == 2)
            || (id % 10 != 0 && id % 10 != 9 && ship == 3)
            || (id % 10 != 0 && id % 10 != 9 && id % 10 != 8 && ship == 4)
        ) {
            knowBadOrGoodMoveAndRedrawCells(id, logic, ship)

        } else {
            //place the ship to the left
            if (id % 10 == 0 && ship in 2..4) {
                knowBadOrGoodMoveAndRedrawCells(id - ship + 1, logic, ship)

            } else if (id % 10 == 9 && ship in 3..4) {
                knowBadOrGoodMoveAndRedrawCells(id - ship + 2, logic, ship)

            } else if (ship == 4) {
                knowBadOrGoodMoveAndRedrawCells(id - ship + 3, logic, ship)
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
                        redrawCells(container!!.id, logicForAdding, 1)
                    }

                    view.id == R.id.ship_2 -> {
                        redrawCells(container!!.id, logicForAdding, 2)
                    }

                    view.id == R.id.ship_3 -> {
                        redrawCells(container!!.id, logicForAdding, 3)
                    }

                    view.id == R.id.ship_4 -> {
                        redrawCells(container!!.id, logicForAdding, 4)
                    }
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("onDrag", "ACTION_DRAG_EXITED")
                Log.d("DRAG_EXITED_good_start", cellsWithGoodMove.toString())
                Log.d("DRAG_EXITED_bad_start", cellsWithBadMove.toString())
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
                Log.d("ACTION_DROP_good_start", cellsWithGoodMove.toString())
                Log.d("ACTION_DROP_bad_start", cellsWithBadMove.toString())

                view.visibility = View.VISIBLE
                val owner = view.parent as ViewGroup
                owner.removeView(view)
                for (id in cellsWithGoodMove) {
                    field!!.cells[id - 1].background = getDrawable(R.drawable.ship_1)
                    field!!.cells[id - 1].setHasShip()
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
                owner.addView(view)

                Log.d("ACTION_DROP_good_end", cellsWithGoodMove.toString())
                Log.d("ACTION_DROP_bad_end", cellsWithBadMove.toString())

            }
        }
        return true
    }
}