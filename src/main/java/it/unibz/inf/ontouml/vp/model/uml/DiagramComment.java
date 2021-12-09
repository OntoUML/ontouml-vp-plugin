package it.unibz.inf.ontouml.vp.model.uml;

import com.vp.plugin.diagram.IDiagramUIModelComment;

public class DiagramComment {

  private static final String ontoumlPluginCommentAuthor = "OntoUML Plugin";
  private static final String ontoumlPluginCommentSummary = "OntoUML Diagram";
  private static final String ontoumlPluginCommentContent = "This diagram has been set as an OntoUML Diagram.";

  public static boolean isOntoumlDiagramComment(IDiagramUIModelComment comment) {
    return ontoumlPluginCommentAuthor.equals(comment.getAuthor()) &&
        ontoumlPluginCommentSummary.equals(comment.getSummary()) &&
        ontoumlPluginCommentContent.equals(comment.getContent());
  }

  public static void setOntoumlDiagramComment(IDiagramUIModelComment comment) {
    if(!isEmpty(comment)) {
      throw new RuntimeException("The comment is not empty. It cannot be used to set an OntoUML diagram.");
    }

    comment.setAuthor(ontoumlPluginCommentAuthor);
    comment.setSummary(ontoumlPluginCommentSummary);
    comment.setContent(ontoumlPluginCommentContent);
  }

  public static boolean isEmpty(IDiagramUIModelComment comment) {
    final boolean hasAuthor = comment.getAuthor() != null && !"".equals(comment.getAuthor());
    final boolean hasSummary = comment.getSummary() != null && !"".equals(comment.getSummary());
    final boolean hasContent = comment.getContent() != null && !"".equals(comment.getContent());

    return !hasAuthor && !hasSummary && !hasContent;
  }

}
