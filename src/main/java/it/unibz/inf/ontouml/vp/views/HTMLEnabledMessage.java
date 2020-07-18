package it.unibz.inf.ontouml.vp.views;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.net.URI;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class HTMLEnabledMessage extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public HTMLEnabledMessage(String htmlBody) {
		super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    // Process the click event on the link (for example with java.awt.Desktop.getDesktop().browse())
                    System.out.println(e.getURL()+" was clicked");
                    
                    try {
            			final Desktop desktop = Desktop.getDesktop();
            			desktop.browse(e.getURL().toURI());
            		} catch (UnsupportedOperationException unsupportedException) {
            			ViewUtils.reportBugErrorDialog(true);
            			unsupportedException.printStackTrace();
            		} catch (SecurityException securityException) {
            			ViewUtils.reportBugErrorDialog(true);
            			securityException.printStackTrace();
            		} catch(Exception exception) {
            			ViewUtils.reportBugErrorDialog(false);
            			exception.printStackTrace();
            		}
                }
            }
        });
        setEditable(false);
        setBorder(null);
	}

	static StringBuffer getStyle() {
		// for copying style
		JLabel label = new JLabel();
		Font font = label.getFont();
		Color color = label.getBackground();

		// create some css from the label's font
		StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
		style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		style.append("font-size:" + font.getSize() + "pt;");
		style.append("background-color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
		return style;
	}
}
