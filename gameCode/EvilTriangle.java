package gameCode;

import java.awt.Color;

import lib.*;

public class EvilTriangle extends RN2Node {
	public boolean facingUp;
	public boolean isConquerred = false;
	public RN2Vector velocity = new RN2Vector(0, 0);
	public EvilTriangle(boolean facingUp) {
		this.facingUp = facingUp;
		buildBody();
		if(!facingUp) {
			this.zRotation = Math.PI;
		}
	}
	private void buildBody() {
		RN2PolygonNode mainBody = new RN2PolygonNode(new RN2Point[]{
				new RN2Point(-50, 0),
				new RN2Point(50, 0),
				new RN2Point(0, 100),
		});
		mainBody.color = Color.RED;
		addChild(mainBody);
		
	}
	public boolean pointInDoomSight(RN2Point pt) {
		// I'll assume the point to be relative to the triangle. It can be converted outside
		// this method.
		if(pt.x > -50 && pt.x < 50 && pt.y >= 100) {
			return true;
		}
		
		return false;
	}
	
	public void update(double dt) {
		position.x += velocity.dx*dt;
		position.y += velocity.dy*dt;
	}
}
