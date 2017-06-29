/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.jamk.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JLayeredPane;

public class PaintCursor extends JLayeredPane
{
    private Point position;
    private Point size;
    private Color color;
    private boolean visible = true;
    
    public PaintCursor()
    {
        
    }
    
    public void setPosition(Point position)
    {
        this.position = position;
    }
    
    public Point getPosition()
    {
        return this.position;
    }
    
    public Point getCursorSize()
    {
        return size;
    }

    public void setCursorSize(Point size)
    {
        this.size = size;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
    
    @Override
    public void paintComponent(Graphics g) 
    {
        if(visible)
        {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(color);
            g.drawOval(position.x, position.y, size.x, size.y);
        }
    }
    
    public void isVisible(boolean visibility)
    {
        this.visible = visibility;
    }
}
