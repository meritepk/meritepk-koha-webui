package pk.merite.koha.webui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pk.merite.koha.webui.reports.BarcodeReport;


/**
 * @author MohsinA
 *
 */

public class BarcodeGeneratorController extends DefaultController {
    
    protected boolean individual = true;
    protected int barcode = 0;
    protected JTextField txtBarcode;
    protected JList txtBarcodeList;
    protected DefaultListModel txtBarcodeListModel;
    protected JTextField txtFrom;
    protected JTextField txtTo;
    protected JTextField txtAboveLabel;
    protected JTextField txtBelowLabel;
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#componentAdded(java.lang.String, java.awt.Component)
     */
    public void componentAdded(String name, String value, Component c) {
        if(c instanceof JButton) {
            ((JButton)c).addActionListener(this);
        } else if(c instanceof JRadioButton) {
            JRadioButton button = (JRadioButton)c;
            button.addActionListener(this);
            button.setActionCommand(value);
        } else if("txtBarcode".equals(name)) {
            txtBarcode = (JTextField)c;
        } else if("txtBarcodeList".equals(name)) {
            txtBarcodeList = (JList)((JScrollPane)c).getViewport().getView();
            txtBarcodeListModel = new DefaultListModel();
            txtBarcodeList.setModel(txtBarcodeListModel);
            txtBarcodeList.setSelectionModel(new DefaultListSelectionModel());
        } else if("txtFrom".equals(name)) {
            txtFrom = (JTextField)c;
        } else if("txtTo".equals(name)) {
            txtTo = (JTextField)c;
        } else if("txtAboveLabel".equals(name)) {
            txtAboveLabel = (JTextField)c;
        } else if("txtBelowLabel".equals(name)) {
            txtBelowLabel = (JTextField)c;
        }
    }
    
    /**
     * @see pk.merite.koha.webui.controller.DefaultController#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if("Add".equals(e.getActionCommand())) {
            if(txtBarcode.getText().length() > 0) {
                txtBarcodeListModel.addElement(txtBarcode.getText());
            }
        } else if("Remove".equals(e.getActionCommand())) {
            int indices[] = txtBarcodeList.getSelectedIndices();
            for (int i = 0; indices != null && i < indices.length; i++) {
                System.out.println(indices[i]);
                txtBarcodeListModel.removeElementAt(indices[i]);
            }
            if(txtBarcodeListModel.getSize() == 0) {
                txtBarcodeListModel.removeAllElements();
            }
        } else if("Generate Barcodes".equals(e.getActionCommand())) {
            StringBuffer buffer = new StringBuffer();
            if(individual) {
                if(txtBarcodeListModel.getSize() <= 0) {
                    JOptionPane.showMessageDialog(txtBarcodeList, "Please add barcode first ...");
                } else {
                    buffer.append("SELECT barcode, title, classification, author, copyrightdate FROM biblio a, biblioitems b, items c WHERE c.biblioitemnumber = b.biblioitemnumber AND b.biblionumber = a.biblionumber ");
                    buffer.append("AND (c.barcode IN (");
                    for (int i = 0; i < txtBarcodeListModel.getSize(); i++) {
                        if(i > 0) {
                            buffer.append(",");
                        }
                        buffer.append("\'");
                        buffer.append(txtBarcodeListModel.elementAt(i));
                        buffer.append("\'");
                    }
                    buffer.append("))");
                    buffer.append(" ORDER BY c.barcode");
                }
            } else {
                if(txtFrom.getText().length() <= 0) {
                    JOptionPane.showMessageDialog(txtBarcodeList, "Please enter value in From field ...");
                } else if(txtTo.getText().length() <= 0) {
                    JOptionPane.showMessageDialog(txtBarcodeList, "Please enter value in To field ...");
                } else {
                    buffer.append("SELECT barcode, title, classification, author, copyrightdate FROM biblio a, biblioitems b, items c WHERE c.biblioitemnumber = b.biblioitemnumber AND b.biblionumber = a.biblionumber ");
                    if(barcode == 0) {
                        buffer.append("AND c.barcode BETWEEN '" + txtFrom.getText() + "' AND '" + txtTo.getText() + "' ORDER BY c.barcode");
                    } else if(barcode == 1) {
                        buffer.append("AND c.dateaccessioned BETWEEN '" + txtFrom.getText() + "' AND '" + txtTo.getText() + "' ORDER BY c.dateaccessioned, c.barcode");
                    } else {
                        buffer.append("AND classification BETWEEN '" + txtFrom.getText() + "' AND '" + txtTo.getText() + "'  ORDER BY classification, c.barcode");
                    }
                }
            }
            if(buffer.length() > 0) {
                System.out.println(buffer);
                new BarcodeReport().createReport(buffer.toString(), txtAboveLabel.getText(), txtBelowLabel.getText());
            }
        } else if("individual".equals(e.getActionCommand())) {
            individual = true;
        } else if("continuous".equals(e.getActionCommand())) {
            individual = false;
        } else if("barcode".equals(e.getActionCommand())) {
            barcode = 0;
        } else if("dateaccessioned".equals(e.getActionCommand())) {
            barcode = 1;
        } else if("classification".equals(e.getActionCommand())) {
            barcode = 2;
        }
    }
}
