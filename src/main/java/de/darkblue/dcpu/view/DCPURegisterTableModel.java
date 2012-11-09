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

import de.darkblue.dcpu.interpreter.DCPU;
import de.darkblue.dcpu.interpreter.MemoryListener;
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Word;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Florian Frankenberger
 */
public class DCPURegisterTableModel extends AbstractTableModel implements MemoryListener {

    private final DCPU dcpu;
    
    public DCPURegisterTableModel(DCPU dcpu) {
        this.dcpu = dcpu;
        this.dcpu.registerListener(this);
    }

    @Override
    public int getRowCount() {
        return Register.values().length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Register";
            default:
                return "Value";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Register.values()[rowIndex];
            default:
                return dcpu.getRegister(Register.values()[rowIndex]);
        }
    }

    @Override
    public void onRamValueChanged(DCPU dcpu, Word position) {
    }

    @Override
    public void onRegisterValueChanged(DCPU dcpu, Register register) {
        fireTableCellUpdated(register.ordinal(), 1);
    }
    
    
    
}
