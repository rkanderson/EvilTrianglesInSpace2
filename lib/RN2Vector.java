package lib;

public class RN2Vector {
	public double dx, dy;
	public RN2Vector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	public String toString() {
		return "("+dx+", "+dy+")";
	}
}
