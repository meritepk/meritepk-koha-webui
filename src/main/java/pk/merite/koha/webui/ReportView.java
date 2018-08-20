package pk.merite.koha.webui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author MohsinA
 */

public class ReportView extends JPanel implements ActionListener {

    protected Pageable pageable;
    protected int page = 0;
    protected JFrame frame = new JFrame("Report Viewer");

    public void showReport(Pageable pageable) {
        this.pageable = pageable;

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton("Print");
        Dimension dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("First");
        dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("Previous");
        dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("Next");
        dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("Last");
        dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("Close");
        dimension = button.getPreferredSize();
        if(dimension.width < 100) {
            dimension.width = 100;
            button.setPreferredSize(dimension);
        }
        button.setFont(ApplicationView.defaultMenuFont);
        button.addActionListener(this);
        panel.add(button);
        
        PageFormat format = pageable.getPageFormat(page);
        Dimension d = this.getPreferredSize();
        d.setSize(format.getWidth() + 50, format.getHeight() + 50);
        this.setPreferredSize(d);
        
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(this), BorderLayout.CENTER);
        frame.setSize(d.width + 26, 600);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - frame.getSize().width) / 2;
        int y = (screen.height - frame.getSize().height - 25) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }
    
    public void showPage() {
        PageFormat format = pageable.getPageFormat(page);
        Dimension d = this.getPreferredSize();
        d.setSize(format.getWidth() + 50, format.getHeight() + 50);
        this.setPreferredSize(d);
        repaint();
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        Dimension d = this.getPreferredSize();
        Dimension size = this.getSize();
        int x = (size.width - d.width) / 2;
        int y = (size.height - d.height) / 2;
        g.setColor(Color.black);
        g.drawString("Page: " + (page + 1) + " of " + pageable.getNumberOfPages(), 25, 20);
        g.setColor(Color.white);
        g.fillRect(x + 25, y + 25, d.width - 50, d.height - 50);
        g.translate(x, y);
        try {
            pageable.getPrintable(page).print(g, pageable.getPageFormat(page), page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if("Next".equals(e.getActionCommand())) {
            if(page + 1 < pageable.getNumberOfPages()) {
                page ++;
                showPage();
            }
        } else if("Previous".equals(e.getActionCommand())) {
            if(page > 0) {
                page --;
                showPage();
            }
        } else if("First".equals(e.getActionCommand())) {
            if(page > 0) {
                page = 0;
                showPage();
            }
        } else if("Last".equals(e.getActionCommand())) {
            if(page + 1 < pageable.getNumberOfPages()) {
                page = pageable.getNumberOfPages() - 1;
                showPage();
            }
        } else if("Close".equals(e.getActionCommand())) {
            frame.dispose();
        } else if("Print".equals(e.getActionCommand())) {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(pageable);
            if(printerJob.printDialog()) {
                try {
                    printerJob.print();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
    
}
