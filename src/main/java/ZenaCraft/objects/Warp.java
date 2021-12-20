package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import ZenaCraft.App;
import net.milkbowl.vault.economy.Economy;

public class Warp implements Serializable{
    static final long serialVersionUID = 100L;

    private final double X;
    private final double Y;
    private final double Z;
    private final UUID wUuid;
    private final String name;

    private double factionTax = 0;
    private int rankReq;

    //new rank system integration
    private String perm;

    transient Location loc;

    public Warp(Location loc, String name) {
        this.name = name;
        this.loc = loc;
        X = loc.getX();
        Y = loc.getY();
        Z = loc.getZ();
        perm = "warp." + name;
        wUuid = loc.getWorld().getUID();
    }

    // copy constructor
    public Warp(Warp that) {
        this.name = that.name;
        this.loc = that.loc;
        this.X = that.X;
        this.Y = that.Y;
        this.Z = that.Z;
        this.wUuid = that.wUuid;
        this.factionTax = that.factionTax;
        this.rankReq = that.rankReq;
        this.perm = that.perm;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // first do the deserelisation
        in.defaultReadObject();

        // now for the special things
        loc = new Location(Bukkit.getWorld(wUuid), X, Y, Z);           
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warp w = (Warp) o;
        return name.equals(w.getName());
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    public void warpPlayer(Player player){
        Economy econ = App.getEconomy();

        double warpcost = calcWarpCost(player);

        if (econ.getBalance(player) < warpcost){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't have enough money to warp here!");
            player.setMetadata("warpPlayer", new FixedMetadataValue(App.getPlugin(App.class), false));
            return;
        }

        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        if (faction.equals(App.factionIOstuff.defaultFaction)){
            if (!faction.getPlayerRank(player).hasPerm(perm)){
                App.getCommon().invalidRank(player, perm);
                player.setMetadata("warpPlayer", new FixedMetadataValue(App.getPlugin(App.class), false));
                return;
            }
        }

        if (!player.hasMetadata("warpPlayer") || !player.getMetadata("warpPlayer").get(0).asBoolean()){
            DecimalFormat df = new DecimalFormat("0.00");
            player.sendMessage(App.zenfac + ChatColor.WHITE + "are you sure you want to warp to " + ChatColor.AQUA + name +
            ChatColor.WHITE + "? This costs " + ChatColor.GOLD + "Ƒ" + df.format(warpcost) + ChatColor.RESET +
            ". Retype this command to confirm!");
            player.setMetadata("warpPlayer", new FixedMetadataValue(App.getPlugin(App.class), true));
            return;
        }

        player.sendMessage(App.zenfac + ChatColor.WHITE + "warping to: " + ChatColor.BOLD + name);
        player.teleport(loc);

        //money stuff
        econ.withdrawPlayer(player, player.getWorld().getName(), warpcost);
        faction.addBalance(warpcost*(factionTax/(1 + factionTax)));
        
        //call playerMoveEvent
        PlayerMoveEvent e = new PlayerMoveEvent(player, player.getLocation(), loc);
        e.callEvent();
        player.setMetadata("warpPlayer", new FixedMetadataValue(App.getPlugin(App.class), false));
    }

    public double calcWarpCost(Player p){
        double wc = App.getPlugin(App.class).getConfig().getDouble("warpCost");
        double warpcost;
        if (p.getWorld().equals(loc.getWorld())) warpcost = wc*p.getLocation().distance(loc)*(1 + factionTax);
        else warpcost = wc*p.getLocation().distance(new Location(p.getWorld(), 0, 100, 0))*(1 + factionTax) + 
            wc*loc.distance(new Location(loc.getWorld(), 0, 100, 0))*(1 + factionTax);
        return warpcost;
    }

    //getters en setters
    public String getName(){
        return this.name;
    }
    public UUID getID(){
        return this.wUuid;
    }
    public Location getLocation(){
        return this.loc;
    }
    public double getFactionTax(){
        return factionTax;
    }
    public void setFactionTax(double factionTax){
        this.factionTax = factionTax;
    }
    public int getOldRankReq(){
        return rankReq;
    }
    public String getPerm(){
        return this.perm;
    }
    public void setPerm(){
        this.perm = "warp." + name;
    }
}