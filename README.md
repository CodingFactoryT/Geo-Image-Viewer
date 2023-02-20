# Geo Image Viewer
This project was originally created for Ghana to help them categorize and locate their dumpingsites, which unfortunately can be found everywhere in the country.
You will see this at some places in the app (some things are labelled "dumpingsite" instead of "waypoint", you can choose a dumpingsity type, ...). But of course you can also use this project to categorize other coordinates from pictures into locations.

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

## Documentation
### UML diagrams
Latest UML diagram:

![Image failed to load](https://github.com/madcomputerscientists/geoimageviewer/blob/main/plans/UML/MainDiagram-latest.png?raw=true)

The UML diagrams for this project are stored [here](https://github.com/madcomputerscientists/geoimageviewer/tree/main/plans/UML).

## Feel free to create [Issues](https://github.com/madcomputerscientists/geoimageviewer/issues) and [Pull Requests](https://github.com/madcomputerscientists/geoimageviewer/pulls)!
