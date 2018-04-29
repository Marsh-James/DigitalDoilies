import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Stack;

/**
 * @author James Marsh
 * This class manages all the storage and drawing of instructions
 * 		and user input along with responses to interface changes by means of getter and setters
 * 
 * An independent set of listeners is also defined so that the main panel can react to user input when
 * 		drawing objects
 *
 */
public class Display extends JPanel {
	
	/**
	 * Method defines the specific set of possible events for which the interface
	 * 		must respond
	 */
	class drawMouseListener implements MouseListener, MouseMotionListener{
		
		// This method responds to mouse dragged user input
		public void mouseDragged(MouseEvent e) {
			// So that the mouse position stays consistent the values are stored
			int currentX = e.getX();
			int currentY = e.getY();
			
			/*
			 *  If statement checks rough area to see if the user is not trying to draw in the same space
			 *  	resulting in smoother lines. The smoothness of the lines is defined by the mouseSensitivity which
			 *  	can be controlled by the user on the main interface inside the control panel
			 */
			if ((currentX > mouseSensitivity  + mouseBeginX || currentX < mouseBeginX - mouseSensitivity ) ||
					(currentY > mouseBeginY + mouseSensitivity  || currentY < mouseBeginY - mouseSensitivity )) {

				// Line next position is drawn on next
				nextPath.lineTo(currentX, currentY);
				// Draw method to display the information is updated
				draw(nextPath);
				
				// Mouse begin and finish is set for the next sensitivity check when the mouse moves again
				mouseBeginX = e.getX();
				mouseBeginY = e.getY();
			}
		}

		// Method handles mouse pressed event
		public void mousePressed(MouseEvent e) { 
			// Redo stack is removed so that the user can not redo anything to stop insertion anomalies
			Display.this.clearRedo();
			// Mouse initial position is set so it can be checked if it is dragged
			mouseBeginX = e.getX();
			mouseBeginY = e.getY();
			
			// New instance of the path is creates so that it can be updated on drag
			nextPath = new Path2D.Double();
			// Line position is set and starts to be drawn
			nextPath.moveTo(mouseBeginX, mouseBeginY);
			nextPath.lineTo(mouseBeginX, mouseBeginY);
		}

		// Method handles mouse released event
		public void mouseReleased(MouseEvent e) { 
			// A new instance of a frame is generated so it can be set and pushed onto the instruction stack
			LineData nextData = new LineData();
			
			// All information used to draw the current line is stored so it can be unpacked when required
			nextData.setLine(nextPath);
			nextData.setReflect(isRepeated);
			nextData.setEraser(isEraser);
			nextData.setPenSize(penSize);
			nextData.setPenColor(penRed, penGreen, penBlue);
			
			// Data is pushed onto the instruction stack
			penStack.push(nextData);
		}
		
		// On mouse click the line is updated if needs be
		public void mouseClicked(MouseEvent e) {
			// Simple position passed into the line as it does not need to be checked
			nextPath.lineTo(e.getX(), e.getY());
			// Points are drawn
			draw(nextPath);
			/*
			 * Final redraw allows for the instruction stack to be fully redrawn to make sure
			 * 		the interface stays responsive and updates when needs be
			 */
			redraw();
		}
		
		public void mouseMoved(MouseEvent e) { }

		public void mouseEntered(MouseEvent e) { }

		public void mouseExited(MouseEvent e) {	}

	}
	
	// All variables are initialised on startup and set as private for better encapsulation
	private BufferedImage drawSpace;
	
	private Stack<LineData> penStack = new Stack<>(); 
	private Stack<LineData> redoStack = new Stack<>(); 
	private Path2D nextPath = null;
	
	private int mouseSensitivity = 1;
	private int mouseBeginX = 0;
	private int mouseBeginY = 0;
	
	private int centerX = 0;
	private int centerY = 0;
	
	private int penRed = 192;
	private int penGreen = 192;
	private int penBlue = 192;
	private int penSize = 2;
	
	private boolean isRepeated = true;
	private boolean isEraser = false;
	private boolean isSectorsOn = true;
	private double sectors = 30.0;	
	
	/**
	 * Constructor sets any information post initialisation if required
	 * @param doilyHeight doily height is stored for later processing
	 * @param doilyWidth doily width is stored for later image processing
	 */
	public Display(int doilyHeight, int doilyWidth) {
		// Simple size settings are defined so that the design is consistent 
		this.setPreferredSize(new Dimension(doilyWidth, doilyHeight));
		this.setMaximumSize(new Dimension(doilyWidth, doilyHeight));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		// Listeners are added to the main drawing panel
		this.addMouseListener(new drawMouseListener());
		this.addMouseMotionListener(new drawMouseListener());
		
		// Default background is set
		this.setBackground(Color.GRAY);
	}
	
	// Method clears information from the stacks for the display
	public void clear() {
		clearSurface();
		repaint();
		penStack.clear();
	}
	
	// Method saves the current information for the display onto a smaller gallery panel
	public void save(Gallery userGallery) {
		userGallery.saveDoilyToFrame(this, drawSpace, penStack);
	}
	
	// Method draws the given instruction set onto the buffered image displayed from the gallery
	public void displaySavedDoily(Stack<LineData> doilyPaintInstructions) {
		// Stacks are copied for better consistency
		Stack<LineData> currentInstructions = new Stack<LineData>();
		currentInstructions.addAll(doilyPaintInstructions);
		
		// Surface is cleared for new drawing and is repainted to draw lines
		clearSurface();
		repaint();
		
		// Normals stacks are cleared so that the new information can be appended to a clear stack
		redoStack.clear();
		penStack.clear();
		// Instructions are appended to stack
		penStack.addAll(currentInstructions);
		// Stack is redrawn on buffered image
		redraw();
	}
	
	// Given frame is passed through and removed if invoked
	public void remove(Gallery userGallery) {
		userGallery.removeDoilyFrame();
	}
	
	// Method operates an undo function invoked from button press
	public void undo() {
		// Provided there is instruction to undo in the stack
		if (!penStack.empty()) {
			// Clear the buffered image to draw on
			clearSurface();
			// Update that change on the display
			repaint();
			// Remove the top most instruction and store in the redo stack if redo is invoked
			redoStack.push(penStack.pop());
			// Redraw the entire stack which excludes the most recent change
			redraw();
		}
	}
	
	// Method operates an redo function invoked from button press
	public void redo() {
		// Provided there is instruction to redo in the stack
		if (!redoStack.empty()) {
			// Clear the buffered image to draw on
			clearSurface();
			// Update that change on the display
			repaint();
			// Remove the top most instruction and store in the undo stack if undo is invoked
			penStack.push(redoStack.pop());
			// Redraw the entire stack which excludes the most recent change
			redraw();
		}
	}
	
	// Method allows the eraser type to be set by boolean argument
	public void setEraser(boolean isEraser) {
		this.isEraser = isEraser;
	}
	
	// Method allows the sector lines to be drawn or removed by boolean argument
	public void setSectorsOn(boolean isOn) {
		this.isSectorsOn = isOn;
		// After value is updated whole image is redrawn
		repaint();
		/*
		 *  When the image base on the display is redrawn the stack is then redrawn on
		 *  	the buffered image to remove the change of new insertion or update anomalies on user
		 *  	mouse button press
		 */
		redraw();
	}
	
	// Method allows the repeated drawings to be set by boolean argument
	public void setRepeated(boolean isRepeated) {
		this.isRepeated = isRepeated;
	}
	
	// Method allows for the pen red value to be updated by integer argument
	public void setPenRed(int red) {
		this.penRed = red;
	}
	
	// Method allows for the pen green value to be updated by integer argument
	public void setPenGreen(int green) {
		this.penGreen = green;
	}
	
	// Method allows for the pen blue value to be updated by integer argument
	public void setPenBlue(int blue) {
		this.penBlue = blue;
	}
	
	// Method allows for the mouse sensitivity value to be updated by integer argument
	public void setMouseSensitivity(int mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}
	
	// Method allows for the pen size value to be updated by integer argument
	public void setPenSize(int penSize) {
		this.penSize = penSize;
	}
	
	// Method allows the number of sectors to be updated and redrawn real time
	public void setSectorVal(Integer sectorCount) {
		this.sectors = sectorCount.doubleValue();
		// Entire buffered image is cleared
		clearSurface();
		// Updated number of sectors is now redrawn by the default paintComponent
		repaint();
		// Repeated number of drawing instances are redrawn to the correct number of sectors
		redraw();
	}
		
	/**
	 * Method draws the default background for the display panel, since display extends jpanel
	 * 		is may operate in the same fashion. This method is called on repaint() and updates the base
	 * 		surface of the doily
	 */
	public void paintComponent(Graphics g) {
		// Extension of JPanel requires a super instantiation of the jpanel
		super.paintComponent(g);
		
		// If there exists no buffered image for this display one is created on the same dimensions
		if (drawSpace == null) {
			// Buffered image is assigned and created if one does not exist
			drawSpace = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			// Buffered image is stored inside the label which sits on top of the display panel
			JLabel drawnLabel = new JLabel(new ImageIcon(drawSpace));
			// Alignment is set to sit in the centre of the display panel
			drawnLabel.setHorizontalAlignment(JLabel.CENTER);
			drawnLabel.setVerticalAlignment(JLabel.CENTER);
			
			this.add(drawnLabel);
		}
		
		// Centre values are stored so they may be reused and remain consistent
		centerX = (this.getWidth() / 2);
		centerY = (this.getHeight() / 2);
		// Pen drawing the sector lines are the same set to a color lighter than the background
		g.setColor(Color.LIGHT_GRAY);
		
		// If the centre lines are on in the boolean value stored as a class member variable
		if (this.isSectorsOn) {
			// Circular information is received
			double radius = (this.getHeight() / 2);
			double theta = 360.d / this.sectors;
		
			/*
			 * Using trigonometry and position of the next line end from the centre are calculated by using
			 * 		sin and cosine waves which oscillate out of phase of each other, they can thus be used to calculate
			 * 		the edge coordinates of a circle by looking at the arc length
			 * 
			 * Loop starts at -90 as they are drawn from the eastward facing line thus when they are reflected they appear
			 * 		to remain symmetrical by shifting a constant line from east to north
			 */
			for (double i = -90.0; i < 360.d; i += theta) {
				double nextX = radius * Math.cos(Math.toRadians(i)) + centerX;
				double nextY =  radius * Math.sin(Math.toRadians(i)) + centerY;
				// End line is drawn from the centre to the calculated coordinate
				g.drawLine(centerX, centerY, (int)nextX, (int)nextY);
			}
		}
	}
	
	// Method redraws the entire instruction stack (penStack variable)
	private void redraw() {
		// So no inconsistencies arise the stack is copied
		Stack<LineData> tempHistoryStack = new Stack<LineData>();
		// So the stack is not drawn in inverse order the stack has to be reversed
		tempHistoryStack.addAll(reverseStack(penStack));
		
		// All initial instance information must be stored so it can be returned
		boolean tempRepeat = this.isRepeated;
		boolean tempEraser = this.isEraser;
		int tempPenSize = this.penSize;
		int tempPenRed = this.penRed;
		int tempPenBlue = this.penBlue;
		int tempPenGreen = this.penGreen;
		
		// A while loop will iterate through the copied instruction stack
		while (!tempHistoryStack.empty()) {
			// One at a time the information is popped from the stack
			LineData nextStackData = tempHistoryStack.pop();
			// All the information for the time of the drawing is set and the line is drawn
			Path2D nextStackLine = nextStackData.getLine();
			
			this.isRepeated = nextStackData.getReflect();
			this.isEraser = nextStackData.getEraser();
			this.penSize = nextStackData.getPenSize();
			this.penRed = nextStackData.getPenRed();
			this.penGreen = nextStackData.getPenGreen();
			this.penBlue = nextStackData.getPenBlue();
			
			// The line is drawn by the invoking of the draw method
			draw(nextStackLine);
		}
		// Once all redrawing is complete, the original settings are set back
		this.isRepeated = tempRepeat;
		this.isEraser = tempEraser;
		this.penRed = tempPenRed;
		this.penGreen = tempPenGreen;
		this.penBlue = tempPenBlue;
		this.penSize = tempPenSize;
		
		// The temporary stack is the cleared to remove unnecessary memory usage
		tempHistoryStack.clear();
	}
	
	/**
	 * This method takes a given line and draws it to the specified number of times
	 * @param currentPath is a Path2D object the the path information inside it
	 */
	private void draw(Path2D currentPath) {
		// An object to repeat is generated as a Graphics2D from the stored BufferedImage
		Graphics2D repeatObj = (Graphics2D) drawSpace.getGraphics();
		// The angle between repeats is calculated as a float for better redrawing
		double theta = 360.d / this.sectors;
		
		/*
		 * If the current pen is an eraser alpha composite mask is set over the top to modify
		 * 		the pixels in the buffered image to draw out
		 * Otherwise it is set to the normal mask and the pen information set by he JSliders by the user
		 * 		are used
		 */
		if (isEraser) {
			repeatObj.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
		} else {
			repeatObj.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			repeatObj.setColor(new Color(this.penRed, this.penGreen, this.penBlue));
		}
			
		// The loop operates on the number of sectors the line needs to be drawn in
		for (double i = 0.0; i < this.sectors; i += 1) {
			// The path2D stroke size is set by the user stroke size JSlider in the control panel
			repeatObj.setStroke(new BasicStroke(this.penSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			/*
			 * AffineTransform is a linear transformation mask that is applied
			 * 		to the path in order to recalculate  its position given a new amount of rotation
			 * i * theta is equivalent to the new angle of rotation
			 */
			AffineTransform linearTransform = AffineTransform.getRotateInstance(
	                Math.toRadians(theta * i),
	                this.getWidth()/2,
	                this.getHeight()/2
	        );
			
			// The rotated object is then drawn onto the Graphics2D object
			repeatObj.draw(linearTransform.createTransformedShape(currentPath));
			
			// If the boolean argument is satisfied another transformation takes place
            if (isRepeated) {
            	/*
            	 *  AffineTransform applies another linear transformation to mirror the image cross a sector line
            	 *  	which allows for the reflection criterion to be met
            	 */
            	linearTransform.translate(this.getWidth(), 0);
                linearTransform.scale(-1, 1);

                // Reflected object is then drawn using this transformation
                repeatObj.draw(linearTransform.createTransformedShape(currentPath));
            }
            // Buffered image is then updated on top of the display
    		repaint();
		}
	}
	
	// Method removes all information on the display buffered image
	private void clearSurface() {
		// Graphics object created from buffered image
		Graphics2D graphicsObj = (Graphics2D) drawSpace.getGraphics();
		// Transparent gray background is set (color.darkgray values)
        graphicsObj.setBackground(new Color(192, 192, 192, 0));
        // Clear rectangle is drawn over the top to mask previous drawing
        graphicsObj.clearRect(0, 0, drawSpace.getWidth(), drawSpace.getHeight());
	}
	
	// Method takes a given stack and reverses it returning that as the output
	private Stack<LineData> reverseStack(Stack<LineData> originalStack) {
		// Stack is copied to remove change of any anomalies occurring from reversal
		Stack<LineData> bullStack = new Stack<LineData>();
		bullStack.addAll(originalStack);
		// Reversed stack instance is instantiated
		Stack<LineData> reversedStack = new Stack<LineData>();
		
		/*
		 * While the original stack is not empty each value is popped and pushed into the new stack
		 * 		resulting in the top object being at the bottom of the new stack and vice versa
		 */
		while (!bullStack.empty()) {
			reversedStack.push(bullStack.pop());
		}
		
		// Reverse stack is returned as the intended output
		return reversedStack;
	}
	
	// Method clears the redo stack
	private void clearRedo() {
		/*
		 *  Simple invoke of the clear method on the stack
		 *  	this is done to improve code readability
		 */
		redoStack.clear();
	}
}
