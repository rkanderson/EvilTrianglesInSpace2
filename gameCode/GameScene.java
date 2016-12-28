package gameCode;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lib.*;

public class GameScene extends RN2Scene {

	
	ArrayList<Integer> keysDown;
	HashMap<String, Double> designatedZPositions = new HashMap<String, Double>();
	
	Player player;
	int score;
	double playerSpeed = 200;
	ArrayList<EvilTriangle> evilTriangles = new ArrayList<EvilTriangle>();
	double triangleWreckSpeed = 1000;
	double triangleGenCounter = 0;
	
	RN2Point cameraTargetPosition = new RN2Point(0, 0);
	double starConcentration = 0.0001; //starConcentration X numOfPixels on screen = number of stars on screen.
	ArrayList<RN2PolygonNode> starz = new ArrayList<RN2PolygonNode>();
	
	public GameScene(RN2GamePanel gamePanel, double width, double height) {
		super(gamePanel, width, height);
	}

	@Override
	public void initialize() {
		keysDown = gamePanel.get().keysDown;
		// This is my way of keeping order among the zPositions. Once more layers are added, 
		// thou shalt be thankful
		designatedZPositions.put("Player", 2.0);
		designatedZPositions.put("Star", 0.0);
		designatedZPositions.put("EvilTriangle", 1.0);

		
		player = new Player();
		this.addChild(player);
		player.zPosition = designatedZPositions.get("Player");
		player.velocity.dx = playerSpeed;
		cameraTargetPosition = new RN2Point(player.position.x + 200, player.position.y);
				
		
		this.runAction(RN2Action.repeatForever(RN2Action.sequence(new RN2Action[]{
				RN2Action.fadeInWithDuration(2),
				RN2Action.waitForDuration(2),
				RN2Action.fadeOutWithDuration(2)
		})));
	}

	@Override
	public void update(double deltaTime) {
		
		player.update(deltaTime);
		
		// A really renegade way to polish camera movement
		cameraTargetPosition.x = player.position.x + 200;
		cameraTargetPosition.y = player.position.y;
		camera.position.x += (cameraTargetPosition.x - camera.position.x) / 10;
		camera.position.y += (cameraTargetPosition.y - camera.position.y) / 10;
		
		//Spawn new triangles?
		triangleGenCounter += deltaTime;
		if(triangleGenCounter > 2.5) {
			triangleGenCounter = 0;
			EvilTriangle et = new EvilTriangle(Math.random() >= 0.5 ? true : false);
			et.position = camera.convertPointToNode(new RN2Point(width/2 + 100, 0), this);
			et.zPosition = this.designatedZPositions.get("EvilTriangle");
			evilTriangles.add(et);
			addChild(et);
		}
		// Update Triangles
		for(int i = evilTriangles.size()-1; i>=0; i--) {
			EvilTriangle et = evilTriangles.get(i);
			et.update(deltaTime);
			if(et.pointInDoomSight(this.convertPointToNode(player.position, et))) {
				et.velocity.dy = (et.facingUp ? triangleWreckSpeed : -triangleWreckSpeed);
			}
			if(player.position.distanceTo(et.convertPointToNode(new RN2Point(0, 100), this)) <= player.radius ||
					player.position.distanceTo(et.convertPointToNode(new RN2Point(0, 0), this)) <= player.radius) {
				gameOver();
			}
			if(player.position.x > et.position.x && !et.isConquerred) {
				et.isConquerred = true;
				score += 1;
			}
			if(et.position.distanceTo(camera.position) >= 10000.0) {
				evilTriangles.remove(et);
			}
		}
		
		// Add stars if necessary.
		int starsINeed = (int)(starConcentration * this.width * this.height) - starz.size();
		if(starsINeed > 0) {
			for(int i=0; i<starsINeed; i++) {
				addStar();
			}
		}
		// Remove stars that are off screen
		for(int i=starz.size()-1; i>=0; i--) {
			// Notice how I traverse the array list backwards to avoid a concurrent 
			// modification exception.
			RN2PolygonNode star = starz.get(i);
			RN2Point starRelativeToCamera = this.convertPointToNode(star.position, camera);
			if(starRelativeToCamera.x > -width/2 && starRelativeToCamera.x < width/2 &&
					starRelativeToCamera.y > -height/2 && starRelativeToCamera.y < height/2) {
				//Nada!
			} else {
				starz.remove(star);
				this.removeChild(star);
			}
		}
				
	}
	
	private void gameOver() {
		System.out.println("Game over. Score: "+score);
		System.exit(0);
		// This shows how lazy i am about game over experience.
	}
	
	/**
	 * Constructs and adds exactly one star at some random position visible to the camera.
	 */
	private void addStar() {
		RN2PolygonNode star = new RN2PolygonNode.RegularPolygonBuilder()
				.withNumOfSides(3).withRadius(5).build(); //my Starz are triangles!
		star.color = new Color(100, 100, 100);
		star.position.x = camera.position.x - width/2 + Math.random()*width;
		star.position.y = camera.position.y - height/2 + Math.random()*height;
		star.zRotation = 2*Math.PI * Math.random();
		star.setOpacity(0);
		// Add a cool fade in effect
		RN2Action fadeIn = RN2Action.fadeToOpacityWithDuration(1, 1);
		star.runAction(fadeIn);
		this.addChild(star);
		starz.add(star);
	}

	@Override
	public void keyDown(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_UP && !keysDown.contains(KeyEvent.VK_DOWN)) {
			player.velocity.dy = playerSpeed*Math.sin(Math.PI/4);
			player.velocity.dx = playerSpeed*Math.cos(Math.PI/4);
		}else if (e.getKeyCode()==KeyEvent.VK_DOWN && !keysDown.contains(KeyEvent.VK_UP)){
			player.velocity.dy = playerSpeed*Math.sin(-Math.PI/4);
			player.velocity.dx = playerSpeed*Math.cos(-Math.PI/4);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_UP) {
			if(keysDown.contains(KeyEvent.VK_DOWN)) {
				player.velocity.dy = playerSpeed*Math.sin(-Math.PI/4);
				player.velocity.dx = playerSpeed*Math.cos(-Math.PI/4);
			} else {
				player.velocity.dy = 0;
				player.velocity.dx = playerSpeed;
			}
			
		}else if (e.getKeyCode()==KeyEvent.VK_DOWN){
			if(keysDown.contains(KeyEvent.VK_UP)) {
				player.velocity.dy = playerSpeed*Math.sin(Math.PI/4);
				player.velocity.dx = playerSpeed*Math.cos(Math.PI/4);
			} else {
				player.velocity.dy = 0;
				player.velocity.dx = playerSpeed;
			}
		}
	}

	@Override
	public void mouseDown(RN2Click c) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(RN2Click c) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(RN2Click c) {
//		followMouse.position = c.getPositionRelativeToNode(camera);
	}

	@Override
	public void mouseDragged(RN2Click c) {
	}

}
