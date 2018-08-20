package pk.merite.koha.webui.reports;

import java.awt.Color;
import java.awt.ComponentOrientation;
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
import java.util.Hashtable;

import javax.swing.JTextArea;
import javax.swing.RepaintManager;

import pk.merite.koha.webui.ReportView;
import pk.merite.koha.webui.utils.Context;


/**
 * @author MohsinA
 *
 */

public class BookCardReport implements Pageable, Printable {
    
    public static final int RECORDS_PER_PAGE = 4;
    protected int numberOfPages = 0;
    protected PageFormat pageFormat = new PageFormat();
    protected ArrayList results = new ArrayList();
    protected Font documentFont;
    protected Font defaultFont;
    protected JTextArea txtSide = new JTextArea();
    protected JTextArea txtMain = new JTextArea();
    
    /**
     * 
     */
    public BookCardReport() {
    	defaultFont = Font.decode("Arial-Normal-10");
    	RepaintManager.currentManager(txtSide).setDoubleBufferingEnabled(false);
    	txtSide.setFont(defaultFont);
    	txtSide.setBounds(0, 0, 1 * 72, (int)(2.5 * 72));
    	txtSide.setLineWrap(true);
    	RepaintManager.currentManager(txtMain).setDoubleBufferingEnabled(false);
    	txtMain.setFont(defaultFont);
    	txtMain.setBounds(0, 0, 4 * 72, (int)(2.5 * 72));
    	txtMain.setLineWrap(true);
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
        int x = (int)pageFormat.getImageableX();
        int y = (int)pageFormat.getImageableY();
        documentFont = g.getFont();
        g.translate(x, y);
        g.setColor(Color.black);
        int index = pageIndex * RECORDS_PER_PAGE;
        for (int i = index; i < index + RECORDS_PER_PAGE && i < results.size(); i++) {
        	ArrayList data = (ArrayList)results.get(i);
        	if(data.size() == 4) {
        		setMarcSubFields(data);
        	}
        	draw(g, data);
        }
        g.setFont(documentFont);
        return PAGE_EXISTS;
    }
    
    protected void draw(Graphics g, ArrayList data) {
    	String classification = (String)data.get(1);
    	String authormark = (String)data.get(2);
    	ArrayList barcodes = (ArrayList)data.get(3);
    	Hashtable table = (Hashtable)data.get(4);
    	StringBuffer side = new StringBuffer();
    	StringBuffer main = new StringBuffer();
    	if(classification.length() > 8) {
        	side.append(classification.substring(0, 8));
    	} else {
        	side.append(classification);
        	for (int j = classification.length(); j < 8; j++) {
        		side.append(' ');
			}
    	}
    	side.append("\r\n");
    	if(authormark.length() > 8) {
        	side.append(authormark.substring(0, 8));
    	} else {
        	side.append(authormark);
    	}
    	side.append("\r\n");
    	for (int j = 0; j < barcodes.size(); j++) {
        	side.append("\r\n");
        	String barcode = (String)barcodes.get(j);
        	if(barcode.length() > 8) {
            	side.append(barcode.substring(0, 8));
        	} else {
            	side.append(barcode);
        	}
		}
    	String s100a = (String)table.get("100a");
    	if(s100a != null) {
    		main.append(s100a);
        	main.append("\r\n");
        	main.append("  ");
    	} else {
    		main.append(table.get("245a") == null ? "" : table.get("245a"));
    		main.append(table.get("245c") == null ? "" : table.get("245c"));
        	main.append("\r\n");
        	main.append("  ");
    	}
    	String s245a = (String)table.get("245a");
    	if(s245a != null) {
    		main.append(s245a);
    	}
    	String s245c = (String)table.get("245c");
    	if(s245c != null) {
    		main.append(s245c);
    	}
    	String s250a = (String)table.get("250a");
    	if(s250a != null) {
    		main.append(" -- ");
    		main.append(s250a);
    	}
		main.append(" -- ");
    	String s260a = (String)table.get("260a");
    	if(s260a != null) {
    		main.append(s260a);
    	}
    	String s260b = (String)table.get("260b");
    	if(s260b != null) {
    		main.append(s260b);
    	}
    	String s260c = (String)table.get("260c");
    	if(s260c != null) {
    		main.append(s260c);
    	}
    	main.append("\r\n");
    	main.append("  ");
    	String s300a = (String)table.get("300a");
    	if(s300a != null) {
    		main.append(s300a);
    	}
    	String s300b = (String)table.get("300b");
    	if(s300b != null) {
    		main.append(s300b);
    	}
    	String s300c = (String)table.get("300c");
    	if(s300c != null) {
    		main.append(s300c);
    	}
    	main.append("\r\n");
    	String s500 = (String)table.get("500");
    	if(s500 != null) {
        	main.append("\r\n");
        	main.append("  ");
    		main.append(s500);
    	}
    	main.append("\r\n");
    	main.append("  ");
    	String s650a = (String)table.get("650a");
    	if(s650a != null) {
    		main.append(s650a);
    	}
    	String s700a = (String)table.get("700a");
    	if(s700a != null) {
    		main.append(s700a);
    	}

    	boolean rtl = true;
    	String s546a = (String)table.get("546a");
    	if(s546a == null || s546a.equalsIgnoreCase("eng")) {
    		rtl = false;
    	}
    	if(rtl) {
	    	g.translate(10, 10);
	    	txtMain.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	    	txtMain.setText(main.toString());
	    	txtMain.paint(g);
	    	g.translate(72 * 4, 0);
	    	txtSide.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	    	txtSide.setText(side.toString());
	    	txtSide.paint(g);
	    	g.translate(-72 * 4, (int)(72 * 2.5));
    	} else {
	    	g.translate(10, 10);
	    	txtSide.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	    	txtSide.setText(side.toString());
	    	txtSide.paint(g);
	    	g.translate(62, 0);
	    	txtMain.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	    	txtMain.setText(main.toString());
	    	txtMain.paint(g);
	    	g.translate(-72, (int)(72 * 2.5));
    	}
    }
    
    public void createReport(String sql) {
        Context context = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            context = new Context();
            statement = context.prepareStatement(sql);
            result = statement.executeQuery();
            byte bytes[] = null;
            String previous = null;
        	ArrayList data = null;
        	ArrayList barcodes = null;
            while(result.next()) {
            	String biblionumber = "", barcode = "", classification = "", classification2 = "";
                bytes = result.getBytes("biblionumber");
                if(bytes != null && bytes.length > 0) {
                	biblionumber = new String(bytes, "utf-8");
                }
                bytes = result.getBytes("barcode");
                if(bytes != null && bytes.length > 0) {
                    barcode = new String(bytes, "utf-8");
                }
                if(previous == null || !previous.equals(biblionumber)) {
                	previous = biblionumber;
	                bytes = result.getBytes("classification");
	                if(bytes != null && bytes.length > 0) {
	                    classification = new String(bytes, "utf-8");
	                }
	                int index = classification.indexOf(' ');
	                if(index > 0) {
	                    classification2 = classification.substring(index + 1);
	                    classification = classification.substring(0, index);
	                }
	                barcodes = new ArrayList();
	                barcodes.add(barcode);
	                data = new ArrayList();
	                data.add(biblionumber);
	                data.add(classification);
	                data.add(classification2);
	                data.add(barcodes);
	                results.add(data);
                } else {
                	barcodes.add(barcode);
                }
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
        ReportView viewer = new ReportView();
        viewer.showReport(this);
    }

    public void setMarcSubFields(ArrayList data) {
        Context context = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            context = new Context();
            String sql = "SELECT * FROM marc_subfield_table WHERE bibid = " + data.get(0) + " ORDER BY tag, subfieldcode";
            statement = context.prepareStatement(sql);
            result = statement.executeQuery();
            byte bytes[] = null;
            Hashtable table = new Hashtable();
            while(result.next()) {
            	String key = result.getString("tag");
            	String subfieldcode = result.getString("subfieldcode");
            	if(subfieldcode != null) {
            		key += subfieldcode;
            	}
            	String subfieldvalue = null;
                bytes = result.getBytes("subfieldvalue");
                if(bytes != null && bytes.length > 0) {
                	subfieldvalue = new String(bytes, "utf-8");
                }
                if(subfieldvalue != null) {
                	table.put(key, subfieldvalue);
                }
            }
            data.add(table);
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
    }
}
