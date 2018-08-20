package pk.merite.koha.webui.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import pk.merite.koha.webui.ApplicationView;
import pk.merite.koha.webui.LoginView;
import pk.merite.koha.webui.utils.Context;


/**
 * @author MohsinA
 *
 */

public class LoginController extends DefaultController {
    
    protected JTextField txtUserId;
    protected JTextField txtPassword;
    protected JTextField txtDatabase;
    protected JTextField txtHost;
    protected JTextField txtPort;
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#componentAdded(java.lang.String, java.awt.Component)
     */
    public void componentAdded(String name, String value, Component c) {
        if(c instanceof JButton) {
            JButton button = (JButton)c;
            button.setPreferredSize(new Dimension(100, 25));
            button.addActionListener(this);
        } else if("txtUserId".equals(name)) {
            txtUserId = (JTextField)c;
        } else if("txtPassword".equals(name)) {
            txtPassword = (JTextField)c;
        } else if("txtDatabase".equals(name)) {
            txtDatabase = (JTextField)c;
        } else if("txtHost".equals(name)) {
            txtHost = (JTextField)c;
        } else if("txtPort".equals(name)) {
            txtPort = (JTextField)c;
        }
    }
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if("OK".equals(e.getActionCommand())) {
            System.setProperty("login.view", "Y");
            System.setProperty("hostname", txtHost.getText());
            System.setProperty("database", txtDatabase.getText());
            System.setProperty("user", txtUserId.getText());
            System.setProperty("pass", txtPassword.getText());
            System.setProperty("port", txtPort.getText());
            try {
                new Context().openConnection().close();
                LoginView.frame.dispose();
                ApplicationView.main(null);
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(LoginView.frame, ee.getMessage());
            }
        } else if("Cancel".equals(e.getActionCommand())) {
            System.exit(0);
        }
    }
}
