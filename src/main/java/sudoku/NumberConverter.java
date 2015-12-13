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

package sudoku;

public class NumberConverter
{
    private static final String CHARS = " 123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String getValueAsString(int value) {
        return CHARS.substring(value, value+1);
    }

    public static int getCharAsValue(char c) {
        if (c == '0') {
            return 0;
        }
        return CHARS.indexOf(c);
    }

}
