import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class PrintPreview extends JFrame {

    private static final int PRINTABLE_JOB = 0;
    private static final int PAGEABLE_JOB = 1;

    protected int m_wPage;
    protected int m_hPage;
    protected int jobType;
    protected Printable m_printable;
    protected Pageable m_pageable;
    protected JComboBox m_cbScale;
    protected PreviewContainer m_preview;

    public PrintPreview(Printable target) {
        this(target, "Print Preview");
    }

    public PrintPreview(Printable target, String title) {
        super(title);

        m_printable = target;
        jobType = PRINTABLE_JOB;

        init();
    }

    public PrintPreview(Pageable target) {
        this(target, "Print Preview");
    }

    public PrintPreview(Pageable target, String title) {
        super(title);

        m_pageable = target;
        jobType = PAGEABLE_JOB;

        init();
    }

    private void init() {
        System.out.println("Starting Print Previewer...");
        setSize(700, 500);
        JToolBar tb = new JToolBar();
        JButton bt = new JButton("Print");
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Use default printer, no dialog
                    PrinterJob prnJob = PrinterJob.getPrinterJob();

                    if (jobType == PRINTABLE_JOB) {
                        prnJob.setPrintable(m_printable);
                    } else {
                        prnJob.setPageable(m_pageable);
                    }

                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    prnJob.print();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    dispose();
                } catch (PrinterException ex) {
                    ex.printStackTrace();
                    System.err.println("Printing error: "+ex.toString());
                }
            }
        });
        bt.setAlignmentY(0.5f);
        bt.setMargin(new Insets(4,6,4,6));
        tb.add(bt);

        bt = new JButton("Close");
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
        bt.setAlignmentY(0.5f);
        bt.setMargin(new Insets(2,6,2,6));
        tb.add(bt);

        String[] scales = { "100 %", "50 %", "25 %", "10 %" };
        m_cbScale = new JComboBox(scales);

        m_cbScale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        String str = m_cbScale.getSelectedItem().toString();

                        if (str.endsWith("%"))
                            str = str.substring(0, str.length()-1);

                        str = str.trim();
                        int scale = 0;

                        try {
                            scale = Integer.parseInt(str);
                        } catch (NumberFormatException ex) {
                            return;
                        }

                        int w = (int)(m_wPage*scale/100);
                        int h = (int)(m_hPage*scale/100);
                        Component[] comps = m_preview.getComponents();

                        for (int k=0; k<comps.length; k++) {
                            if (!(comps[k] instanceof PagePreview))
                                continue;

                            PagePreview pp = (PagePreview)comps[k];
                            pp.setScaledSize(w, h);
                        }

                        m_preview.doLayout();
                        m_preview.getParent().getParent().validate();
                    }
                };

                runner.start();
            }
        });
        m_cbScale.setMaximumSize(m_cbScale.getPreferredSize());
        m_cbScale.setEditable(true);
        tb.addSeparator();
        tb.add(m_cbScale);
        getContentPane().add(tb, BorderLayout.NORTH);
        m_preview = new PreviewContainer();
        if (jobType == PRINTABLE_JOB) {
            PrinterJob prnJob = PrinterJob.getPrinterJob();
            PageFormat pageFormat = prnJob.defaultPage();
            if (pageFormat.getHeight()==0 || pageFormat.getWidth()==0) {
                System.err.println("Unable to determine default page size");
                return;
            }

            m_wPage = (int)(pageFormat.getWidth());
            m_hPage = (int)(pageFormat.getHeight());
            int scale = 100;
            int w = (int)(m_wPage*scale/100);
            int h = (int)(m_hPage*scale/100);
            int pageIndex = 0;

            try {
                while (true) {
                    BufferedImage img = new BufferedImage(m_wPage,m_hPage, BufferedImage.TYPE_INT_RGB);
                    Graphics g = img.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, m_wPage, m_hPage);
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                      RenderingHints.VALUE_ANTIALIAS_ON);
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                                                      RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    if (m_printable.print(g, pageFormat, pageIndex) != Printable.PAGE_EXISTS)
                        break;

                    PagePreview pp = new PagePreview(w, h, img);
                    m_preview.add(pp);

                    pageIndex++;
                }
            } catch (PrinterException e) {
                e.printStackTrace();
                System.err.println("Printing error: "+e.toString());
            }
        } else {
            PrinterJob prnJob = PrinterJob.getPrinterJob();
            int pageIndex = 0;
            try {
                while (pageIndex < m_pageable.getNumberOfPages()) {
                    PageFormat pageFormat = m_pageable.getPageFormat(pageIndex);

                    if (pageFormat.getHeight()==0 || pageFormat.getWidth()==0) {
                        System.err.println("Unable to determine default page size");
                        return;
                    }

                    m_wPage = (int)(pageFormat.getWidth());
                    m_hPage = (int)(pageFormat.getHeight());
                    int scale = 100;
                    int w = (int)(m_wPage*scale/100);
                    int h = (int)(m_hPage*scale/100);

                    BufferedImage img = new BufferedImage(m_wPage,m_hPage, BufferedImage.TYPE_INT_RGB);
                    Graphics g = img.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, m_wPage, m_hPage);

                    if (m_pageable.getPrintable(pageIndex).print(g, pageFormat, pageIndex) != Printable.PAGE_EXISTS)
                        break;


                    PagePreview pp = new PagePreview(w, h, img);
                    m_preview.add(pp);

                    pageIndex++;
                }
            } catch (PrinterException e) {
                e.printStackTrace();
                System.err.println("Printing error: "+e.toString());
            }
        }


        JScrollPane ps = new JScrollPane(m_preview);
        getContentPane().add(ps, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    class PreviewContainer extends JPanel {
        protected int H_GAP = 16;
        protected int V_GAP = 10;

        public Dimension getPreferredSize() {
            int n = getComponentCount();
            if (n == 0) return new Dimension(H_GAP, V_GAP);
            Component comp = getComponent(0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;
            Dimension dp = getParent().getSize();
            int nCol = Math.max((dp.width-H_GAP)/(w+H_GAP), 1);
            int nRow = n/nCol;
            if (nRow*nCol < n) nRow++;
            int ww = nCol*(w+H_GAP) + H_GAP;
            int hh = nRow*(h+V_GAP) + V_GAP;
            Insets ins = getInsets();
            return new Dimension(ww+ins.left+ins.right,hh+ins.top+ins.bottom);
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void doLayout() {
            Insets ins = getInsets();
            int x = ins.left + H_GAP;
            int y = ins.top + V_GAP;
            int n = getComponentCount();
            if (n == 0) return;
            Component comp = getComponent(0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;
            Dimension dp = getParent().getSize();
            int nCol = Math.max((dp.width-H_GAP)/(w+H_GAP), 1);
            int nRow = n/nCol;
            if (nRow*nCol < n)
                nRow++;
            int index = 0;
            for (int k = 0; k<nRow; k++) {
                for (int m = 0; m<nCol; m++) {
                    if (index >= n)
                        return;

                    comp = getComponent(index++);
                    comp.setBounds(x, y, w, h);
                    x += w+H_GAP;
                }
                y += h+V_GAP;
                x = ins.left + H_GAP;
            }
        }
    }

    class PagePreview extends JPanel {

        protected int m_w;
        protected int m_h;
        protected Image m_source;
        protected Image m_img;

        public PagePreview(int w, int h, Image source) {
            m_w = w;
            m_h = h;
            m_source= source;
            m_img = m_source.getScaledInstance(m_w, m_h,
            Image.SCALE_SMOOTH);
            m_img.flush();
            setBackground(Color.white);
            setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
        }

        public void setScaledSize(int w, int h) {
            m_w = w;
            m_h = h;
            m_img = m_source.getScaledInstance(m_w, m_h,
            Image.SCALE_SMOOTH);
            repaint();
        }

        public Dimension getPreferredSize() {
            Insets ins = getInsets();
            return new Dimension(m_w+ins.left+ins.right,m_h+ins.top+ins.bottom);
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(m_img, 0, 0, this);
            paintBorder(g);
        }
    }
}