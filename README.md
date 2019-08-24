# BULK TextureCreator

Simple Tool to create seamless PBR Texturesets (especially Ground Textures) from Photographs in bulk.

## Getting Started
A pre-compiled jar program of BULK Texture Creator can be Downloaded from here:

[**DOWNLOAD**](https://github.com/Erarnitox/bulk-texture-creator/blob/master/bin/BULK.jar?raw=true)

Once started the Program should look like this:

![ScreenShotStart](https://raw.githubusercontent.com/Erarnitox/bulk-texture-creator/master/res/1.PNG)

You can now select a picture from your input folder. 

BULK will then select every picture in that folder.

Once you hit the `Create` Button BULK will then start to create an albedo, normal and roughness map
for every picture in that folder and store the output Textures in a Folder `Output`. 

### Prerequisites

You need to have Java installed on your Computer.

If you dont have it already get it from [**here**](https://java.com/de/download/).

### Configuration

BULK will try to load the File `Texture.conf` when the program starts and will create it if it doesnt exist already.

The default config looks like this:

```
Size:
1024

Strength:
0.9

Divisor:
4
```

**Size:** the target resolution of the texture set.

**Strength:** when making tileable how soft is the bending. Where 1.0 is hard and 0.0 is soft.

**Divisor:** used to downsample the Normal map for smoother results. Where 1 very sharp and 5 very blury


[Raw paste of default config](https://raw.githubusercontent.com/Erarnitox/bulk-texture-creator/master/res/Texture.conf)

## Example Results:
Here is an Example:

First of all the Input Picture:
![Input](https://github.com/Erarnitox/bulk-texture-creator/blob/master/res/Example/Inputs/RedStone.JPG)

From that BULK will create a seamless albedo map in the resolution you have specified: 
![Albedo](https://github.com/Erarnitox/bulk-texture-creator/blob/master/res/Example/Output/RedStone.JPG_0005_albedo.png)

Bulk will then downsample the Texture by the Divisor you have specified and will calculate a normal map:
![Normal](https://github.com/Erarnitox/bulk-texture-creator/blob/master/res/Example/Output/RedStone.JPG_0005_normal.png)

Bulk will also provide a basic roughness map for the texture: 
![Roughness](https://github.com/Erarnitox/bulk-texture-creator/blob/master/res/Example/Output/RedStone.JPG_0005_rough.png)

You can find a couple of example Results [here](https://github.com/Erarnitox/bulk-texture-creator/tree/master/res/Example)
## Video Demonstration:

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details

