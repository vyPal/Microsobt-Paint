# MyMicrosobtPain
Some school project using Java and JavaFX 🤢

# How to build
## Clone the repository
```bash
git clone https://github.com/vyPal/MyMicrosobtPain.git
```

## Install the JavaFX SDK
1. Download the JavaFX SDK from the official website: https://gluonhq.com/products/javafx/
2. Extract the downloaded archive to a folder of your choice
3. Set the `PATH_TO_FX` environment variable to the path of the extracted folder:
```bash
export PATH_TO_FX=/path/to/javafx-sdk-11.0.2/lib
```

## Build the project
For ease of use, a Makefile is provided. To build the project, simply run:
```bash
make
```

# How to run
To run the project, execute the following command:
```bash
make run
```

# How to add files to the Makefile
To add a new file to the Makefile, simply add the file to the `CLASSES` variable in the Makefile. For example, to add a file named `MyFile.java`, add the following line to the Makefile:
```Makefile
CLASSES = \
    MyFile.java \
    ...
```

