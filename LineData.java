import java.awt.geom.Path2D;

/**
 * 
 * @author James Marsh
 * This class is used to store each stroke and the data associated
 * The use case for this class is on the undo and redo stacks so the correct pen information
 * 		is accessed and used. This also includes information on pen reflection
 *
 */
class LineData {
	private Path2D line = null;
	
	private boolean reflect = false;
	private boolean eraser = false;
	
	private int penRed = 0;
	private int penGreen = 0;
	private int penBlue = 0;
	private int penSize = 0;
	
	// Method takes instance of Path2D drawn and stored
	public void setLine(Path2D line) {
		this.line = line;
	}
	
	// Method takes the reflect value at the time of drawing and is stored
	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}
	
	// Method takes the eraser value at the time of drawing and is stored
	public void setEraser(boolean eraser) {
		this.eraser = eraser;
	}
	
	// Method takes the pen size value at the time of drawing and is stored
	public void setPenSize(int penSize) {
		this.penSize = penSize;
	}
	
	// Method takes the color values at the time of drawing and is stored
	public void setPenColor(int red, int green, int blue) {
		this.penRed = red;
		this.penGreen = green;
		this.penBlue = blue;
	}
	
	// Method returns the stored value of the Path2D for the instance of drawing Path2D stored
	public Path2D getLine() {
		return this.line;
	}
	
	// Method returns the stored value of the reflect for the instance of drawing boolean stored
	public boolean getReflect() {
		return this.reflect;
	}
	
	// Method returns the stored value of the eraser for the instance of drawing boolean stored
	public boolean getEraser() {
		return this.eraser;
	}
	
	// Method returns the stored value of the red pen for the instance of drawing value stored
	public int getPenRed() {
		return this.penRed;
	}
	
	// Method returns the stored value of the green pen for the instance of drawing value stored
	public int getPenGreen() {
		return this.penGreen;
	}
	
	// Method returns the stored value of the blue pen for the instance of drawing value stored
	public int getPenBlue() {
		return this.penBlue;
	}
	
	// Method returns the stored value of the pen for the instance of drawing value stored
	public int getPenSize() {
		return this.penSize;
	}
		
}
	
