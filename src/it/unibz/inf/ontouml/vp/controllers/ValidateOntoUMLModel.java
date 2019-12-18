package it.unibz.inf.ontouml.vp.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.ontoumlschema.Class;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationLink;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationSet;
import it.unibz.inf.ontouml.vp.ontoumlschema.Model;
import it.unibz.inf.ontouml.vp.ontoumlschema.Package;
import it.unibz.inf.ontouml.vp.ontoumlschema.Property;
import it.unibz.inf.ontouml.vp.ontoumlschema.Relation;
import it.unibz.inf.ontouml.vp.ontoumlschema.Stereotypes;
import it.unibz.inf.ontouml.vp.ontoumlschema.StructuralElement;

public class ValidateOntoUMLModel implements VPActionController {

	Model modelSchema;

	/**
	 *
	 */
	/**
	 *
	 */
	@Override
	public void performAction(VPAction arg0) {

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
					
					/*
				case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
					Relation newRelation = new Relation((IRelationship) modelElement);
					newElements.put(modelElement.getId(), newRelation);
					
					
					if (modelElement.getModelType().equals("Association")) {
						Relation newRelation = new Relation();
						
						IAssociation association = (IAssociation) modelElement;
						
						Property propertyFrom = new Property();
						Property propertyTo = new Property();
						
						propertyFrom.setURI(association.getFrom().getId());
						propertyTo.setURI(association.getTo().getId());
						
						
						
						if(association.getFrom().getModelType().equals("Class"))
							propertyFrom.setPropertyType(Class.baseURI + association.getFrom().getId());
						if(association.getFrom().getModelType().equals("Model"))
							propertyFrom.setPropertyType(Model.baseURI + association.getFrom().getId());
						if(association.getFrom().getModelType().equals("Package"))
							propertyFrom.setPropertyType(Package.baseURI + association.getFrom().getId());
						if(association.getFrom().getModelType().equals("Generalization"))
							propertyFrom.setPropertyType(GeneralizationLink.baseURI + association.getFrom().getId());
						if(association.getFrom().getModelType().equals("Association"))
							propertyFrom.setPropertyType(Relation.baseURI + association.getFrom().getId());
						if(association.getFrom().getModelType().equals("GeneralizationSet"))
							propertyFrom.setPropertyType(GeneralizationSet.baseURI + association.getFrom().getId());
						
						if(association.getTo().getModelType().equals("Class"))
							propertyFrom.setPropertyType(Class.baseURI + association.getTo().getId());
						if(association.getTo().getModelType().equals("Model"))
							propertyFrom.setPropertyType(Model.baseURI + association.getTo().getId());
						if(association.getTo().getModelType().equals("Package"))
							propertyFrom.setPropertyType(Package.baseURI + association.getTo().getId());
						if(association.getTo().getModelType().equals("Generalization"))
							propertyFrom.setPropertyType(GeneralizationLink.baseURI + association.getTo().getId());
						if(association.getTo().getModelType().equals("Association"))
							propertyFrom.setPropertyType(Relation.baseURI + association.getTo().getId());
						if(association.getTo().getModelType().equals("GeneralizationSet"))
							propertyFrom.setPropertyType(GeneralizationSet.baseURI + association.getTo().getId());
					
						String fromEndMult = ((IAssociationEnd) association.getFromEnd()).getMultiplicity();
						int firstDotFrom = fromEndMult.indexOf(".");
						int lastDotFrom = fromEndMult.lastIndexOf(".");
						
						if(firstDotFrom >= 1 && lastDotFrom == 2) {
							propertyFrom.setLowerbound(fromEndMult.substring(0,firstDotFrom));
							propertyFrom.setUpperbound(fromEndMult.substring(lastDotFrom + 1,fromEndMult.length()));
						}else {
							propertyFrom.setLowerbound(fromEndMult);
						}
						
						String toEndMult = ((IAssociationEnd) association.getToEnd()).getMultiplicity();
						int firstDotTo = toEndMult.indexOf(".");
						int lastDotTo = toEndMult.lastIndexOf(".");
						
						if(firstDotTo >= 1 && lastDotTo == 2) {
							propertyFrom.setLowerbound(toEndMult.substring(0,firstDotTo));
							propertyFrom.setUpperbound(fromEndMult.substring(lastDotFrom + 1,toEndMult.length()));
						}else {
							propertyFrom.setLowerbound(toEndMult);
						}
						
						newRelation.addProperty(propertyFrom);
						newRelation.addProperty(propertyTo);
					}
					break;
					
				case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
					break;
					*/
					
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
					GeneralizationLink newGeneralization = new GeneralizationLink((IGeneralization) modelElement);
					newElements.put(modelElement.getId(), newGeneralization);
					break;
					
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
//					if (modelElement.getModelType().equals("GeneralizationSet")) {
//						GeneralizationSet newGeneralizationSet = new GeneralizationSet();
//						newGeneralizationSet.setName(modelElement.getName());
//						newGeneralizationSet.setURI(modelElement.getId());
//						newElements.put(modelElement.getId(), newGeneralizationSet);
//		
//						IGeneralizationSet generalizationSet = (IGeneralizationSet) modelElement;
//						
//						IGeneralization[] generalizations = generalizationSet.toGeneralizationArray();
//						
//						for(IGeneralization generalization : generalizations)
//							newGeneralizationSet.addTuple(GeneralizationLink.baseURI + generalization.getId());
//		
//					}
					break;
			}
			

		}
		

		for (IModelElement modelElement : allElements) {

			String id = modelElement.getId();

			Package parent = null;

			if (modelElement.getModelType().equals("Model") || modelElement.getModelType().equals("Package")
					|| modelElement.getModelType().equals("Class")) {

				if (modelElement.getParent() == null) {
					parent = packageRoot;
				} else {
					String idParent = modelElement.getParent().getId();
					parent = (Package) newElements.get(idParent);
				}

				StructuralElement newElement = newElements.get(id);
				parent.addStructuralElement(newElement);
			}

			// TODO: COLOCAR GENERALIZATION DENTRO DO PACOTE DO SUBTIPO(STUDENT NO NOSSO
			// EXEMPLO)
//			if (modelElement.getModelType().equals("Generalization") || modelElement.getModelType().contentEquals("Association")
//					|| modelElement.getModelType().equals("GeneralizationSet")) {
//				parent = packageRoot;
//
//				StructuralElement newElement = newElements.get(id);
//				parent.addStructuralElement(newElement);
//			}

//			if (modelElement.getModelType().equals("Association"))
//				parent = packageRoot;

		}

		modelSchema.addStructuralElement(packageRoot);
		try {
			validateModel(modelSchema);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void update(VPAction arg0) {
		
	}
	
	private void validateModel(Model modelSchema) throws MalformedURLException, IOException {
		GsonBuilder builder = new GsonBuilder(); 
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		String json = gson.toJson(modelSchema);
		
		System.out.println("[" + (new Timestamp(System.currentTimeMillis())) + "] Validating model: ");
		System.out.println(json);
		
		String url = "https://ontouml.herokuapp.com/v1/verification";
		HttpsURLConnection httpsClient = (HttpsURLConnection) new URL(url).openConnection();
		httpsClient.setRequestMethod("POST");
		httpsClient.setRequestProperty("Content-Type", "application/json");
//		httpsClient.setRequestProperty("Accept", "*/*");
//		httpsClient.setRequestProperty("Cache-Control", "no-cache");
//		httpsClient.setRequestProperty("Connection", "keepAlive");
//		httpsClient.setRequestProperty("Host", "ontouml.herokuapp.com");
//		httpsClient.setRequestProperty("My-Token", "aa187df6-5f6c-47e5-81e2-7e0ba8715a3b,c6c2b720-6b9f-481a-bc8a-1f736c3eab3a");
//		httpsClient.setRequestProperty("User-Agent", "PostmanRuntime/7.20.1");
//		httpsClient.setRequestProperty("cache-control", "no-cache");		
//		  -H 'Accept-Encoding: gzip, deflate' \
//		  -H 'Content-Length: 2727' \
		
		// Send post request
		httpsClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpsClient.getOutputStream())) {
        	wr.writeBytes(json);
            wr.flush();
        }
        
        int responseCode = httpsClient.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(httpsClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            //print result
            System.out.println("[" + (new Timestamp(System.currentTimeMillis())) + "] Validation terminated.");
            
            ViewManager vm = ApplicationManager.instance().getViewManager();
            vm.clearMessages("OntoUML Issues");
            vm.showMessage(response.toString(),"OntoUML Issues");
//            System.out.println(response.toString());

        }
        
	}

}