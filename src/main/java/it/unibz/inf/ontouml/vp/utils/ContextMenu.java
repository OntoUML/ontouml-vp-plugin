package it.unibz.inf.ontouml.vp.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
final class ContextMenu extends JPopupMenu {
	private JMenuItem takeMeThere;
	private JMenuItem openSpec;
	private ActionListener menuListener;

	public ContextMenu() {

	}

	public ContextMenu(String idModelElement) {

		menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "Take me there!":
					System.out.println("Firing 'Highlight'");
					ViewUtils.highlightDiagramElement(idModelElement);
					break;
				case "Open Specification":
					System.out.println("Firing 'Open Specification'");
					ViewUtils.openSpecDiagramElement(idModelElement);
					break;
				default:
					break;
				}
			}
		};

		takeMeThere = new JMenuItem("Take me there!", new ImageIcon(ViewUtils.getFilePath(ViewUtils.NAVIGATION_LOGO)));
		takeMeThere.addActionListener(menuListener);
		openSpec = new JMenuItem("Open Specification", new ImageIcon(ViewUtils.getFilePath(ViewUtils.MORE_HORIZ_LOGO)));
		openSpec.addActionListener(menuListener);
		add(takeMeThere);
		add(openSpec);
	}

	public void disableItem(String item) {
		switch (item) {
		case "Take me there!":
			takeMeThere.setEnabled(false);
			break;
		case "Open Specification":
			openSpec.setEnabled(false);
			break;
		default:
			break;
		}
	}
}
