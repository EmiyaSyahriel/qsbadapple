package id.psw.qsbadapple

import android.app.Activity
import android.app.AlertDialog
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Bundle
import android.service.quicksettings.TileService
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import java.util.concurrent.Executor

class ArrangerActivity : Activity() {
    private lateinit var dataCount : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.action == TileService.ACTION_QS_TILE_PREFERENCES){
            (application as App).resetPlayer()
        }else{
            dataCount = EditText(this)
            dataCount.inputType = InputType.TYPE_CLASS_NUMBER
            val dlg = AlertDialog.Builder(this)
            dlg.setTitle("How many paddings?")
            dlg.setView(dataCount)
            dlg.setPositiveButton("Arrange"){ d, _ ->
                doPlotting(dataCount.text.toString().toInt())
                d.dismiss()
            }
            dlg.setNegativeButton("Exit"){ d,_->
                d.cancel()
                finish()
            }
            dlg.create().show()
        }
    }

    private fun doPlotting(paddingCount: Int) {
        val sbm = getSystemService(StatusBarManager::class.java)
        var p = paddingCount
        var x = 0
        var y = 0
        var rq : (Int) -> Unit = {}
        val exec = Executor {
            runOnUiThread {
                it.run()
            }
        }

        rq = { status:Int ->
            if(status != StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED){
                Toast.makeText(applicationContext, "Cannot add tile, exiting ...", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            if(p > 0){
                sbm.requestAddTileService(
                    ComponentName(this, "id.psw.qsbadapple.PaddingService$p"),
                    "Bad Apple Padding Tile ($p)",
                    Icon.createWithResource(this, R.drawable.ic_player),
                    exec,
                ){
                    rq(it)
                }
                p--
            }else if(x <= 4 && y < 4){
                if(x == 4){
                    y++
                    x = 0
                }
                sbm.requestAddTileService(
                    ComponentName(this, "id.psw.qsbadapple.DisplayTile$x$y"),
                    "Bad Apple Display Tile ($x, $y)",
                    Icon.createWithResource(this, R.drawable.ic_badapple),
                    exec,
                ){
                    rq(it)
                }
                x++
            }else
            {
                finish()
            }
        }

        rq(StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED)
    }
}