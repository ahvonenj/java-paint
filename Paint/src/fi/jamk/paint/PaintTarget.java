package fi.jamk.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class PaintTarget extends JPanel implements MouseListener, MouseMotionListener
{
    private static final int AREASIZE = 800;                    // Vakiokoko piirtoalueelle
    private BufferedImage imagewrap;                            // Bufferedimageen tallennettaan kaikki piirretty,
                                                                // jotta se voidaan esimerkiksi kirjoittaa tiedostoon                             
    // Kaikki saatavilla olevat kynät ja niitä vastaavat numerot
    public static final int ERASE = 0;
    public static final int PIXEL = 1;
    public static final int CIRCLE = 2;
    public static final int RECTANGLE = 3;
    
    private static int pen = PIXEL;                                    // Tämänhetkinen kynä, johon alustetaan alkuarvoksi PIXEL-tyyppinen kynä
    
    private Point startPos = new Point(0, 0);                   // Hiiren alkukordinaatit ( Point(x, y) )
    private Point endPos = new Point(0, 0);                     // Hiiren loppukordinaatit ( Point(x, y) )
    Font font;
    
    private static Color currentColor = Color.BLACK;
    
    public PaintTarget()
    {
        setPreferredSize(new Dimension(AREASIZE, AREASIZE));    // Asetetaan piirtoalueen koko
        setBackground(Color.white);                             // Piirtoalueen taustaväriksi valkoinen
        this.addMouseListener(this);                            // Liitetään mouselistener painttargettiin itseensä (Painttarget itse on mouselistener)
        this.addMouseMotionListener(this);                      // Sama homma kun mouselistener, mutta tämä tarkastelee hiiren liikkeitä
        font = new Font("Comic Sans MS", Font.PLAIN,32);
    }
    
    public static void setPen(int pen)
    {
        PaintTarget.pen = pen; // Asettaa metodille parametriksi annetun kynän tämänhetkiseksi kynäksi
    }

    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        
        Graphics2D graphics2d = (Graphics2D)graphics; //Castataan Graphics -> Graphics2D
        
        // imagewrapin initialisointi
        if (imagewrap == null) 
        {
            int w = this.getWidth();
            int h = this.getHeight();
            imagewrap = (BufferedImage)this.createImage(w, h);
            Graphics2D initializedGraphics = imagewrap.createGraphics();
            initializedGraphics.setColor(Color.white);
            initializedGraphics.fillRect(0, 0, w, h);
        }
        
        graphics2d.drawImage(imagewrap, null, 0, 0);  // draw previous shapes
        
        Draw(graphics2d);
    }
    
    private void Draw(Graphics2D graphics2d)
    {
        graphics2d.setColor(getPenColor());
        switch(pen)
        {
            case 1:
                //graphics2d.fillArc(startPos.x, startPos.y, endPos.x - startPos.x, endPos.y - startPos.y, 200, 300);
                //graphics2d.drawString("LE PAC MAN", endPos.x -160, endPos.y +50);
                
                graphics2d.setFont(font);
                graphics2d.setColor(Color.MAGENTA);
                graphics2d.drawString("Y", endPos.x, endPos.y);
                graphics2d.setColor(Color.GREEN);
                graphics2d.drawString("O", endPos.x+20, endPos.y);
                graphics2d.setColor(Color.BLUE);
                graphics2d.drawString("L", endPos.x+50, endPos.y);
                graphics2d.setColor(Color.RED);
                graphics2d.drawString("O", endPos.x+65, endPos.y);
                break;
            case 2:
                graphics2d.fillOval(startPos.x, startPos.y, endPos.x - startPos.x, endPos.y - startPos.y);
                break;
            case 3:
                graphics2d.fillRect(startPos.x, startPos.y, endPos.x - startPos.x, endPos.y - startPos.y);
                break;
        }
    }
    
    public static void setPenColor(Color color)
    {
        PaintTarget.currentColor = color;
    }
    
    private Color getPenColor()
    {
        return this.currentColor;
    }
    
    public void mousePressed(MouseEvent e) 
    {
        /*Kun hiiren nappia painetaan, niin sen sijainti tallennetaan sekä aloituspiste -pisteolioon
        SEKÄ loppupiste -pisteolioon. Loppupisteen asettaminen tässä vaiheessa on eräänlainen pieni
        varotoimenpide. (Joku kysyy miksi, no siksi että loppupiste ei jää tyhjäksi/bugittamaan)*/
        
        this.startPos = e.getPoint();
        this.endPos = e.getPoint();
    }
    
    public void mouseDragged(MouseEvent e) 
    {
        endPos.x = e.getX();   // save new x and y coordinates
        endPos.y = e.getY();
        this.repaint();
    }
    
    public void mouseReleased(MouseEvent e) 
    {
        endPos.x = e.getX();
        endPos.y = e.getY();
        
        Graphics2D grafarea = imagewrap.createGraphics();
        Draw(grafarea);
        
        this.repaint();
    }
    
    //Käyttämättömät hiirieventit, jotka on kuitenkin pakko toteuttaa
    public void mouseMoved   (MouseEvent e) {}
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited  (MouseEvent e) {}
    public void mouseClicked (MouseEvent e) {}
}
