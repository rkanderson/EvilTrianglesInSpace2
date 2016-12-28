package lib;

public class RN2Point {
	public double x,y;
	public RN2Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public RN2Point(RN2Point dummy) {
		this.x = dummy.x;
		this.y = dummy.y;
	}
	public double distanceTo(RN2Point other) {
		return Math.sqrt(Math.pow(other.x-this.x, 2) +
				Math.pow(other.y-this.y, 2));
	}
	
	public void rotateAboutOtherPointByRad(RN2Point other, double rad) {
		//rotates pt1 about pt2 by given angle
		double rotX = this.x, rotY = this.y;
		double centerX = other.x; double centerY = other.y;
		double newX = centerX + (rotX-centerX)*Math.cos(rad) - (rotY-centerY)*Math.sin(rad);
		double newY = centerY + (rotX-centerX)*Math.sin(rad) + (rotY-centerY)*Math.cos(rad);
		this.x = newX;
		this.y = newY;
	}
	
	public void rotateAboutOriginByRad(double rad) {
		rotateAboutOtherPointByRad(new RN2Point(0,0), rad);
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}
}
