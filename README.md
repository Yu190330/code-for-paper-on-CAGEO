# code-for-paper-on-CAGEO
Code for the paper
Tetrahedral generation of 3-d geological model based on TetGen
Fengyu Jiang, Mu Huang(corresponding author), Ziming Xiong, Yiming Liu
(Army Engineering University of PLA, Nanking, China)

### Introduction

This .zip file contains code for the paper Tetrahedral generation of 3-d geological model based on TetGen. 
* 1.format_conversion<br>
Code in this folder is used to convert the format of the model from .obj to others.(in this paper, the .obj format model is built with [BM_GeoModeler](http://www.geotechbim.com/)). Then the model could be imported into [TetGen](http://wias-berlin.de/software/index.jsp?id=TetGen&lang=1), [FLAC3D](https://www.itasca.co.uk/software/FLAC3D) and [Cesium](https://github.com/CesiumGS/cesium).
* 2.Cesium_Three<br>
Code in this folder is used to visualize the model in [Cesium](https://github.com/CesiumGS/cesium) and [Three](https://threejs.org/). This code is developed based on a [sample project of Cesium](https://cesium.com/blog/2017/10/23/integrating-cesium-with-threejs/). We write a Loader to load the .tri format model.
* 3.Examples<br>
This folder contains an example to test code.

### Usage 
* .obj to .poly<br>
The code Obj2poly`[..\format_conversion\src\format_conversion\Obj2poly.java]` is used for converting the model to .poly format. The code needs to run through the IDE. The path of the model needs to be spicified for running the code. The .poly format model could be tested by [TetView](http://wias-berlin.de/software/tetgen/tetview.html).
* TetGen to FLAC3D<br>
The code Node_ele2f3grid`[..\format_conversion\src\format_conversion\Node_ele2f3grid.java]` is used for converting the model to .f3grid format.The code needs to run through the IDE. The path of the model needs to be spicified for running the code.(We use Flac3D 6.0 to test the model)
* TetGen to Cesium<br>
The code Node_ele2tri`[..\format_conversion\src\format_conversion\Node_ele2tri.java]` is used for converting the model to .tri format.The code needs to run through the IDE and a local server. The path of the model needs to be spicified for running the code. We provide a html file`[..\Cesium_Three\index.html]` for testing the model. 
* Examples<br>
An example of .obj format`[..\Examples\example.obj]`is provided for testing code.
