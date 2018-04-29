import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author James Marsh
 * Method sets up and initialises all sections of the doily on top of a master panel
 * Master Panel organises the layout in the correct fashion using BorderLayout
 *
 */
public class DigitalWindow extends JFrame {
	// Setup the JFrame component that Digital window utilises
	public DigitalWindow(String title) { super(title);}
	
	// Initialise method begins all the panels and the original master panel
	public void init() {
		// Settings for window close and resize setting set false
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set false for the implementation, buffered image used on top of the JPanel can not be resized
		this.setResizable(false);
		
		// Doily master size is set
		int doilyWidth = 650;
		int doilyHeight = 650;
		
		// Master JPanel is developed for each component to be setup
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		this.add(masterPanel);

		/**
		 * Each sub-panel on the master panel is setup and arguments passed through
		 * The panels are added to the master panel in the correct order to be displayed which is
		 * 		defined by the BorderLayout argument
		 */
		Display doilyDisplayPanel = new Display(doilyHeight, doilyWidth);
		masterPanel.add(doilyDisplayPanel, BorderLayout.CENTER);
			
		Gallery doilyGalleryPanel = new Gallery(doilyHeight, doilyWidth);
		masterPanel.add(doilyGalleryPanel, BorderLayout.SOUTH);
		
		ControlPanel doilyControlPanel = new ControlPanel(doilyDisplayPanel, doilyGalleryPanel);
		masterPanel.add(doilyControlPanel, BorderLayout.NORTH);
		
		// Preferred sizes are setup for the GUI on visible
		this.pack();
		// GUI is set visible to become intractable
		this.setVisible(true);
	}
}
