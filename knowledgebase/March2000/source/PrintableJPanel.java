import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

public class PrintableJPanel extends JPanel implements Printable {

    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D)g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        paint (g);

        return Printable.PAGE_EXISTS;
    }
}