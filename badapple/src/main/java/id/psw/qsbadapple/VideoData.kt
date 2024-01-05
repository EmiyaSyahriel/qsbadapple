package id.psw.qsbadapple

@Suppress("ArrayInDataClass") // We are not doing equality check here
data class VideoData(val count:Int, val frames:Array<FrameData>) {
    data class FrameData (val tileFlag:Short, val tiles:Array<Tile>){
        data class Tile(val title:Int, val subtitle:Int)
    }
}