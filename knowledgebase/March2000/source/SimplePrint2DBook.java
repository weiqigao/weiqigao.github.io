import java.awt.*;
import java.awt.print.*;

public class SimplePrint2DBook implements Printable {
    private String stringToPrint;

    public SimplePrint2DBook() {
        this("This is the Default 2D String: Go Blues!!!");
    }

    public SimplePrint2DBook(String stringToPrint) {
        this.stringToPrint = stringToPrint;
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Helvetica", Font.PLAIN, 24));
        g2.translate(pf.getImageableX(), pf.getImageableY());

        g2.setColor(Color.gray);
        g2.draw(new Rectangle((int) pf.getImageableWidth(), (int) pf.getImageableHeight()));

        Paint defaultPaint = new GradientPaint(100f, 100f, Color.blue,
                             (float) g2.getFontMetrics().getStringBounds(stringToPrint, g2).getWidth(), 100f, Color.red);

        g2.setClip(100,175,401,51);
        g2.draw(new Rectangle(100,175,400,30));

        g2.setPaint(defaultPaint);
        g2.drawString(stringToPrint, 100, 200);

        defaultPaint = new GradientPaint(100f, 100f, Color.green,
                       (float) g2.getFontMetrics().getStringBounds(stringToPrint, g2).getWidth(), 100f, Color.yellow);

        g2.translate(0, 0);
        g2.setClip(100,275,401,51);

        g2.setColor(Color.gray);
        g2.draw(new Rectangle(100,275,400,30));

        g2.setPaint(defaultPaint);
        g2.drawString(stringToPrint, 100, 300);

        return Printable.PAGE_EXISTS;
    }

    public static void main(String[] args) {
        // Get a PrintJob
        PrinterJob pj = PrinterJob.getPrinterJob();
        Book book = new Book();
        Printable painter;

        // Specify the painter
        if (args.length == 0) {
            painter = new SimplePrint2DBook();
        } else {
            painter = new SimplePrint2DBook(args[0]);
        }

        PageFormat pageFormat = pj.pageDialog(pj.defaultPage());
        book.append(painter, pageFormat);
        pj.setPageable(book);

        // Show the print dialog
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException pe) {
                System.out.println("Exception while printing.\n");
                pe.printStackTrace();
            }
        }

        new PrintPreview(book, "Print Preview - SimplePrint2DBook");
    }
}