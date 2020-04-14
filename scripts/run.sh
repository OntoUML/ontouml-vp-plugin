#!/bin/bash

#user=$(whoami)
#appDir=${appDir:-"/Applications/Visual Paradigm.app/Contents/Resources/app"}
#pluginDir=${pluginsDir:-"/Users/${user}/Library/Application Support/VisualParadigm/plugins/ontouml-vp-plugin-0.0.2-SNAPSHOT/classes"}

while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        param="${1/--/}"
        declare $param="$2"
        # echo $1 $2 // Optional to see the parameter:value result
   fi

  shift
done

gson="$appDir/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar"
vpLibDir="$appDir/lib/*"
vpOrmlibDir="$appDir/ormlib/*"

classpath="$pluginDir:$gson:$vpLibDir:$vpOrmlibDir"
java=$(which java)

cd "$appDir/bin" || return

echo "$java -classpath ${classpath} RV debug"
$java -classpath "$classpath" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=n RV debug