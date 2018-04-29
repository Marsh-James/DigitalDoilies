import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.Stack;

/**
 * @author James Marsh
 * This class manages the gallery and user interactions with the control panel to
 * 		store images and restore images that have been saved
 *
 */
public class Gallery extends JPanel{
	/**
	 * The show panel class holds the specific data and the current selected for each
	 * 		individual panel which holds images independent of each other
	 * 
	 */
	class showPanel extends JPanel{
		
		private Stack<LineData> instructions = new Stack<LineData>();
		
		private boolean resetImg = false;
		private boolean isSelected = false;
		private Image paintedImg = null;
		
		// Method clears the background and border and sets the preferred size for consistency of scaling
		public showPanel() {
			this.setBackground(Color.white);
			this.setPreferredSize(new Dimension(100,100));
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		// Method toggles the current value, inverting it from the stored value
		public void setToggleSelected() {
			isSelected = !isSelected;
		}
		
		// Method sets the current image stored as to the one that is passed in
		public void setImage(Image doilyImage) {
			paintedImg = doilyImage;
		}
		
		// Method sets the current stored stack as to the one passed in
		public void setInstructions(Stack<LineData> doilyInstructions) {
			instructions = doilyInstructions;
		}
		
		// Method returns the boolean stored to check if the current panel is selected
		public boolean getSelected() {
			return isSelected;
		}
		
		// Method returns the Stack stored to check if the current panel is selected
		public Stack<LineData> getInstructions() {
			return instructions;
		}
		
		// Method clears and signs the panel to be reset later on
		public void resetImage() {
			resetImg = true;
			instructions.clear();
		}
		
		// Method redraws the panel with the given image provided that it was not marked for reset
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Default background initialised
			this.setBackground(Color.GRAY);
			
			// Reset query is tested and sets as background image or resets if required
			if (!resetImg) {
				g.drawImage(paintedImg, -5, 0, this);
				
			} else {
				paintedImg = null;
				resetImg = false;
			}
		}
	}
	
	/**
	 * 
	 * Listener class that is implements MouseListener so that
	 * 		any panel that adds it will operate on mouse changes and clicks on it
	 *
	 */
	class showPanelListener implements MouseListener {
		
		// Method resets all selects and updates the border when a panel is clicked
		public void mouseClicked(MouseEvent e) {
			// Resets all panels
			resetAllSelects();
			// Toggles current panel as the selected panel
			((showPanel) e.getComponent()).setToggleSelected();
			// Updates all borders for each panel
			updateBorder((showPanel)e.getComponent(), Color.blue);
			
			// If the panel contains a saved doily it is displayed
			displaySavedDoily();
		}

		// Mouse entered method allows for border change when mouse enters its space
		public void mouseEntered(MouseEvent e) {
			updateBorder((showPanel)e.getComponent(), Color.red);
		}

		// Mouse exit method allows for border change when mouse exits its space
		public void mouseExited(MouseEvent e) {
			updateBorder((showPanel)e.getComponent(), Color.black);
		}

		public void mousePressed(MouseEvent e) { }

		public void mouseReleased(MouseEvent e) { }
	}
	
	// Private values allow for strong encapsulation and definitions
	private showPanel[] savedDoilies = new showPanel[12];	
	private Display currentDisplayInstance = null;
	
	private int currentSelect = -1;
	private int mainDoilyHeight = 0;
	private int mainDoilyWidth = 0;
	
	/**
	 * Constructor takes and initialises the gallery with the slider bars so that it is easier
	 * 		to navigate and use
	 * @param height is taken so that the images can be scaled properly
	 * @param width is taken so that the images can be set properly
	 * 
	 * All arguments are stored for later use
	 */
	public Gallery(int height, int width) {
		mainDoilyHeight = height;
		mainDoilyWidth = width;
		this.setPreferredSize(new Dimension(650, 125));
		
		// Layout is defined as a border layout for the gallery
		this.setLayout(new BorderLayout());
		// Master panel is set for each gallery saved panel
		JPanel galleryWindow = new JPanel();
		// The master panel as a sub layout of flow layout so that panels all appear in a line
		galleryWindow.setLayout(new FlowLayout());
		// Border is set so that the drag bar is not obscured
		galleryWindow.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		
		// Loop adds each panel to an index of the array, far more effective than adding 12 separate panels
		for (int i = 0; i < 12; i++) {
			savedDoilies[i] = new showPanel();
			savedDoilies[i].addMouseListener(new showPanelListener());
			// Each new window is added to the master panel
			galleryWindow.add(savedDoilies[i]);
		}
		
		// Master panel is added to the gallery instance on the digital window
		this.add(galleryWindow, BorderLayout.CENTER);
		
		// Drag bar is added and conditions set
		JScrollPane galleryScroll = new JScrollPane(galleryWindow);
		// Conditional policy set so that there does not exist a vertical scroll bar
		galleryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		// Scroll bar is added to the bottom of the master panel
		this.add(galleryScroll, BorderLayout.SOUTH);
		
	}
	
	/*
	 *  Method is activated on click of panel and sends the stack information to a setter on the main display
	 *  	 window for the image to be shown on the main display
	 */
	public void displaySavedDoily() {
		// Check to see if there exists a set of instructions for the panel in question
		if (!savedDoilies[currentSelect].getInstructions().isEmpty()) {
			// If there does then that stack of instructions is passed back out
			this.currentDisplayInstance.displaySavedDoily(savedDoilies[currentSelect].getInstructions());
		}
	}
	
	/**
	 * Method for displaying the doily as a smaller image in the gallery
	 * @param currentDisplayInstance passes in an instance of the drawing panel if there already isn't one
	 * @param doilyImg The image to be resized is passed in
	 * @param doilyInstructions The instructions for redrawing and resuming drawing is passed in to be stored
	 */
	public void saveDoilyToFrame(Display currentDisplayInstance, BufferedImage doilyImg, Stack<LineData> doilyInstructions) {
		/*
		 *  If the drawing panel does not already exist then it must be passed in so that it's setters can be recalled when required
		 *  This is done for stronger encapsulation of the project by only using setters and getters to interact with private variables
		 */
		if (this.currentDisplayInstance == null) {
			this.currentDisplayInstance = currentDisplayInstance;
			// If the doily size as has changed dependent on preferred information then it will update the sizing
			mainDoilyHeight = doilyImg.getHeight();
			mainDoilyWidth = doilyImg.getWidth();
		}
		
		// For a given image it will be rescaled down to match the size of the gallery panel of 100x100
		Image scaledImg = doilyImg.getScaledInstance((int)(mainDoilyWidth * (100.0 / (double)mainDoilyHeight)), 100, Image.SCALE_SMOOTH);
		
		// If there is a current version of the panel which is selected hence the -1 acts as a null pointer it will store the image
		if (currentSelect != -1) {
			// Temporary stack is created so that the stored version is independent of the current and ongoing modified version
			Stack<LineData> transferStack = new Stack<LineData>();
			transferStack.addAll(doilyInstructions);
			// Information for the future and current images are stored for referencing
			savedDoilies[currentSelect].setInstructions(transferStack);
			savedDoilies[currentSelect].setImage(scaledImg);
			
			// Gallery object is repainted so that it displays the new image
			savedDoilies[currentSelect].repaint();
		}
	}
	
	// Method removes the image from the frame
	public void removeDoilyFrame() {
		// Check to see if the frame a frame has been clicked or not
		if (currentSelect != -1) {
			// If there is a the reset image for the specific gallery panel is invoked and repainted to show the change
			savedDoilies[currentSelect].resetImage();
			savedDoilies[currentSelect].repaint();
		}
	}	
	
	// Method updates the border colours
	private void updateBorder(showPanel modifPanel, Color borderColor) {
		// The border color is set independent of if it is selected by default
		modifPanel.setBorder(BorderFactory.createLineBorder(borderColor));
		for (int i = 0; i < 12; i++) {
			if (savedDoilies[i].getSelected() == true) {
				// The current selected border is then updated to blue
				savedDoilies[i].setBorder(BorderFactory.createLineBorder(Color.blue));
				// The current selected panel is then tracked by updating the currentSelect variable
				currentSelect = i;
				
			}
		}
	}
	
	// Method resets all selected panels
	private void resetAllSelects() {
		for (int i = 0; i < 12; i++) {
			if (savedDoilies[i].getSelected() == true) {
				// If any panels are set to be true they are returned to false
				savedDoilies[i].setToggleSelected();
			}
			// Default border color background of black is set to update the change
			savedDoilies[i].setBorder(BorderFactory.createLineBorder(Color.black));
		}
	}
}
