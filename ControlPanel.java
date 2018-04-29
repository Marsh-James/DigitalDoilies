import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author James Marsh
 * This class is used to perform the original setup most of the user interface. This includes:
 * 		All JButtons
 * 		All JLabels
 * 		All JSpinners
 * 		All JSliders
 * Each section is divided into sections seen with the 'create' prefix on method identifiers.
 */
public class ControlPanel extends JPanel {
	// Constants are initialised, only used for multiple instantiations of JSliders
	private static final int PEN_START = 2;
	private static final int PEN_MIN = 0;
	private static final int PEN_MAX = 20;

	private static final int COLOR_START = 192;
	private static final int COLOR_MIN = 0;
	private static final int COLOR_MAX = 255;
	
	/**
	 * Instances of the drawing pane and saved doilies 
	 * @param doilyDisplayPanel Is used to store the current drawing pane
	 * @param doilyGalleryPaenl Is used to store the current instance of all
	 * 							drawings stored by a user
	 */
	Display doilyDisplayPanel = null;
	Gallery doilyGalleryPanel = null;

	/**
	 * @param chainDisplay Instance of the doily display pane is in when the control panel is initialised
	 * @param chainGallery Instance of the gallery is passed in as the control panel is initialised 
	 */
	public ControlPanel(Display chainDisplay, Gallery chainGallery) {
		this.doilyDisplayPanel = chainDisplay;
		this.doilyGalleryPanel = chainGallery;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		// All sub-panels are created and added by their respective methods
		createButtonPanel();
		createPenPanel();
		createSpinnerPanel();
		createRemovePanel();
	}
	
	/**
	 * Remove panel deals with buttons that interact with the gallery
	 * They utilise access to the instance of the display panel from when it was passed in
	 */
	private void createRemovePanel() {
		JPanel removePanel = new JPanel(new BorderLayout());
		
		// Identifying label on the button 'SAVE' is created to inform the user of functionality
		JButton btnAdd = new JButton("SAVE");
		
		// When button is added action listener for on click event is defined to invoke the public save method
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ControlPanel.this.doilyDisplayPanel.save(doilyGalleryPanel);
			}
		});
		// 'SAVE' Button is added to the sub-panel
		removePanel.add(btnAdd);
		
		// Identifying label on the button 'REMOVE' is created to inform the user of functionality
		JButton btnRemove = new JButton("REMOVE");
		// East location is set on the panel so that 'REMOVE' button is confined to one corner
		removePanel.add(btnRemove, BorderLayout.EAST);
		// When button is added action listener for on click event is defined to invoke the public remove method
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ControlPanel.this.doilyDisplayPanel.remove(doilyGalleryPanel);
			}
		});
		// 'REMOVE' Button is added to the sub-panel
		this.add(removePanel);
	}
	
	/**
	 * Button panel deals with buttons that interact with the drawing settings
	 * They utilise access to the instance of the display panel from when it was passed in
	 */
	private void createButtonPanel() {
		JPanel btnEventPanel = new JPanel();
		btnEventPanel.setLayout(new BoxLayout(btnEventPanel, BoxLayout.LINE_AXIS));

		// Identifying label on the radio button 'PEN' is created to inform the user of functionality
		JRadioButton rdoPen = new JRadioButton("PEN");
		// To initially start with a drawing device, the pen is set as the standard default
		rdoPen.setSelected(true);
		// Action listener will set the eraser status to false to indicate the type is a PEN as it can only be one of the two
		rdoPen.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            ControlPanel.this.doilyDisplayPanel.setEraser(false);
	        }
	    });
		// 'PEN' radio button is added to the sub-panel
		btnEventPanel.add(rdoPen);
		
		// Identifying label on the radio button 'ERASER' is created to inform the user of functionality
		JRadioButton rdoEraser = new JRadioButton("ERASER");
		// Action listener will set the eraser status true to indicate the type is an ERASER as it can only be one of the two
		rdoEraser.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            ControlPanel.this.doilyDisplayPanel.setEraser(true);
	        }
	    });
		// 'ERASER' radio button is added to the sub-panel
		btnEventPanel.add(rdoEraser);
		
		// Button group is added so auto switching will occur when one radio button is selected
		ButtonGroup penTypeGroup = new ButtonGroup();
		// Both radio buttons are added to the group so switching will occur
	    penTypeGroup.add(rdoPen);
	    penTypeGroup.add(rdoEraser);
		
	    // Identifying label on the check box 'TOGGLE LINES' is created to inform the user of functionality
		JCheckBox cbxTogLines = new JCheckBox("TOGGLE LINES");
		// Lines are set as on so they will operate consistently on start up
		cbxTogLines.setSelected(true);
		// Action listener will pass through the current status of the check box and be passed into a setter
		cbxTogLines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	ControlPanel.this.doilyDisplayPanel.setSectorsOn(cbxTogLines.isSelected());
            }
		});
		// 'TOGGLE LINES' check box is added to the sub-panel
		btnEventPanel.add(cbxTogLines);

		 // Identifying label on the check box 'REFLECT' is created to inform the user of functionality
		JCheckBox cbxReflect = new JCheckBox("REFLECT");
		// Reflection status is set as on so they will operate consistently on start up
		cbxReflect.setSelected(true);
		// Action listener will pass through the current status of the check box and be passed into a setter
		cbxReflect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	ControlPanel.this.doilyDisplayPanel.setRepeated(cbxReflect.isSelected());
            }
		});
		// 'REFLECT' check box is added to the sub-panel
		btnEventPanel.add(cbxReflect);
		
		btnEventPanel.add(new JLabel("                   "));
		btnEventPanel.add(new JLabel(" OPTIONS "));
		
		// Identifying label on the button 'UNDO' is created to inform the user of functionality
		JButton btnUndo = new JButton("UNDO");
		// Action listener will invoke the undo method inside the doily display panel to modify the path stack
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ControlPanel.this.doilyDisplayPanel.undo();
			}
		});
		// 'UNDO' button is added to the sub-panel
		btnEventPanel.add(btnUndo);
		
		// Identifying label on the button 'REDO' is created to inform the user of functionality
		JButton btnRedo = new JButton("REDO");
		// Action listener will invoke the redo method inside the doily display panel to modify the path stack
		btnRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ControlPanel.this.doilyDisplayPanel.redo();
			}
		});
		// 'REDO' button is added to the sub-panel
		btnEventPanel.add(btnRedo);
		
		// Identifying label on the button 'CLEAR' is created to inform the user of functionality
		JButton btnClear = new JButton("CLEAR");
		// Action listener will invoke the clear method inside the doily display panel to reset the path stack
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ControlPanel.this.doilyDisplayPanel.clear();
			}
		});
		// 'CLEAR' button is added to the sub-panel
		btnEventPanel.add(btnClear);
		
		// Border is defined for the panel to give proper spacing to all sides and spread out the interface
		btnEventPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// The event panel is added to the Control Panel instance
		this.add(btnEventPanel);
	}
	
	/**
	 * Pen panel deals with buttons and sliders that interact with the drawing settings
	 * They utilise access to the instance of the display panel from when it was passed in
	 */
	private void createPenPanel() {
		// Pen panel is created to organise the buttons and sliders in a specific order for this panel
		JPanel penPanel = new JPanel();
		penPanel.setLayout(new BoxLayout(penPanel, BoxLayout.PAGE_AXIS));
		
		// Pen size panel is added as a subpanel to the master pen panel for specific ordering of components
		JPanel penSizePanel = new JPanel();
		penSizePanel.setLayout(new BoxLayout(penSizePanel, BoxLayout.LINE_AXIS));
		
		// Label added to the side of the pen size JSlider to identify it as there is no native tag
		penSizePanel.add(new JLabel("Pen Size"));
		/*
		 *  A method defined to create the slider is invoked so that an instance of the slider with the correct
		 *  Settings is passed in and assigned
		 */
		JSlider sizeSlider = createSizeSlider();
		penSizePanel.add(sizeSlider);
		// Listener waits for the slider to be modified before returning its value using the defined setter
		sizeSlider.addChangeListener(new ChangeListener() {
		          public void stateChanged(ChangeEvent e) {
		              ControlPanel.this.doilyDisplayPanel.setPenSize(sizeSlider.getValue());
		          }
		});
		// Pen size panel is added to the master pen panel
		penPanel.add(penSizePanel);
		
		// Pen color panel is added as a sub-panel to the master pen panel for specific ordering of color modifiers
		JPanel penColorPanel = new JPanel();
		// Border is defined for the sub-panel to give proper spacing to all sides and spread out the interface
		penColorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		penColorPanel.setLayout(new BoxLayout(penColorPanel, BoxLayout.LINE_AXIS));
		
		// Label added to the side of the pen red color JSlider to identify it as there is no native tag
		penColorPanel.add(new JLabel(" RED "));
		/*
		 *  A method defined to create the slider is invoked so that an instance of the slider with the correct
		 *  Settings is passed in and assigned
		 */
		JSlider redColorSlider = createColorSlider();
		// Listener waits for the slider to be modified before returning its value using the defined setter
		redColorSlider.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              ControlPanel.this.doilyDisplayPanel.setPenRed(redColorSlider.getValue());
	          }
		});
		penColorPanel.add(redColorSlider);
		
		// Label added to the side of the pen green color JSlider to identify it as there is no native tag
		penColorPanel.add(new JLabel(" GREEN "));
		/*
		 *  A method defined to create the red color slider is invoked so that an instance of the slider with the correct
		 *  Settings is passed in and assigned
		 */
		JSlider greenColorSlider = createColorSlider();
		// Listener waits for the slider to be modified before returning its value using the defined setter
		greenColorSlider.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              ControlPanel.this.doilyDisplayPanel.setPenGreen(greenColorSlider.getValue());
	          }
		});
		penColorPanel.add(greenColorSlider);
		
		// Label added to the side of the blue color JSlider to identify it as there is no native tag
		penColorPanel.add(new JLabel(" BLUE "));
		JSlider blueColorSlider = createColorSlider();
		// Listener waits for the slider to be modified before returning its value using the defined setter
		blueColorSlider.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              ControlPanel.this.doilyDisplayPanel.setPenBlue(blueColorSlider.getValue());
	          }
		});
		penColorPanel.add(blueColorSlider);
		penPanel.add(penColorPanel);
		
		// Border is defined for the sub-panel to give proper spacing to all sides and spread out the interface
		penPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// Pen master panel is added to the current instance of Control Panel
		this.add(penPanel);
		
	}
	
	/**
	 * Spinner panel deals with JSpinners that interact with the drawing settings
	 * They utilise access to the instance of the display panel from when it was passed in
	 */
	private void createSpinnerPanel() {
		// Sector counting panel is created to organise the JSpinner associated with it
		JPanel sectorPanel = new JPanel();
		sectorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		sectorPanel.setLayout(new BoxLayout(sectorPanel, BoxLayout.LINE_AXIS));
		
		// Label added to the side of the JSpinner to identify it as there is no native tag
		sectorPanel.add(new JLabel(" MOUSE SENSITIVITY "));
		// Spinner model defined with specific implementation
		SpinnerModel sensitivitySM = new SpinnerNumberModel(1, 0, 30, 1);
		// Model is applied to the spinner so that it will operate in the defined fashion
		JSpinner sensitivityCounter = new JSpinner(sensitivitySM);
		sectorPanel.add(sensitivityCounter);
		
		// Listener invokes the setter for mouse sensitivity on the Control Panel when value is updated
		sensitivityCounter.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              ControlPanel.this.doilyDisplayPanel.setMouseSensitivity((Integer)sensitivityCounter.getValue());
	          }
		});
		// Mouse sensitivity JSpinner is added to the control panel
		this.add(sectorPanel);
		
		// Label added to the side of the JSpinner to identify it as there is no native tag
		sectorPanel.add(new JLabel(" NUMBER OF SECTORS "));
		// Spinner model defined with specific implementation
		SpinnerModel sectorSM = new SpinnerNumberModel(30, 0, 200, 1);
		// Model is applied to the spinner so that it will operate in the defined fashion
		JSpinner sectorCounter = new JSpinner(sectorSM);
		// Sector panel is has the JSpinner added to itself
		sectorPanel.add(sectorCounter);	
		// Listener invokes the setter for the number of sectors on the Control Panel when value is updated
		sectorCounter.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              ControlPanel.this.doilyDisplayPanel.setSectorVal((Integer)sectorCounter.getValue());
	          }
		});
		// Sector JSpinner panel is added to the control panel
		this.add(sectorPanel);
	}
	
	/**
	 * Creates a pen size slider for pen interaction when drawing
	 * @return Returns a programmed instance of JSlider that uses the pen criterion defined in constants
	 */
	private JSlider createSizeSlider() {
		// Pen slider using defined constant is defined
		JSlider penSizeSlider = new JSlider(JSlider.HORIZONTAL, PEN_MIN, PEN_MAX, PEN_START);
		// Spacing and label notations native to the slider are defined
		penSizeSlider.setMajorTickSpacing(5);
		penSizeSlider.setMinorTickSpacing(1);
		// Painting settings for the JSlider are set
		penSizeSlider.setPaintTicks(true);
		penSizeSlider.setPaintLabels(true);
		
		return penSizeSlider;
	}
	
	/**
	 * Creates a color slider for pen interaction when drawing
	 * @return Returns a programmed instance of JSlider that uses the pen criterion defined in constants
	 */
	private JSlider createColorSlider() {
		// Color slider using defined constant is defined
		JSlider penColorSlider = new JSlider(JSlider.HORIZONTAL, COLOR_MIN , COLOR_MAX, COLOR_START );
		// Spacing and label notations native to the slider are defined
		penColorSlider.setMajorTickSpacing(50);
		penColorSlider.setMinorTickSpacing(5);
		// Painting settings for the JSlider are set, this includes the labels and tick settings
		penColorSlider.setPaintTicks(true);
		penColorSlider.setPaintLabels(true);
		
		return penColorSlider;
	}
}
