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
import de.darkblue.dcpu.parser.DCPUCode;
import de.darkblue.dcpu.parser.Parser;
import de.darkblue.dcpu.parser.ParserException;
import de.darkblue.dcpu.parser.SemanticException;
import de.darkblue.dcpu.parser.instructions.Word;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Florian Frankenberger
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Icon ICON_LINE_ERROR = SwingUtils.loadIcon("process-stop-3.png");
    private static final Icon ICON_RUN = SwingUtils.loadIcon("arrow-right-3.png");
    private static final Icon ICON_STOP = SwingUtils.loadIcon("media-playback-stop-7.png");
    private static final Icon ICON_CURRENT_LINE = SwingUtils.loadIcon("go-next-context.png");
    private static final FileFilter FILE_FILTER = new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".asm") || f.getName().endsWith(".dasm");
            }

            @Override
            public String getDescription() {
                return ".asm / .dasm DCPU Assembler File";
            }
        };
    
    private RSyntaxTextArea codeArea;
    private final DCPU dcpu;
    
    private final MemoryFrame memoryFrame;
    private final RegistersFrame registersFrame;
    
    private volatile boolean needsCompilation = true;
    private RTextScrollPane codeScrollPane;
    
    private File codeFile = null;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame(DCPU dcpu) {
        this.dcpu = dcpu;
        this.dcpu.registerListener(new DCPUListener() {

            @Override
            public void onRamValueChanged(DCPU dcpu, Word position) {
            }

            @Override
            public void onRegisterValueChanged(DCPU dcpu, Register register) {
                if (register == Register.PC) {
                    //if the PC changed the programm was started - so we enable
                    //the reset button
                    resetButton.setEnabled(true);
                }
            }

            @Override
            public void onStartEmulation(DCPU dcpu) {
                runButton.setIcon(ICON_STOP);
                nextStepButton.setEnabled(false);
            }

            @Override
            public void onStopEmulation(DCPU dcpu) {
                runButton.setIcon(ICON_RUN);
                nextStepButton.setEnabled(true);
            }

            @Override
            public void onResetEmulation(DCPU dcpu) {
            }

            @Override
            public void onCyclesUpdate(DCPU dcpu, long totalCycles) {
            }

            @Override
            public void onNewLine(DCPU dcpu, int line) {
                onLineUpdate(line);
            }
            
        });
        
        initComponents();
        initCodeArea();
        
        this.setLocationRelativeTo(null);
        final Point location = this.getLocation();
        
        this.memoryFrame = new MemoryFrame(this, dcpu);
        this.memoryFrame.setLocation(location.x + (this.getWidth() * 3) / 7, 
                location.y + this.getHeight() / 2);
        this.memoryFrame.setVisible(true);
        
        final Point memoryFrameLocation = this.memoryFrame.getLocation();
        this.registersFrame = new RegistersFrame(this, dcpu);
        this.registersFrame.setLocation(memoryFrameLocation.x + memoryFrame.getWidth() - registersFrame.getWidth(), 
                location.y + this.getHeight() / 2 - this.registersFrame.getHeight());
        this.registersFrame.setVisible(true);
        
        this.setCodeFile(null);
    }
    
    private void setNeedsCompilation(boolean needsCompilation) {
        this.needsCompilation = needsCompilation;
        this.compileButton.setEnabled(needsCompilation);
        if (this.needsCompilation) {
            this.resetButton.setEnabled(false);
            this.runButton.setEnabled(false);
            this.nextStepButton.setEnabled(false);
        } else {
            this.resetButton.setEnabled(false);
            this.runButton.setEnabled(true);
            this.nextStepButton.setEnabled(true);
        }
    }
    
    private void onUpdate() {
        dcpu.stop();
        hideGutterIcon();
        
        setNeedsCompilation(true);
        this.saveButton.setEnabled(true);
        this.saveAsButton.setEnabled(true);
    }
    
    private void initCodeArea() {
        this.codeArea = new RSyntaxTextArea(new RSyntaxDocument(new DasmTokenMakerFactory(), "dasm"));
        
        this.codeArea.setCodeFoldingEnabled(true);
        this.codeArea.setAntiAliasingEnabled(true);
        this.codeArea.setBracketMatchingEnabled(true);
        this.codeArea.setAnimateBracketMatching(false);
        
        this.codeArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                onUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onUpdate();
            }
            
        });
        
        
        try {
            Theme theme = Theme.load(this.getClass().getResourceAsStream("defaultTheme.xml"));
            theme.apply(codeArea);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        codeScrollPane = new RTextScrollPane(codeArea);
        codeScrollPane.setIconRowHeaderEnabled(false);
        
        codeScrollPane.setLineNumbersEnabled(true);
        
        AutoCompletion autoCompletion = new AutoCompletion(new DasmAutocompleteProvider());
        autoCompletion.install(codeArea);
        
        getContentPane().add(codeScrollPane, java.awt.BorderLayout.CENTER);        
    }
    
    private void clearGutterIcon() {
        codeScrollPane.getGutter().removeAllTrackingIcons();
    }
    
    private void hideGutterIcon() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                clearGutterIcon();
                codeScrollPane.setIconRowHeaderEnabled(false);
            }
            
        });
    }
    
    private void setGutterIcon(final int line, final Icon icon) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                clearGutterIcon();
                codeScrollPane.setIconRowHeaderEnabled(true);
                try {
                    codeScrollPane.getGutter().addLineTrackingIcon(line, icon);
                } catch (BadLocationException ex) {
                    //may happen but is ignored because it is of no consequence
                }
            }
            
        });
    }
    
    private void compile() {
        try {
            final String code = this.codeArea.getText();
            final Reader reader = new StringReader(code);
            
            final Parser parser = new Parser(reader);
            final DCPUCode compiledCode = parser.parse();
            
            compiledCode.store(dcpu);
            dcpu.clearRegisters();
            
            setNeedsCompilation(false);
            this.clearGutterHints();
        } catch (IOException e) {
            //should not happen
        } catch (ParserException e) {
            setError(e.getPlainMessage(), e.getAffectedLineNo());
        } catch (SemanticException e) {
            setError(e.getMessage(), null);
        }
    }
    
    private void setError(String errorMsg, Integer lineNo) {
        this.errorText.setText(errorMsg);
        if (lineNo != null) {
            this.setGutterIcon(lineNo, ICON_LINE_ERROR);
        } else {
            this.hideGutterIcon();
        }
        this.errorPanel.setVisible(true);
    }
    
    private void clearGutterHints() {
        this.hideGutterIcon();
        this.errorPanel.setVisible(false);
    }
    
    private void onLineUpdate(int lineNo) {
        this.setGutterIcon(lineNo, ICON_CURRENT_LINE);
    }
    
    private void setCodeFile(File codeFile) {
        this.saveButton.setEnabled(false);
        this.saveAsButton.setEnabled(false);

        this.codeFile = codeFile;
        if (codeFile != null) {
            this.setTitle("DCPU - " + this.codeFile.getName());
        } else {
            this.setTitle("DCPU - [unnamed]");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        compileButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 32767));
        resetButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        nextStepButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        registerWindowToggle = new javax.swing.JToggleButton();
        memoryWindowToggle = new javax.swing.JToggleButton();
        errorPanel = new javax.swing.JPanel();
        errorPanel.setVisible(false);
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        errorText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DCPU");
        setMinimumSize(new java.awt.Dimension(300, 200));
        setPreferredSize(new java.awt.Dimension(1024, 600));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-new-5.png"))); // NOI18N
        newButton.setToolTipText("new file");
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-open-5.png"))); // NOI18N
        openButton.setToolTipText("open file ...");
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-save-5.png"))); // NOI18N
        saveButton.setToolTipText("save");
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);

        saveAsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-save-as-5.png"))); // NOI18N
        saveAsButton.setToolTipText("save as ...");
        saveAsButton.setEnabled(false);
        saveAsButton.setFocusable(false);
        saveAsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveAsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveAsButton);
        jToolBar1.add(filler4);

        compileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/run-build-2.png"))); // NOI18N
        compileButton.setToolTipText("compile");
        compileButton.setEnabled(false);
        compileButton.setFocusable(false);
        compileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        compileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        compileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(compileButton);
        jToolBar1.add(filler3);

        resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-revert-5.png"))); // NOI18N
        resetButton.setToolTipText("reset execution");
        resetButton.setEnabled(false);
        resetButton.setFocusable(false);
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(resetButton);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/arrow-right-3.png"))); // NOI18N
        runButton.setToolTipText("run program");
        runButton.setEnabled(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(runButton);

        nextStepButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/arrow-right-double.png"))); // NOI18N
        nextStepButton.setToolTipText("execute the next instruction");
        nextStepButton.setEnabled(false);
        nextStepButton.setFocusable(false);
        nextStepButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextStepButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextStepButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(nextStepButton);
        jToolBar1.add(filler5);

        registerWindowToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/kdb_table.png"))); // NOI18N
        registerWindowToggle.setSelected(true);
        registerWindowToggle.setToolTipText("dcpu registers");
        registerWindowToggle.setFocusable(false);
        registerWindowToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registerWindowToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        registerWindowToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerWindowToggleActionPerformed(evt);
            }
        });
        jToolBar1.add(registerWindowToggle);

        memoryWindowToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/database-table.png"))); // NOI18N
        memoryWindowToggle.setSelected(true);
        memoryWindowToggle.setToolTipText("dcpu memory");
        memoryWindowToggle.setFocusable(false);
        memoryWindowToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        memoryWindowToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        memoryWindowToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memoryWindowToggleActionPerformed(evt);
            }
        });
        jToolBar1.add(memoryWindowToggle);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Error");

        errorText.setEditable(false);
        errorText.setBackground(new java.awt.Color(52, 42, 42));
        errorText.setColumns(20);
        errorText.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        errorText.setForeground(new java.awt.Color(193, 204, 194));
        errorText.setRows(5);
        jScrollPane2.setViewportView(errorText);

        javax.swing.GroupLayout errorPanelLayout = new javax.swing.GroupLayout(errorPanel);
        errorPanel.setLayout(errorPanelLayout);
        errorPanelLayout.setHorizontalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 954, Short.MAX_VALUE)
            .addGroup(errorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap())
        );
        errorPanelLayout.setVerticalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
        );

        getContentPane().add(errorPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextStepButtonActionPerformed
        dcpu.step();
    }//GEN-LAST:event_nextStepButtonActionPerformed

    private void compileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileButtonActionPerformed
        compile();
    }//GEN-LAST:event_compileButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        dcpu.reset();
        clearGutterHints();
        resetButton.setEnabled(false);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if (dcpu.isRunning()) {
            dcpu.stop();
        } else {
            dcpu.start();
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        codeArea.setText("");
        setCodeFile(null);
        
        this.saveButton.setEnabled(false);
    }//GEN-LAST:event_newButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(FILE_FILTER);
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            try {
                try (FileReader reader = new FileReader(file)) {
                    StringBuilder sb = new StringBuilder();
                    char[] buffer = new char[512];
                    int read = 0;
                    do {
                        read = reader.read(buffer);
                        if (read > 0) {
                            sb.append(buffer, 0, read);
                        }
                    } while (read >= 0);
                    this.codeArea.setText(sb.toString());
                    this.setCodeFile(file);
                }
            } catch (IOException e) {
                
            }
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (this.codeFile == null) {
            saveAsButtonActionPerformed(null);
        } else {
            saveCodeTo(this.codeFile);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(FILE_FILTER);

        int returnVal = fileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            saveCodeTo(file);      
            this.setCodeFile(file);
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void registerWindowToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerWindowToggleActionPerformed
        this.registersFrame.setVisible(registerWindowToggle.isSelected());
    }//GEN-LAST:event_registerWindowToggleActionPerformed

    private void memoryWindowToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memoryWindowToggleActionPerformed
        this.memoryFrame.setVisible(memoryWindowToggle.isSelected());
    }//GEN-LAST:event_memoryWindowToggleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton compileButton;
    private javax.swing.JPanel errorPanel;
    private javax.swing.JTextArea errorText;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToggleButton memoryWindowToggle;
    private javax.swing.JButton newButton;
    private javax.swing.JButton nextStepButton;
    private javax.swing.JButton openButton;
    private javax.swing.JToggleButton registerWindowToggle;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    private void saveCodeTo(final File file) {
        try {
            try (FileWriter writer = new FileWriter(file)) {
                StringReader stringReader = new StringReader(this.codeArea.getText());
                char[] buffer = new char[512];
                int read = 0;
                do {
                    read = stringReader.read(buffer);
                    if (read > 0) {
                        writer.write(buffer, 0, read);
                    }
                } while (read >= 0);
                this.saveButton.setEnabled(false);
            }
        } catch (IOException e) {
            
        }
    }
}
