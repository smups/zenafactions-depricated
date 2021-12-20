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

    public static String gdpr = "This server is" +
    " running " + ChatColor.BOLD + "ZenaFactions" + ChatColor.RESET + " " + ChatColor.RED +
    " from within the European Union. Your server admin has chosen to log performance " +
    "data to aid in the development of this plugin. As per EU GDPR regulations, he/she must " +
    "ask for explicit consent from users to share this data with me. " + ChatColor.DARK_RED + "" +
    ChatColor.BOLD + "By playing on this server you give your explicit consent for performance metrics to be shared with " +
    "a third party, I.E. the developers of ZenaFactions. You also confirm that you are at least 16" +
    " years of age or have explicit consent form a parent or supervisor. " + ChatColor.RESET + "" +
    ChatColor.RED + " If you are a server operator and did not expect this message, please take a look" +
    " at the ZenaFactions config files. Thank you!";

    //ranks and perms
    public boolean hasPerm(Player player, String perm){
        if (perm == null) return true;
        Faction f = App.factionIOstuff.getPlayerFaction(player);
        if (f == null) return false;
        return f.getPlayerRank(player).getPerms().contains(perm);
    }

    // Messages
    public boolean opCommand(Player player){
        player.sendMessage(App.zenfac + ChatColor.RED + "Admin command only!");
        return true;
    }
    public boolean invalidSyntax(Player player) {
        player.sendMessage(App.zenfac + ChatColor.RED + "Invalid Syntax! Use /help zenafactions for help");
        return true;
    }
    public void invalidSyntax() {
        Bukkit.getLogger().info(App.zenfac + ChatColor.RED + "Invalid Syntax! Use /help zenafactions for help");
    }

    public boolean invalidRank(Player player, String perm) {
        player.sendMessage(App.zenfac + ChatColor.RED + "You don't have the appropriate rank to do this!"
                + " You have to have the permission: " + perm);
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
    public void EUgdpr(Player player){
        String msg = App.zenfac + ChatColor.RED + "Hi there " + ChatColor.BOLD + player.getName() +
        ChatColor.RESET + " " + ChatColor.RED + "and welcome to the Server! " + gdpr;

        player.sendMessage(msg);
    }
    @Nullable
    public boolean rankNoExist(Player player, String rName){
        if (rName == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "That rank doesn't exist!");
        }
        else{
            player.sendMessage(App.zenfac + ChatColor.RED + "Rank " + rName + " does not exist!");
        }
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