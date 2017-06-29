package fi.jamk.paint;

public class Main
{
    public static void main(String args[])
    {
        /*
            Ohjelma alkaa tästä, tehdään uusi Paint -(pää)olio
            Periaatteessa paint-olion voisi jättää pois ja luoda ikkunan suoraan, mutta nyt on tehty näin
        */
        Paint paint = new Paint(new Window());
    }
}
