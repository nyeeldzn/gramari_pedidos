package helpers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.ProdutoPedido;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class BillPrintable implements Printable {

    public void setProdutos(ObservableList<ProdutoPedido> produtos) {
        this.produtos = produtos;
    }

    ObservableList<ProdutoPedido> produtos = FXCollections.observableArrayList();

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException
    {

        int r= produtos.size();
        int result = NO_SUCH_PAGE;
        if(pageIndex == 0){

            Graphics2D g2d = (Graphics2D) graphics;
            double width = pageFormat.getImageableWidth();
            g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

            try{
                int y = 20;
                int yShift = 10;
                int headerRectHeight = 15;

                g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2d.drawString("===================================", 12, y);y+=yShift;
                g2d.drawString("             SunOnRails            ", 12, y);y+=yShift;
                g2d.drawString("===================================", 12, y);y+=yShift;
                g2d.drawString(" Item Name                    QTD. ", 12, y);y+=yShift;
                g2d.drawString("-----------------------------------", 12, y);y+=headerRectHeight;

                for(int s=0; s<r; s++){
                    g2d.drawString(" " + produtos.get(s).getNome() + "     " + produtos.get(s).getQtd(),10,y);y+=yShift;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            result = PAGE_EXISTS;
        }
        return result;
    }

}