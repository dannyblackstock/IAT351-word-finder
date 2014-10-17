import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

	// JList to contain list of words
	JList wordList;

	public WordFinder() {
		super("Word Finder");

		// configure GUI
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.getContentPane().add(panel);

		panel.setLayout(new GridBagLayout());
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

		// create a word list model
		DefaultListModel wordListModel = new DefaultListModel();

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

		// convert generic List to array
		String[] dataArray = new String[data.size()];
		dataArray = data.toArray(dataArray);

		// fill out the words from the default file
		for (String s : dataArray)
			wordListModel.addElement(s);
		// System.out.println(s);

		// create a list model that is a filtered version of the full world list
		FilteredListModel filteredListModel = new FilteredListModel(
				wordListModel);

		wordList = new JList(filteredListModel);

		// add the word list to a scrolling pane
		JScrollPane scrollPane = new JScrollPane(wordList);
		panel.add(scrollPane, c);

		// create text field change handler
		TextFieldHandler inputFieldHandler = new TextFieldHandler(
				filteredListModel, inputField.getText());

		// add event listener to the inputField to listen to the underlying
		// Document for changes
		inputField.getDocument().addDocumentListener(inputFieldHandler);

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
