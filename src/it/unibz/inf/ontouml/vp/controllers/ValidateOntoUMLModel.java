package it.unibz.inf.ontouml.vp.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.IStereotype;

import it.unibz.inf.ontouml.vp.ontoumlschema.Class;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationLink;
import it.unibz.inf.ontouml.vp.ontoumlschema.GeneralizationSet;
import it.unibz.inf.ontouml.vp.ontoumlschema.Model;
import it.unibz.inf.ontouml.vp.ontoumlschema.Package;
import it.unibz.inf.ontouml.vp.ontoumlschema.Property;
import it.unibz.inf.ontouml.vp.ontoumlschema.Relation;
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
		Package packageRoot = new Package();

		// need to define how to manage these fields
		modelSchema.addAuthor("Tiago Prince Sales");
		modelSchema.addAuthor("Claudenir M. Fonseca");
		modelSchema.addAuthor("Lucas Basseti");
		modelSchema.addAuthor("Victor Viola");
		modelSchema.setURI("https://ontouml.org/archive/" + project.getId());
		modelSchema.setName("Test Schema");

		for (IModelElement modelElement : allElements) {

			if (modelElement.getModelType().equals("Class")) {
				Class newClass = new Class();
				newClass.setName(modelElement.getName());
				newClass.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newClass);
				
				if(modelElement.stereotypeCount() > 0) {
				
					IStereotype[] stereotypes = modelElement.toStereotypeModelArray();

					for (IStereotype stereotype : stereotypes)
						newClass.addStereotype(stereotype.getName());
				}

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

			//	List<String> stringList = new ArrayList<String>();

			//	stringList.add(gen.getTo().getId());
			//	stringList.add(gen.getFrom().getId());
			//	newGeneralization.setTuple(stringList);
				
				newGeneralization.addTuple(gen.getTo().getId());
				newGeneralization.addTuple(gen.getFrom().getId());

			}
			
			if (modelElement.getModelType().equals("Association")) {
				Relation newRelation = new Relation();
				newRelation.setName(modelElement.getName());
				newRelation.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newRelation);
				
				if(modelElement.stereotypeCount() > 0) {
					
					IStereotype[] stereotypes = modelElement.toStereotypeModelArray();

					for (IStereotype stereotype : stereotypes)
						newRelation.addStereotype(stereotype.getName());
				}
				
				IAssociation association = (IAssociation) modelElement;
				
				Property propertyFrom = new Property();
				Property propertyTo = new Property();
				
				propertyFrom.setPropertyType(association.getFrom().getId());
				propertyTo.setPropertyType(association.getTo().getId());
			
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
			
			if (modelElement.getModelType().equals("GeneralizationSet")) {
				GeneralizationSet newGeneralizationSet = new GeneralizationSet();
				newGeneralizationSet.setName(modelElement.getName());
				newGeneralizationSet.setURI(modelElement.getId());
				newElements.put(modelElement.getId(), newGeneralizationSet);

				IGeneralizationSet generalizationSet = (IGeneralizationSet) modelElement;
				
				IGeneralization[] generalizations = generalizationSet.toGeneralizationArray();
				
				for(IGeneralization generalization : generalizations)
					newGeneralizationSet.addTuple(generalization.getId());

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
			if (modelElement.getModelType().equals("Generalization") || modelElement.getModelType().contentEquals("Association") || modelElement.getModelType().equals("GeneralizationSet")) {
				parent = packageRoot;

				StructuralElement newElement = newElements.get(id);
				parent.addStructuralElement(newElement);
			}

//			if (modelElement.getModelType().equals("Association"))
//				parent = packageRoot;

		}

		modelSchema.addStructuralElement(packageRoot);

		Gson gson = new Gson();
		String json = gson.toJson(modelSchema);
		System.out.println(json);

	}

	@Override
	public void update(VPAction arg0) {

	}

}