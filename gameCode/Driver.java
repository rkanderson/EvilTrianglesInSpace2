package gameCode;

import java.awt.Color;

import javax.swing.JFrame;

import lib.*;

public class Driver {
	static final double framePause = 1.0/60.0;
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Evil Triangles in Space");
        frame.setSize(800, 600);
        RN2GamePanel gameView = new RN2GamePanel();
        frame.add(gameView);
        gameView.initialize();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Can set properties here
        RN2Scene theScene = new GameScene(gameView, (double)gameView.getWidth(), 
        		(double)gameView.getHeight());
        theScene.backgroundColor = Color.BLACK;
        gameView.presentScene(theScene);
        
        while(true) {
        	gameView.repaint();
        	gameView.update(framePause);
            Thread.sleep((long)(framePause*1000));
        }

	}
	

}
