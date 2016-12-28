package lib;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RN2Node {
	/**
	 * A generic class to represent any game object at all. Visible or invisible.
	 * Contains basic properties such as position, scale, rotation, and opacity.
	 */
	
	public CopyOnWriteArrayList<RN2Node> children = new CopyOnWriteArrayList<RN2Node>();
	public WeakReference<RN2Node> parent;

	public String name = "unnamed";
	public RN2Point position = new RN2Point(0, 0);
	public double zPosition = 0;
	public double zRotation = 0; // in radians
	public double xScale = 1, yScale = 1;
	protected double opacity = 1;
	HashMap<String, RN2Action> runningActions = new HashMap<String, RN2Action>();
	
	public RN2Node() {
		
	}
	
	public void runAllActionsForSelfAndChildren(double deltaTime) {
		for(RN2Action action: runningActions.values()) {
			action.runTheActionBlock(deltaTime);
		}
		
		runningActions.entrySet().removeIf(e-> e.getValue().actionFinished() );
		
		if(children.size()==0) {
			//Woohoo! Done!
		} else {
			for(RN2Node child: children) {
				child.runAllActionsForSelfAndChildren(deltaTime);
			}
		}
	}
	
	public void runActionWithKey(RN2Action a, String key) {
		a.activate(this);
		runningActions.put(key, a);
	}
	
	public void runAction(RN2Action a) {
		runActionWithKey(a, "null");
	}
	
	public RN2Action getActionWithKey(String key) {
		return runningActions.get(key);
	}
	
	/**
	 * Sets this node's opacity
	 * @param o new opacity
	 * @throws IllegalArgumentException if 0<=opacity<=1 isn't followed.
	 */
	public void setOpacity(double o) throws IllegalArgumentException {
		if(o>1 || o<0) {
			throw new IllegalArgumentException("Opacity must be greater than 0 and less than 1.");
		} else {
			this.opacity = o;
		}
	}
	
	public void incrementOpacity(double o) throws IllegalArgumentException {
		setOpacity(opacity + o);
	}
	
	/**
	 * Adds a given node to this node's children.
	 * @param newNode the node to be added
	 * @throws IllegalStateException if the node to be added already is a child of another node
	 */
	public void addChild(RN2Node newNode) throws IllegalStateException {
		if(newNode.parent == null) {
			children.add(newNode);
			newNode.parent = new WeakReference<RN2Node>(this);
		} else {
			throw new IllegalStateException("Cannot add a child that already has a parent.");
		}
	}
	
	/**
	 * Removes the given child node from its parent node's children array.
	 * @param child the child to be removed
	 * @throws IllegalStateException if the unwanted child can't be found
	 */
	public void removeChild(RN2Node child) throws IllegalStateException {
		if(children.contains(child)) {
			children.remove(child);
		}
		else {
			throw new IllegalStateException("The child isn't in the "
					+ "children array and therfore can't be removed.");
		}
	}
	
	public RN2Node getChildNodeByName(String name) throws IllegalStateException {
		for(RN2Node child: children) {
			if(child.name.equals(name)) {
				return child;
			}
		}
		throw new IllegalStateException("No child with the specified name could be "
				+ "found from this parent's children.");
	}
	
	/**
	 * The address is an array of integers that functions like a map to get to 
	 * this node starting from the highest ancestor. The number of elements in
	 * the array indicates which generation the node is in. The numbers themselves
	 * are indices in children arrays. The final number in the address is what index
	 * this node is at in its parent's children array. If this node itself has no parent,
	 * the array is empty.
	 * @return the node's address.
	 */
	public int[] getAddress() {
		if(parent==null) {
			return new int[]{};
		} else if(parent.get().parent==null) {
			return new int[]{parent.get().children.indexOf(this)};
		} else {
			return RN2ArrayUtils.concatenate(parent.get().getAddress(), 
					new int[]{parent.get().children.indexOf(this)});
		}
	}
	
	/**
	 * Converts a given point from the reference frame of this node to 
	 * the reference frame of a destination node, taking into account position,
	 * zRotation, and x/y scales.
	 * @param point the point to be converted
	 * @param destinationNode the final node the point should be converted to
	 * @return the converted point
	 * @throws IllegalStateException if the conversion is impossible or unnecessary
	 */
	public RN2Point convertPointToNode(RN2Point point, RN2Node destinationNode) throws IllegalStateException {
		if(destinationNode==this) { //A super derp exception
			throw new IllegalStateException("Why would you need to convert the point?"
					+ " Your destination node is the same as your starting node.");
		}
		
		// Base case: If the destinationNode is a direct child of this node
		if(children.contains(destinationNode)) {
			return convertPointToBeRelativeToChild(point, destinationNode);
		}
		
		// Base case: If the destination node is this node's parent
		if(parent != null && parent.get() == destinationNode) {
			return convertPointToBeRelativeToParent(point);
		}
				
		// If none of the base cases are satisfied, we must compute the next move
		// using addresses. Remember that these addresses are like maps that tell you
		// which turns to take and in which order.
		// -If this node is a direct ancestor of the destination node, go down
		//- Otherwise, go up;
		int[] destinationAddress = destinationNode.getAddress();
		int[] myAddress = this.getAddress();
		if(destinationAddress.length > myAddress.length && 
				RN2ArrayUtils.arraysEqual(myAddress, 
				Arrays.copyOfRange(destinationAddress, 0, myAddress.length))) {
			RN2Node rightPathChild = children.get(destinationAddress[myAddress.length]);
			return rightPathChild.convertPointToNode(convertPointToBeRelativeToChild(point, rightPathChild), destinationNode);
		} else {
			return parent.get().convertPointToNode(
					convertPointToBeRelativeToParent(point), destinationNode);
		}
		
	}
	
	private RN2Point convertPointToBeRelativeToParent(RN2Point point) {
		RN2Point pt = new RN2Point(point); // Dummy point
		// Just a few steps.
		// -Multiply pt by this node's scale values
		// -Rotate point by this node's zRotation
		// -Add this node's position
		pt.x *= this.xScale;
		pt.y *= this.yScale;
		pt.rotateAboutOriginByRad(this.zRotation);
		pt.x += this.position.x;
		pt.y += this.position.y;
		return pt;
	}
	
	private RN2Point convertPointToBeRelativeToChild(RN2Point point, RN2Node child) {
		// The same as the previous method, but the OPPOSITE
		RN2Point pt = new RN2Point(point);
		pt.x -= child.position.x;
		pt.y -= child.position.y;
		pt.rotateAboutOriginByRad(-child.zRotation);
		pt.x /= child.xScale;
		pt.y /= child.yScale;
		return pt;
	}
	
	
	/**
	 * @return the zPosition from the ultimate perspective (ie relative to the highest parent)
	 */
	public double getAbsoluteZPosition() {
		if(parent==null) {
			return zPosition;
		} else {
			return zPosition + parent.get().getAbsoluteZPosition();
		}
	}
	
//	public double getAbsoluteZRotation() {
//		// returns the zPosition from the ultimate perspective (ie relative to the highest parent)
//		if(parent==null) {
//			return zRotation;
//		} else {
//			return zRotation + parent.get().getAbsoluteZRotation();
//		}
//	}

	
	/**
	 * @return the opacity the node should be rendered with.
	 */
	public double getAbsoluteOpacity() {
		if(parent==null) {
			return opacity;
		} else  {
			return this.opacity * parent.get().getAbsoluteOpacity();
		}
	}
	
	public RN2Node duplicate() {
		RN2Node clone = new RN2Node();
		setSameBasicPropertiesOnNode(clone);
		addDuplicatesOfAllChildrenToNode(clone);
		return clone;
	}
	
	protected void setSameBasicPropertiesOnNode(RN2Node other) {
		other.position.x = position.x;
		other.position.y = position.y;
		other.zPosition = zPosition;
		other.opacity = opacity;
		other.xScale = xScale; other.yScale = yScale;
		other.zRotation = zRotation;
	}
	
	protected void addDuplicatesOfAllChildrenToNode(RN2Node other) {
		for(RN2Node child: children) {
			other.addChild(child.duplicate());
		}
	}
		
}
