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

class GenerateField : AppCompatActivity(),
    View.OnDragListener, View.OnTouchListener {

    private var field: Field? = null
    private var canAdd = false

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
                            container.background = getDrawable(R.drawable.button_good_move)
                            canAdd = true
                        } else if (!field!!.cells[container.id - 1].hasShip()) {
                            canAdd = false
                            container.background = getDrawable(R.drawable.button_bad_move)
                        }
                    }

                    view.id == R.id.ship_2 -> {

                    }

                    view.id == R.id.ship_3 -> {

                    }

                    view.id == R.id.ship_4 -> {

                    }
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("onDrag", "ACTION_DRAG_EXITED")
                if (field!!.cells[container!!.id - 1].hasShip()) {
                    container.background = getDrawable(R.drawable.ship_1)
                } else {
                    container.background = getDrawable(R.drawable.button_background)
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> Log.d("onDrag", "ACTION_DRAG_ENDED")

            DragEvent.ACTION_DROP -> {
                Log.d("onDrag", "ACTION_DROP")
                view.visibility = View.VISIBLE
                if (canAdd) {
                    val owner = view.parent as ViewGroup
                    owner.removeView(view)
                    container!!.background = getDrawable(R.drawable.ship_1)
                    field!!.cells[container.id - 1].setHasShip()
                    owner.addView(view)
                } else {
                    container!!.background = getDrawable(R.drawable.button_background)
                }
            }
        }
        return true
    }
}