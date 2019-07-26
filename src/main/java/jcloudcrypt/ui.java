package jcloudcrypt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ui implements ActionListener {
    JFrame window;
    JTextField encryptPathField;
    JButton encryptBrowse;
    JButton encryptButton;
    JPasswordField encryptKeyField;
    JPasswordField encryptVerifyField;
    File selectedFile;
    JButton decryptBrowse;
    JButton decryptButton;
    JTextField decryptPathField;
    JPasswordField decryptKeyField;
    JCheckBox obfuscateName;

    public ui() {
    }

    public void mainWindow() {
        window = new JFrame("JCloudCrypt");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setResizable(false);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel encryptPanel = new JPanel();
        encryptPanel.setLayout(new BoxLayout(encryptPanel, BoxLayout.LINE_AXIS));
        setupEncryptPanelLeft(encryptPanel);
        setupEncryptPanelMid(encryptPanel);
        setupEncryptPanelRight(encryptPanel);
        JPanel decryptPanel = new JPanel();
        decryptPanel.setLayout(new BoxLayout(decryptPanel, BoxLayout.LINE_AXIS));
        setupDecryptPanelLeft(decryptPanel);
        setupDecryptPanelMid(decryptPanel);
        setupDecryptPanelRight(decryptPanel);
        tabbedPane.addTab("Encrypt", encryptPanel);
        tabbedPane.addTab("Decrypt", decryptPanel);
        window.add(tabbedPane);
        window.pack();
        window.setVisible(true);
    }

    private void browseWindow(char selector) {
        JFileChooser fileChooser = new JFileChooser();
        if (selector == 'd') {
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.jcc", "jcc"));
        }
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            if (selector == 'e') {
                encryptPathField.setText(selectedFile.getAbsolutePath());
            } else if (selector == 'd') {
                decryptPathField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private void setupEncryptPanelLeft(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        JLabel pathLabel = new JLabel("Path to File:");
        pathLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(pathLabel);
        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(keyLabel);
        JLabel verifyLabel = new JLabel("Verify Key:");
        verifyLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(verifyLabel);
        panel.add(textFieldPane);
    }

    private void setupEncryptPanelMid(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        encryptPathField = new JTextField(25);
        textFieldPane.add(encryptPathField);
        encryptKeyField = new JPasswordField(25);
        textFieldPane.add(encryptKeyField);
        encryptVerifyField = new JPasswordField(25);
        textFieldPane.add(encryptVerifyField);
        panel.add(textFieldPane);
    }

    private void setupEncryptPanelRight(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        encryptBrowse = new JButton("Browse");
        encryptBrowse.addActionListener(this);
        textFieldPane.add(encryptBrowse);
        textFieldPane.add(Box.createHorizontalStrut(15));
        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this);
        textFieldPane.add(encryptButton);
        obfuscateName = new JCheckBox("Obfuscate filename");
        // textFieldPane.add(obfuscateName);
        panel.add(textFieldPane);
    }

    private void setupDecryptPanelLeft(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        JLabel pathLabel = new JLabel("Path to File:");
        pathLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(pathLabel);
        textFieldPane.add(Box.createRigidArea(new Dimension(0, 15)));
        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(keyLabel);
        panel.add(textFieldPane);
    }

    private void setupDecryptPanelMid(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        decryptPathField = new JTextField(25);
        decryptPathField.addActionListener(this);
        textFieldPane.add(decryptPathField);
        textFieldPane.add(Box.createRigidArea(new Dimension(0, 8)));
        decryptKeyField = new JPasswordField(25);
        textFieldPane.add(decryptKeyField);
        panel.add(textFieldPane);
    }

    private void setupDecryptPanelRight(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        decryptBrowse = new JButton("Browse");
        decryptBrowse.addActionListener(this);
        textFieldPane.add(decryptBrowse);
        textFieldPane.add(Box.createRigidArea(new Dimension(0, 8)));
        decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(this);
        textFieldPane.add(decryptButton);
        panel.add(textFieldPane);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == encryptButton) {
            // System.out.println(obfuscateName.isSelected());
            // Empty key field
            if (encryptKeyField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this.window, "Key field is empty.");
            }
            // Keys not empty and keys match
            else if (Arrays.equals(encryptKeyField.getPassword(), encryptVerifyField.getPassword())) {
                File file = new File(encryptPathField.getText());
                // Filepath is actually a file
                if (file.isFile()) {
                    int confirm = JOptionPane.showConfirmDialog(this.window,
                            "Please safely store or remember your keys.\nIf lost, the data is not recoverable.\n\nDo you wish to continue?",
                            "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        encrypt encryption = new encrypt();
                        encryption.encryptFile(encryptKeyField.getPassword(), file.getPath(), obfuscateName.isSelected());
                        encryptPathField.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(this.window, "File is not valid.", "File Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                encryptKeyField.setText("");
                encryptVerifyField.setText("");
            }
            // Keys do not match
            else {
                JOptionPane.showMessageDialog(this.window, "Keys do not match.", "Key Verification Error",
                        JOptionPane.ERROR_MESSAGE);
                encryptKeyField.setText("");
                encryptVerifyField.setText("");
            }
        } else if (e.getSource() == encryptBrowse) {
            browseWindow('e');
        } else if (e.getSource() == decryptBrowse) {
            browseWindow('d');
        } else if (e.getSource() == decryptButton) {
            // Path is empty
            if (decryptPathField.getText().equals("")) {
                JOptionPane.showMessageDialog(this.window, "File is not valid.", "File Error",
                        JOptionPane.ERROR_MESSAGE);
                decryptKeyField.setText("");
                // Key is empty
            } else if (decryptKeyField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this.window, "Key is empty.", "Key Error", JOptionPane.ERROR_MESSAGE);
            } else {
                File file = new File(decryptPathField.getText());
                decrypt decryption = new decrypt();
                if (file.isFile()) {
                    // Checks that key is correct
                    if (decryption.checkKey(decryptKeyField.getPassword(), file.getPath())) {
                        decryption.decryptFile(decryptKeyField.getPassword(), file.getPath());
                        decryptKeyField.setText("");
                        // key is not correct
                    } else {
                        JOptionPane.showMessageDialog(this.window, "Key is incorrect.", "Key Error",
                                JOptionPane.ERROR_MESSAGE);
                        decryptKeyField.setText("");
                    }
                    // if path is not a file
                } else {
                    JOptionPane.showMessageDialog(this.window, "File is not valid.", "File Error",
                            JOptionPane.ERROR_MESSAGE);
                    decryptKeyField.setText("");
                }
            }
        }
    }// ENDS actionPerformed
}// ENDS Ui