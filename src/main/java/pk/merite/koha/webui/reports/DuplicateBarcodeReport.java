package pk.merite.koha.webui.reports;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import pk.merite.koha.webui.ReportView;
import pk.merite.koha.webui.utils.Context;


/**
 * @author MohsinA
 *
 */

public class DuplicateBarcodeReport implements Pageable, Printable {
    
    public static final int RECORDS_PER_PAGE = 35;
    protected int numberOfPages = 0;
    protected PageFormat pageFormat = new PageFormat();
    protected ArrayList records = new ArrayList();
    protected Font documentFont;
    protected Font defaultFont;
    
    
    /**
     * 
     */
    public DuplicateBarcodeReport() {
        try {
            defaultFont = Font.decode("Arial-Normal-12");
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
        for (int i = index; i < index + RECORDS_PER_PAGE && i < records.size(); i++) {
            y += 5;
            String data[] = (String[])records.get(i);
            String value = data[0];
            g.setFont(defaultFont);            
            g.drawString(value, x, y);
			value = data[1];
            if(value.length() > 50) {
				value = value.substring(0, 50);
            }
			g.drawString(value, x + 100, y);
			value = data[4];
			g.drawString(value, x + 400, y);
            y += 15;
        }
        g.setFont(documentFont);
        return PAGE_EXISTS;
    }
    
    public void createReport() {
        Context context = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            context = new Context();
            statement = context.prepareStatement("SELECT d.barcode FROM items d GROUP BY d.barcode HAVING count(d.barcode) > 1");
            result = statement.executeQuery();
            ArrayList barcodes = new ArrayList();
            while(result.next()) {
            	barcodes.add(result.getString("barcode"));
          	}
          	result.close();
          	result = null;
          	statement.close();
          	statement = null;
          	if(barcodes.isEmpty()) {
          		JOptionPane.showMessageDialog(null, "No duplicate barcode found");
          	} else {
				StringBuffer buffer = new StringBuffer();
				buffer.append("SELECT barcode, title, classification, author, copyrightdate FROM biblio a, biblioitems b, items c WHERE c.biblioitemnumber = b.biblioitemnumber AND b.biblionumber = a.biblionumber ");
				buffer.append("AND barcode IN ( '");
				buffer.append(barcodes.get(0));
				buffer.append("'");
				for (int i = 1; i < barcodes.size(); i++) {
					buffer.append(", '");
					buffer.append(barcodes.get(i));
					buffer.append("'");
				}
				buffer.append(" )");
				statement = context.prepareStatement(buffer.toString());
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
					records.add(data);
				}
				numberOfPages = records.size() / RECORDS_PER_PAGE;
		        if(records.size() % RECORDS_PER_PAGE != 0) {
		        	numberOfPages += 1;
		        }
        
				Paper paper = pageFormat.getPaper();
				paper.setImageableArea(20, 20, paper.getWidth() - 40, paper.getHeight() - 40);
				pageFormat.setPaper(paper);
				ReportView viewer = new ReportView();
				viewer.showReport(this);
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
    }
}
