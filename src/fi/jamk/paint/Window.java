package fi.jamk.paint;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame implements ActionListener, ChangeListener, MouseListener, KeyListener, MouseWheelListener
{
    
    private PaintTarget painttarget;
    PaintGui paintgui;
    JFileChooser fc;

    final JOptionPane sureToClear = new JOptionPane(
    "Are you sure you want to clear the image?",
    JOptionPane.QUESTION_MESSAGE,
    JOptionPane.YES_NO_OPTION);
    
    FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("jpg files (*.jpg)", "jpg");
    FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("png files (*.png)", "png");
    FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter("bmp files (*.bmp)", "bmp");
    FileNameExtensionFilter ypgFilter = new FileNameExtensionFilter("ypg files (*.ypg)", "ypg");
    
    public Window()
    {  
        String[] titles = new String[]
        { 
            "\"Getting real tired of your paint, Master Wayne\"",
            "\"A fine dose of paint for a fine lad of mine\"",
            "\"Nothing beats a nice slice of paint on a rainy day\"",
            "\"I love the smell of paint in the morning\"",
            "\"Frankly, my dear, I don't give a paint\"",
            "\"I think this is the beginning of a beautiful paintship\"",
            "\"You can't handle the paint!\"",
            "\"What we've got here is a failure to paint\"",
        };
        
        Random r = new Random();
        paintgui = new PaintGui();
        painttarget = paintgui.getPaintTarget1();
        this.add(paintgui);
        this.setTitle("PAINT - " + titles[r.nextInt(titles.length)]);
        this.setResizable(false);
        this.pack();
        //this.setSize(965, 830);
        this.setLocation(( Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width ) / 2,
                         ( Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height ) / 2 );
        
        painttarget.toggleFill(paintgui.getToolcheckbox_fillshapes().isSelected());
        painttarget.toggleSweep(paintgui.getToolcheckbox_sweepmode().isSelected());
        painttarget.setPenSize(paintgui.getToolslider_pensize().getValue());
        painttarget.toggleAntialias(paintgui.getFunctioncheckbox_antialiased().isSelected());
        paintgui.getTooltext_pensize().setText(Integer.toString(paintgui.getToolslider_pensize().getValue()));
        
        fc = new JFileChooser(System.getProperty("user.home"));       
        
        fc.addChoosableFileFilter(jpgFilter);
        fc.addChoosableFileFilter(pngFilter);
        fc.addChoosableFileFilter(bmpFilter);
        fc.addChoosableFileFilter(ypgFilter);
        
        fc.setFileFilter(ypgFilter);
        
        paintgui.setActionListeners(this, this, this);
        painttarget.addKeyListener(this);
        painttarget.addMouseWheelListener(this);
        painttarget.requestFocus();
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource().equals(paintgui.getPenbutton_normal()))
        {
            setGuiPen(1);
        }
        else if(e.getSource().equals(paintgui.getPenbutton_line()))
        {
            setGuiPen(2);
        }
        else if (e.getSource().equals(paintgui.getPenbutton_circle()))
        {
            setGuiPen(3);
        }
        else if (e.getSource().equals(paintgui.getPenbutton_rectangle()))
        {
            setGuiPen(4);
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_save()))
        {
            saveImage();
        }
        else if (e.getSource().equals(paintgui.getToolcheckbox_fillshapes()))
        {
            painttarget.toggleFill(paintgui.getToolcheckbox_fillshapes().isSelected());
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_clear()))
        {
            if(getUserConfirm("Are you sure you want to clear?"))
                painttarget.clearArea();
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_rotateanticlockwise()))
        {
            painttarget.extra_rotate(-1);
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_rotateclockwise()))
        {
            painttarget.extra_rotate(1);
        }
        else if (e.getSource().equals(paintgui.getTooltext_pensize()))
        {
            try
            {
                if(Integer.parseInt((paintgui.getTooltext_pensize().getText())) <= 100 && Integer.parseInt((paintgui.getTooltext_pensize().getText())) >= 0)
                {
                    //paintgui.getToolslider_pensize().setToolTipText("WHEEE");
                    painttarget.setPenSize(Integer.parseInt((paintgui.getTooltext_pensize().getText())));
                    paintgui.getToolslider_pensize().setValue(Integer.parseInt((paintgui.getTooltext_pensize().getText())));
                }
                else
                {
                    
                    paintgui.getTooltext_pensize().setText("Range: 0-100");
                }
            }
            catch(Exception nanE)
            {
                paintgui.getTooltext_pensize().setText("NaN");
            }
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_open()))
        {
            openImage();
        }
        else if (e.getSource().equals(paintgui.getToolcheckbox_sweepmode()))
        {
            painttarget.toggleSweep(paintgui.getToolcheckbox_sweepmode().isSelected());
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_fill()))
        {
            if(getUserConfirm("Are you sure you want to fill the area?"))
                painttarget.fillArea();
        }
        else if (e.getSource().equals(paintgui.getFunctionbutton_undo()))
        {
            painttarget.undo();
        }
        else if (e.getSource().equals(paintgui.getFunctioncheckbox_antialiased()))
        {
            painttarget.toggleAntialias(paintgui.getFunctioncheckbox_antialiased().isSelected());
        }
        
        painttarget.requestFocus();
    }
    
    private boolean getUserConfirm(String question)
    {
            Object[] options = {"Yes", "No"};
            int c = JOptionPane.showOptionDialog(this, question, "Confirm action",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
            if (c == JOptionPane.YES_OPTION) 
            {
                return true;
            } 
            else if (c == JOptionPane.NO_OPTION)
            {
                return false;
            } 
            else
            {
                return false;
            }
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource().equals(paintgui.getToolslider_pensize()))
        {
            painttarget.setPenSize(paintgui.getToolslider_pensize().getValue());
            paintgui.getTooltext_pensize().setText(Integer.toString(paintgui.getToolslider_pensize().getValue()));
        }
        painttarget.requestFocus();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource().equals(paintgui.getToolpanel_colorbox()))
        {
            Color c = JColorChooser.showDialog(null, "Pen color", Color.BLACK);
            
            if(c != null)
            {
                painttarget.setPenColor(c);

                if(c.getAlpha() < 255 || c.getTransparency() > 0)
                {
                    Color cb = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
                    paintgui.getToolpanel_colorbox().setBackground(cb);
                }
                else
                {
                    paintgui.getToolpanel_colorbox().setBackground(c);
                }
            }
        }
        painttarget.requestFocus();
    }
    
    
    
    private void saveImage()
    {
        fc.addChoosableFileFilter(jpgFilter);
        fc.addChoosableFileFilter(pngFilter);
        fc.addChoosableFileFilter(bmpFilter);
        fc.addChoosableFileFilter(ypgFilter);
        
        fc.setFileFilter(ypgFilter);
        
        int path = fc.showSaveDialog(this);
        
        String ext="";

        if(fc.getFileFilter() == jpgFilter)
        { 
           ext=".jpg";
        }
        else if(fc.getFileFilter() == pngFilter)
        {
           ext=".png";
        }
        else if(fc.getFileFilter() == bmpFilter)
        {
           ext=".bmp";
        }
        else if(fc.getFileFilter() == ypgFilter)
        {
           ext=".ypg";
        }
        else
        {
            ext=".jpg";
        }
        
        if(fc.getSelectedFile() != null)
        {
            try
            {
                if(hasFormatExtension(fc.getSelectedFile().getName()))
                    ImageIO.write(painttarget.getImage(), "png", new File(fc.getSelectedFile().getAbsolutePath()));
                else
                    ImageIO.write(painttarget.getImage(), "png", new File(fc.getSelectedFile().getAbsolutePath() + ext));
            }
            catch (IOException ioe) 
            {
                ioe.printStackTrace();
            }
        }
        painttarget.requestFocus();
    }
    
    private boolean hasFormatExtension(String filename)
    {       
        if(filename.endsWith(".jpg") ||
           filename.endsWith(".png") ||
           filename.endsWith(".bmp") ||
           filename.endsWith(".ypg"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private void openImage()
    {
        fc.removeChoosableFileFilter(jpgFilter);
        fc.removeChoosableFileFilter(pngFilter);
        fc.removeChoosableFileFilter(bmpFilter);
        fc.removeChoosableFileFilter(ypgFilter);
        
        fc.setFileFilter(ypgFilter);
        
        int path = fc.showOpenDialog(this);
        
        if(fc.getSelectedFile() != null)
        {
            if(fc.getSelectedFile().getName().endsWith("ypg"))
            {
                try
                {
                    BufferedImage buf = ImageIO.read(new File(fc.getSelectedFile().getPath()));
                    
                    if(buf != null)
                        painttarget.setImagewrap(buf);
                }
                catch (Exception ioe) 
                {
                    System.out.println(ioe);
                    JOptionPane.showMessageDialog(null,"File doesn't exist!","Warning!",JOptionPane.WARNING_MESSAGE);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"You can only open .ypg files!","Warning!",JOptionPane.WARNING_MESSAGE);
            }
        }
        painttarget.requestFocus();
    }
    
    private void setGuiPen(int pen)
    {
        switch(pen)
        {
            case 1:
                painttarget.setPen(1);
                break;
            case 2:
                painttarget.setPen(2);
                break;
            case 3:
                painttarget.setPen(3);
                break;
            case 4:
                painttarget.setPen(4);
                break;    
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.isControlDown())
        {
            switch(e.getKeyCode())
            {
                case 37:
                    painttarget.extra_rotate(-1);
                    break;
                case 39:
                    painttarget.extra_rotate(1);
                    break;
                case 49:
                    setGuiPen(1); //1
                    break;
                case 50:
                    setGuiPen(2); //2
                    break;
                case 51:
                    setGuiPen(3); //3
                    break;
                case 52:
                    setGuiPen(4); //4
                    break;
                case 65: //a
                    painttarget.toggleAntialias(!painttarget.isAntialiased());
                    break;
                case 70: //f
                    painttarget.toggleFill(!painttarget.isFillEnabled());
                    paintgui.getToolcheckbox_fillshapes().setSelected(!paintgui.getToolcheckbox_fillshapes().isSelected());
                    break;
                case 83: //s
                    painttarget.toggleSweep(!painttarget.isSweepEnabled());
                    paintgui.getToolcheckbox_sweepmode().setSelected(!paintgui.getToolcheckbox_sweepmode().isSelected());
                    break;
                case 90: //z
                    painttarget.undo();
                    break;
            }
        }
        
        switch(e.getKeyCode())
        {
            case 27:
                if(getUserConfirm("Are you sure you want to clear?"))
                    painttarget.clearArea();
                break;
            case 112: //F1
                paintgui.getTabmenu().setSelectedIndex(0);
                break;
            case 113: //F2
                paintgui.getTabmenu().setSelectedIndex(1);
                break;
            case 114: //F3
                paintgui.getTabmenu().setSelectedIndex(2);
                break;    
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(e.isControlDown())
        {
            int deltaScroll = e.getWheelRotation() * 4;
            int direction = (Math.abs(deltaScroll) > 0) ? 1 : -1;

            if(direction == -1)
            {
                painttarget.setPenSize(painttarget.getPensize() - deltaScroll);
                paintgui.getToolslider_pensize().setValue(paintgui.getToolslider_pensize().getValue() - deltaScroll);
            }
            else
            {
                painttarget.setPenSize(painttarget.getPensize() + deltaScroll);
                paintgui.getToolslider_pensize().setValue(paintgui.getToolslider_pensize().getValue() + deltaScroll);
            }
            painttarget.updateCursor(e.getX(), e.getY());
        }
    }

    
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){} 
}

