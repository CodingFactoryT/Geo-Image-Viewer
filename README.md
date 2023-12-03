# Geo Image Viewer
This project was originally created for Ghana to help them categorize and locate their dumpingsites, which unfortunately can be found everywhere in the country.
You will see this at some places in the app (some things are labeled "dumpingsite" instead of "waypoint", you can choose a dumpingsity type, ...). But of course you can also use this project to categorize other coordinates from pictures into locations.

## Features
<ul>
 <li>import (folders with) images by dragging and dropping them onto the map</li>
 <li>any errors while uploading images are indicated</li>
 <li>change map representation (Default, OSM, Satellite, Hybrid)</li>
 <li>change app behavior in the settings menu</li>
 <li>view all dumpingsites with their information in a table</li>
 <li>get information about one location on the sidebar panel</li>
 <li>zoom and move the map</li>
 <li>move the waypoints, the app will dynamically update the coordinates</li>
 <li>inform yourself about the features in the tutorial</li>
 <li>system for logging errors while importing images</li>
 <li>start map and import images via console</li>
 <li>activate/deactivate logging via console</li>
 <li>the app saves the state of itself when a user closes it, so no information is lost if you open the app the next time</li>
 <li>load map via an api</li>      
 <li>images are grouped to dumpingsites</li>
 <li>take a screenshot of the map without the UI</li>
</ul>





## Usage
Drag and drop pictures with GPS-Information from your local explorer onto the map, the program will show you where they were taken.

### Command line arguments
| shortcut | option            | argument 1         | description                                   |
|----------|-------------------|--------------------|-----------------------------------------------|
| -h       | --help            |                    | prints help message and exits                 |
| -l       | --log-file        | /path/to/file      | enables logging and sets log file accordingly |
| -d       | --image-directory | /path/to/directory | sets image directory accordingly              |

## Dependencies

### [metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
We use this library, so we can support many file types at once with little effort.

### [jxmapviewer2](https://github.com/msteiger/jxmapviewer2)
This library helps us to draw the maps from OpenStreetMap.

### [commons-cli](https://commons.apache.org/proper/commons-cli/)
In order to parse command line arguments, we use this library.

### [commons-csv](https://commons.apache.org/proper/commons-csv/)
We are using this library to easily read and write CSV files.

## Requirements
This project uses [Maven](https://maven.apache.org/) to manage its dependencies.
The next instructions can only be followed if Maven has been successfully installed onto your system.

## Building the app
The following commands should provide you a running build.

### 1. Build script:
Works on most GNU+Linux based operating systems (Build + Execute):
`/path/to/geoimageviewer/build.sh`

### 2. Manual procedure:
```bash
cd /path/to/geoimageviewer
mvn clean package
java -jar target/geoimageviewer-1.0-SNAPSHOT.jar
```

### 3. Executable file:
You can also use the executable file from the Releases tab in GitHub to execute this project

## Documentation
### UML diagrams
Latest UML diagram:

![Image failed to load](https://github.com/madcomputerscientists/geoimageviewer/blob/main/plans/UML/MainDiagram-latest.png?raw=true)

The UML diagrams for this project are stored [here](https://github.com/madcomputerscientists/geoimageviewer/tree/main/plans/UML).

## Did you find any bugs or have ideas for new features?
Feel free to contact me via the following options:
<ul>
 <li> Discord Server: https://discord.gg/deftTGQzb2 </li>
 <li> E-Mail: codingfactoryt@gmail.com </li>
</ul>
<br>

or create an [Issue](https://github.com/CodingFactoryT/Geo-Image-Viewer/issues)
