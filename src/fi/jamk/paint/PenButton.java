package fi.jamk.paint;

import javax.swing.JButton;

public class PenButton extends JButton //Oma kynänappi-luokka
{
    String name; //Itse annettu nimi, josta napin voi tunnistaa
    
    public PenButton(String name, String text)
    {
        super(text);
        this.name = name;
    }
    
    @Override
    public String toString() //Ylikirjoitetaan toString-metodi ja laitetaan se palauttamaan napin nimi
    {
        return this.name;
    }
}
