package ZenaCraft.commands;

import java.text.DecimalFormat;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.loans.AvaliableLoan;
import net.milkbowl.vault.economy.Economy;

public class TakeLoan implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) return App.invalidSyntax(player);

        int loan;

        try{
            loan = Integer.parseInt(args[1]) - 1;
        }
        catch (Exception e){
            App.invalidSyntax(player);
            return true;
        }

        Faction faction = null;;
        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction mf = (Faction) mEntry.getValue();
            if (mf.getName().equals(args[0])) faction = mf;
        }

        if(faction == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " does not exist!");
            return true;
        }

        AvaliableLoan chosen = faction.getAvaliableLoans().get(loan);
        faction.assignLoan(player, chosen);

        Economy econ = App.getEconomy();
        econ.depositPlayer(player, chosen.getInitAmount());

        DecimalFormat df = new DecimalFormat("0.00");

        player.sendMessage(App.zenfac + ChatColor.GREEN + "You took out a lone with " + 
            faction.getPrefix() + ChatColor.GREEN + " worth " + ChatColor.GOLD + "Ƒ" +
            df.format(chosen.getInitAmount()) + ChatColor.GREEN + " Be sure to pay it back " +
            "in " + ChatColor.RED + String.valueOf(faction.getLoanLength()) + ChatColor.GREEN +" hours!");

        return true;
    }    
}