package it.unibz.inf.ontouml.vp.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.model.Model;
import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class JCheckBoxTree extends JTree {

	private static final long serialVersionUID = -4194122328392241790L;

	JCheckBoxTree selfPointer = this;

	// Defining data structure that will enable to fast check-indicate the state of
	// each node
	// It totally replaces the "selection" mechanism of the JTree
	private class CheckedNode {
		boolean isSelected;
		boolean hasChildren;
		boolean allChildrenSelected;

		public CheckedNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_) {
			isSelected = isSelected_;
			hasChildren = hasChildren_;
			allChildrenSelected = allChildrenSelected_;
		}
	}

	HashMap<TreePath, CheckedNode> nodesCheckingState;
	HashSet<TreePath> checkedPaths = new HashSet<TreePath>();

	// Defining a new event type for the checking mechanism and preparing
	// event-handling mechanism
	protected EventListenerList listenerList = new EventListenerList();

	public class CheckChangeEvent extends EventObject {
		private static final long serialVersionUID = -8100230309044193368L;

		public CheckChangeEvent(Object source) {
			super(source);
		}
	}

	public interface CheckChangeEventListener extends EventListener {
		public void checkStateChanged(CheckChangeEvent event);
	}

	public void addCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.add(CheckChangeEventListener.class, listener);
	}

	public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.remove(CheckChangeEventListener.class, listener);
	}

	void fireCheckChangeEvent(CheckChangeEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CheckChangeEventListener.class) {
				((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
			}
		}
	}

	// Override
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		resetCheckingState();
	}

	// New method that returns only the checked paths (totally ignores original
	// "selection" mechanism)
	public TreePath[] getCheckedPaths() {
		return checkedPaths.toArray(new TreePath[checkedPaths.size()]);
	}

	// Returns true in case that the node is selected, has children but not all of
	// them are selected
	public boolean isSelectedPartially(TreePath path) {
		CheckedNode cn = nodesCheckingState.get(path);
		return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
	}

	private void resetCheckingState() {
		nodesCheckingState = new HashMap<TreePath, CheckedNode>();
		checkedPaths = new HashSet<TreePath>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
		if (node == null) {
			return;
		}
		addSubtreeToCheckingStateTracking(node);
	}

	// Creating data structure of the current model for the checking mechanism
	private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
		TreeNode[] path = node.getPath();
		TreePath tp = new TreePath(path);
		CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
		nodesCheckingState.put(tp, cn);
		for (int i = 0; i < node.getChildCount(); i++) {
			addSubtreeToCheckingStateTracking(
					(DefaultMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
		}
	}

	// Overriding cell renderer by a class that ignores the original "selection"
	// mechanism
	// It decides how to show the nodes due to the checking-mechanism
	private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {
		private static final long serialVersionUID = -7341833835878991719L;
		JCheckBox checkBox;
		JLabel label;

		public CheckBoxCellRenderer() {
			super();
			this.setLayout(new BorderLayout());
			checkBox = new JCheckBox();
			label = new JLabel();
			add(label, BorderLayout.WEST);
			add(checkBox, BorderLayout.CENTER);
			setOpaque(false);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			TreePath tp = new TreePath(node.getPath());
			CheckedNode cn = nodesCheckingState.get(tp);

			if (cn == null) {
				return this;
			}

			if (obj instanceof IDiagramUIModel)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.PACKAGE_LOGO)));
			else if (obj instanceof IClass)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.CLASS_LOGO)));
			else if (obj instanceof IAttribute)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.CLASS_LOGO)));
			else if (obj instanceof IAssociation)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.ASSOCIATION_LOGO)));
			else if (obj instanceof IAssociationEnd)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.CLASS_LOGO)));
			else if (obj instanceof IGeneralization)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.GENERALIZATION_LOGO)));
			else if (obj instanceof IGeneralizationSet)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.GENERALIZATION_LOGO)));
			else if (obj instanceof IAssociationClass)
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.ASSOCIATION_LOGO)));
			else
				label.setIcon(new ImageIcon(ViewUtils.getFilePath(ViewUtils.PACKAGE_LOGO)));

			checkBox.setSelected(cn.isSelected);
			checkBox.setText(getNameNode(node));

			tree.repaint();

			checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);

			return this;
		}

	}

	private String getNameNode(DefaultMutableTreeNode node) {
		String nameNode = "";

		Object obj = ((DefaultMutableTreeNode) node).getUserObject();

		if (obj instanceof IAttribute) {
			nameNode = "Attribute";

			String attributeName = "";
			String attributeType = "";

			if (((IAttribute) obj).getName() != null)
				attributeName = ((IAttribute) obj).getName();

			if (((IAttribute) obj).getType() != null)
				attributeType = ((IAttribute) obj).getTypeAsString();

			nameNode = attributeName + " : " + attributeType;

		} else if (obj instanceof IAssociation) {
			nameNode = "Association";
			String nameFrom = "associationFrom";
			String nameTo = "associationTo";

			if (((IAssociation) obj).getFrom().getName() != null
					&& !((IAssociation) obj).getFrom().getName().equals(""))
				nameFrom = ((IAssociation) obj).getFrom().getName();

			if (((IAssociation) obj).getTo().getName() != null && !((IAssociation) obj).getTo().getName().equals(""))
				nameTo = ((IAssociation) obj).getTo().getName();

			nameNode = "(" + nameFrom + " -> " + nameTo + ")";

		} else if (obj instanceof IAssociationEnd) {
			nameNode = "AssociationEnd";

			if (((IAssociationEnd) obj).getName() != null && !((IAssociationEnd) obj).getName().equals(""))
				nameNode = ((IAssociationEnd) obj).getName();

		} else if (obj instanceof IGeneralization) {
			nameNode = "Generalization";
			String nameFrom = "generalizationFrom";
			String nameTo = "generalizationTo";

			if (((IGeneralization) obj).getFrom().getName() != null
					&& !((IGeneralization) obj).getFrom().getName().equals(""))
				nameFrom = ((IGeneralization) obj).getFrom().getName();

			if (((IGeneralization) obj).getTo().getName() != null
					&& !((IGeneralization) obj).getTo().getName().equals(""))
				nameTo = ((IGeneralization) obj).getTo().getName();

			nameNode = "(" + nameFrom + " -> " + nameTo + ")";

		} else if (obj instanceof IAssociationClass) {
			nameNode = "AssociationClass";
			String nameFrom = "associationClassFrom";
			String nameTo = "associationClassTo";

			if (((IAssociationClass) obj).getFrom().getName() != null
					&& !((IAssociationClass) obj).getFrom().getName().equals(""))
				nameFrom = ((IAssociationClass) obj).getFrom().getName();

			if (((IAssociationClass) obj).getTo().getName() != null
					&& !((IAssociationClass) obj).getTo().getName().equals(""))
				nameTo = ((IAssociationClass) obj).getTo().getName();

			nameNode = "(" + nameFrom + " -> " + nameTo + ")";

		} else if (obj instanceof IDiagramUIModel) {
			nameNode = "Diagram";

			if (((IDiagramUIModel) obj).getName() != null && !((IDiagramUIModel) obj).getName().equals(""))
				nameNode = ((IDiagramUIModel) obj).getName();

		} else if (obj instanceof IModelElement) {
			nameNode = "ModelElement";

			if (((IModelElement) obj).getName() != null && !((IModelElement) obj).getName().equals(""))
				nameNode = ((IModelElement) obj).getName();

		} else if (obj instanceof String) {
			nameNode = obj.toString();
		}

		return nameNode;
	}

	/*
	 * private void visitAllNodes(DefaultMutableTreeNode node) {
	 * 
	 * Object obj = ((DefaultMutableTreeNode) node).getUserObject();
	 * 
	 * if (node.getChildCount() >= 0) { for (Enumeration<? extends TreeNode> e =
	 * node.children(); e.hasMoreElements();) { DefaultMutableTreeNode n =
	 * (DefaultMutableTreeNode) e.nextElement(); visitAllNodes(n); } } }
	 */

	public JCheckBoxTree(String type) {

		super(getTreeModel(type));
		// Disabling toggling by double-click
		this.setToggleClickCount(0);
		// Overriding cell renderer by new one defined above
		CheckBoxCellRenderer cellRenderer = new CheckBoxCellRenderer();
		this.setCellRenderer(cellRenderer);

		DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
			private static final long serialVersionUID = -8190634240451667286L;

			public void setSelectionPath(TreePath path) {
				boolean checkMode = !nodesCheckingState.get(path).isSelected;
				checkSubTree(path, checkMode);
				updatePredecessorsWithCheckMode(path, checkMode);
				// Firing the check change event
				fireCheckChangeEvent(new CheckChangeEvent(new Object()));
				// Repainting tree after the data structures were updated
				selfPointer.repaint();
			}

			public void addSelectionPath(TreePath path) {
			}

			public void removeSelectionPath(TreePath path) {
			}

			public void setSelectionPaths(TreePath[] pPaths) {
			}
		};

		this.setSelectionModel(dtsm);
	}

	// When a node is checked/unchecked, updating the states of the predecessors
	protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
		TreePath parentPath = tp.getParentPath();
		// If it is the root, stop the recursive calls and return
		if (parentPath == null) {
			return;
		}
		CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
		parentCheckedNode.allChildrenSelected = true;
		parentCheckedNode.isSelected = false;
		for (int i = 0; i < parentNode.getChildCount(); i++) {
			TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
			CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
			// It is enough that even one subtree is not fully selected
			// to determine that the parent is not fully selected
			if (!childCheckedNode.allChildrenSelected) {
				parentCheckedNode.allChildrenSelected = false;
			}
			// If at least one child is selected, selecting also the parent
			if (childCheckedNode.isSelected) {
				parentCheckedNode.isSelected = true;
			}
		}
		if (parentCheckedNode.isSelected) {
			checkedPaths.add(parentPath);
		} else {
			checkedPaths.remove(parentPath);
		}
		// Go to upper predecessor
		updatePredecessorsWithCheckMode(parentPath, check);
	}

	// Recursively checks/unchecks a subtree
	protected void checkSubTree(TreePath tp, boolean check) {
		CheckedNode cn = nodesCheckingState.get(tp);
		cn.isSelected = check;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
		for (int i = 0; i < node.getChildCount(); i++) {
			checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
		}
		cn.allChildrenSelected = check;
		if (check) {
			checkedPaths.add(tp);
		} else {
			checkedPaths.remove(tp);
		}
	}

	private static TreeModel getTreeModel(String type) {
		if (type.equals("package"))
			return getTreeModelPackage();
		else
			return getTreeModelDiagram();
	}

	private static TreeModel getTreeModelPackage() {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL, IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION, IModelElementFactory.MODEL_TYPE_DATA_TYPE };

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Models");

		for (IModelElement rootElement : project.toModelElementArray(rootLevelElements))
			setChildrenRecursively(rootElement, root);
		
		return new DefaultTreeModel(root);
	}

	private static TreeModel getTreeModelDiagram() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject()
				.toDiagramArray();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Class Diagram");

		if (diagramArray != null) {

			for (IDiagramUIModel diagram : diagramArray) {

				if (diagram instanceof IClassDiagramUIModel) {
					DefaultMutableTreeNode parent;
					parent = new DefaultMutableTreeNode(diagram);
					root.add(parent);
					setChildrenFromDiagrams(diagram, parent);
				}
			}
		}

		return new DefaultTreeModel(root);
	}

	private static DefaultMutableTreeNode setChildrenRecursively(IModelElement modelElement, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode newRoot = parent;
		
		final String[] rootLevelElementsPriority = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL };
		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION, IModelElementFactory.MODEL_TYPE_DATA_TYPE };
		
		if(modelElement instanceof IModel) {
			IModel model = (IModel) modelElement;
			
			
			DefaultMutableTreeNode newParent;
			newParent = new DefaultMutableTreeNode(model);
			
			for(IModelElement element : model.toChildArray(rootLevelElementsPriority)) {
				
				newRoot.add(setChildrenRecursively(element, newParent));
			}
			
			for(IModelElement element : model.toChildArray(rootLevelElements)) {
				
				newRoot.add(setChildrenRecursively(element, newParent));
			}
		}
		
		if(modelElement instanceof IPackage) {
			IPackage pckg = (IPackage) modelElement;
			
			DefaultMutableTreeNode newParent;
			newParent = new DefaultMutableTreeNode(pckg);
			
			for(IModelElement element : pckg.toChildArray(rootLevelElementsPriority)) {
				
				newRoot.add(setChildrenRecursively(element, newParent));
			}
			
			for(IModelElement element : pckg.toChildArray(rootLevelElements)) {
				newRoot.add(setChildrenRecursively(element, newParent));
			}
		}
		
		
		if(modelElement instanceof IClass) {
			IClass _class = (IClass) modelElement;
			DefaultMutableTreeNode newParent;
			
			newParent = new DefaultMutableTreeNode(_class);

			IAttribute[] attributes = _class.toAttributeArray();

			for (int j = 0; attributes != null && j < attributes.length; j++)
				newParent.add(new DefaultMutableTreeNode(attributes[j]));

			newRoot.add(newParent);
		}
		
		if(modelElement instanceof IAssociation) {
			IAssociation association = (IAssociation) modelElement;
			DefaultMutableTreeNode newParent;
		
			newParent = new DefaultMutableTreeNode(association);

			IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
			IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

			if (fromEnd != null)
				newParent.add(new DefaultMutableTreeNode(fromEnd));

			if (toEnd != null)
				newParent.add(new DefaultMutableTreeNode(toEnd));

			newRoot.add(newParent);
		}

		if(modelElement instanceof IGeneralizationSet) {
			IGeneralizationSet generalizationSet = (IGeneralizationSet) modelElement;
			DefaultMutableTreeNode newParent;
			
			newParent = new DefaultMutableTreeNode(generalizationSet);
			newRoot.add(newParent);
		}
		
		
		if(modelElement instanceof IGeneralization) {
			IGeneralization generalization = (IGeneralization)modelElement;
			DefaultMutableTreeNode newParent;

			newParent = new DefaultMutableTreeNode(generalization);
			newRoot.add(newParent);
		}

		if(modelElement instanceof IAssociationClass) {
			IAssociationClass associationClass = (IAssociationClass) modelElement;
			DefaultMutableTreeNode newParent;

			newParent = new DefaultMutableTreeNode(associationClass);
			newRoot.add(newParent);
		}

		return newRoot;
	}

	private static DefaultMutableTreeNode setChildrenFromDiagrams(IDiagramUIModel diagram,
			DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode newRoot = parent;
		IDiagramElement[] classes = diagram.toDiagramElementArray(IModelElementFactory.MODEL_TYPE_CLASS);
		IDiagramElement[] associations = diagram.toDiagramElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
		IDiagramElement[] generalizationSets = diagram
				.toDiagramElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET);
		IDiagramElement[] generalizations = diagram
				.toDiagramElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION);
		IDiagramElement[] associationClasses = diagram
				.toDiagramElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS);

		for (int i = 0; classes != null && i < classes.length; i++) {
			if (classes[i].getModelElement() != null) {
				IClass _class = (IClass) classes[i].getModelElement();

				DefaultMutableTreeNode newParent;
				newParent = new DefaultMutableTreeNode(_class);

				IAttribute[] attributes = _class.toAttributeArray();

				for (int j = 0; attributes != null && j < attributes.length; j++)
					newParent.add(new DefaultMutableTreeNode(attributes[j]));

				newRoot.add(newParent);
			}
		}

		for (int i = 0; associations != null && i < associations.length; i++) {
			if (associations[i].getModelElement() != null) {
				IAssociation association = (IAssociation) associations[i].getModelElement();
				DefaultMutableTreeNode newParent;

				newParent = new DefaultMutableTreeNode(association);

				IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
				IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

				if (fromEnd != null)
					newParent.add(new DefaultMutableTreeNode(fromEnd));

				if (toEnd != null)
					newParent.add(new DefaultMutableTreeNode(toEnd));

				newRoot.add(newParent);

			}
		}

		for (int i = 0; generalizationSets != null && i < generalizationSets.length; i++) {
			if (generalizationSets[i].getModelElement() != null) {
				IGeneralizationSet generalizationSet = (IGeneralizationSet) generalizationSets[i].getModelElement();
				DefaultMutableTreeNode newParent;
				
				newParent = new DefaultMutableTreeNode(generalizationSet);
				newRoot.add(newParent);
			}
		}

		for (int i = 0; generalizations != null && i < generalizations.length; i++) {
			if (generalizations[i].getModelElement() != null) {
				IGeneralization generalization = (IGeneralization) generalizations[i].getModelElement();
				DefaultMutableTreeNode newParent;

				newParent = new DefaultMutableTreeNode(generalization);
				newRoot.add(newParent);
			}
		}

		for (int i = 0; associationClasses != null && i < associationClasses.length; i++) {
			if (associationClasses[i].getModelElement() != null) {
				IAssociationClass associationClass = (IAssociationClass) associationClasses[i].getModelElement();
				DefaultMutableTreeNode newParent;

				newParent = new DefaultMutableTreeNode(associationClass);
				newRoot.add(newParent);
			}
		}

		return newRoot;
	}

}