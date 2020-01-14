package it.unibz.inf.ontouml.vp.utils;

import java.sql.Timestamp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vp.plugin.ApplicationManager;

/**
 * 
 * Class responsible for facilitating display of messages on Visual Paradigm's log.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ViewUtils {
	
	public static final String SCOPE_PLUGIN = "OntoUML";
//	public static final String SCOPE_ALL_PLUGINS = "";
	public static final String SCOPE_VERIFICATION = "Verification Log";
	public static final String SCOPE_DEVELOPMENT_LOG = "DevLog";
	
	public static void log(String message) {
		ApplicationManager.instance().getViewManager().showMessage(timestamp() + message);
	}
	
	public static void log(String message, String scope) {
		ApplicationManager.instance().getViewManager().showMessage(timestamp() + message, scope);
	}
	
	public static void simpleLog(String message) {
		ApplicationManager.instance().getViewManager().showMessage(message);
	}
	
	public static void simpleLog(String message, String scope) {
		ApplicationManager.instance().getViewManager().showMessage(message, scope);
	}
	
	public static void clearLog(String scope) {
		ApplicationManager.instance().getViewManager().clearMessages(scope);
	}
	
	private static String timestamp() {
		return "[" + (new Timestamp(System.currentTimeMillis())) + "] ";
	}
	
	public static void logVerificationResponse(String responseMessage) {
		try {
			JsonObject response = (JsonObject) new JsonParser().parse(responseMessage).getAsJsonObject();

			if (response.has("valid") && response.get("valid").getAsBoolean()) {
				ViewUtils.simpleLog("The model was verified and no syntactical errors were found.\n", SCOPE_PLUGIN);
			} 
			else {
				final JsonArray errors = response.get("meta").getAsJsonArray();
				
				for (JsonElement elem : errors) {
					final JsonObject error = elem.getAsJsonObject();
					final String line = '[' + error.get("title").getAsString() + "]\t " + error.get("detail").getAsString()
							.replaceAll("ontouml/1.0/", "").replaceAll("ontouml/2.0/", "");
					
					ViewUtils.simpleLog(line.trim(), SCOPE_PLUGIN);
				}
			}
		} catch (JsonSyntaxException e) {
			ViewUtils.log("Remote verification error. Please submit your Visual Paradigm's log and the time of the error our developers", SCOPE_PLUGIN);
			e.printStackTrace();
		}
	}

}
