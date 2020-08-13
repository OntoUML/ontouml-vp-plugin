package it.unibz.inf.ontouml.vp.listeners;

import javax.swing.*;

import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.IssueLogMenu;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public final class IssueLogMenuListener extends MouseAdapter {
	private ArrayList<String> idModelElementList;
	private JList<Object> messageList;

	public IssueLogMenuListener(ArrayList<String> list, JList<Object> messages) {
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
		IssueLogMenu menu;
		String idModelElement = idModelElementList.get(messageList.locationToIndex(e.getPoint()));

		if (idModelElement == null) {
			menu = new IssueLogMenu();
		} else {
			menu = new IssueLogMenu(idModelElement);
			if (!ViewUtils.isElementInAnyDiagram(idModelElement)) {
				menu.disableItem("Take me there!");
			}
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
