package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class CreateLoan implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(player);

        double amount;

        try{
            amount = Double.parseDouble(args[0]);
        }
        catch (Exception e){
            return App.invalidSyntax(player);
        }

        Faction f = App.factionIOstuff.getPlayerFaction(player);

        if (f.getBalance() < amount){
            player.sendMessage(App.zenfac + ChatColor.RED + "Your faction doesn't have enough money to create this loan! Factionbalance has to be greater than loan amount!");
            return true;
        }

        if (f.getMembers().get(player.getUniqueId()) > 1){
            return App.invalidRank(player, 1);
        }

        f.removeBalance(amount);

        f.createLoan(amount);

        player.sendMessage(App.zenfac + ChatColor.GREEN + "created loan!");
        return true;
    }
}
