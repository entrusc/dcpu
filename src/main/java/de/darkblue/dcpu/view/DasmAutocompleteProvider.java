/*
 * Copyright (C) 2012 Florian Frankenberger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.darkblue.dcpu.view;

import de.darkblue.dcpu.parser.instructions.Operation;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Florian Frankenberger
 */
public class DasmAutocompleteProvider extends DefaultCompletionProvider {

    public DasmAutocompleteProvider() {
        initAutocompletion();
    }
    
    private void initAutocompletion() {
        for (Operation operation : Operation.values()) {
            this.addCompletion(new BasicCompletion(this, operation.name(), "Operand " 
                    + operation.name() + " " + (operation.getParameterCount() == 2 ? "b, a" : "a")));
        }
    }
    
}
