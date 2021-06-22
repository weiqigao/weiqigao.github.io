import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;;

public class SimpleFrame extends JFrame {

    private PrintableJPanel panel;

    public SimpleFrame() {
        this("Simple Frame");
    }

    public SimpleFrame(String title) {
        super(title);
        init();
    }

    private void init() {
        panel = new PrintableJPanel();

        JButton btn = new JButton("Preview this");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                previewAction();
            }
        });
        panel.add(btn);

        btn = new JButton("Print this");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                printAction();
            }
        });
        panel.add(btn);

        btn = new JButton("Quit");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                quitAction();
            }
        });
        panel.add(btn);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                quitAction();
            }
        });

        getContentPane().add(panel);
        pack();
        setVisible(true);
    }

    private void printAction() {
        // Get a PrintJob
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(panel);

        // Show the print dialog
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException pe) {
                System.out.println("Exception while printing.\n");
                pe.printStackTrace();
            }
        }
    }

    private void previewAction() {
        new PrintPreview(panel, "Print Preview - PrintablePanel");
    }

    private void quitAction() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SimpleFrame sf = new SimpleFrame("Simple Frame with Print Preview");
    }
}