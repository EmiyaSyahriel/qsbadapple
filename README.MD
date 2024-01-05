# Bad Apple, but Android Quick Setting Tile

## Preview
[[!Preview Image](https://img.youtube.com/vi/yGhvo-Aefz8/0.jpg)](https://www.youtube.com/watch?v=yGhvo-Aefz8)

## Minimum Requirements
- Android 13 
  > Preferably pure/lightly modified AOSP or Android One, not heavily modified OEM ROM such as OneUI 
  > since this app exploits the structure of AOSP Quick Tile Settings
- Tablet, at least can have 4x4 Quick Settings Tile
  > Phone usually have 2x4, some tablet also have variable size tile, this is tested on `Nokia T20 Android One 13`

## Building
There will be no release, since this would involve a distribution of copyrighted media

### Prerequisites
- Original Bad Apple video with audio 
  > Any video would do actually, as long as it can be visually distinguished on black white
- Android Studio Flamingo 2022.2.1 Patch 2 (Lower should be possible)
- FFMpeg somewhere in PATH

### Compiling
- Put the bad apple video to `(Project Directory)/raw_asset/badapple.mp4`
- Open project in Android Studio
- Wait for indexing end
- Run Gradle Task `:badapple:bakeAssets` to convert assets
  > On Android Studio, Can be run from 
  > `Gradle Pane (usually at right side) > badapple > assets > bakeAssets`,

  > Audio file is encoded to low quality to preserve size, you can always edit the 
  > `buildSrc/src/main/kotlin/BuildAssetTask.kt:34` file to edit the ffmpeg encoding parameters
- Connect your Android Tablet (make sure it has USB Debugging on)
- Run the App by `Run Menu > Run`

## How to use
- Open the QS Bad Apple Icon, set how many padding necessary to create new page for all display tiles
- Arrange the display tiles, they have coordinates on them in (X, Y)
- If all arranged, Press Tile (0,0) to Play 

## License
MIT License