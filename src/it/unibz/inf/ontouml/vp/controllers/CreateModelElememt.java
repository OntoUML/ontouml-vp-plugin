package it.unibz.inf.ontouml.vp.controllers;

import java.awt.*;
import java.awt.geom.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.factory.IModelElementFactory;

public class CreateModelElememt extends AbstractShapeController implements VPShapeController {
//	private ApplicationManager app = ApplicationManager.instance();
	
	private DiagramManager dm = ApplicationManager.instance().getDiagramManager();
	private IModelElementFactory fac = IModelElementFactory.instance();

	@Override
	public void drawShape(Graphics2D aG, Paint aLineColor, Paint aFillColor, Stroke aStroke, VPShapeInfo aShapeInfo) {
		IDiagramUIModel d = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
		IClass c = fac.createClass();
		c.setName("New Kind");
		c.addStereotype("kind");
		
		IClassUIModel c_ui = (IClassUIModel) dm.createDiagramElement(d, c);
		c_ui.setX(aShapeInfo.getDiagramElement().getX());
		c_ui.setY(aShapeInfo.getDiagramElement().getY());
		aShapeInfo.setDiagramElement(c_ui);
		
		System.out.println(aShapeInfo.getDiagramElement().getShapeType());
//		aShapeInfo.
		
//		Shape lShape = createShape(aShapeInfo);
//		aG.setColor(new Color(255, 211, 15));
//		aG.fill(aShapeInfo.getBounds());
//		aG.setColor(Color.black);
//		aG.draw(aShapeInfo.getBounds());
	}

	@Override
	public boolean contains(int aX, int aY, VPShapeInfo aShapeInfo) {
//		Shape lShape = createShape(aShapeInfo);
		return aShapeInfo.getBounds().contains(aX, aY);
	}

	private Shape createShape(VPShapeInfo aShapeInfo) {
		Rectangle2D lBounds = aShapeInfo.getBounds();

		double lWidth = lBounds.getWidth();
		double lHeight = lBounds.getHeight();
		double lXUnit = lWidth / 18;
		double lYUnit = lHeight / 18;
		GeneralPath lPath = new GeneralPath();
		Point2D.Double[] lPoints = new Point2D.Double[] { new Point.Double(lXUnit * 1, lYUnit * 5),
				new Point.Double(lXUnit * 11, lYUnit * 0), new Point.Double(lXUnit * 17, lYUnit * 14),
				new Point.Double(lXUnit * 8, lYUnit * 17), new Point.Double(lXUnit * 2, lYUnit * 16) };

		int lIndex = 0;
		for (Point2D.Double lPoint : lPoints) {
			lPath.append(new Line2D.Double(lPoint, lPoints[(lIndex + 1) % lPoints.length]), true);
			lIndex++;
		}

		lPath.append(new Arc2D.Double(lXUnit * 3, lYUnit * 6, lXUnit * 3, lYUnit * 4, 0, 360, Arc2D.OPEN), false);
		lPath.append(new Arc2D.Double(lXUnit * 8, lYUnit * 4.5, lXUnit * 3, lYUnit * 4, 0, 360, Arc2D.OPEN), false);

		lPath.append(new Line2D.Double(lXUnit * 6.5, lYUnit * 12.5, lXUnit * 7.8, lYUnit * 13), false);
		lPath.append(new Line2D.Double(lXUnit * 7.8, lYUnit * 13, lXUnit * 9, lYUnit * 12), false);

		return lPath;
	}

}
