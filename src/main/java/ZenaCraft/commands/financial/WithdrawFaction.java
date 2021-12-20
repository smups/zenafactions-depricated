package ZenaCraft.commands.financial;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import net.milkbowl.vault.economy.Economy;

public class WithdrawFaction extends TemplateCommand{

    public WithdrawFaction(){
        super(1);
    }

    @Override
    protected boolean run(){
        Double amount = formatDouble(args[0]);
        if(amount == null) return true;

        Faction f = App.factionIOstuff.getPlayerFaction(player);

        if(f.getBalance() < amount) return insufficientFactionFunds(player);

        if(f.getPlayerRank(player) > 1) return invalidRank(player, 1);

        Economy econ = App.getEconomy();
        econ.depositPlayer(player, amount);
        f.removeBalance(amount);

        player.sendMessage(App.zenfac + ChatColor.GREEN + "Transferred " +
            formatMoney(amount) + " " + ChatColor.GREEN + "into your account from " +
            f.getPrefix());

        return true;
    } 
}