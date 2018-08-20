package pk.merite.koha.webui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import pk.merite.koha.webui.controller.DefaultController;

public class ApplicationView extends JFrame implements HyperlinkListener {
    
    public static final ApplicationView frame = new ApplicationView();

    public JEditorPane editor;
    public JScrollPane editorScroller;
    public JDialog proxyDialog;
    public JEditorPane proxyEditor;
    public JScrollPane proxyEditorScroller;
    public JTextArea console;
    public String header;
    public String footer;
    
    public DefaultController controller = new DefaultController();

    public class HtmlEditorKit extends HTMLEditorKit {
        public ViewFactory getViewFactory() {
            return new HtmlFactory();
        }

        public HtmlEditorKit() {
        }
    }

    public class HtmlFactory extends HTMLEditorKit.HTMLFactory {
        public View create(Element e) {
            AttributeSet attrs = e.getAttributes();
            Object o = attrs.getAttribute(StyleConstants.NameAttribute);
            if (o == HTML.Tag.INPUT || o == HTML.Tag.SELECT || o == HTML.Tag.TEXTAREA) {
                return new HtmlFormView(e);
            } else {
                View view = super.create(e);
                return view;
            }
        }

        public HtmlFactory() {
        }
    }

    public class HtmlFormView extends FormView {
        protected Component createComponent() {
            Component c = super.createComponent();
            AttributeSet attributes = super.getElement().getAttributes();
            String name = (String) attributes.getAttribute(HTML.Attribute.NAME);
            String value = (String) attributes.getAttribute(HTML.Attribute.VALUE);
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                Dimension d = btn.getPreferredSize();
                if(d.width < 100) {
                    d.width = 100;
                }
                btn.setPreferredSize(d);
                btn.setFont(ApplicationView.defaultMenuFont);
                btn.removeActionListener(HtmlFormView.this);
            } else if (c instanceof JComboBox) {
                c.setBackground(Color.white);
            } else if (c instanceof JRadioButton) {
                c.setBackground(Color.white);
            } else if (c instanceof JScrollPane) {
                ((JScrollPane)c).getViewport().getView().setFont(ApplicationView.defaultMenuFont);
            }
            controller.componentAdded(name, value, c);
            return c;
        }

        public HtmlFormView(Element e) {
            super(e);
        }
    }

    public ApplicationView() {
        editor = new JEditorPane("text/html", "");
        editor.setEditable(false);
        editor.setEditorKit(new HtmlEditorKit());
        editor.addHyperlinkListener(this);
        editorScroller = new JScrollPane(editor);
        proxyEditor = new JEditorPane("text/html", "");
        proxyEditor.setEditable(false);
        proxyEditor.setEditorKit(new HtmlEditorKit());
        proxyEditorScroller = new JScrollPane(proxyEditor);
        getContentPane().add(editorScroller);
        try {
            editor.setPage(new URL("file:/" + new File("Default.html").getAbsolutePath()));
        } catch (Exception e) {
        }
    }
    
    public void setPage(String page, JEditorPane editor) {
        char chars[] = new char[1024 * 4];
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            if(header == null) {
	            reader = new BufferedReader(new InputStreamReader(new FileInputStream("Header.html"), "utf-8"));
	            for (int read = reader.read(chars); read > 0; read = reader.read(chars)) {
	                buffer.append(chars, 0, read);
	            }
	            reader.close();
	            header = buffer.toString();
	            buffer.delete(0, buffer.length());
            }

            if(footer == null) {
	            reader = new BufferedReader(new InputStreamReader(new FileInputStream("Footer.html"), "utf-8"));
	            for (int read = reader.read(chars); read > 0; read = reader.read(chars)) {
	                buffer.append(chars, 0, read);
	            }
	            reader.close();
	            footer = buffer.toString();
	            buffer.delete(0, buffer.length());
            }

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(page), "utf-8"));
            for (int read = reader.read(chars); read > 0; read = reader.read(chars)) {
                buffer.append(chars, 0, read);
            }
            reader.close();            

            try {
                int start = page.lastIndexOf('/');
                page = page.substring(start + 1);
                int end = page.lastIndexOf('.');
                if(end > 0) {
                    page = page.substring(0, end);
                }
                String p = getClass().getName();
                p = p.substring(0, p.lastIndexOf('.'));
                controller = (DefaultController)Class.forName(p + ".controller." + page + "Controller").newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                controller = new DefaultController();
            }
            editor.setText(header + buffer.toString() + footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            frame.setTitle("Pakistan Library Automation Group");
            frame.setDefaultCloseOperation(3);
            frame.setSize(800, 580);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screen.width - frame.getSize().width) / 2;
            int y = (screen.height - frame.getSize().height - 25) / 2;
            frame.setLocation(x, y);
            frame.setVisible(true);
            frame.setPage("Default.html", frame.editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
    }
    
    
    /**
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            setPage(e.getURL().toString().substring(5), editor);
        }
    }

    protected void log(String message) {
        console.append(message);
        console.append("\n");
    }

    public static Font defaultMenuFont = new Font("Dialog", 0, 12);

}
