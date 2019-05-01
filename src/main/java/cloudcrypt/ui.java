package cloudcrypt;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class ui implements ActionListener {
    JFrame window;
    JTextField encryptPathField;
    JButton encryptBrowse;
    JButton encryptButton;
    JPasswordField encryptKeyField;
    JPasswordField encryptVerifyField;
    File selectedFile;

    public ui() {
    }

    public void mainWindow() {
        window = new JFrame("Cloud Crypt");
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
        setupDecryptPanel(decryptPanel);
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
        panel.add(textFieldPane);
        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this);
        textFieldPane.add(encryptButton);
        panel.add(textFieldPane);
    }

    private void setupDecryptPanel(JPanel panel) {

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == encryptPathField) {
            System.out.println("Path Updated!");

        } else if (e.getSource() == encryptBrowse) {
            browseWindow();
        } else if (e.getSource() == encryptButton) {
            if (encryptKeyField.getText().equals("")) {
                JOptionPane.showMessageDialog(this.window, "Key field is empty.");
            } else if (encryptKeyField.getText().equals(encryptVerifyField.getText())) {
                System.out.println("Matches!");
            } else {
                JOptionPane.showMessageDialog(this.window, "Keys do not match.");
            }
        }
    }
}
/*
 * Done: 1. Password matches 2. Password not empty TODO: Encrypt Button needs to
 * check if path is a file
 */