package cloudcrypt;

import javax.swing.*;

public class ui{
    public ui(){
        mainWindow();
    }

    private void mainWindow(){
        JFrame window = new JFrame("Cloud Crypt");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(350,200);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel encryptPanel = new JPanel();
        JPanel decryptPanel = new JPanel();
        tabbedPane.addTab("Encrypt", encryptPanel);
        tabbedPane.addTab("Decrypt", decryptPanel);
        tabbedPane.setVisible(true);
        window.setVisible(true);
    }
}


