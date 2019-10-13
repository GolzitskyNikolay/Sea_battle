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

    private fun redrawUsualCells(
        from: Int, to: Int,
        listOfCells: MutableList<Int>, isGoodMove: Boolean
    ) {
        for (id in from..to) {
            listOfCells.add(id + 1)
            if (isGoodMove) {
                field!!.cells[id].background =
                    getDrawable(R.drawable.button_good_move)
            } else {
                field!!.cells[id].background =
                    getDrawable(R.drawable.button_bad_move)
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
                        if (!field!!.cells[container!!.id - 1].hasShip()
                            && logicForAdding.checkBeforeAdding(
                                1, container.id, true, field!!.cells
                            )
                        ) {
                            cellsWithGoodMove.add(container.id)
                            container.background = getDrawable(R.drawable.button_good_move)

                        } else if (!field!!.cells[container.id - 1].hasShip()) {
                            cellsWithBadMove.add(container.id)
                            container.background = getDrawable(R.drawable.button_bad_move)
                        }
                    }

                    view.id == R.id.ship_2 -> {
                        if (container!!.id % 10 != 0) {
                            if (logicForAdding.checkBeforeAdding(
                                    2, container.id, true, field!!.cells
                                ) && !field!!.cells[container.id - 1].hasShip()
                            ) {
                                redrawUsualCells(
                                    container.id - 1, container.id,
                                    cellsWithGoodMove, true
                                )

                            } else {
                                redrawUsualCells(
                                    container.id - 1, container.id,
                                    cellsWithBadMove, false
                                )
                            }

                        } else {
                            if (logicForAdding.checkBeforeAdding(
                                    2, container.id - 1, true, field!!.cells
                                ) && !field!!.cells[container.id - 2].hasShip()
                            ) {
                                redrawUsualCells(
                                    container.id - 2, container.id - 1,
                                    cellsWithGoodMove, true
                                )


                            } else {
                                redrawUsualCells(
                                    container.id - 2, container.id - 1,
                                    cellsWithBadMove, false
                                )
                            }
                        }

                    }

                    view.id == R.id.ship_3 -> {

                        if (container!!.id % 10 != 0 && container.id % 10 != 9) {
                            if (logicForAdding.checkBeforeAdding(
                                    3, container.id, true, field!!.cells
                                ) && !field!!.cells[container.id - 1].hasShip()
                            ) {
                                redrawUsualCells(
                                    container.id - 1, container.id + 1,
                                    cellsWithGoodMove, true
                                )

                            } else {
                                redrawUsualCells(
                                    container.id - 1, container.id + 1,
                                    cellsWithBadMove, false
                                )
                            }

                        } else {
                            if (container.id % 10 == 0) {
                                if (logicForAdding.checkBeforeAdding(
                                        3, container.id - 2, true, field!!.cells
                                    ) && !field!!.cells[container.id - 3].hasShip()
                                ) {
                                    redrawUsualCells(
                                        container.id - 3, container.id - 1,
                                        cellsWithGoodMove, true
                                    )


                                } else {
                                    redrawUsualCells(
                                        container.id - 3, container.id - 1,
                                        cellsWithBadMove, false
                                    )
                                }
                            } else {
                                if (logicForAdding.checkBeforeAdding(
                                        3, container.id - 1, true, field!!.cells
                                    ) && !field!!.cells[container.id - 2].hasShip()
                                ) {
                                    redrawUsualCells(
                                        container.id - 2, container.id,
                                        cellsWithGoodMove, true
                                    )


                                } else {
                                    redrawUsualCells(
                                        container.id - 2, container.id,
                                        cellsWithBadMove, false
                                    )
                                }
                            }
                        }


                    }

                    view.id == R.id.ship_4 -> {

                        if (container!!.id % 10 != 0 && container.id % 10 != 9
                            && container.id % 10 != 8
                        ) {

                            if (logicForAdding.checkBeforeAdding(
                                    4, container.id, true, field!!.cells
                                ) && !field!!.cells[container.id - 1].hasShip()
                            ) {

                                redrawUsualCells(
                                    container.id - 1, container.id + 2,
                                    cellsWithGoodMove, true
                                )

                            } else {
                                redrawUsualCells(
                                    container.id - 1, container.id + 2,
                                    cellsWithBadMove, false
                                )
                            }

                        } else {
                            if (container.id % 10 == 0) {
                                if (logicForAdding.checkBeforeAdding(
                                        4, container.id - 3, true, field!!.cells
                                    ) && !field!!.cells[container.id - 4].hasShip()
                                ) {
                                    redrawUsualCells(
                                        container.id - 4, container.id - 1,
                                        cellsWithGoodMove, true
                                    )


                                } else {
                                    redrawUsualCells(
                                        container.id - 4, container.id - 1,
                                        cellsWithBadMove, false
                                    )
                                }

                            } else if (container.id % 10 == 9) {
                                if (logicForAdding.checkBeforeAdding(
                                        4, container.id - 2, true, field!!.cells
                                    ) && !field!!.cells[container.id - 3].hasShip()
                                ) {
                                    redrawUsualCells(
                                        container.id - 3, container.id,
                                        cellsWithGoodMove, true
                                    )


                                } else {
                                    redrawUsualCells(
                                        container.id - 3, container.id,
                                        cellsWithBadMove, false
                                    )
                                }

                            } else {
                                if (logicForAdding.checkBeforeAdding(
                                        4, container.id - 1, true, field!!.cells
                                    ) && !field!!.cells[container.id - 2].hasShip()
                                ) {
                                    redrawUsualCells(
                                        container.id - 2, container.id + 1,
                                        cellsWithGoodMove, true
                                    )


                                } else {
                                    redrawUsualCells(
                                        container.id - 2, container.id + 1,
                                        cellsWithBadMove, false
                                    )
                                }
                            }
                        }

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

            DragEvent.ACTION_DRAG_ENDED -> Log.d("onDrag", "ACTION_DRAG_ENDED")

            DragEvent.ACTION_DROP -> {
                Log.d("onDrag", "ACTION_DROP")

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
                owner.addView(view)

            }
        }
        return true
    }
}