package sudoku.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import sudoku.UndoTableModel;

public class UndoKeys
{
	@SuppressWarnings("serial")
	public static void addUndoKeys(JComponent comp, final UndoTableModel tableModel, final JTable table) {
		InputMap inputMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = comp.getActionMap();
		
		KeyStroke meta_z = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		KeyStroke meta_y = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		
		inputMap.put(meta_z, "undo");
		inputMap.put(meta_y, "redo");
		
		actionMap.put("undo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				Component editor = table != null ? table.getEditorComponent() : null;
				if (editor != null && SwingUtilities.isDescendingFrom(focusOwner, editor)) {
					int editingRow = table.getEditingRow();
					int editingCol = table.getEditingColumn();
					Object editorValue = table.getCellEditor().getCellEditorValue();
					boolean changed = tableModel.isChanged(editingRow, editingCol, editorValue);
					table.getCellEditor().cancelCellEditing();
					if (!changed) {
						tableModel.undo();
					}
				}
				else {
					tableModel.undo();
				}
			}
			
		});

		actionMap.put("redo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				Component editor = table != null ? table.getEditorComponent() : null;
				if (editor != null && SwingUtilities.isDescendingFrom(focusOwner, editor)) {
					int editingRow = table.getEditingRow();
					int editingCol = table.getEditingColumn();
					Object editorValue = table.getCellEditor().getCellEditorValue();
					boolean changed = tableModel.isChanged(editingRow, editingCol, editorValue);
					table.getCellEditor().cancelCellEditing();
					if (!changed) {
						tableModel.redo();
					}
				}
				else {
					tableModel.redo();
				}
			}
			
		});

	}
}
