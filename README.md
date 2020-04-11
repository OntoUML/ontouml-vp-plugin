# OntoUML Plugin for Visual Paradigm

This project contains a [Visual Paradigm](https://www.visual-paradigm.com/) plugin that enables the development of OntoUML models.

This project is defined under the umbrella of the [OntoUML Server](https://github.com/OntoUML/ontouml-server) project and is currently a proof of concept.

If you are interested to know more, feel free to open an issue to provide feedback on the project or reach our team members for more specific cases:
* [Claudenir M. Fonseca](https://github.com/claudenirmf)
* [Tiago Prince Sales](https://github.com/tgoprince)
* [Lucas Bassetti](https://github.com/LucasBassetti)
* [Victor Viola](https://github.com/victorviola)

## Installation Instructions

To install the **OntoUML Plugin for Visual Paradigm**, please go to our [releases page](https://github.com/OntoUML/ontouml-vp-plugin/releases).

## Development Instructions

Requirements:
* [Visual Paradigm](https://www.visual-paradigm.com/)
* [Maven](https://maven.apache.org/)
* Java 8 or later

Build instructions:

1. Clone the repository  

* Clone this project within Visual Paradigm's `plugins` folder, typically:
	* `~/.config/VisualParadigm/plugins/` in **Linux**
	* `C:\Users\<YOUR_USER_NAME>\AppData\Roaming\VisualParadigm\plugins\` in **Windows**
	* `~/Library/Application Support/VisualParadigm/plugins/` in **macOS**

```git clone https://github.com/OntoUML/ontouml-vp-plugin.git```

* To import necessary Visual Paradigm dependencies, run the `initialization` command passing the path to your Visual Paradigm installation `VISUAL_PARADIGM_PATH`
	* `VISUAL_PARADIGM_PATH` in **macOS** is typically `/Applications/Visual\ Paradigm.app/Contents/Resources/app/`

```mvn initialize -Dvisualparadigm.app.dir=VISUAL_PARADIGM_PATH```

* To compile your source, run

```mvn compile -Dvisualparadigm.app.dir=VISUAL_PARADIGM_PATH```

* To generate an ZIP plugin installation, run

```mvn package -Dvisualparadigm.app.dir=VISUAL_PARADIGM_PATH```
