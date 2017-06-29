package fi.jamk.paint;

import javax.swing.JFrame;

public class Paint
{
    Window window;
    
    public Paint(Window window)
    {
        this.window = window;                                           // Asetetaan paint-luokalle annettu ikkuna muuttujaan
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // Kun ikkunan "rastia" painetaan, niin koko ohjelma sulkeutuu
        this.window.setVisible(true);                                   // Lopuksi ikkuna on hyvä asettaa näkyväksi :-D
    }
}
