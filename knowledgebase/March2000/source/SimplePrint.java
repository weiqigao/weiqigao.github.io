import java.awt.*;
import java.awt.print.*;

public class SimplePrint implements Printable {

    private String stringToPrint;

    public SimplePrint() {
        this("This is the Default String. Go Blues!!!");
    }

    public SimplePrint(String stringToPrint) {
        this.stringToPrint = stringToPrint;
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        g.setFont(new Font("Helvetica", Font.PLAIN, 24));
        g.setColor(Color.green);
        g.drawString(stringToPrint, 100, 100);

        return Printable.PAGE_EXISTS;
    }

    public static void main(String[] args) {
        // Get a PrintJob
        PrinterJob pj = PrinterJob.getPrinterJob();
        Printable painter;

        // Specify the painter
        if (args.length == 0) {
            painter = new SimplePrint();
        } else {
            painter = new SimplePrint(args[0]);
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

        new PrintPreview(painter, "Print Preview - SimplePrint");
    }
}