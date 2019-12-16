package it.unibz.inf.ontouml.vp.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.ontoumlschema.Class;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationLink;
import it.unibz.inf.ontouml.vp.ontoumlschema.Model;
import it.unibz.inf.ontouml.vp.ontoumlschema.Package;
import it.unibz.inf.ontouml.vp.ontoumlschema.Relation;
import it.unibz.inf.ontouml.vp.ontoumlschema.StructuralElement;

public class ValidateOntoUMLModel implements VPActionController {

	Model modelSchema;

	@Override
	public void performAction(VPAction arg0) {

		IProject project = ApplicationManager.instance().getProjectManager().getProject();

		IModelElement[] allElements = project.toAllLevelModelElementArray();

		HashMap<String, StructuralElement> newElements = new HashMap<String, StructuralElement>();

		modelSchema = new Model();
		Package packageRoot = new Package();
		
		//need to define how to manage these fields
		modelSchema.addAuthor("Tiago Prince Sales");
		modelSchema.addAuthor("Claudenir M. Fonseca");
		modelSchema.addAuthor("Lucas Basseti");
		modelSchema.addAuthor("Victor Viola");
		modelSchema.setURI("https://ontouml.org/archive/"+project.getId());
		modelSchema.setName("Test Schema");

		for (IModelElement modelElement : allElements) {

			if (modelElement.getModelType().equals("Class")) {
				Class newClass = new Class();
				newClass.setName(modelElement.getName());
				newClass.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newClass);
			}

			if (modelElement.getModelType().equals("Model")) {
				Package newPackage = new Package();
				newPackage.setName(modelElement.getName());
				newPackage.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newPackage);
			}
			
			if (modelElement.getModelType().equals("Package")) {
				Package newPackage = new Package();
				newPackage.setName(modelElement.getName());
				newPackage.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newPackage);
			}

			if (modelElement.getModelType().equals("Generalization")) {
				GeneralizationLink newGeneralization = new GeneralizationLink();
				newGeneralization.setName(modelElement.getName());
				newGeneralization.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newGeneralization);

				IGeneralization gen = (IGeneralization) modelElement;

				List<String> stringList = new ArrayList<String>();

				stringList.add(gen.getTo().getId());
				stringList.add(gen.getFrom().getId());
				newGeneralization.setTuple(stringList);

			}

		}

		for (IModelElement modelElement : allElements) {

			String id = modelElement.getId();

			Package parent = null;

			if (modelElement.getModelType().equals("Model") || modelElement.getModelType().equals("Package") || modelElement.getModelType().equals("Class")) {

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
			if (modelElement.getModelType().equals("Generalization")) {
				parent = packageRoot;

				StructuralElement newElement = newElements.get(id);
				parent.addStructuralElement(newElement);
			}

//			if (modelElement.getModelType().equals("Association"))
//				parent = packageRoot;

		}

		modelSchema.setRoot(packageRoot);

		Gson gson = new Gson();
		String json = gson.toJson(modelSchema);
		System.out.println(json);

	}

	@Override
	public void update(VPAction arg0) {

	}
	
}