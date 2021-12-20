package ZenaCraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ZenaCraft.objects.Faction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.text.DecimalFormat;
import java.time.Duration;

import javax.annotation.Nullable;;

public class Common {

    private Plugin plugin = App.getPlugin(App.class);
    private String moneyChar = plugin.getConfig().getString("Currency Symbol");

	// Messages
    public boolean invalidSyntax(Player player) {
        player.sendMessage(App.zenfac + ChatColor.RED + "Invalid Syntax! Use /help zenafactions for help");
        return true;
    }
    public void invalidSyntax() {
        Bukkit.getLogger().info(App.zenfac + ChatColor.RED + "Invalid Syntax! Use /help zenafactions for help");
    }

    public boolean invalidRank(Player player, int rankReq) {
        Faction f = App.factionIOstuff.getPlayerFaction(player);
        player.sendMessage(App.zenfac + ChatColor.RED + "You don't have the appropriate rank to do this!"
                + " You have to be at least: " + f.getColour().asString() + f.getRanks()[rankReq]);
        return true;
    }

    public boolean insufficientFunds(Player player){
        player.sendMessage(App.zenfac + ChatColor.RED + "You don't have enough money to do this!");
        return true;
    }

    public boolean insufficientFactionFunds(Player player){
        player.sendMessage(App.zenfac + ChatColor.RED + "Your faction doesn't have enough money to do this!");
        return true;
    }
    @Nullable
    public boolean playerNoExist(Player player, String pName){
        if(pName == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "That player doesn't exist!");
        }
        else{
            player.sendMessage(App.zenfac + ChatColor.RED + "Player " + pName + "doens't exist!");
        }
        return true;
    }
    @Nullable
    public boolean factionNoExist(Player player, String fName){
        if(fName == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "That faction doesn't exist!");

        }
        else{
            player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + fName + "doesn't exist!");
        }
        return true;
    }
    public boolean noMember(Player player){
        player.sendMessage(App.zenfac + ChatColor.RED + "That player isn't in your faction!");
        return true;
    }

    // Formats
    public String formatMoney(double amount) {
        DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00");
        return ChatColor.GOLD + moneyChar + df.format(amount);
    }
    public String formatPercent(double amount){
        DecimalFormat df = new DecimalFormat("0");
        return df.format(amount*100) + "%";
    }
    @Nullable
    public Double formatDouble(String st, Player player){
        try{
            return Double.parseDouble(st);
        }
        catch(Exception e){
            if(player != null) invalidSyntax(player);
        }
        return null;
    }
    @Nullable
    public Integer formatInt(String st, Player player){
        try{
            return Integer.parseInt(st);
        }
        catch (Exception e){
            if (player != null) invalidSyntax(player);
        }
        return null;
    }
    public String formatDateTime(Duration d){
        String timeString = "";
        if(d.toDays() != 0) timeString += String.valueOf(d.toDays()) + " days, ";
        if(d.toHours() != 0) timeString += String.valueOf(d.toHours()%24) + " hours, ";
        if(d.toMinutes() != 0) timeString += String.valueOf(d.toMinutes()%60) + " min., ";
        timeString += String.valueOf(d.toSeconds()%60) + " sec. ";
        return timeString;
    }

}
