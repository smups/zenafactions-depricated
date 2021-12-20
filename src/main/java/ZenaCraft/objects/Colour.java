package ZenaCraft.objects;

import java.io.Serializable;

import org.bukkit.ChatColor;

public class Colour implements Serializable{
    static final long serialVersionUID = 100L;

    private int hexColour;
    private ChatColor mcColour;

    public Colour(int hex, ChatColor mc){
        hexColour= hex;
        mcColour = mc;
    }
    public Colour(Colour that){
        this.hexColour = that.hexColour;
        this.mcColour = that.mcColour;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Colour colour = (Colour) o;
        return colour.asHex() == hexColour;
    }

    public int asHex(){
        return this.hexColour;
    }
    public String asString(){
        return "" + this.mcColour;
    }
}