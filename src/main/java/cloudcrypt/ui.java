package cloudcrypt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ui implements ActionListener {
    JTextField encryptPathField;
    JButton encryptBrowse;
    JPasswordField encryptKeyField;
    JPasswordField encryptVerifyField;

    public ui() {
        mainWindow();
    }

    private void mainWindow() {
        JFrame window = new JFrame("Cloud Crypt");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel encryptPanel = new JPanel();
        setupEncryptPanelTop(encryptPanel);
        setupEncryptPanelMid(encryptPanel);
        JPanel decryptPanel = new JPanel();
        setupDecryptPanel(decryptPanel);
        tabbedPane.addTab("Encrypt", encryptPanel);
        tabbedPane.addTab("Decrypt", decryptPanel);
        window.add(tabbedPane);
        window.pack();
        window.setVisible(true);
    }

    private void setupEncryptPanelTop(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        JLabel pathLabel = new JLabel("Path to File:");
        textFieldPane.add(pathLabel);
        encryptPathField = new JTextField(25);
        encryptPathField.addActionListener(this);
        textFieldPane.add(encryptPathField);
        encryptBrowse = new JButton("Browse");
        textFieldPane.add(encryptBrowse);
        panel.add(textFieldPane);
    }

    private void setupEncryptPanelMid(JPanel panel) {
        JPanel textFieldPane = new JPanel();
        JLabel keyLabel = new JLabel("Key:");
        textFieldPane.add(keyLabel);
        encryptKeyField = new JPasswordField(25);
        textFieldPane.add(encryptKeyField);

        panel.add(textFieldPane);
    }

    private void setupDecryptPanel(JPanel panel) {

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == encryptPathField) {
            System.out.println("Text Updated!");
        }
    }
}
