package id.psw.qsbadapple

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

open class DisplayTileService(private val x:Int, private val y:Int) : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val app = applicationContext
        if(app is App){
            app.registerDisplay(this, x, y)
        }

        onEnd()
    }

    fun onEnd(){
        qsTile.label = "Tile"

        if(x == 0 && y == 0){
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.subtitle = "Play"
        }else if(x == 1 && y == 0){
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.subtitle = "Stop (when playing)"
        }
        else
        {
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.subtitle = "($x, $y)"
        }

        qsTile.updateTile()
    }

    fun setData(active:Boolean, line1:String, line2:String){
        qsTile.label = line1
        qsTile.subtitle = line2
        qsTile.state = if(active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        val app = applicationContext
        if(app is App){
            if(x == 0 && y == 0){
                app.playPauseToggle()
            }else if(x== 1 && y == 0){
                app.resetPlayer()
            }
        }
    }
}