package pk.merite.koha.webui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import pk.merite.koha.webui.reports.DuplicateBarcodeReport;


/**
 * @author MohsinA
 *
 */

public class DuplicateBarcodeController extends DefaultController {
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#componentAdded(java.lang.String, java.awt.Component)
     */
    public void componentAdded(String name, String value, Component c) {
        if(c instanceof JButton) {
            ((JButton)c).addActionListener(this);
        }
    }
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if("Preview".equals(e.getActionCommand())) {
        	new DuplicateBarcodeReport().createReport();
        }
    }
}
