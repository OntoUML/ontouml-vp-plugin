echo "Initiating Visual Paradigm execution..."

USER=$(whoami)
VP_APP_DIR="/Applications/Visual Paradigm.app/Contents/Resources/app/bin"
VP_PLUGINS_DIR="/Users/${USER}/Library/Application Support/VisualParadigm/plugins"
M2_LOCAL="/Users/${USER}/.m2/repository/"

CWD=$VP_APP_DIR

CLASSES="$VP_PLUGINS_DIR/ontouml-vp-plugin/classes"
GSON="$M2_LOCAL/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar"
OPENAPI="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.openapi/1.0.0/com.visualparadigm.app.lib.openapi-1.0.0.jar"
VPPLATFORM="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.vpplatform/1.0.0/com.visualparadigm.app.lib.vpplatform-1.0.0.jar:"
LIB01="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib01/1.0.0/com.visualparadigm.app.lib.lib01-1.0.0.jar:"
LIB02="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib02/1.0.0/com.visualparadigm.app.lib.lib02-1.0.0.jar:"
LIB03="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib03/1.0.0/com.visualparadigm.app.lib.lib03-1.0.0.jar:"
LIB04="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib04/1.0.0/com.visualparadigm.app.lib.lib04-1.0.0.jar:"
LIB05="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib05/1.0.0/com.visualparadigm.app.lib.lib05-1.0.0.jar:"
LIB06="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib06/1.0.0/com.visualparadigm.app.lib.lib06-1.0.0.jar:"
LIB07="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib07/1.0.0/com.visualparadigm.app.lib.lib07-1.0.0.jar:"
LIB09="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.lib09/1.0.0/com.visualparadigm.app.lib.lib09-1.0.0.jar:"
JNIWRAP="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.jniwrap/1.0.0/com.visualparadigm.app.lib.jniwrap-1.0.0.jar:"
WINPACK="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.lib.winpack/1.0.0/com.visualparadigm.app.lib.winpack-1.0.0.jar:"
ORM="$M2_LOCAL/com/visualparadigm/app/com.visualparadigm.app.ormlib.orm/1.0.0/com.visualparadigm.app.ormlib.orm-1.0.0.jar"

PROJECT_CLASSPATH="$CLASSES:$GSON:$OPENAPI:$VPPLATFORM:$LIB01:$LIB02:$LIB03:$LIB04:$LIB05:$LIB06:$LIB07:$LIB09:$JNIWRAP:$WINPACK:$ORM"
# PROJECT_CLASSPATH="$GSON:$OPENAPI:$VPPLATFORM:$LIB01:$LIB02:$LIB03:$LIB04:$LIB05:$LIB06:$LIB07:$LIB09:$JNIWRAP:$WINPACK:$ORM"

JAVA_JVM="/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk//Contents/Home/bin/java"

# echo "cd $VP_APP_DIR"
cd "$VP_APP_DIR"
# ls

# echo "java -classpath \"$PROJECT_CLASSPATH\" RV debug"
$JAVA_JVM -classpath "$PROJECT_CLASSPATH" RV debug