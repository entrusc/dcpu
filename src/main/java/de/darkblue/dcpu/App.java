package de.darkblue.dcpu;

import de.darkblue.dcpu.parser.DCPUCode;
import de.darkblue.dcpu.parser.Parser;
import java.io.FileReader;

public class App {
    
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(new FileReader("D:/temp/test.dcpu"));
        DCPUCode code = parser.parse();
        
        System.out.println(code);
        
    }
    
}
