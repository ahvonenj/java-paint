package fi.jamk.paint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame
{
    private PaintTarget painttarget = new PaintTarget(); //Luodaan uusi piirtoalue / piirtokohde
    
    public Window()
    {  
        PaintGui paintgui = new PaintGui();     // Luodaan editorilla tehty PaintGui -olio
        paintgui.setSwitchTarget(painttarget);  // Editorilla luotu placeholder-jpaneeli swapataan jpanelista periytettyyn ja kustomoituun painttargettiin
        this.add(paintgui);                     // Editorilla tehty GUI on itseasiassa yksi iso jpaneeli, joten se voidaan lisätä Windowiin joka on JFrame
        this.setTitle("Paint");                 // Ikkunan yläotsikko
        this.pack();                            // Pakataan ikkunan sisältö jotta kaikki komponentit asettuu tiiviisti ja hyvin
    }
}

