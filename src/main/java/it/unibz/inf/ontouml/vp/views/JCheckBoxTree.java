package it.unibz.inf.ontouml.vp.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
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
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

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
		ElementNode node = (ElementNode) getModel().getRoot();
		if (node == null) {
			return;
		}
		addSubtreeToCheckingStateTracking(node);
	}

	// Creating data structure of the current model for the checking mechanism
	private void addSubtreeToCheckingStateTracking(ElementNode node) {
		TreeNode[] path = node.getPath();
		TreePath tp = new TreePath(path);
		CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
		nodesCheckingState.put(tp, cn);
		for (int i = 0; i < node.getChildCount(); i++) {
			addSubtreeToCheckingStateTracking(
					(ElementNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
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
			ElementNode node = (ElementNode) value;
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

			checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);

			return this;
		}

	}

	private String getNameNode(ElementNode node) {
		String nameNode = "";

		Object obj = ((ElementNode) node).getUserObject();

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

			if (((IAssociation) obj).getFrom() == null) {
				nameFrom = "";
			} else {
				nameFrom = ((IAssociation) obj).getFrom().getName();
			}

			if (((IAssociation) obj).getTo() == null) {
				nameTo = "";
			} else {
				nameTo = ((IAssociation) obj).getTo().getName();
			}

			nameNode = "(" + nameFrom + " -> " + nameTo + ")";
			nameNode = "(" + nameFrom + " -> " + nameTo + ")";

		} else if (obj instanceof IAssociationEnd) {
			nameNode = "AssociationEnd";

			if (((IAssociationEnd) obj).getName() != null && !((IAssociationEnd) obj).getName().equals(""))
				nameNode = ((IAssociationEnd) obj).getName();

		} else if (obj instanceof IGeneralization) {
			nameNode = "Generalization";
			String nameFrom = "generalizationFrom";
			String nameTo = "generalizationTo";

			if (((IGeneralization) obj).getFrom() == null) {
				nameFrom = "";
			} else {
				nameFrom = ((IGeneralization) obj).getFrom().getName();
			}

			if (((IGeneralization) obj).getTo() == null) {
				nameTo = "";
			} else {
				nameTo = ((IGeneralization) obj).getTo().getName();
			}

			nameNode = "(" + nameFrom + " -> " + nameTo + ")";

		} else if (obj instanceof IAssociationClass) {
			nameNode = "AssociationClass";
			String nameFrom = "associationClassFrom";
			String nameTo = "associationClassTo";

			if (((IAssociationClass) obj).getFrom() == null) {
				nameFrom = "";
			} else {
				nameFrom = ((IAssociationClass) obj).getFrom().getName();
			}

			if (((IAssociationClass) obj).getTo() == null) {
				nameTo = "";
			} else {
				nameTo = ((IAssociationClass) obj).getTo().getName();
			}

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
				findSimilarNodes((ElementNode) path.getLastPathComponent());

				selectTypeNodes((ElementNode) path.getLastPathComponent());

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

	private void visitAllNodesAndCheckSimilar(ElementNode root, ElementNode nodeToCompare) {

		// if same Object
		if (((ElementNode) root).getUserObject().equals(nodeToCompare.getUserObject())) {

			TreeNode[] old_treeNode = nodeToCompare.getPath();
			TreePath oldTp = new TreePath(old_treeNode);

			TreeNode[] new_treeNode = root.getPath();
			TreePath newTp = new TreePath(new_treeNode);

			// if different paths
			if (!oldTp.toString().equals(newTp.toString())) {

				boolean checkMode = nodesCheckingState.get(oldTp).isSelected;
				checkSubTree(newTp, checkMode);
				updatePredecessorsWithCheckMode(newTp, checkMode);
				fireCheckChangeEvent(new CheckChangeEvent(new Object()));
			}

		}

		if (root.getChildCount() >= 0) {
			for (Enumeration<? extends TreeNode> e = root.children(); e.hasMoreElements();) {
				ElementNode n = (ElementNode) e.nextElement();

				visitAllNodesAndCheckSimilar(n, nodeToCompare);
			}
		}
	}

	protected void findSimilarNodes(ElementNode node) {

		for (int i = 0; i < node.getChildCount(); i++)
			findSimilarNodes((ElementNode) node.getChildAt(i));

		ElementNode root = (ElementNode) this.getModel().getRoot();
		visitAllNodesAndCheckSimilar(root, node);
	}

	private void visitAllNodesAndCheckType(ElementNode root, ElementNode nodeToCompare) {
		String typeOriginal = "";
		String typeCurrentNode = "";
		
		if (nodeToCompare.getUserObject() instanceof IClass)
			typeOriginal = ((IClass) nodeToCompare.getUserObject()).getName();

		if (nodeToCompare.getUserObject() instanceof IAttribute)
			typeOriginal = ((IAttribute) nodeToCompare.getUserObject()).getTypeAsString();

		if (root.getUserObject() instanceof IClass)
			typeCurrentNode = ((IClass) root.getUserObject()).getName();
		
		if (root.getUserObject() instanceof IDataType)
			typeCurrentNode = ((IDataType) root.getUserObject()).getName();

		if (typeCurrentNode.contentEquals(typeOriginal)) {

			TreeNode[] old_treeNode = nodeToCompare.getPath();
			TreePath oldTp = new TreePath(old_treeNode);

			TreeNode[] new_treeNode = root.getPath();
			TreePath newTp = new TreePath(new_treeNode);

			// if different paths
			if (!oldTp.toString().equals(newTp.toString())) {
				boolean checkMode = nodesCheckingState.get(oldTp).isSelected;
			
				if ((root.getUserObject() instanceof IClass || root.getUserObject() instanceof IDataType) && checkMode == true) {

					CheckedNode cn = nodesCheckingState.get(newTp);
					cn.isSelected = checkMode;

					if (checkMode)
						checkedPaths.add(newTp);
					else
						checkedPaths.remove(newTp);
				}

			}
		}

		if (root.getChildCount() >= 0) {
			for (Enumeration<? extends TreeNode> e = root.children(); e.hasMoreElements();) {
				ElementNode n = (ElementNode) e.nextElement();

				visitAllNodesAndCheckType(n, nodeToCompare);
			}
		}
	}

	protected void selectTypeNodes(ElementNode node) {
		ElementNode root = (ElementNode) this.getModel().getRoot();
		visitAllNodesAndCheckType(root, node);
	}

	// When a node is checked/unchecked, updating the states of the predecessors
	protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
		TreePath parentPath = tp.getParentPath();
		// If it is the root, stop the recursive calls and return
		if (parentPath == null)
			return;

		CheckedNode currentNode = nodesCheckingState.get(tp);
		// It is allowed to choose a class without its attributes
		if (((ElementNode) tp.getLastPathComponent()).getUserObject() instanceof IAttribute
				&& currentNode.isSelected == false)
			return;

		CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
		ElementNode parentNode = (ElementNode) parentPath.getLastPathComponent();
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
		ElementNode node = (ElementNode) tp.getLastPathComponent();
		selectTypeNodes(node);
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
				IModelElementFactory.MODEL_TYPE_DATA_TYPE };
		final String[] anyLevelElements = { IModelElementFactory.MODEL_TYPE_GENERALIZATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION, IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS };

		ElementNode root = new ElementNode("All Models");

		for (IModelElement rootElement : project.toModelElementArray(rootLevelElements))
			setChildrenRecursively(rootElement, root);

		for (IModelElement rootElement : project.toAllLevelModelElementArray(anyLevelElements))
			setChildrenRecursively(rootElement, root);

		return new DefaultTreeModel(root);
	}

	private static TreeModel getTreeModelDiagram() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject()
				.toDiagramArray();

		ElementNode root = new ElementNode("Class Diagram");

		if (diagramArray != null) {

			for (IDiagramUIModel diagram : diagramArray) {

				if (diagram instanceof IClassDiagramUIModel) {
					ElementNode parent;
					parent = new ElementNode(diagram);
					root.add(parent);
					setChildrenFromDiagrams(diagram, parent);
				}
			}
		}

		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String[] datatypes = { IModelElementFactory.MODEL_TYPE_DATA_TYPE };

		for (IModelElement datatype : project.toModelElementArray(datatypes)) {
			ElementNode datatypeNodes = new ElementNode(datatype);
			root.add(datatypeNodes);
		}

		return new DefaultTreeModel(root);
	}

	private static ElementNode setChildrenRecursively(IModelElement modelElement, ElementNode parent) {
		ElementNode newRoot = parent;

		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL, IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION, IModelElementFactory.MODEL_TYPE_DATA_TYPE,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET, IModelElementFactory.MODEL_TYPE_GENERALIZATION };

		if (modelElement instanceof IModel) {
			IModel model = (IModel) modelElement;

			ElementNode newParent;
			newParent = new ElementNode(model);

			for (IModelElement element : model.toChildArray(rootLevelElements))
				newRoot.add(setChildrenRecursively(element, newParent));
		}

		if (modelElement instanceof IPackage) {
			IPackage pckg = (IPackage) modelElement;

			ElementNode newParent;
			newParent = new ElementNode(pckg);

			for (IModelElement element : pckg.toChildArray(rootLevelElements))
				newRoot.add(setChildrenRecursively(element, newParent));
		}

		if (modelElement instanceof IClass) {
			IClass _class = (IClass) modelElement;
			ElementNode newParent;

			newParent = new ElementNode(_class);

			IAttribute[] attributes = _class.toAttributeArray();

			for (int j = 0; attributes != null && j < attributes.length; j++)
				newParent.add(new ElementNode(attributes[j]));

			newRoot.add(newParent);
		}

		if (modelElement instanceof IAssociation) {
			IAssociation association = (IAssociation) modelElement;
			ElementNode newParent;

			newParent = new ElementNode(association);

			IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
			IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

			if (fromEnd != null)
				newParent.add(new ElementNode(fromEnd));

			if (toEnd != null)
				newParent.add(new ElementNode(toEnd));

			newRoot.add(newParent);
		}

		if (modelElement instanceof IDataType) {
			IDataType _class = (IDataType) modelElement;

			ElementNode newParent;
			newParent = new ElementNode(_class);

			newRoot.add(newParent);
		}

		if (modelElement instanceof IGeneralizationSet) {
			IGeneralizationSet generalizationSet = (IGeneralizationSet) modelElement;
			ElementNode newParent;

			newParent = new ElementNode(generalizationSet);
			newRoot.add(newParent);
		}

		if (modelElement instanceof IGeneralization) {
			IGeneralization generalization = (IGeneralization) modelElement;
			ElementNode newParent;

			if (generalization.getFrom() != null && generalization.getTo() != null) {
				newParent = new ElementNode(generalization);
				newRoot.add(newParent);
			}
		}

		if (modelElement instanceof IAssociationClass) {
			IAssociationClass associationClass = (IAssociationClass) modelElement;
			ElementNode newParent;

			newParent = new ElementNode(associationClass);
			newRoot.add(newParent);
		}

		return newRoot;
	}

	private static ElementNode setChildrenFromDiagrams(IDiagramUIModel diagram, ElementNode parent) {
		ElementNode newRoot = parent;
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

				ElementNode newParent;
				newParent = new ElementNode(_class);

				IAttribute[] attributes = _class.toAttributeArray();

				for (int j = 0; attributes != null && j < attributes.length; j++)
					newParent.add(new ElementNode(attributes[j]));

				newRoot.add(newParent);
			}
		}

		for (int i = 0; associations != null && i < associations.length; i++) {
			if (associations[i].getModelElement() != null) {
				IAssociation association = (IAssociation) associations[i].getModelElement();
				ElementNode newParent;

				newParent = new ElementNode(association);

				IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
				IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

				if (fromEnd != null)
					newParent.add(new ElementNode(fromEnd));

				if (toEnd != null)
					newParent.add(new ElementNode(toEnd));

				newRoot.add(newParent);

			}
		}

		for (int i = 0; generalizationSets != null && i < generalizationSets.length; i++) {
			if (generalizationSets[i].getModelElement() != null) {
				IGeneralizationSet generalizationSet = (IGeneralizationSet) generalizationSets[i].getModelElement();
				ElementNode newParent;

				newParent = new ElementNode(generalizationSet);
				newRoot.add(newParent);
			}
		}

		for (int i = 0; generalizations != null && i < generalizations.length; i++) {
			if (generalizations[i].getModelElement() != null) {
				IGeneralization generalization = (IGeneralization) generalizations[i].getModelElement();
				ElementNode newParent;

				newParent = new ElementNode(generalization);
				newRoot.add(newParent);
			}
		}

		for (int i = 0; associationClasses != null && i < associationClasses.length; i++) {
			if (associationClasses[i].getModelElement() != null) {
				IAssociationClass associationClass = (IAssociationClass) associationClasses[i].getModelElement();
				ElementNode newParent;

				newParent = new ElementNode(associationClass);
				newRoot.add(newParent);
			}
		}

		return newRoot;
	}

	private void visitAllNodesAndCheckSameId(ElementNode root, String id) {

		TreeNode[] treeNode = root.getPath();
		TreePath tp = new TreePath(treeNode);
		CheckedNode cn = new CheckedNode(false, root.getChildCount() > 0, false);

		TreePath parentPath = tp.getParentPath();
		// the root item is assumed to be always selected if not this function is not
		// called
		if (parentPath == null) {
			cn.isSelected = true;
			checkedPaths.add(tp);
			nodesCheckingState.put(tp, cn);
		}

		// if modelElement
		if (((ElementNode) root).getUserObject() instanceof IModelElement) {
			IModelElement nodeElement = (IModelElement) root.getUserObject();

			if (nodeElement.getId().contentEquals(id)) {
				cn.isSelected = true;
				checkedPaths.add(tp);
				nodesCheckingState.put(tp, cn);
			}
		}

		// if Diagram
		if (((ElementNode) root).getUserObject() instanceof IDiagramUIModel) {
			IDiagramUIModel diagramElement = (IDiagramUIModel) root.getUserObject();

			if (diagramElement.getId().contentEquals(id)) {
				cn.isSelected = true;
				checkedPaths.add(tp);
				nodesCheckingState.put(tp, cn);
			}
		}

		if (root.getChildCount() >= 0) {
			for (Enumeration<? extends TreeNode> e = root.children(); e.hasMoreElements();) {
				ElementNode n = (ElementNode) e.nextElement();

				visitAllNodesAndCheckSameId(n, id);
			}
		}
	}

	public void setNodesCheck(HashSet<String> idElements) {
		resetCheckingState();
		ElementNode root = (ElementNode) treeModel.getRoot();

		Iterator<String> ite = idElements.iterator();

		while (ite.hasNext()) {
			String id = ite.next();
			visitAllNodesAndCheckSameId(root, id);
		}

	}

	public static class ElementNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 1L;

		public ElementNode(Object userObject) {
			super(userObject);
		}

		@Override
		public void add(MutableTreeNode newChild) {
			super.add(newChild);
			sort();
		}

		@SuppressWarnings("unchecked")
		public void sort() {
			Collections.sort(children, compare());
		}

		@SuppressWarnings("rawtypes")
		private Comparator compare() {
			return new Comparator<DefaultMutableTreeNode>() {
				@Override
				public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
					String arg1 = "";
					String arg2 = "";

					if (o1 == null)
						return 0;
					if (o2 == null)
						return 0;

					if (o1.getUserObject() instanceof IDiagramUIModel) {
						if (o2.getUserObject() instanceof IDiagramUIModel) {
							return ((IDiagramUIModel) o1.getUserObject()).getName()
									.compareTo(((IDiagramUIModel) o2.getUserObject()).getName());
						} else {
							return 0;
						}
					}

					if (o1.getUserObject() instanceof IModelElement) {
						if (o2.getUserObject() instanceof IModelElement) {
							IModelElement element1 = (IModelElement) o1.getUserObject();
							IModelElement element2 = (IModelElement) o2.getUserObject();

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)
									&& element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)
									&& element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)
									&& element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)) {
								return -1;
							} else if (element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_PACKAGE)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)
									&& element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)) {
								return -1;
							} else if (element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_MODEL)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_CLASS)
									&& element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_CLASS)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_CLASS)) {
								return -1;
							} else if (element2.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_CLASS)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)
									&& element2.getModelType()
											.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
								return -1;
							} else if (element2.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
								return 1;
							}

							if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET)
									&& element2.getModelType()
											.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET)) {
								return -1;
							} else if (element2.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)
									&& element2.getModelType()
											.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
								return -1;
							} else if (element2.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)
									&& element2.getModelType()
											.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
								return -1;
							} else if (element2.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
								return 1;
							}

							if (element1.getModelType().contentEquals(IModelElementFactory.MODEL_TYPE_DATA_TYPE)
									&& element2.getModelType()
											.contentEquals(IModelElementFactory.MODEL_TYPE_DATA_TYPE)) {
								arg1 = element1.getName();
								arg2 = element2.getName();
							} else if (element1.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_DATA_TYPE)) {
								return -1;
							} else if (element2.getModelType()
									.contentEquals(IModelElementFactory.MODEL_TYPE_DATA_TYPE)) {
								return 1;
							}

						}
					}

					if (arg1 == null)
						arg1 = "";
					if (arg2 == null)
						arg2 = "";

					return arg1.compareTo(arg2);
				}

			};

		}
	}

}