import java.awt.*;
import java.awt.print.*;

public class SimplePrint2D implements Printable {
    private String stringToPrint;

    public SimplePrint2D() {
        this("This is the Default 2D String: Go Blues!!!");
    }

    public SimplePrint2D(String stringToPrint) {
        this.stringToPrint = stringToPrint;
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(new Font("Helvetica", Font.PLAIN, 24));

        Paint defaultPaint = new GradientPaint(100f, 100f, Color.blue,
                             (float) g2.getFontMetrics().getStringBounds(stringToPrint, g2).getWidth(), 100f, Color.red);

        g2.setPaint(defaultPaint);
        g2.translate(pf.getImageableX(), pf.getImageableY());
        g2.drawString(stringToPrint, 0, 0);


        defaultPaint = new GradientPaint(100f, 100f, Color.green,
                       (float) g2.getFontMetrics().getStringBounds(stringToPrint, g2).getWidth(), 100f, Color.yellow);

        g2.setPaint(defaultPaint);
        g2.translate(100, 300);
        g2.drawString(stringToPrint, 0, 0);

        return Printable.PAGE_EXISTS;
    }

    public static void main(String[] args) {
        // Get a PrintJob
        PrinterJob pj = PrinterJob.getPrinterJob();
        Printable painter;

        // Specify the painter
        if (args.length == 0) {
            painter = new SimplePrint2D();
        } else {
            painter = new SimplePrint2D(args[0]);
        }

        pj.setPrintable(painter);

        // Show the print dialog
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException pe) {
                System.out.println("Exception while printing.\n");
                pe.printStackTrace();
            }
        }

        new PrintPreview(painter, "Print Preview - SimplePrint2D");
    }
}