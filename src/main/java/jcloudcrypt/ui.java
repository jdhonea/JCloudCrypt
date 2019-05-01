package jcloudcrypt;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.*;

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

    public ui() {
    }

    public void mainWindow() {
        window = new JFrame("JCloudCrypt");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        // window.setResizable(false);
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

    private void browseWindow() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            encryptPathField.setText(selectedFile.getAbsolutePath());
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
        encryptPathField.addActionListener(this);
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
        panel.add(textFieldPane);
    }

    private void setupDecryptPanelLeft(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.PAGE_AXIS));
        JLabel pathLabel = new JLabel("Path to File:");
        pathLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textFieldPane.add(pathLabel);
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
        decryptKeyField = new JPasswordField(25);
        textFieldPane.add(decryptKeyField);
    }

    private void setupDecryptPanelRight(JPanel panel) {

    }

    // TODO: Should be made neater and more legible.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == encryptButton) {
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
                        encrypt.encryptFile(encryptKeyField.getPassword(), file.getPath());
                        encryptPathField.setText("");
                        decrypt.decryptFile(encryptKeyField.getPassword(), file.getPath() + ".crypt");
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
            browseWindow();
        }
    }// ENDS actionPerformed
}// ENDS Ui

// TODO: Check if key follows key requirements
// TODO: Implement decrypt ui