/*
 * Copyright (c) 2015 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 *
 * This Software is provided to You under the terms of the European
 * Union Public License (the "EUPL") version 1.1 as published by the
 * European Union. Any use of this Software, other than as authorized
 * under this License is strictly prohibited (to the extent such use
 * is covered by a right of the copyright holder of this Software).
 *
 * This Software is provided under the License on an "AS IS" basis and
 * without warranties of any kind concerning the Software, including
 * without limitation merchantability, fitness for a particular purpose,
 * absence of defects or errors, accuracy, and non-infringement of
 * intellectual property rights other than copyright. This disclaimer
 * of warranty is an essential part of the License and a condition for
 * the grant of any rights to this Software.
 *
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 *
 *  Contributors:
 *      2015 - Sven Erik Knop - initial API and implementation
 *
 */

package sudoku.swing;

import sudoku.NumberConverter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class FieldLimit extends PlainDocument
{
    private int limit;

    public FieldLimit(int limit) {
       super();
       this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
       if (str == null)
         return;

        int value = NumberConverter.getCharAsValue(str.charAt(0));
        String strValue = NumberConverter.getValueAsString(value);

        if ((value <= limit) && getLength() == 0) {
            super.insertString(offset, strValue, attr);
        }

    }
}
