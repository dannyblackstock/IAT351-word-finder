import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// we don't want java.awt.List

/**
 * WordFinder is an interface for searching a word list. When the user types any
 * part of a word, the interface displays all the words that match.
 */
public class WordFinder extends JFrame {

	private WordList words = new WordList();

	/**
	 * Make a WordFinder window.
	 */

	// class constants
	private static final int WINDOW_WIDTH = 275; // pixels
	private static final int WINDOW_HEIGHT = 275; // pixels
	private static final int TEXT_WIDTH = 20; // characters

	// window for GUI
	private JFrame window = new JFrame("Word Finder");

	// add a panel so we can listen to keyboard events
	JPanel panel = new JPanel();

	// "Find:" label
	JLabel findLabel = new JLabel("Find:");

	// input field
	private JTextField inputField = new JTextField(TEXT_WIDTH);

	// clear button
	JButton clearButton = new JButton("Clear");

	// total words found label
	JLabel numWordsString = new JLabel("45427 words total.");

	// JTable to contain list of words
	JTable wordListTable;

	// Create a table sorter object to filter the display
	TableRowSorter<DefaultTableModel> sorter;
	
	public WordFinder() {
		super("Word Finder");

		// configure GUI
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.getContentPane().add(panel);

		panel.setLayout(new GridBagLayout());
		
		// define gridbag constraints
		GridBagConstraints c = new GridBagConstraints();

		panel.setFocusable(true);
		panel.requestFocusInWindow();

		// add find label
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;

		panel.add(findLabel, c);

		// add input field
		c.gridx = 1;
		c.gridwidth = 4;
		c.weightx = 1;

		panel.add(inputField, c);

		// add clear button
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 0;

		panel.add(clearButton, c);

		// add "0 words containing '' label
		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = 4;
		c.weightx = 1;
		c.weighty = 0;

		panel.add(numWordsString, c);

		// add word list box
		c.gridy = 2;
		c.gridx = 1;
		c.gridwidth = 4;
		c.weightx = 1;
		c.weighty = 1;

		c.fill = GridBagConstraints.BOTH;

		// load in the default word list
		URL url = WordFinder.class.getResource("words.txt");
		System.out.println(url);
		if (url == null)
			throw new RuntimeException("Missing resource: words");
		try {
			words.load(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// test string list data
		// String[] data = { "a", "b", "c", "a", "b", "c", "a", "b", "c", "a",
		// "b", "c", "a", "b", "c" };

		@SuppressWarnings("unchecked")
		List<String> data = words.find("");
		
//		DefaultListModel wordListModel = new DefaultListModel();
		
		// create a word list model. a one column table with the right number of rows
		DefaultTableModel wordListModel = new DefaultTableModel(new Object[] {"words"}, data.size());

		// convert generic List to array
		String[] dataArray = new String[data.size()];
		dataArray = data.toArray(dataArray);
		
		System.out.println(wordListModel.getRowCount());
		// insert the words into the table from the default file
		for (int i=0; i < wordListModel.getRowCount(); i++) {
			wordListModel.setValueAt(dataArray[i], i, 0);
		}
		
		// initialize the sorter object
		sorter = new TableRowSorter<DefaultTableModel>(wordListModel);
		
		// create the actual table that will be displayed, using the table model
		wordListTable = new JTable(wordListModel);
		
		// add the word table to a scrolling pane
		JScrollPane scrollPane = new JScrollPane(wordListTable);
		panel.add(scrollPane, c);

		// add event listener to the inputField to listen to the underlying
		// Document for changes
		inputField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			// call newFilter() whenever a letter is changed, essentially
			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
//				System.out.println(inputField.getText());
				newFilter();
			}
		});

		// clear button event handler
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// clear the input field
				inputField.setText("");
			}
		});

		// display GUI
		window.setVisible(true);

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	// Code from http://stackoverflow.com/questions/7067303/jtable-filtering-with-jtextfield-doesnt-work
	// called whenever the input field changes
	private void newFilter() {
		// generic object used to sort/filter models
	    RowFilter<DefaultTableModel, Object> rowFilter = null;
	    try {
	    	// try matching the table model with the entered word, ignoring case (regex)
	        rowFilter = RowFilter.regexFilter("(?i)" + inputField.getText());
	    }
	    catch(java.util.regex.PatternSyntaxException ex) {
	        return;
	    }
	    // set the table sorter object to use the defined row filter
	    sorter.setRowFilter(rowFilter);
	    
	    // set the table to be sorted by the table sorter
	    wordListTable.setRowSorter(sorter);
	}
	
	/**
	 * Main method. Makes and displays a WordFinder window.
	 * 
	 * @param args
	 *            Command-line arguments. Ignored.
	 */
	public static void main(String[] args) {
		// In general, Swing objects should only be accessed from
		// the event-handling thread -- not from the main thread
		// or other threads you create yourself. SwingUtilities.invokeLater()
		// is a standard idiom for switching to the event-handling thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Make and display the WordFinder window.
				new WordFinder();
			}
		});
	}
}
