import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldHandler implements DocumentListener {

	FilteredListModel filteredListModel;
	String textFieldString;

	TextFieldHandler(FilteredListModel filteredListModel, String textFieldString) {
		this.filteredListModel = filteredListModel;
		this.textFieldString = textFieldString;
	}

	public void changedUpdate(DocumentEvent e) {
	}

	public void removeUpdate(DocumentEvent e) {
		changeEvent();
	}

	public void insertUpdate(DocumentEvent e) {
		changeEvent();
	}

	public void changeEvent() {
		System.out.println(textFieldString);

		filteredListModel.setFilter(new FilteredListModel.Filter() {
			public boolean accept(Object element) {
				// cast Object into String so we can use the contains() method
				String currentWord = (String) element;
				
				// if the casting worked
				if (currentWord instanceof String) {
					// check if the word has part of the string in it
					if (currentWord.contains(textFieldString)) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false; // put your filtering logic here.
				}
			}
		});
	}

}
