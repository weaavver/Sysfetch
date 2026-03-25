/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sysfetch;

import java.awt.*;
import javax.swing.text.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


/**
 *
 * @author weaver
 */
public class FirstPage extends javax.swing.JFrame {
    
    private int protectedPosition = 0;
    
    class ProtectedFilter extends DocumentFilter {
    private FirstPage parent;

    public ProtectedFilter(FirstPage parent) {
        this.parent = parent;
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (offset < parent.protectedPosition) {
            return; // block delete before prompt
        }
        super.remove(fb, offset, length);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (offset < parent.protectedPosition) {
            offset = parent.protectedPosition;
        }
        super.insertString(fb, offset, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (offset < parent.protectedPosition) {
            return; // block overwrite before prompt
        }
        super.replace(fb, offset, length, text, attrs);
    }
}
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FirstPage.class.getName());

    /**
     * Creates new form FirstPage
     */
    
    private java.io.File currentDir = new java.io.File(System.getProperty("user.dir"));
    
    public FirstPage() {
    initComponents();
    
    AbstractDocument doc = (AbstractDocument) Terminal.getDocument();
    doc.setDocumentFilter(new ProtectedFilter(this));
    
    Terminal.append("$ Allowed commands: ls, cd, mkdir, \ncat, pwd, touch, whoami, ping, man\n$ ");
    protectedPosition = Terminal.getText().length();
    Terminal.setCaretPosition(protectedPosition); // <-- caret starts after $
    setupTerminalKeys();
    
}
    
    private String runCommand(String command) {
    StringBuilder output = new StringBuilder();

    try {
        // WHITELIST (only allow safe commands)
        String base = command.split(" ")[0];
        java.util.List<String> allowed = java.util.Arrays.asList(
                "ls", "cd", "mkdir", "cat", "pwd", "touch", "whoami", "ping",
                "man", "echo"
        );     
                
        if (!allowed.contains(base)) {
            return "Command not found.";
        }

        // HANDLE cd manually (important!)
        if (base.equals("cd")) {
            String[] parts = command.split(" ");
            if (parts.length < 2) return "";

            java.io.File newDir = new java.io.File(currentDir, parts[1]);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDir = newDir;
            } else {
                return "Directory not found";
            }
            return "";
        }

        // Run command using bash
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        pb.directory(currentDir); // set working directory
        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();

    } catch (IOException | InterruptedException e) {
        return "Error: " + e.getMessage();
    }

    return output.toString();
}
    
    private void setupTerminalKeys() {
    Terminal.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int caret = Terminal.getCaretPosition();

            // Block caret moving left/up before the prompt
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP)
                    && caret <= protectedPosition) {
                e.consume();
                Terminal.setCaretPosition(protectedPosition);
                return;
            }

            // Block caret moving right/down past the end (optional)
            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN)
                    && caret >= Terminal.getDocument().getLength()) {
                e.consume();
                return;
            }

            // Block backspace/delete at or before prompt
            if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE)
                    && caret <= protectedPosition) {
                e.consume();
                Terminal.setCaretPosition(protectedPosition);
                return;
            }

            // Handle Enter key
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();

                // Optional: get user input after prompt
                String text = Terminal.getText();
                String command = "";
                String result = "";
                
                if (text.length() > protectedPosition) {
                    command = text.substring(protectedPosition).trim();
                }
                
                // Run command
                result = runCommand(command);

                // Print output
                
                Terminal.append("\n" + result);

                // New prompt
                Terminal.append("\n$ ");
                protectedPosition = Terminal.getText().length();
                Terminal.setCaretPosition(protectedPosition);
            }
        }
    });
    }
    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Terminal = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel2.setText("Terminal");

        Terminal.setColumns(20);
        Terminal.setFont(new java.awt.Font("Liberation Mono", 0, 12)); // NOI18N
        Terminal.setRows(5);
        jScrollPane2.setViewportView(Terminal);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 235, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FirstPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Terminal;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
