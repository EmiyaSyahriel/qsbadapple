package id.psw.qsbadapple

import android.app.Application
import android.media.MediaPlayer
import java.io.DataInputStream
import java.io.File
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class App : Application() {
    private val displays = arrayOf<DisplayTileService?>(
        null, null, null, null,
        null, null, null, null,
        null, null, null, null,
        null, null, null, null
    )

    private lateinit var player : MediaPlayer
    private var isPlayerReady = false
    private lateinit var video : VideoData
    private lateinit var timer : Timer
    override fun onCreate() {
        super.onCreate()

        loadVideo()

        val fd = assets.open("audio.bad")
        val mps = File(dataDir, "audio.bad")
        if(mps.exists()) mps.delete()
        mps.createNewFile()

        val mpo = mps.outputStream()
        fd.transferTo(mpo)
        mpo.flush()
        mpo.close()
        fd.close()

        player = MediaPlayer()
        player.setDataSource(mps.absolutePath)
        player.setOnPreparedListener { isPlayerReady = true }
        player.prepareAsync()

        timer = Timer()
        timer.scheduleAtFixedRate(timerTask { updateDisplay() }, 0, 30)
    }

    private fun loadVideo(){
        val f = DataInputStream(assets.open("video.bad"))
        val frameCount = f.readInt()

        video = VideoData(frameCount, Array(frameCount){
            VideoData.FrameData(f.readShort(), Array(16){
                VideoData.FrameData.Tile(f.readInt(), f.readInt())
            })
        })
        f.close()
    }

    override fun onTerminate() {
        player.reset()
        player.release()
        timer.purge()
        super.onTerminate()
    }

    private fun updateDisplay(){
        if(player.isPlaying){
            val fi = ((player.currentPosition / 1000.0) * 30).roundToInt()
            val frame = video.frames[fi]
            val sbt = StringBuilder()
            val sbs = StringBuilder()
            for(i in 0 until 16){
                val dsp = displays[i] ?: continue // Skip Null Display

                val sshr = 15 - i
                val flag = ((frame.tileFlag.toInt() shr sshr) and 1 ) == 1

                sbt.clear()
                var t = frame.tiles[i].title
                if(flag) t = t.inv()

                for(c in 0 until 32){
                    if(c % 2 == 0) continue // Skip Evens since now using wide char
                    val ch = if((t shr (31 - c)) and 1 == 1) '█' else '　'
                    sbt.append(ch)
                }

                sbs.clear()
                var s = frame.tiles[i].subtitle
                if(flag) s = s.inv()
                for(c in 0 until 32){
                    if(c % 2 == 0) continue // Skip Evens since now using wide char
                    val ch = if((s shr (31 - c)) and 1 == 1) '█' else '　'
                    sbs.append(ch)
                }

                dsp.setData(flag, sbt.toString(), sbs.toString())
            }
        }
    }

    fun registerDisplay(tile: DisplayTileService, x: Int, y: Int) {
        val i = (y * 4) + x
        displays[i] = tile
    }

    private fun resetAll(){
        for(d in displays){
            d?.onEnd()
        }
    }

    fun playPauseToggle(){
        if(isPlayerReady){
            if(player.isPlaying){
                player.pause()
                resetAll()
            }else{
                player.start()
            }
        }
    }

    fun resetPlayer() {
        if(isPlayerReady){
            player.stop()
            isPlayerReady = false
            player.prepareAsync()
            resetAll()
        }
    }
}