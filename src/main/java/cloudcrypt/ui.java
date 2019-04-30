package cloudcrypt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ui implements ActionListener{
    public ui(){
        mainWindow();
    }

    private void mainWindow(){
        JFrame window = new JFrame("Cloud Crypt");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(350,200);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel encryptPanel = new JPanel();
        setupEncryptPanel(encryptPanel);
        JPanel decryptPanel = new JPanel();
        setupDecryptPanel(decryptPanel);
        tabbedPane.addTab("Encrypt", encryptPanel);
        tabbedPane.addTab("Decrypt", decryptPanel);
        window.add(tabbedPane);
        window.setVisible(true);
    }

    private void setupEncryptPanel(JPanel panel){
        JLabel pathLabel = new JLabel("Path to File:");
        panel.add(pathLabel);
        JTextField pathField = new JTextField(25);
        pathField.addActionListener(this);
        System.out.println(pathField.getText());
        panel.add(pathField);
    }

    private void setupDecryptPanel(JPanel panel){

    }

    public void actionPerformed(ActionEvent e) {

    }
}


