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
import de.darkblue.dcpu.interpreter.DCPUListener;
import de.darkblue.dcpu.interpreter.Register;
import de.darkblue.dcpu.parser.instructions.Word;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Florian Frankenberger
 */
public class DCPUMemoryTableModel extends AbstractTableModel implements DCPUListener {

    private final DCPU dcpu;
    
    public DCPUMemoryTableModel(DCPU dcpu) {
        this.dcpu = dcpu;
        this.dcpu.registerListener(this);
    }

    @Override
    public int getRowCount() {
        return (int) Math.ceil(dcpu.getRamSize() / 16); //16 per row
    }

    @Override
    public int getColumnCount() {
        return 17;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Word.class;
    }
    
    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        } else {
            return String.format("0x%02x", column - 1);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            //col 0 is always the address
            return String.format("0x%04x", rowIndex * 16);
        } else {
            int position = rowIndex * 16 + (columnIndex - 1);
            final Word positionWord = new Word();
            positionWord.setSignedInt(position);

            return this.dcpu.getRam(positionWord);
        }
    }

    @Override
    public void onRamValueChanged(DCPU dcpu, Word position) {
        int positionValue = position.unsignedIntValue();
        int row = positionValue / 16;
        int col = positionValue % 16;
        
        fireTableCellUpdated(row, col + 1);
    }

    @Override
    public void onRegisterValueChanged(DCPU dcpu, Register register) {
    }

    @Override
    public void onStartEmulation(DCPU dcpu) {
    }

    @Override
    public void onStopEmulation(DCPU dcpu) {
    }

    @Override
    public void onResetEmulation(DCPU dcpu) {
    }

    @Override
    public void onCyclesUpdate(DCPU dcpu, long totalCycles) {
    }

    @Override
    public void onNewLine(DCPU dcpu, int lin) {
    }
    
    
    
}
