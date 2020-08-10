package it.unibz.inf.ontouml.vp.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

final class ContextMenuListener extends MouseAdapter {
	private ArrayList<String> idModelElementList;
	private JList<Object> messageList;

	ContextMenuListener(ArrayList<String> list, JList<Object> messages) {
		super();
		idModelElementList = list;
		messageList = messages;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		final Point p = e.getPoint();
		final int index = messageList.locationToIndex(p);

		messageList.setSelectedIndex(index);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		messageList.clearSelection();
	}

	public void mouseReleased(MouseEvent e) {
		doPop(e);
	}

	private void doPop(MouseEvent e) {
		ContextMenu menu;
		String idModelElement = idModelElementList.get(messageList.locationToIndex(e.getPoint()));

		if (idModelElement == null) {
			menu = new ContextMenu();
		} else {
			menu = new ContextMenu(idModelElement);
			if (!ViewUtils.isElementInAnyDiagram(idModelElement)) {
				menu.disableItem("Take me there!");
			}
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
