package gameCode;
import java.awt.Color;

import lib.*;


public class Player extends RN2Node {
	
	
	public double radius = 50;
	public RN2Vector velocity = new RN2Vector(0, 0);
	public double rotationalVelocity = -Math.PI/2;
	private RN2PolygonNode mainBody;
	
	public Player() {
		super();
		// Let's start with appearance
		constructBody();
		
	}
	
	/**
	 * Constructs visual parts of player's body.
	 */
	private void constructBody() {
		mainBody = RN2PolygonNode.RegularPolygonBuilder
				.builder().withNumOfSides(6).withRadius(radius).build();

		mainBody.color = Color.YELLOW;
		mainBody.zPosition = 0;
		addChild(mainBody);
		
		RN2PolygonNode eye = new RN2PolygonNode.RegularPolygonBuilder()
				.withNumOfSides(15).withRadius(radius*2/3).build();
		eye.color = Color.WHITE;
		eye.zPosition = 1;
		this.addChild(eye);
		
		RN2PolygonNode pupil =  new RN2PolygonNode.RegularPolygonBuilder()
				.withNumOfSides(15).withRadius(radius/4).build();
		pupil.color = Color.BLACK;
		pupil.zPosition = 2;
		pupil.position = new RN2Point(15, 0);
		this.addChild(pupil);
		
		RN2PolygonNode littleShineySpotOnPupil = new RN2PolygonNode.RegularPolygonBuilder()
				.withNumOfSides(15).withRadius(radius*1/14).build();
		littleShineySpotOnPupil.color = Color.WHITE;
		littleShineySpotOnPupil.zPosition = 0.5;
		littleShineySpotOnPupil.position = new RN2Point(5, 5);
		pupil.addChild(littleShineySpotOnPupil);
	}
	
	
	/**
	 * Updates many properties. Should be called every frame.
	 * @param deltaTime the amount of time that has passed since 
	 * the last update method was called. If it's the first time being called, 
	 * then it's zero.
	 */
	public void update(double deltaTime) {
		position.x += velocity.dx*deltaTime;
		position.y += velocity.dy*deltaTime;
		
		mainBody.zRotation += rotationalVelocity*deltaTime;
	}
}
