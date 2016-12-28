package lib;

import java.awt.Color;

public class RN2PolygonNode extends RN2Node {
	/**
	 * A node class for representing all types of polygons with different colors.
	 * The coordinates of the vertices can be relative to any origin, allowing for
	 * different pivot and scaling behaviors. Convenient builder classes have been defined
	 * for common polygons.
	 */
	public RN2Point[] vertices;
	public Color color = Color.RED;
	public RN2PolygonNode(double[] xv, double[] yv, int numPoints) {
		vertices = new RN2Point[numPoints];
		for(int i=0; i<numPoints; i++) {
			vertices[i] = new RN2Point(xv[i], yv[i]);
		}
	}
	public RN2PolygonNode(RN2Point[] vertices) {
		this.vertices = vertices;
	}
	
	@Override
	public RN2PolygonNode duplicate() {
		RN2PolygonNode clone = new RN2PolygonNode(vertices.clone());
		setSameBasicPropertiesOnNode(clone);
		addDuplicatesOfAllChildrenToNode(clone);
		clone.color = new Color(color.getRed(), color.getGreen(), color.getBlue());
		return clone;
	}
	
	// Builders added for your convenience when creating a polygon node. you're welcome.
	public static class RectBuilder {
		private double width=100, height=100;
		private double anchorX = 0.5, anchorY = 0.5;
		public static RectBuilder builder() {
			return new RectBuilder();
		}
		public RectBuilder withWidth(double w) {width = w; return this;}
		public RectBuilder withHeight(double h) {height = h; return this;}
		public RectBuilder withAnchorX(double ax) {anchorX = ax; return this;}
		public RectBuilder withAnchorY(double ay) {anchorY = ay; return this;}
		public RN2PolygonNode build() {
			RN2Point[] vertices = new RN2Point[4];
			vertices[0] = new RN2Point(0-width*anchorX, 0-height*anchorY);
			vertices[1] = new RN2Point(0-width*anchorX, height-height*anchorY);
			vertices[2] = new RN2Point(width-width*anchorX, height-height*anchorY);
			vertices[3] = new RN2Point(width-width*anchorX, 0-height*anchorY);
			return new RN2PolygonNode(vertices);
		}
	}
	public static class RegularPolygonBuilder {
		private int numSides = 3;
		private double radius = 50;
		public static RegularPolygonBuilder builder() {
			return new RegularPolygonBuilder();
		}
		public RegularPolygonBuilder withNumOfSides(int n) {numSides = n; return this;}
		public RegularPolygonBuilder withRadius(double r) {radius = r; return this;}

		public RN2PolygonNode build() {
			RN2Point[] vertices = new RN2Point[numSides];
			double deltaAngle = 2*Math.PI/numSides;
			for(int i=0; i<numSides; i++) {
				vertices[i] = new RN2Point(radius*Math.cos(i*deltaAngle), 
						radius*Math.sin(i*deltaAngle));
			}
			return new RN2PolygonNode(vertices);
		}
	}
	
}
