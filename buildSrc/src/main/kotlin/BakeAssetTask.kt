import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.DataOutputStream
import java.io.File
import java.nio.ByteBuffer

abstract class BakeAssetTask: DefaultTask(){

    @get:InputFile
    var badApple : File? = null

    @get:OutputFile
    var audioFile : File? = null
    @get:OutputFile
    var videoFile : File? = null

    @TaskAction
    fun main(){
        val ba = badApple ?: throw IllegalArgumentException("Please set `badApple`!")
        if(!ba.exists()){
            throw IllegalArgumentException("Bad Apple file not found")
        }

        createVideo(ba)
        createAudio(ba)
    }

    private fun createAudio(ba: File){
        val af = audioFile ?: throw IllegalArgumentException("Please set `audioFile`!")
        println("Creating audio file ... ")
        val p = ProcessBuilder("ffmpeg",
            "-i", ba.absolutePath,
            "-ac", "1",
            "-ar", "22050",
            "-b:a", "32k",
            "-y",
            "-f", "adts",
            af.absolutePath
        )
        val ffmpeg = p.start()
        while(ffmpeg.isAlive){
            println("ffmpeg : ${ffmpeg.errorReader().readLine()}")
        }
        val e = ffmpeg.exitValue()
        if(e == 0){
            println("Audio file created ...")
        }else{
            ffmpegThrowError(ffmpeg)
        }
    }

    private fun createVideo(ba: File){
        val vf = videoFile ?: throw IllegalArgumentException("Please set `videoFile`!")
        val tf = File(temporaryDir, "video.bin")
        if(tf.exists()) tf.delete()
        println("Creating video file")
        val p = ProcessBuilder(
            "ffmpeg",
            "-i", ba.absolutePath,
            "-f", "rawvideo",
            "-y",
            "-an",
            "-pix_fmt", "gray",
            "-s", "128x8",
            tf.absolutePath)
        val ffmpeg = p.start()
        while(ffmpeg.isAlive){
            println("ffmpeg : ${ffmpeg.errorReader().readLine()}")
        }
        val e = ffmpeg.exitValue()
        if(e == 0){
            println("Video file parsed ...")
        }else{
            ffmpegThrowError(ffmpeg)
        }

        if(!tf.exists()) throw Exception("Video conversion success but failed to read intermediate video file")
        val inf = tf.inputStream()
        val data = inf.readAllBytes()
        inf.close()

        val res = 128*8
        val frame = data.size / res
        println("Video file - $frame frame found")

        if(!vf.exists()) vf.createNewFile()

        val file = DataOutputStream(vf.outputStream())
        file.writeInt(frame)

        for(j in 0 until (data.size / res)){

            println("Processing frame $j")
            val startIndex = (j * res)
            val bar = ByteArray(128*8){ data[startIndex + it] }
            processFrame(bar, file)
        }

        file.flush()
        file.close()
    }

    private fun processFrame(bar: ByteArray, file: DataOutputStream){
        // Detect background (Tile On/Off)
        // xs = 0, 31, 64, 127
        // ys = 0, 2, 5, 7

        var bg = 0
        for(y in arrayOf(0,2,4,6)) for(x in arrayOf(0,32,64,96)) // Btw, anyone know what this scope style called
        {
            val i1 = (y * 128) + x
            val i2 = ((y + 1) * 128) + x

            var v = 0
            for(i in 0 until 32){
                // 0xFF = -1 = 255, 0x80 = -128 = 128
                if(bar[i1 + i] < 0) v++
                if(bar[i2 + i] < 0) v++
            }

            bg = (bg shl 1) or (if (v < 28) 0 else 1)
        }
        file.writeShort(bg)

        // Do the frame compression
        for(y in arrayOf(0,2,4,6)) for(x in arrayOf(0, 32, 64, 96))
        {
            val i1 = (y * 128) + x
            val i2 = ((y +1) * 128) + x
            var v1 = 0
            var v2 = 0
            for(i in 0 until 32){
                val vv1 = if(bar[i1 + i] < 0) 1 else 0
                val vv2 = if(bar[i2 + i] < 0) 1 else 0
                v1 = (v1 shl 1) or vv1
                v2 = (v2 shl 1) or vv2
            }
            file.writeInt(v1)
            file.writeInt(v2)
        }
        file.flush()
    }

    private fun ffmpegThrowError(ffmpeg:Process){
        val e = ffmpeg.exitValue()
        val err = ffmpeg.errorStream.readAllBytes()
        val bb = ByteBuffer.wrap(err)
        println("Video parsing failed ... FFMPEG returns $e")
        throw Exception("ffmpeg return $e - ${Charsets.UTF_8.decode(bb)}")
    }
}
