package it.unibz.inf.ontouml.vp.model.uml;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;

/**
 * 
 * Implementation of ModelElement to handle IAssociation objects to be
 * serialized as ontouml-schema/Association
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class Association implements ModelElement {

	private final IAssociation sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("description")
	@Expose
	private String description;

	@SerializedName("properties")
	@Expose
	private List<Property> properties;

	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments;

	@SerializedName("stereotypes")
	@Expose
	private List<String> stereotypes;

	@SerializedName("isAbstract")
	@Expose
	private boolean isAbstract;

	@SerializedName("isDerived")
	@Expose
	private boolean isDerived;

	public Association(IAssociation source) {
		this.sourceModelElement = source;

		this.type = ModelElement.TYPE_RELATION;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());

		addProperty(new Property((IAssociationEnd) source.getFromEnd()));
		addProperty(new Property((IAssociationEnd) source.getToEnd()));

		String[] stereotypes = source.toStereotypeArray();
		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			addStereotype(stereotypes[i]);
		}

		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
		setAbstract(source.isAbstract());
		setDerived(source.isDerived());
	}

	public Association(IAssociation source, HashSet<String> modelElements) {
		this.sourceModelElement = source;

		this.type = ModelElement.TYPE_RELATION;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());

		if (modelElements.contains(source.getFromEnd().getId()))
			addProperty(new Property((IAssociationEnd) source.getFromEnd()));

		if (modelElements.contains(source.getToEnd().getId()))
			addProperty(new Property((IAssociationEnd) source.getToEnd()));

		String[] stereotypes = source.toStereotypeArray();
		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			addStereotype(stereotypes[i]);
		}

		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
		setAbstract(source.isAbstract());
		setDerived(source.isDerived());
	}

	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}

	@Override
	public IAssociation getSourceModelElement() {
		return this.sourceModelElement;
	}

	@Override
	public String getOntoUMLType() {
		return this.type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = ModelElement.safeGetString(name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = ModelElement.safeGetString(description);
	}

	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
	}

	public List<String> getStereotypes() {
		return this.stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
	}

	public String getStereotype(int position) {
		return this.stereotypes.get(position);
	}

	public void addStereotype(String name) {
		if (this.stereotypes == null)
			this.stereotypes = new ArrayList<String>();

		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {

		if (this.stereotypes.contains(name))
			this.stereotypes.remove(name);
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Property getProperty(int position) {
		return this.properties.get(position);
	}

	public void addProperty(Property property) {
		if (this.properties == null)
			this.properties = new ArrayList<Property>();

		this.properties.add(property);
	}

	public void removeProperty(Property property) {

		if (this.properties.contains(property))
			this.properties.remove(property);
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isDerived() {
		return this.isDerived;
	}

	public void setDerived(boolean isDerived) {
		this.isDerived = isDerived;
	}

	public static IClass getSource(IAssociation association) {
		return (IClass) association.getFrom();
	}

	public static IClass getTarget(IAssociation association) {
		return (IClass) association.getTo();
	}

	public static void setSource(IAssociation association, IClass newSource) {
		association.setFrom(newSource);
	}

	public static void setTarget(IAssociation association, IClass newTarget) {
		association.setTo(newTarget);
	}
	
	public static IAssociationEnd getSourceEnd(IAssociation association) {
		return (IAssociationEnd) association.getFromEnd();
	}

	public static IAssociationEnd getTargetEnd(IAssociation association) {
		return (IAssociationEnd) association.getToEnd();
	}

	public static void invertAssociation(IAssociation association) {
		final IClass originalSource = getSource(association);
		final IClass originalTarget = getTarget(association);

		setSource(association, originalTarget);
		setTarget(association, originalSource);

		final IDiagramElement[] associationViews = association.getDiagramElements();
		final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
		
		for (int i = 0; associationViews != null && i < associationViews.length; i++) {
			final IAssociationUIModel originalView = (IAssociationUIModel) associationViews[i];
			final IDiagramUIModel diagram = originalView.getDiagramUIModel();
			final IDiagramElement originalViewSource = originalView.getFromShape();
			final IDiagramElement originalViewTarget = originalView.getToShape();
			
			if(diagram == null || originalViewSource == null || originalViewTarget == null) {
				continue;
			}
			
			final boolean isMasterView = originalView.isMasterView();
			final boolean isShowDirection = originalView.isShowDirection();
			Point[] points = originalView.getPoints();
			originalView.deleteViewOnly();
			
			if(points != null) {
				final List<Point> pointsListToReverse = Arrays.asList(points);
				Collections.reverse(pointsListToReverse);
				points = pointsListToReverse.toArray(points); 
			}
			
			final IAssociationUIModel invertedView = (IAssociationUIModel) dm.createConnector(diagram, association,
					originalViewTarget, originalViewSource, points);
			
			if(isMasterView) {
				invertedView.toBeMasterView();
			}
			
			invertedView.setShowDirection(isShowDirection);
			invertedView.resetCaption();
			
		}
	}

}