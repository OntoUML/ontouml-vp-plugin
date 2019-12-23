package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.ontoumlschema.Association;
import it.unibz.inf.ontouml.vp.ontoumlschema.Class;
import it.unibz.inf.ontouml.vp.ontoumlschema.Generalization;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationSet;
import it.unibz.inf.ontouml.vp.ontoumlschema.Model;
import it.unibz.inf.ontouml.vp.ontoumlschema.Package;

public class ValidateOntoUMLModel implements VPActionController {

//	private static final String VERIFICATION_SERVICE_URL = "http://localhost:3000/v1/verification";
	private static final String VERIFICATION_SERVICE_URL = "https://ontouml.herokuapp.com/v1/verification";

	Model modelSchema;
	
	private static String VERIFICATION_LOG = "Verification Log";

	@Override
	public void performAction(VPAction arg0) {
		// TODO Verify memory leak
		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.execute(new Runnable() {
			@Override public void run() { 
				performVerification();
			}
		});
	}

	@Override
	public void update(VPAction arg0) {
		
	}
	
	private void performVerification() {
		ViewManager vm = ApplicationManager.instance().getViewManager();
		vm.clearMessages(VERIFICATION_LOG);
		vm.showMessage("[" + (new Timestamp(System.currentTimeMillis())) + "] Initiating verification.",
				VERIFICATION_LOG);

		try {
			verifyModel(generateModel());
			vm.showMessage("[" + (new Timestamp(System.currentTimeMillis())) + "] Verification terminated.",
					VERIFICATION_LOG);
		} 
		catch (Exception e) {
			e.printStackTrace();
			vm.showMessage("[" + (new Timestamp(System.currentTimeMillis())) + "] Verification terminated with error.",
					VERIFICATION_LOG);
			vm.showMessage("Please share your log (including your model, if possible) with our developers at <https://github.com/OntoUML/ontouml-vp-plugin>.",
					VERIFICATION_LOG);
		}

	}
	
	private Model generateModel() {
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
		String[] rootLevelElements = {
				IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL,
				IModelElementFactory.MODEL_TYPE_CLASS
		};
		IModelElement[] projectElements = project.toModelElementArray(rootLevelElements);

		modelSchema = new Model();
		modelSchema.addAuthor(project.getProjectProperties().getAuthor());
		modelSchema.setURI("https://ontouml.org/archive/" + project.getId());
		modelSchema.setName("Test Schema");
		
		for (int i=0; projectElements!=null && i<projectElements.length; i++) {
			IModelElement projectElement = projectElements[i];
			
			switch(projectElement.getModelType()) {
				case IModelElementFactory.MODEL_TYPE_PACKAGE:
					Package newPackage = new Package((IPackage) projectElement);
					modelSchema.addStructuralElement(newPackage);
					break;
					
				case IModelElementFactory.MODEL_TYPE_MODEL:
					Model newModelPackage = new Model((IModel) projectElement);
					modelSchema.addStructuralElement(newModelPackage);
					break;
					
				case IModelElementFactory.MODEL_TYPE_CLASS:
					Class newClass = new Class((IClass) projectElement);
					modelSchema.addStructuralElement(newClass);
					break;
			}
		}
		
		String[] anyLevelElements = {
				IModelElementFactory.MODEL_TYPE_GENERALIZATION,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS
		};
		projectElements = project.toAllLevelModelElementArray(anyLevelElements);
		
		for (int i=0; projectElements!=null && i<projectElements.length; i++) {
			IModelElement projectElement = projectElements[i];
			
			switch(projectElement.getModelType()) {
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
					Generalization newGeneralization = new Generalization((IGeneralization) projectElement);
					modelSchema.addStructuralElement(newGeneralization);
					break;
				case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
					Association newAssociation = new Association((IAssociation) projectElement);
					modelSchema.addStructuralElement(newAssociation);
					break;
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
					GeneralizationSet newGeneralizationSet = new GeneralizationSet((IGeneralizationSet) projectElement);
					modelSchema.addStructuralElement(newGeneralizationSet);
					break;
					
//				TODO Add remaining elements
//				case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
			}
		}
		
		return modelSchema;
	}
	
	private void verifyModel(Model modelSchema) throws MalformedURLException, IOException {
		GsonBuilder builder = new GsonBuilder(); 
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		String json = gson.toJson(modelSchema);
		
		// Send json model to clipboard
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection sl = new StringSelection(json);
		c.setContents(sl, sl);
		System.out.println("Generated JSON copied to clipboard ;)");
		
		System.out.println("[" + (new Timestamp(System.currentTimeMillis())) + "] Validating model: ");
		System.out.println(json);
		
		try {
			URL url = new URL (VERIFICATION_SERVICE_URL);
			
			HttpURLConnection con;
			try{
				con = (HttpsURLConnection) url.openConnection();
			}
			catch (ClassCastException e) {
				con = (HttpURLConnection) url.openConnection();
			};
			
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			
			OutputStream os = con.getOutputStream();
			byte[] input = json.getBytes();
			os.write(input, 0, input.length);
			os.flush();
			os.close();
			
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			BufferedReader br;
			
			if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			br.close();
					    
			System.out.println(response.toString());
			displayVerificationResponse(response.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

	private void displayVerificationResponse(String responseMessage) {
		ViewManager vm = ApplicationManager.instance().getViewManager();

		try {
			JsonObject response = (JsonObject) new JsonParser().parse(responseMessage).getAsJsonObject();

			if (response.has("valid") && response.get("valid").getAsBoolean()) {
				String line = "The model was verified and no syntactical errors were found.\n";
				vm.showMessage(line.trim(), VERIFICATION_LOG);
			} else {
				JsonArray errors = response.get("meta").getAsJsonArray();
				for (JsonElement elem : errors) {
					JsonObject error = elem.getAsJsonObject();
					String line = '[' + error.get("title").getAsString() + "]\t " + error.get("detail").getAsString()
							.replaceAll("ontouml/1.0/", "").replaceAll("ontouml/2.0/", "");
					vm.showMessage(line.trim(), VERIFICATION_LOG);
				}
			}
		} catch (JsonSyntaxException e) {
			vm.showMessage(
					"Remote verification error."
							+ " Please submit your Visual Paradigm's log and the time of the error our developers",
					VERIFICATION_LOG);
			vm.showMessage(
					"Your Visual Paradigm's can be exported through the menu 'Help' > 'About' > 'Export Log File...'.",
					VERIFICATION_LOG);
			e.printStackTrace();
		}
	}

}