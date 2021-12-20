package ZenaCraft.commands;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.loans.AvaliableLoan;

public class ListLoans implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 0) return App.invalidSyntax(player);

        String response = App.zenfac + "Avaliable loans: ";

        DecimalFormat df1 = new DecimalFormat("0,00");
        DecimalFormat df2 = new DecimalFormat("00");

        for(Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction f = (Faction) mEntry.getValue();
            response += ChatColor.WHITE + "[" + f.getPrefix() + ChatColor.WHITE + "]";
            
            int i = 1;
            for(AvaliableLoan l : f.getAvaliableLoans()){
                response += "Loan [" + String.valueOf(i) + "] ";
                response += ChatColor.GOLD + "Ƒ" + df1.format(l.getInitAmount());
                response += ChatColor.BOLD + "" + ChatColor.RED + df2.format(l.getInterest()) + "%";
                response += ChatColor.RESET + " , ";
            }
        }

        player.sendMessage(response);
        return true;
    }
}