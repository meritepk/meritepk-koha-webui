package pk.merite.koha.webui.reports;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.RepaintManager;

import pk.merite.koha.webui.ReportView;
import pk.merite.koha.webui.utils.Context;


/**
 * @author MohsinA
 *
 */

public class MemberCardReport implements Pageable, Printable {
    
    public static final int RECORDS_PER_PAGE = 4;
    public static final int CARD_WIDTH = (int)(3.3 * 72);
    public static final int CARD_HEIGHT = (int)(2.2 * 72);
    protected int numberOfPages = 0;
    protected PageFormat pageFormat = new PageFormat();
    protected String txtDepartment;
    protected String txtInstruction;
    protected ArrayList results = new ArrayList();
    protected Font documentFont;
    protected Font defaultFont;
    protected Font headingFont;
    protected Font heading2Font;
    protected Font barcodeFont;
    protected ReportView viewer = new ReportView();
    protected JTextArea editor = new JTextArea();
    protected JLabel address = new JLabel();
    
    
    /**
     * 
     */
    public MemberCardReport() {
        try {
            barcodeFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("configs/IDAutomationHC39M_Free.ttf")).deriveFont(10.0F);
            defaultFont = Font.decode("Arial-Normal-10");
            headingFont = defaultFont.deriveFont(Font.BOLD, 12.0F);
            heading2Font = defaultFont.deriveFont(Font.PLAIN, 11.0F);
        	RepaintManager.currentManager(editor).setDoubleBufferingEnabled(false);
        	editor.setFont(defaultFont);
        	editor.setBounds(0, 0, (int)(3.2 * 72), (int)(2 * 72));
        	editor.setLineWrap(true);
        	editor.setWrapStyleWord(true);
        	RepaintManager.currentManager(address).setDoubleBufferingEnabled(false);
        	address.setFont(defaultFont);
        	address.setBounds(0, 0, 125, 15);
        	address.setHorizontalAlignment(JLabel.RIGHT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see java.awt.print.Pageable#getNumberOfPages()
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * @see java.awt.print.Pageable#getPageFormat(int)
     */
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pageFormat;
    }

    /**
     * @see java.awt.print.Pageable#getPrintable(int)
     */
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return this;
    }

    /**
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if(pageIndex >= numberOfPages) {
            return NO_SUCH_PAGE;
        }
        documentFont = g.getFont();
        int x = (int)pageFormat.getImageableX();
        int y = (int)pageFormat.getImageableY();
        g.translate(x, y);
        g.setColor(Color.black);
        int index = pageIndex * RECORDS_PER_PAGE;
        y += 10;
        for (int i = index; i < index + RECORDS_PER_PAGE && i < results.size(); i++) {
            String data[] = (String[])results.get(i);
            g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
            g.drawImage(new ImageIcon("/usr/koha228/intranet/htdocs/logo.gif").getImage(), x + 5, y + 5, 56, 55, viewer);
            g.setFont(headingFont);
            g.drawString(data[5], x + 65, y + 25);
            g.setFont(heading2Font);
            g.drawString("Library Membership Card", x + 75, y + 45);
            g.setFont(defaultFont);
            g.drawString("Membership #: " + data[0], x + 10, y + 75);
            g.drawString("Name: " + data[1] + " " + data[2], x + 10, y + 90);
            g.drawString("Department: " + txtDepartment, x + 10, y + 105);
            g.drawString("Issuing Authority", x + 10, y + 150);
            g.drawImage(new ImageIcon("/usr/koha228/intranet/htdocs/Patronimages/" + data[0] + ".jpg").getImage(), x + 180, y + 50, 50, 60, viewer);
            g.setFont(barcodeFont);
            String barcode = "*" + data[0] + "*";
            int width = 50 - g.getFontMetrics().charsWidth(barcode.toCharArray(), 0, barcode.length());
            if(width > 0) {
            	width = 0;
            }
            g.drawString(barcode, x + 180 + width, y + 145);

            g.drawRect(x + CARD_WIDTH + 10, y, CARD_WIDTH, CARD_HEIGHT);
            g.setFont(defaultFont);
            //g.drawString(txtInstruction + data[3], x + 310, y + 25);
        	g.translate(x + CARD_WIDTH + 15, y + 5);
        	editor.setText(txtInstruction + data[3]);
        	editor.paint(g);
        	g.translate(-x - CARD_WIDTH - 15, -y - 5);
            //g.drawString(txtInstruction + data[3], x + 310, y + 25);
            //g.drawString(data[5], x + CARD_WIDTH + 160, y + 120);
            //g.drawString(data[6], x + CARD_WIDTH + 160, y + 135);
            //g.drawString(data[7], x + CARD_WIDTH + 160, y + 150);
        	g.translate(x + CARD_WIDTH + 110, y + 110);
        	address.setText(data[5]);
        	address.paint(g);
        	g.translate(0, 15);
        	address.setText(data[6]);
        	address.paint(g);
        	g.translate(0, 15);
        	address.setText(data[7]);
        	address.paint(g);
            g.translate(-(x + CARD_WIDTH + 110), -(y + 140));
            g.drawString("Sign of Card Holder", x + CARD_WIDTH + 20, y + 150);
            y += 180;
        }
        g.setFont(documentFont);
        return PAGE_EXISTS;
    }
    
    public void createReport(String sql, String txtDepartment, String txtInstruction) {
        this.txtDepartment = txtDepartment;
        this.txtInstruction = txtInstruction;
        Context context = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            context = new Context();
            statement = context.prepareStatement(sql);
            result = statement.executeQuery();
            byte bytes[] = null;
            while(result.next()) {
            	String cardnumber = "", firstname = "", surname = "", expiry = "", branchcode = "", branchname = "", branchaddress1 = "", branchphone = "";
                bytes = result.getBytes("cardnumber");
                if(bytes != null && bytes.length > 0) {
                	cardnumber = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("firstname");
                if(bytes != null && bytes.length > 0) {
                	firstname = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("surname");
                if(bytes != null && bytes.length > 0) {
                	surname = new String(bytes, "utf-8");
                }
                Date date = result.getDate("expiry");
                if(date != null) {
                	expiry = new SimpleDateFormat("dd MMMM yyyy").format(date);
                }
                bytes = result.getBytes("branchcode");
                if(bytes != null && bytes.length > 0) {
                	branchcode = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("branchname");
                if(bytes != null && bytes.length > 0) {
                	branchname = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("branchaddress1");
                if(bytes != null && bytes.length > 0) {
                	branchaddress1 = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("branchphone");
                if(bytes != null && bytes.length > 0) {
                	branchphone = new String(bytes, "utf-8");
                }
                String data[] = {cardnumber, firstname, surname, expiry, branchcode, branchname, branchaddress1, branchphone};
                results.add(data);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            if(context != null) {
                try {
                    context.closeConnection();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
        
        numberOfPages = results.size() / RECORDS_PER_PAGE;
        if(results.size() % RECORDS_PER_PAGE != 0) {
        	numberOfPages += 1;
        }
        
        Paper paper = pageFormat.getPaper();
        paper.setImageableArea(20, 20, paper.getWidth() - 40, paper.getHeight() - 40);
        pageFormat.setPaper(paper);
        viewer.showReport(this);
    }
}
