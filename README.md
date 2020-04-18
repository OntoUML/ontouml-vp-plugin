# OntoUML Plugin for Visual Paradigm

This project contains a [Visual Paradigm](https://www.visual-paradigm.com/) plugin that enables the development of OntoUML models.

This project is defined under the umbrella of the [OntoUML Server](https://github.com/OntoUML/ontouml-server) project and is currently a proof of concept.

If you are interested to know more, feel free to open an issue to provide feedback on the project or reach our team members for more specific cases:
* [Claudenir M. Fonseca](https://github.com/claudenirmf)
* [Tiago Prince Sales](https://github.com/tgoprince)
* [Lucas Bassetti](https://github.com/LucasBassetti)
* [Victor Viola](https://github.com/victorviola)

## User Instructions

To install the **OntoUML Plugin for Visual Paradigm**, please go to our [releases page](https://github.com/OntoUML/ontouml-vp-plugin/releases).

## Developer Instructions

### Requirements

* [Visual Paradigm](https://www.visual-paradigm.com/)
* Java 8 or later
* Maven (optional, a Maven Wrapper is embedded in the project) 

### Build instructions

#### Setting up

1. Clone the project 

`git clone https://github.com/OntoUML/ontouml-vp-plugin.git`


2. Open `pom.xml` and set the value of the variables listed below:
    
    2.1. Set the path to the folder in which the Visual Paradigm application is located:
    
    ```xml
    <visualparadigm.app.dir>
       /Applications/Visual Paradigm.app/Contents/Resources/app
    </visualparadigm.app.dir>
    ```
   
    This path is typically:
    
    - On Windows: `C:\Program Files\Visual Paradigm CE 16.1`
    - On macOS: `/Applications/Visual Paradigm.app/Contents/Resources/app/`
   
   2.2. Set the path to Visual Paradigm's plugin folder
    
   ```xml
    <visualparadigm.plugin.dir>
        /Users/<YOUR_USERNAME>/Library/Application Support/VisualParadigm/plugins
    </visualparadigm.plugin.dir>
    ```
    
    This path is typically:
    
    - On Linux: `~/.config/VisualParadigm/plugins/`
    - On Windows: `C:\Users\<YOUR_USER_NAME>\AppData\Roaming\VisualParadigm\plugins\`
    - On macOS: `/Users/<YOUR_USERNAME>/Library/Application Support/VisualParadigm/plugins/`

#### Maven Commands

The commands listed below invoke Maven through the Maven Wrapper. 
On Windows, replace `./mvnw` for `./mvnw.cmd`

In any OS, to invoke your local Maven installation instead of the wrapper, replace `./mvnw` with `mvn`.

* To **compile** the plugin, run: 
    
     ```shell script
    $ ./mvnw compile
    ```

    The compiled files will be created in the `target` folder.

* To **compile** and **package** the plugin, run 

    ```shell script
    $ ./mvnw package
    ```
  
  This command will create, within the `target` folder, a `*.zip` file that can be distributed to the end users of the plugin.

* To **compile**, **package**, and **install** the plugin, run

    ```shell script
    $ ./mvnw install
    ```
  
    In addition to the previous results, this command will copy the compiled files to Visual Paradigm's `plugins` folder.

* To **run** Visual Paradigm while being able to see the output in the command line, run:

    ```shell script
    $ ./mvnw exec:exec
    ```
  
    Note that before running this command, you should install the plugin. That can be achieved by running:
    
    ```shell script
    $ ./mvnw install exec:exec
    ``` 
  
* To **clean** the built files, run:

    ```shell script
    $ ./mvnw clean
    ```
  
    This does not remove the installed files, just the `target` folder and its contents.

