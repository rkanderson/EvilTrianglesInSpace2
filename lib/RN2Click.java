package lib;

public class RN2Click {
	public int x, y;
	public RN2Click(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Returns the position of the click relative to any node in the game world. That means
	 * that it's highest ancestor must an extension of RN2Scene. Use this method whenever
	 * you want to get useful data for mouse input, cuz otherwise you'll need to deal with
	 * int x and y stuff in a different coordinate system and it'll be annoying as sh*t.
	 * @param node any node (that's part of your game world)
	 * @return the point of the click relative to the given node
	 * @throws IllegalArgumentException if the highest ancestor of the node IS NOT an RN2Scene
	 */
	public RN2Point getPositionRelativeToNode(RN2Node node) throws IllegalArgumentException {
		RN2Scene scene;
		try {
			scene = (RN2Scene)getHighestAncestorOfNode(node);
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("The passed in node needs to have an "
					+ "RN2Scene object as its highest ancestor. This node u just passed"
					+ " is in an improper family or just kinda floating around on it's "
					+ "own. Be sure that it's added to game scene directly or indirectly.");
		}
		// It's very easy to convert the node to be relative to where the camera would be,
		// let's do that first.
		double relX = x - scene.width*scene.anchorX;
		double relY = scene.height - y - scene.height*scene.anchorY;
		RN2Point relativeToCamera = new RN2Point(relX, relY);
		if(node == scene.camera) {
			return relativeToCamera;
		} else {
			// If the node isn't the camera, then we already have that snazzy convert
			// point to node method, right?
			return scene.camera.convertPointToNode(relativeToCamera, node);
		}
	}
	
	private RN2Node getHighestAncestorOfNode(RN2Node n) {
		if(n.parent == null) {
			return n;
		} else {
			return getHighestAncestorOfNode(n.parent.get());
		}
	}
}
