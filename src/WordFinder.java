import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
	private static final int WINDOW_WIDTH = 500; // pixels
	private static final int WINDOW_HEIGHT = 500; // pixels
	private static final int TEXT_WIDTH = 20; // characters

	int totalWords;

	// window for GUI
	private JFrame window = new JFrame("Word Finder");

	// add a panel
	JPanel panel = new JPanel();

	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("File");

	// file chooser
	JFileChooser chooser = new JFileChooser();
	// filter for the file chooser so that only txt files are accepted
	FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"Text files only", "txt");

	// "Find:" label
	JLabel findLabel = new JLabel("Find:");

	// input field
	private JTextField inputField = new JTextField(TEXT_WIDTH);

	// clear button
	JButton clearButton = new JButton("Clear");

	// total words found label
	JLabel numWordsString = new JLabel("");

	// JTable to contain list of words
	JTable wordListTable = new JTable();
	// table model used by the JTable
	DefaultTableModel wordListModel = new DefaultTableModel();
	// Create a table sorter object to filter the display
	TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>();

	public WordFinder() {
		super("Word Finder");

		// configure GUI
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.getContentPane().add(panel);

		// set up menus
		menuBar.add(menu);
		JMenuItem openMenuItem = new JMenuItem("Open...");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		menu.add(openMenuItem);
		menu.add(exitMenuItem);

		// open menu item opens the file chooser
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// use the file type filter on the file chooser
				chooser.setFileFilter(filter);

				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: "
							+ chooser.getSelectedFile().getAbsolutePath());//chooser.getSelectedFile().getName());
					
//					load in the default word list
					String stringURL = "file:" + chooser.getSelectedFile().getAbsolutePath();
					URL url;
					try {
						url = new URL(stringURL);
						loadFile(url);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		// exit menu item quits the program
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// add the menu bar to the window
		window.setJMenuBar(menuBar);

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

		// test string list data
		// String[] data = { "a", "b", "c", "a", "b", "c", "a", "b", "c", "a",
		// "b", "c", "a", "b", "c" };

		// load in the default word list
		URL url = WordFinder.class.getResource("words.txt");
		loadFile(url);

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
				// System.out.println(inputField.getText());
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

	// Code from
	// http://stackoverflow.com/questions/7067303/jtable-filtering-with-jtextfield-doesnt-work
	// called whenever the input field changes
	private void newFilter() {
		// generic object used to sort/filter models
		RowFilter<DefaultTableModel, Object> rowFilter = null;
		try {
			// try matching the table model with the entered word, ignoring case
			// (regex)
			rowFilter = RowFilter.regexFilter("(?i)" + inputField.getText());
		} catch (java.util.regex.PatternSyntaxException ex) {
			return;
		}
		// set the table sorter object to use the defined row filter
		sorter.setRowFilter(rowFilter);

		// set the table to be sorted by the table sorter
		wordListTable.setRowSorter(sorter);
		
		// update the label for number of words found
		setNumWordsString();
	}

	// method for opening files
	private void loadFile(URL url) {
		System.out.println(url);
		if (url == null)
			throw new RuntimeException("Missing resource: words");
		try {
			words.load(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// reset the table model
		wordListModel.setRowCount(0);

		@SuppressWarnings("unchecked")
		List<String> data = words.find("");

		// DefaultListModel wordListModel = new DefaultListModel();

		// initialize word list model. a one column table with the right number of rows
		wordListModel = new DefaultTableModel(new Object[] { "words" },
				data.size());

		// convert generic List to array
		String[] dataArray = new String[data.size()];
		dataArray = data.toArray(dataArray);

		System.out.println(wordListModel.getRowCount());
		// insert the words into the table from the default file
		for (int i = 0; i < wordListModel.getRowCount(); i++) {
			wordListModel.setValueAt(dataArray[i], i, 0);
		}
		
		// set the table model
		wordListTable.setModel(wordListModel);

		// store total number of words
		totalWords = wordListTable.getRowCount();
		
		// set the sorter object
		sorter.setModel(wordListModel);
		
		// update the label for number of words found
		setNumWordsString();
	}
	
	private void setNumWordsString() {
		// change the text label describing how many words are found
		if (totalWords == wordListTable.getRowCount()) {
			numWordsString.setText(totalWords + " total words.");
		} else if (wordListTable.getRowCount() == 1) {
			numWordsString.setText(wordListTable.getRowCount()
					+ " word found containing " + inputField.getText());
		} else {
			numWordsString.setText(wordListTable.getRowCount()
					+ " words found containing " + inputField.getText());
		}
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
