package fi.jamk.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class PaintTarget extends JPanel implements MouseListener, MouseMotionListener
{
    private final int AREASIZE = 800;                    // Vakiokoko piirtoalueelle
    private BufferedImage imagewrap;                            // Bufferedimageen tallennettaan kaikki piirretty,
                                                                // jotta se voidaan esimerkiksi kirjoittaa tiedostoon                             
    // Kaikki saatavilla olevat kynät ja niitä vastaavat numerot
    public final int NORMAL = 1;
    public final int LINE = 2;
    public final int CIRCLE = 3;
    public final int RECTANGLE = 4;
    
    private int pen = NORMAL;                             // Tämänhetkinen kynä, johon alustetaan alkuarvoksi PIXEL-tyyppinen kynä
    private int pensize;
    
    private static Point startPos = new Point(100, 0);                   // Hiiren alkukordinaatit ( Point(x, y) )
    private static Point endPos = new Point(0, 0);     
    
    Font font;
    
    private static Color currentColor = Color.BLACK;
    private ArrayList<Point> points = new ArrayList<Point>();
    private boolean fillEnabled;
    private boolean sweepEnabled;
    private BufferedImage previousImage;
    private int undoState = 0;
    private boolean wasSweep;
    private boolean antialiased = true;
    
    private PaintCursor cursor = new PaintCursor();
        
        
    public PaintTarget()
    {
        cursor.setColor(Color.BLACK);
        cursor.setPosition(new Point(0,0));
        cursor.setCursorSize(new Point(pensize, pensize));
        
        setPreferredSize(new Dimension(AREASIZE, AREASIZE));    // Asetetaan piirtoalueen koko
        setBackground(Color.white);                             // Piirtoalueen taustaväriksi valkoinen
        font = new Font("Comic Sans MS", Font.PLAIN,32);
        hackPositions();
        this.addMouseListener(this);                            // Liitetään mouselistener painttargettiin itseensä (Painttarget itse on mouselistener)
        this.addMouseMotionListener(this);  
        this.setCursor(this.getToolkit().createCustomCursor(
        new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
        "null"));
    }

    public void setPen(int pen)
    {
        this.pen = pen; // Asettaa metodille parametriksi annetun kynän tämänhetkiseksi kynäksi
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
        createUndoableState(0);
        
        
        graphics2d.drawImage(imagewrap, null, 0, 0);  // draw previous shapes
        cursor.paintComponent(graphics);
        Draw(graphics2d);
        this.requestFocus();
        
    }
    
    private void Draw(Graphics2D graphics2d)
    {
        graphics2d.setColor(getPenColor());
        
        if(antialiased)
        {
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        else
        {
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        
        graphics2d.setStroke(new BasicStroke(pensize, 1, 1));
        
        switch(pen)
        {
            case 1:
                //<editor-fold defaultstate="collapsed" desc="Pacman :-D">
                //graphics2d.fillArc(startPos.x, startPos.y, endPos.x - startPos.x, endPos.y - startPos.y, 200, 300);
                //graphics2d.drawString("LE PAC MAN", endPos.x -160, endPos.y +50);
                
                /*graphics2d.setFont(font);
                 * graphics2d.setColor(Color.MAGENTA);
                 * graphics2d.drawString("Y", endPos.x, endPos.y);
                 * graphics2d.setColor(Color.GREEN);
                 * graphics2d.drawString("O", endPos.x+20, endPos.y);
                 * graphics2d.setColor(Color.BLUE);
                 * graphics2d.drawString("L", endPos.x+50, endPos.y);
                 * graphics2d.setColor(Color.RED);
                 * graphics2d.drawString("O", endPos.x+65, endPos.y);*/
                //</editor-fold>          
               if(points.size() == 1) 
               {
                   graphics2d.drawLine(points.get(0).x, points.get(0).y, points.get(0).x, points.get(0).y);
               }
               else if(points.size() == 2) 
               {
                   graphics2d.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
               }
               else
               {
                    for (int i = 0; i < points.size() - 2; i++)
                    {
                        Point p1 = points.get(i);
                        Point p2 = points.get(i + 1);
                        graphics2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
               }
                break;
            case 2:
                graphics2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);                
                break;
            case 3:
                if(Math.abs(endPos.x - startPos.x) > 0 && Math.abs(endPos.y - startPos.y) > 0)
                {
                    if(fillEnabled)
                    {
                        graphics2d.fillOval(Math.min(endPos.x, startPos.x), Math.min(endPos.y, startPos.y), Math.abs(endPos.x - startPos.x), Math.abs(endPos.y - startPos.y));
                    } 
                    else
                    {
                        graphics2d.drawOval(Math.min(endPos.x, startPos.x), Math.min(endPos.y, startPos.y), Math.abs(endPos.x - startPos.x), Math.abs(endPos.y - startPos.y));
                    } 
                }
                break;
            case 4:
                if(Math.abs(endPos.x - startPos.x) > 0 && Math.abs(endPos.y - startPos.y) > 0)
                {
                    if(fillEnabled)
                    {
                        graphics2d.fillRect(Math.min(endPos.x, startPos.x), Math.min(endPos.y, startPos.y), Math.abs(endPos.x - startPos.x), Math.abs(endPos.y - startPos.y));
                    } 
                    else
                    {
                        graphics2d.drawRect(Math.min(endPos.x, startPos.x), Math.min(endPos.y, startPos.y), Math.abs(endPos.x - startPos.x), Math.abs(endPos.y - startPos.y));
                    }
                }
                break;   
        }
    }
    
    BufferedImage biFlip;
            
    public void extra_rotate(int direction)
    {       
        Graphics2D graphics2d = imagewrap.createGraphics();
        graphics2d.setColor(getPenColor());
        graphics2d.setStroke(new BasicStroke(pensize, 1, 1));
        
        biFlip = new BufferedImage(AREASIZE, AREASIZE, imagewrap.getType());
       
        if(direction == 1)
        {
            for(int i = 0; i < AREASIZE; i++)
            {
                for(int j = 0; j < AREASIZE; j++)
                {
                    biFlip.setRGB(AREASIZE-1-j, i, imagewrap.getRGB(i, j));
                }
            }
        }
        else if(direction == -1)
        {
            for(int i = 0; i < AREASIZE; i++)
            {
                for(int j = 0; j < AREASIZE; j++)
                {
                    biFlip.setRGB(j, AREASIZE - 1 - i, imagewrap.getRGB(i, j));
                }
            }
        }
        else {}
        imagewrap = biFlip;
        this.repaint();
    }
    
    public void setPenColor(Color color)
    {
        PaintTarget.currentColor = color;
    }
    
    private Color getPenColor()
    {
        return this.currentColor;
    }

    public boolean isFillEnabled()
    {
        return fillEnabled;
    }

    public void setIsFillEnabled(boolean isFillEnabled)
    {
        this.fillEnabled = isFillEnabled;
    }

    public boolean isSweepEnabled()
    {
        return sweepEnabled;
    } 

    public int getPensize()
    {
        return pensize;
    }

    public boolean isAntialiased()
    {
        return antialiased;
    }
    
    public void toggleAntialias(boolean state)
    {
        this.antialiased = state;
    }
    
    @Override
    public void mousePressed(MouseEvent e) 
    {
        /*Kun hiiren nappia painetaan, niin sen sijainti tallennetaan sekä aloituspiste -pisteolioon
        SEKÄ loppupiste -pisteolioon. Loppupisteen asettaminen tässä vaiheessa on eräänlainen pieni
        varotoimenpide. (Joku kysyy miksi, no siksi että loppupiste ei jää tyhjäksi/bugittamaan)*/

        this.startPos = e.getPoint();
        this.endPos = e.getPoint();
        points.add(new Point(e.getX(), e.getY()));
        this.repaint();
        this.requestFocus();
    }
    
    Random r = new Random();
    
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        if(sweepEnabled && pen != NORMAL)
        {
            wasSweep = true;
            createUndoableState(3);
            Draw(imagewrap.createGraphics());
        }
        
        endPos.x = e.getX();
        endPos.y = e.getY();
        points.add(new Point(endPos.x, endPos.y));
        
        if(points.size() > 10 && pen == NORMAL)
        {
            Point b = new Point(points.get(points.size() - 2).x, points.get(points.size() - 2).y); // Toimii vihdoin!
            createUndoableState(3);
            Graphics2D grafarea = imagewrap.createGraphics();
            Draw(grafarea);
            points.clear();
            points.add(b);
        }

        this.repaint();
        
        if(pen != NORMAL)
            cursor.isVisible(false);
        
        updateCursor(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        cursor.isVisible(true);
        createUndoableState(4);
        endPos.x = e.getX();
        endPos.y = e.getY();
        createUndoableState(1);
        Graphics2D grafarea = imagewrap.createGraphics();
        Draw(grafarea);
        hackPositions();
        points.clear();
        //this.repaint();
    }
    
    public BufferedImage getImage()
    {
        return imagewrap;
    }
    
    public void toggleFill(boolean isEnabled)
    {
        fillEnabled = isEnabled;
    }
    
    public void setPenSize(int size)
    {
        if(size < 0)
            this.pensize = 0;
        else if(size > 100)
            this.pensize = 100;
        else
            this.pensize = size;
    }
    
    public void clearArea()
    {
        createUndoableState(1);
        
        Graphics2D graphics2d = imagewrap.createGraphics();
        graphics2d.setColor(Color.WHITE);
        graphics2d.fillRect(0, 0, AREASIZE, AREASIZE);
        this.repaint();
    }
    
    public void fillArea()
    {
        createUndoableState(1);
        
        Graphics2D graphics2d = imagewrap.createGraphics();
        graphics2d.setColor(Color.WHITE);
        graphics2d.fillRect(0, 0, AREASIZE, AREASIZE);
        graphics2d.setColor(this.getPenColor());
        graphics2d.fillRect(0, 0, AREASIZE, AREASIZE);
        this.repaint();
    }
    
    private void hackPositions()
    {
        startPos.x = -100;
        startPos.y = -100;
        endPos.x = -100;
        endPos.y = -100;
    }
    
    public void setImagewrap(BufferedImage image)
    {
        Graphics2D graphics2d = imagewrap.createGraphics();
        graphics2d.drawImage(image, null, 0, 0);
        this.repaint();
        //imagewrap. = image;
    }

    //<editor-fold defaultstate="collapsed" desc="Grid">
    // Probleemina se, että gridiä ei voi piilottaa?
    /*public void toggleGrid(boolean visible)
     * {
     * Graphics2D graphics2d = imagewrap.createGraphics();
     * graphics2d.setColor(Color.GRAY);
     * graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     * graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
     * graphics2d.setStroke(new BasicStroke(1, 1, 1));
     * 
     * if(visible)
     * {
     * for(int i = 0; i < AREASIZE; i+=40)
     * {
     * graphics2d.drawLine(i, 0, i, AREASIZE);
     * graphics2d.drawLine(0, i, AREASIZE, i);
     * }
     * }
     * else
     * {
     * clearRect(0, 0, AREASIZE, int height)
     * repaint();
     * }
     * }*/
    //</editor-fold>
    
    public void toggleSweep(boolean state)
    {
        this.sweepEnabled = state;
    }
    
    public void undo()
    {
        if(this.previousImage != null)
        {
            hackPositions();
            Graphics2D graphics2d = imagewrap.createGraphics();

            graphics2d.drawImage(previousImage, null, 0, 0);
            
            this.repaint();
        }
    }
    
    private BufferedImage deepCopy(BufferedImage bi) 
    {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, true, null);
   }
    
    private void createUndoableState(int stateChange)
    {
        if(undoState == 0)
        {
            previousImage = deepCopy(imagewrap);
            undoState = stateChange;
        }
        else if(undoState == 1)
        {
            undoState = stateChange;
        }
        else if(undoState == 2)
        {
            undoState = 1;
        }
        else if(undoState == 3) {}
                
        if(stateChange == 3)
        {
            undoState = 3;
        }
        else if(stateChange == 4)
        {
            undoState = 1;
        }
    }
    
    public void updateCursor(int x, int y)
    {
        cursor.setColor(Color.BLACK);
        
        
        if(pensize == 0)
        {
            cursor.setPosition(new Point(x - 1 / 2, y - 1 / 2));
            cursor.setCursorSize(new Point(1, 1));
        }
        else
        {
            cursor.setPosition(new Point(x - pensize / 2, y - pensize / 2));
            cursor.setCursorSize(new Point(pensize, pensize));
        }
            
        createUndoableState(2);
        this.repaint();
    }
    
    @Override
    public void mouseMoved (MouseEvent e)
    {
        updateCursor(e.getX(), e.getY());
    }
    
    //Käyttämättömät hiirieventit, jotka on kuitenkin pakko toteuttaa
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited  (MouseEvent e) {}
    public void mouseClicked (MouseEvent e) {}
}
