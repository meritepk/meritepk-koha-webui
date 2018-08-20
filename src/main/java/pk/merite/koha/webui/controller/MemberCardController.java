package pk.merite.koha.webui.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pk.merite.koha.webui.reports.MemberCardReport;


/**
 * @author MohsinA
 *
 */

public class MemberCardController extends DefaultController {
    
    protected JTextField txtDepartment;
    protected JTextArea txtInstruction;
	protected JTextField txtFrom;
    protected JTextField txtTo;
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#componentAdded(java.lang.String, java.awt.Component)
     */
    public void componentAdded(String name, String value, Component c) {
        if(c instanceof JButton) {
            ((JButton)c).addActionListener(this);
        } else if("txtDepartment".equals(name)) {
			txtDepartment = (JTextField)c;
        } else if("txtInstruction".equals(name)) {
        	JScrollPane scrollPane = (JScrollPane)c;
        	scrollPane.setPreferredSize(new Dimension(300, 150));
			txtInstruction = (JTextArea)scrollPane.getViewport().getView();
			txtInstruction.setLineWrap(true);
			txtInstruction.setWrapStyleWord(true);
        } else if("txtFrom".equals(name)) {
            txtFrom = (JTextField)c;
        } else if("txtTo".equals(name)) {
            txtTo = (JTextField)c;
        }
    }
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if("Preview".equals(e.getActionCommand())) {
            StringBuffer buffer = new StringBuffer();
            if(txtFrom.getText().length() <= 0) {
                JOptionPane.showMessageDialog(txtFrom, "Please enter value in From field ...");
            } else if(txtTo.getText().length() <= 0) {
                JOptionPane.showMessageDialog(txtTo, "Please enter value in To field ...");
            } else {
                buffer.append("SELECT a.cardnumber, a.firstname, a.surname, a.expiry, a.branchcode, b.branchname, b.branchaddress1, b.branchphone FROM borrowers a, branches b WHERE a.branchcode = b.branchcode AND a.cardnumber BETWEEN '" + txtFrom.getText() + "' AND '" + txtTo.getText() + "' ORDER BY cardnumber");
            }
            if(buffer.length() > 0) {
                new MemberCardReport().createReport(buffer.toString(), txtDepartment.getText(), txtInstruction.getText());
            }
        }
    }
}
