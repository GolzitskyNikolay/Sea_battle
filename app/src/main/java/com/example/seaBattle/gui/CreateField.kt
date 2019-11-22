package com.example.seaBattle.gui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.seaBattle.R
import com.example.seaBattle.core.Field
import com.example.seaBattle.core.LogicForAdding
import kotlinx.android.synthetic.main.creating_field.*

class CreateField : AppCompatActivity(),
    View.OnDragListener, View.OnTouchListener, View.OnClickListener {

    private var field: Field = Field()

    //has ships that were added
    private val historyOfAdding = mutableListOf<List<Int>>()

    //has cells that can has ship
    private var cellsWithGoodMove = mutableListOf<Int>()

    //has cells that can't has ship
    private var cellsWithBadMove = mutableListOf<Int>()

    private var ship2IsHorizontal = true
    private var ship3IsHorizontal = true
    private var ship4IsHorizontal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creating_field)

        field.createEmptyField(field, gridLayout_field, this)

        addListeners()
    }

    private fun addListeners() {

        // we can move ships from these ones to field
        ship_1.setOnTouchListener(this)
        ship_2.setOnTouchListener(this)
        ship_3.setOnTouchListener(this)
        ship_4.setOnTouchListener(this)

        rotate_2.setOnClickListener(this)
        rotate_3.setOnClickListener(this)
        rotate_4.setOnClickListener(this)

        cancel_action.setOnClickListener {
            cancelLastAction()
        }

        //cells will catch ships
        for (cell in field.cells) {
            cell.setOnDragListener(this)
        }

        //if user won't add ship
        layout_in_creating_field.setOnDragListener(this)
    }

    /*
    Delete last ship from field
     */
    private fun cancelLastAction() {
        if (historyOfAdding.isNotEmpty()) {
            val lastShip = historyOfAdding[historyOfAdding.size - 1]
            historyOfAdding.remove(lastShip)

            lastShip.forEach { id ->
                field.cells[id - 1].setHasShip()
                field.cells[id - 1].background = getDrawable(R.drawable.button_background)
                field.ships.remove(id.toString())
            }

            updateText(null, lastShip.size)

            //if user suddenly decides cancel last action -> hide start button
            if (start.visibility == View.VISIBLE) {
                start.visibility = View.INVISIBLE
            }

            field.currentCountOfShips--

            when (lastShip.size) { // show ship again
                4 -> {
                    ship_4.visibility = View.VISIBLE
                    field.countOfFourDeck--
                }
                3 -> {
                    ship_3.visibility = View.VISIBLE
                    field.countOfThreeDeck--
                }
                2 -> {
                    ship_2.visibility = View.VISIBLE
                    field.countOfDoubleDeck--
                }
                1 -> {
                    ship_1.visibility = View.VISIBLE
                    field.countOfSingleDeck--
                }
            }
        }
    }

    // when user clicks buttons -> change orientation of ship
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.rotate_2 -> {
                    if (ship_2.visibility != View.INVISIBLE) {
                        ship2IsHorizontal = changeShipOrientation(
                            R.id.ship_2, R.drawable.ship_2,
                            R.drawable.ship_2_vertical, 2
                        )
                    }
                }
                R.id.rotate_3 -> {
                    if (ship_3.visibility != View.INVISIBLE) {
                        ship3IsHorizontal = changeShipOrientation(
                            R.id.ship_3, R.drawable.ship_3,
                            R.drawable.ship_3_vertical, 3
                        )
                    }
                }
                R.id.rotate_4 -> {
                    if (ship_4.visibility != View.INVISIBLE) {
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

    /*
    When user touches the ship -> the ship begins to move
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        Log.d("creatingField", "onTouch")
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

        // user moves ship to field (dropped ship on the field)
        if (container != layout_in_creating_field) {

            when (dragEvent.action) {


                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d("creatingField", "ACTION_DRAG_ENTERED")

                    //if user can add ship -> cells will become green, else - red
                    showCanUserAddShipOrNot(view, container)
                }


                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d("creatingField", "ACTION_DRAG_EXITED")

                    //when user moved ship to other place
                    redrawCellsBack()
                }


                DragEvent.ACTION_DROP -> {
                    Log.d("creatingField", "ACTION_DROP (on the field)")

                    addShipIfCan(view)

                    //if user dropped ship, when we can't add this one
                    returnShipBackIfCantAdd()

                    //hide ship type if all ships of this type are added to the field
                    hideShipTypeIfNecessary(view)

                    // user added all ships, we can begin our game
                    if (field.currentCountOfShips == field.maxCountOfShips) {
                        startGame()
                    }
                }
            }

            // user didn't add ship to our field (dropped ship out of the field)
        } else if (dragEvent.action == DragEvent.ACTION_DROP) {

            Log.d("creatingField", "ACTION_DROP (out of the field)")
            view.visibility = View.VISIBLE
        }
        return true
    }

    private fun addShipIfCan(view: View) {

        if (cellsWithGoodMove.isNotEmpty()) {
            updateText(view, null)

            val ship = mutableListOf<Int>()

            for (id in cellsWithGoodMove) {
                field.cells[id - 1].background = getDrawable(R.drawable.ship_1)

                field.cells[id - 1].setHasShip()
                field.ships.add(id.toString())
                ship.add(id)
            }

            when (ship.size){
                4 ->{
                    field.countOfFourDeck++
                }
                3 ->{
                    field.countOfThreeDeck++
                }
                2 ->{
                    field.countOfDoubleDeck++
                }
                1 ->{
                    field.countOfSingleDeck++
                }
            }
            historyOfAdding.add(ship)
            field.currentCountOfShips++
        }
        cellsWithGoodMove.clear()
    }

    /*
     If user dropped ship, when we can't add this one (dropped ship on the field)
     */
    private fun returnShipBackIfCantAdd() {
        for (id in cellsWithBadMove) {
            if (field.cells[id - 1].hasShip()) {
                field.cells[id - 1].background = getDrawable(R.drawable.ship_1)
            } else {
                field.cells[id - 1].background =
                    getDrawable(R.drawable.button_background)
            }

        }

        cellsWithBadMove.clear()
    }

    /*
    Hide ship type if all ships of this type are added to the field
     */
    private fun hideShipTypeIfNecessary(view: View) {
        // if user can add this kind of ships -> make ship visible (on left panel),
        // else -> user can't use this kind of ships again
        if ((view.id == R.id.ship_1 &&
                    field.countOfSingleDeck < field.maxCountOfSingleDeck) ||
            (view.id == R.id.ship_2 &&
                    field.countOfDoubleDeck < field.maxCountOfDoubleDeck) ||
            (view.id == R.id.ship_3 &&
                    field.countOfThreeDeck < field.maxCountOfThreeDeck) ||
            (view.id == R.id.ship_4 &&
                    field.countOfFourDeck < field.maxCountOfFourDeck)
        ) {
            view.visibility = View.VISIBLE
        }
    }


    // if user can add ship here -> cells will become green, else - red
    private fun showCanUserAddShipOrNot(view: View, container: View?) {

        val logicForAdding = LogicForAdding()

        when (view.id) {
            R.id.ship_1 -> {
                field.addInListCellsToRedraw(
                    container!!.id, logicForAdding, 1, true, field, false,
                    cellsWithGoodMove, cellsWithBadMove
                )
            }
            R.id.ship_2 -> {
                field.addInListCellsToRedraw(
                    container!!.id, logicForAdding, 2, ship2IsHorizontal, field, false,
                    cellsWithGoodMove, cellsWithBadMove
                )
            }
            R.id.ship_3 -> {
                field.addInListCellsToRedraw(
                    container!!.id, logicForAdding, 3, ship3IsHorizontal, field, false,
                    cellsWithGoodMove, cellsWithBadMove
                )
            }
            R.id.ship_4 -> {
                field.addInListCellsToRedraw(
                    container!!.id, logicForAdding, 4, ship4IsHorizontal, field, false,
                    cellsWithGoodMove, cellsWithBadMove
                )
            }
        }

        for (e in cellsWithGoodMove) {
            field.cells[e - 1].background =
                getDrawable(R.drawable.button_good_move)
        }

        for (e in cellsWithBadMove) {
            field.cells[e - 1].background =
                getDrawable(R.drawable.button_bad_move)
        }

    }

    //when user moved ship to other place (when ship is dropped)
    private fun redrawCellsBack() {
        val allSelectedCells = cellsWithGoodMove
        allSelectedCells.addAll(cellsWithBadMove)
        for (id in allSelectedCells) {
            if (field.cells[id - 1].hasShip()) {
                field.cells[id - 1].background = getDrawable(R.drawable.ship_1)
            } else {
                field.cells[id - 1].background =
                    getDrawable(R.drawable.button_background)
            }
        }
        cellsWithBadMove.clear()
        cellsWithGoodMove.clear()
    }

    private fun startGame() {
        start.visibility = View.VISIBLE

        start.setOnClickListener {

            val preferences = PreferenceManager.getDefaultSharedPreferences(this)

            val editor = preferences.edit()
            editor.putStringSet("cells with ship", field.ships)
            editor.apply()

            startActivity(Intent(this, Play::class.java))
        }
    }

    /*
    Update count of ships.
    "sizeOfRemovingShip" == null, when user added ship
     */
    private fun updateText(view: View?, sizeOfRemovingShip: Int?) {
        var textView: TextView? = null
        val newText = StringBuilder()

        // when user added ship
        if (view != null) {

            when (view.id) {
                R.id.ship_1 -> {
                    textView = text_1
                    newText.append(field.countOfSingleDeck)
                }
                R.id.ship_2 -> {
                    textView = text_2
                    newText.append(field.countOfDoubleDeck)
                }
                R.id.ship_3 -> {
                    textView = text_3
                    newText.append(field.countOfThreeDeck)
                }
                R.id.ship_4 -> {
                    textView = text_4
                    newText.append(field.countOfFourDeck)
                }
            }

            // when user deleted ship, canceled his last action
        } else {
            when (sizeOfRemovingShip) {
                4 -> {
                    textView = text_4
                    newText.append(field.countOfFourDeck)
                }

                3 -> {
                    textView = text_3
                    newText.append(field.countOfThreeDeck)
                }

                2 -> {
                    textView = text_2
                    newText.append(field.countOfDoubleDeck)
                }

                1 -> {
                    textView = text_1
                    newText.append(field.countOfSingleDeck)
                }
            }
        }

        val list = textView!!.text.split(" ")
        newText.append(" ").append(list[1]).append(" ").append(list[2])
        textView.text = newText
    }
}