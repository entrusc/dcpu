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

/**
 * Represents a command that is executed in one cycle within the dcpu
 * 
 * @author Florian Frankenberger
 */
public abstract class Command {

    private final boolean needsDcpuCyle;

    public Command() {
        this(true); //default: need one cycle to execute
    }

    public Command(boolean needsCpuCyle) {
        this.needsDcpuCyle = needsCpuCyle;
    }

    public boolean isNeedsDcpuCyle() {
        return needsDcpuCyle;
    }
    
    public abstract void execute(DCPU dcpu);
    
}
