package id.psw.qsbadapple

import android.service.quicksettings.TileService

open class PaddingQsService(private val title:String, private val desc:String) : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        qsTile.subtitle = desc
        qsTile.label = title
        qsTile.updateTile()
    }
}