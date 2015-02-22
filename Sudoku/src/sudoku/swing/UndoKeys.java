package sudoku.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import sudoku.UndoTableModel;

public class UndoKeys
{
	@SuppressWarnings("serial")
	public static void addUndoKeys(JComponent comp, final UndoTableModel tableModel) {
		InputMap inputMap = comp.getInputMap();
		ActionMap actionMap = comp.getActionMap();
		
		KeyStroke meta_z = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		KeyStroke meta_y = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		
		inputMap.put(meta_z, "undo");
		inputMap.put(meta_y, "redo");
		
		actionMap.put("undo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.undo();
			}
			
		});

		actionMap.put("redo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.redo();
			}
			
		});

	}
}
