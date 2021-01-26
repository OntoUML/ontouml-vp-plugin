package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.*;

public class Project extends OntoumlElement implements ModelContainer {
   private Package model;
   private List<Diagram> diagrams = new ArrayList<>();

   public Project(String id, MultilingualText name) {
      super(null, id, name);
      setProject(this);
   }

   public Project(MultilingualText name) {
      this(null, name);
   }

   public Project() {
      this(null, null);
   }

   @Override
   public String getType() {
      return "Project";
   }

   public Optional<Package> getModel() {
      return Optional.ofNullable(model);
   }

   public Package createModel() {
      return createModel("Model");
   }

   public Package createModel(String modelName) {
      Package model = new Package(modelName);
      setModel(model);
      return model;
   }

   public void setModel(Package model) {
      this.model = model;

      if (model != null)
         model.setContainer(this);
   }

   public boolean hasModel() {
      return getModel().isPresent();
   }

   public List<Diagram> getDiagrams() {
      return diagrams;
   }

   public void setDiagrams(List<Diagram> diagrams) {
      if (diagrams == null)
         throw new NullPointerException("Cannot set the diagram list of a project to null.");

      this.diagrams.clear();
      OntoumlUtils.addIfNotNull(this.diagrams, diagrams);
   }

   public void addDiagram(Diagram diagram) {
      if (diagram == null)
         throw new NullPointerException("Cannot add a null diagram to the project.");

      diagrams.add(diagram);
   }

   public void addDiagrams(List<Diagram> diagrams) {
      if (diagrams == null)
         throw new NullPointerException("Cannot add a null diagram list to the project.");

      OntoumlUtils.addIfNotNull(this.diagrams, diagrams);
   }

   public boolean hasDiagram() {
      return !getDiagrams().isEmpty();
   }

   @Override
   public List<OntoumlElement> getContents() {
      List<OntoumlElement> contents = new ArrayList<>();

      if (diagrams != null)
         contents.addAll(diagrams);

      if (getModel().isPresent())
         contents.add(getModel().get());

      return contents;
   }

}
