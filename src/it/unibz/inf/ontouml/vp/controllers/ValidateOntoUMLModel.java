package it.unibz.inf.ontouml.vp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
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
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.ontoumlschema.Class;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationLink;
import it.unibz.inf.ontouml.vp.ontoumlschema.Model;
import it.unibz.inf.ontouml.vp.ontoumlschema.Package;
import it.unibz.inf.ontouml.vp.ontoumlschema.StructuralElement;

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
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		vm.showMessage("[" + (new Timestamp(System.currentTimeMillis())) + "] Verification terminated.",
				VERIFICATION_LOG);
	}
	
	private Model generateModel() {
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
		IModelElement[] allElements = project.toAllLevelModelElementArray();

		HashMap<String, StructuralElement> newElements = new HashMap<String, StructuralElement>();

		modelSchema = new Model();
		
		Package packageRoot = new Package(project.getName(),"vpp://root");
		modelSchema.addAuthor(project.getProjectProperties().getAuthor());
		modelSchema.setURI("https://ontouml.org/archive/" + project.getId());
		modelSchema.setName("Test Schema");
		
		for (IModelElement modelElement : allElements) {
			
			switch(modelElement.getModelType()) {
				case IModelElementFactory.MODEL_TYPE_PACKAGE:
					Package newModelPackage = new Package((IPackage) modelElement);
					newElements.put(modelElement.getId(), newModelPackage);
					break;
					
				case IModelElementFactory.MODEL_TYPE_MODEL:
					Package newPackage = new Package((IPackage) modelElement);
					newElements.put(modelElement.getId(), newPackage);
					break;
					
				case IModelElementFactory.MODEL_TYPE_CLASS:
					Class newClass = new Class((IClass) modelElement);
					newElements.put(modelElement.getId(), newClass);
					break;
					
					
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
					GeneralizationLink newGeneralization = new GeneralizationLink((IGeneralization) modelElement);
					newElements.put(modelElement.getId(), newGeneralization);
					break;
					
//				TODO Add remaining elements
//				case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
//				case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
//				case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			}
			

		}
		
//		TODO Transport this loop to Package constructor
		for (StructuralElement elem : newElements.values()) {
			IModelElement modelElement = elem.getSourceModelElement();
			String id = modelElement.getId();
			Package parent = null;
			
			System.out.println("Adding " + modelElement.getName());
			
			switch (modelElement.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_MODEL:
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
			case IModelElementFactory.MODEL_TYPE_CLASS:
				if (modelElement.getParent() == null) {
					parent = packageRoot;
				} else {
					String idParent = modelElement.getParent().getId();
					parent = (Package) newElements.get(idParent);
				}

				StructuralElement newElement = newElements.get(id);
				parent.addStructuralElement(newElement);
				break;
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
				parent = packageRoot;
				parent.addStructuralElement(newElements.get(id));
				break ;
			}

		}

		modelSchema.addStructuralElement(packageRoot);
		
		return modelSchema;
		
	}
	
	private void verifyModel(Model modelSchema) throws MalformedURLException, IOException {
		GsonBuilder builder = new GsonBuilder(); 
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		String json = gson.toJson(modelSchema);
		
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