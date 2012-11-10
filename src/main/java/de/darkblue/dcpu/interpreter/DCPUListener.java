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
package de.darkblue.dcpu.interpreter;

import de.darkblue.dcpu.parser.instructions.Word;

/**
 * A listener that is called whenever a value in ram or
 * in a register changes or when the emulator is started, stoped or reset
 * 
 * @author Florian Frankenberger
 */
public interface DCPUListener {
    
    void onStartEmulation(DCPU dcpu);
    
    void onStopEmulation(DCPU dcpu);
    
    void onResetEmulation(DCPU dcpu);
    
    void onRamValueChanged(DCPU dcpu, Word position);
    
    void onRegisterValueChanged(DCPU dcpu, Register register);
    
    void onCyclesUpdate(DCPU dcpu, long totalCycles);
    
}
