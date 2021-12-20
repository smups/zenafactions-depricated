package ZenaCraft.commands.financial;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import net.milkbowl.vault.economy.Economy;

public class DepositFaction extends TemplateCommand{

    public DepositFaction(){
        super(1);
    }

    @Override
    protected boolean run() {
        Double amount = formatDouble(args[0]);
        if(amount == null) return invalidSyntax(player);

        Faction f = App.factionIOstuff.getPlayerFaction(player);
        Economy econ = App.getEconomy();

        if(!econ.has(player, amount)) return insufficientFunds(player);

        econ.withdrawPlayer(player, amount);
        f.addBalance(amount);
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Deposited " + formatMoney(amount) +
            ChatColor.GREEN + " into faction account of: " +f.getPrefix());
            
        return true;
    }
}
