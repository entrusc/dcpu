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
import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    
    private RSyntaxTextArea codeArea;
    private final DCPU dcpu;
    
    private final MemoryFrame memoryFrame;
    private final RegistersFrame registersFrame;
    
    private volatile boolean needsCompilation = true;
    private RTextScrollPane codeScrollPane;
    
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
            
        });
        
        initComponents();
        initCodeArea();
        
        this.setLocationRelativeTo(null);
        final Point location = this.getLocation();
        
        this.memoryFrame = new MemoryFrame(this, dcpu);
        this.memoryFrame.setLocation(location.x + (this.getWidth() * 4) / 5, 
                location.y + this.getHeight() / 2);
        this.memoryFrame.setVisible(true);
        
        final Point memoryFrameLocation = this.memoryFrame.getLocation();
        this.registersFrame = new RegistersFrame(this, dcpu);
        this.registersFrame.setLocation(memoryFrameLocation.x + memoryFrame.getWidth() - registersFrame.getWidth(), 
                location.y + this.getHeight() / 2 - this.registersFrame.getHeight());
        this.registersFrame.setVisible(true);
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
    
    private void initCodeArea() {
        this.codeArea = new RSyntaxTextArea(new RSyntaxDocument(new DasmTokenMakerFactory(), "dasm"));
        
        this.codeArea.setCodeFoldingEnabled(true);
        this.codeArea.setAntiAliasingEnabled(true);
        this.codeArea.setBracketMatchingEnabled(true);
        this.codeArea.setAnimateBracketMatching(false);
        
        this.codeArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setNeedsCompilation(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNeedsCompilation(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setNeedsCompilation(true);
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
        codeScrollPane.setIconRowHeaderEnabled(false);
    }
    
    private void setGutterIcon(int line, Icon icon) {
        clearGutterIcon();
        codeScrollPane.setIconRowHeaderEnabled(true);
        try {
            codeScrollPane.getGutter().addLineTrackingIcon(line, icon);
        } catch (BadLocationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void compile() {
        try {
            final String code = this.codeArea.getText();
            final Reader reader = new StringReader(code);
            
            final Parser parser = new Parser(reader);
            final DCPUCode compiledCode = parser.parse();
            
            final ByteArrayOutputStream byteOut = 
                    new ByteArrayOutputStream();
            compiledCode.store(byteOut);
            byteOut.flush();
            
            final ByteArrayInputStream byteIn = 
                    new ByteArrayInputStream(byteOut.toByteArray());
            dcpu.readRam(byteIn);
            dcpu.clearRegisters();
            
            setNeedsCompilation(false);
            this.clearGutterIcon();
        } catch (IOException e) {
            //should not happen
        } catch (ParserException e) {
            this.setGutterIcon(e.getAffectedLineNo(), ICON_LINE_ERROR);
            this.errorPanel.setText(e.getPlainMessage());
        } catch (SemanticException e) {
            this.errorPanel.setText(e.getMessage());
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 32767));
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 32767));
        compileButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 32767));
        resetButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        nextStepButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorPanel = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DCPU");
        setMinimumSize(new java.awt.Dimension(300, 200));
        setPreferredSize(new java.awt.Dimension(1024, 600));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-new-5.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-open-5.png"))); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/document-export.png"))); // NOI18N
        jButton3.setEnabled(false);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);
        jToolBar1.add(filler1);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/edit-undo-8.png"))); // NOI18N
        jButton4.setEnabled(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/edit-redo-8.png"))); // NOI18N
        jButton5.setEnabled(false);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);
        jToolBar1.add(filler2);

        compileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/darkblue/dcpu/view/cog.png"))); // NOI18N
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
        resetButton.setToolTipText("reset");
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
        nextStepButton.setToolTipText("next instruction");
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

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        errorPanel.setColumns(20);
        errorPanel.setRows(5);
        jScrollPane1.setViewportView(errorPanel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.PAGE_END);

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
        resetButton.setEnabled(false);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if (dcpu.isRunning()) {
            dcpu.stop();
        } else {
            dcpu.start();
        }
    }//GEN-LAST:event_runButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton compileButton;
    private javax.swing.JTextArea errorPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton nextStepButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables
}
