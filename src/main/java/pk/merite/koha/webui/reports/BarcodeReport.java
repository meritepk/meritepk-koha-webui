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
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.RepaintManager;

import pk.merite.koha.webui.ReportView;
import pk.merite.koha.webui.utils.Context;


/**
 * @author MohsinA
 *
 */

public class BarcodeReport implements Pageable, Printable {
    
    public static final int RECORDS_PER_PAGE = 9;
    protected int numberOfPages = 0;
    protected PageFormat pageFormat = new PageFormat();
    protected String txtAbovelLabel;
    protected String txtBelowLabel;
    protected ArrayList barcodes = new ArrayList();
    protected Font documentFont;
    protected Font defaultFont;
    protected Font barcodeFont;
    
    protected JLabel spine = new JLabel();
    
    
    /**
     * 
     */
    public BarcodeReport() {
        try {
            barcodeFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("configs/IDAutomationHC39M_Free.ttf")).deriveFont(10.0F);
            defaultFont = Font.decode("Arial-Normal-12");
        	spine.setFont(defaultFont.deriveFont(Font.BOLD, 14F));
        	spine.setBounds(0, 0, 100, 18);
        	spine.setHorizontalAlignment(JLabel.CENTER);
        	RepaintManager.currentManager(spine).setDoubleBufferingEnabled(false);
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
        int gapY = 18;
        y += 0;
        for (int i = index; i < index + RECORDS_PER_PAGE && i < barcodes.size(); i++) {
            y += 4;
            String data[] = (String[])barcodes.get(i);
            g.setFont(barcodeFont);
            g.drawString("*" + data[0] + "*", x + 150, y + 20);
            g.drawString("*" + data[0] + "*", x + 450, y + 35);
            g.setFont(defaultFont);
			g.drawString(txtAbovelLabel, x + 450, y);
			g.drawString(txtBelowLabel, x + 450, y + 52);
            g.drawString("Call# " + data[2], x, y);
            //g.setFont(defaultFont.deriveFont(Font.BOLD, 14F));
            //g.drawString(data[2], x + 350, y);
            int gap = -12;
            g.translate(x + 310, y + gap);
            spine.setText(data[2]);
            spine.paint(g);
            g.translate(-(x + 310), -(y + gap));
            g.setFont(defaultFont);
            g.drawString("Acc# " + data[0], x, y + (gapY * 1));
            //g.setFont(defaultFont.deriveFont(Font.BOLD, 14F));
            //g.drawString(data[3], x + 350, y + 20);
            gap += gapY;
            g.translate(x + 310, y + gap);
            spine.setText(data[3]);
            spine.paint(g);
            g.translate(-(x + 310), -(y + gap));
            g.setFont(defaultFont);
            g.drawString("Author: " + data[4], x, y + (gapY * 2));
            //g.setFont(defaultFont.deriveFont(Font.BOLD, 14F));
            //g.drawString(data[0], x + 350, y + 40);
            gap += gapY;
            g.translate(x + 310, y + gap);
            spine.setText(data[0]);
            spine.paint(g);
            g.translate(-(x + 310), -(y + gap));
            g.setFont(defaultFont);
            String title = data[1];
            if(title.length() > 35) {
            	title = title.substring(0, 35);
            }
            g.drawString("Title: " + title, x, y  + (gapY * 3));
            //g.setFont(defaultFont.deriveFont(Font.BOLD, 14F));
            //g.drawString(data[5], x + 350, y + 60);
            gap += gapY;
            g.translate(x + 310, y + gap);
            spine.setText(data[5]);
            spine.paint(g);
            g.translate(-(x + 310), -(y + gap));
            y += 80;
        }
        g.setFont(documentFont);
        return PAGE_EXISTS;
    }
    
    public void createReport(String sql, String txtAboveLabel, String txtBelowLabel) {
        this.txtAbovelLabel = txtAboveLabel;
        this.txtBelowLabel = txtBelowLabel;
        Context context = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            context = new Context();
            statement = context.prepareStatement(sql);
            result = statement.executeQuery();
            byte bytes[] = null;
            int index = 0;
            while(result.next()) {
                String barcode = "";
                String title = "";
                String classification = "";
                String classification2 = "";
                String author = "";
                String copyrightdate = "";
                bytes = result.getBytes("barcode");
                if(bytes != null && bytes.length > 0) {
                    barcode = new String(bytes, "utf-8");
                } else {
                    barcode = "";
                }
                bytes = result.getBytes("title");
                if(bytes != null && bytes.length > 0) {
                    title = new String(bytes, "utf-8");
                } else {
                    title = "";
                }
                bytes = result.getBytes("classification");
                if(bytes != null && bytes.length > 0) {
                    classification = new String(bytes, "utf-8");
                } else {
                    classification = "";
                }
                index = classification.indexOf(' ');
                if(index > 0) {
                    classification2 = classification.substring(index + 1);
                    classification = classification.substring(0, index);
                } else {
                    classification2 = "";
                }
                bytes = result.getBytes("author");
                if(bytes != null && bytes.length > 0) {
                    author = new String(bytes, "utf-8");
                } else {
                    author = "";
                }
                bytes = result.getBytes("copyrightdate");
                if(bytes != null && bytes.length > 0) {
                    copyrightdate = new String(bytes, "utf-8");
                } else {
                    copyrightdate = "";
                }
                String data[] = {barcode, title, classification, classification2, author, copyrightdate};
                barcodes.add(data);
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
        
        numberOfPages = barcodes.size() / RECORDS_PER_PAGE;
        if(barcodes.size() % RECORDS_PER_PAGE != 0) {
        	numberOfPages += 1;
        }
        
        Paper paper = pageFormat.getPaper();
        paper.setImageableArea(20, 20, paper.getWidth() - 40, paper.getHeight() - 40);
        pageFormat.setPaper(paper);
        ReportView viewer = new ReportView();
        viewer.showReport(this);
    }
}
