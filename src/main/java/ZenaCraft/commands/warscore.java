package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.War;

public class warscore implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        
        if (args.length != 0) return App.invalidSyntax(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(player);
        War war = App.warThread.getWarFromFaction(faction);

        if (war == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction is not at war!");
            return true;
        }

        String losing = ChatColor.RED + "" + ChatColor.BOLD + "[Losing]" + ChatColor.RESET;
        String winning = ChatColor.GREEN + "" + ChatColor.BOLD + "[Winning]" + ChatColor.RESET;
        String statusquo = "in a " + ChatColor.YELLOW + "" + ChatColor.BOLD + "[Status Quo]" + ChatColor.RESET;
        String reply = App.zenfac + ChatColor.WHITE + "You are ";

        if(war.getAttackers().equals(faction)){
            reply += ChatColor.BOLD + String.valueOf(war.getWarScore()*100) + "%" + ChatColor.RESET + " towards ";
            if (war.getWarScore() > 0.5) reply += winning;
            else if (war.getWarScore() < 0.5) reply += losing;
            else reply += statusquo;

            reply += " your war with " + war.getDefenders().getPrefix();
        }
        else{
            reply += ChatColor.BOLD + String.valueOf(war.getWarScore()*100) + "%" + ChatColor.RESET + " towards ";
            if (war.getWarScore() > 0.5) reply += winning;
            else if (war.getWarScore() < 0.5) reply += losing;
            else reply += statusquo;

            reply += " your war with " + war.getDefenders().getPrefix();
        }
        
        player.sendMessage(reply);
        return true;
    }
}
