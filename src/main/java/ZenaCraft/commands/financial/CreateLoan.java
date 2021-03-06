package ZenaCraft.commands.financial;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class CreateLoan extends TemplateCommand{

    public CreateLoan() {super(1, true, 1);}

    @Override
    protected boolean run() {
        Double amount = formatDouble(args[0]);
        if (amount == null) return true;

        Faction f = App.factionIOstuff.getPlayerFaction(player);

        if(f.getBalance() < amount) return insufficientFactionFunds(player);

        if (hasPerm(player)) return invalidRank(player);
        
        f.removeBalance(amount);
        f.createLoan(amount);

        player.sendMessage(App.zenfac + ChatColor.GREEN + "created loan worth: " + formatMoney(amount));
        return true;
    }
}