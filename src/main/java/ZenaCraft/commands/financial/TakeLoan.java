package ZenaCraft.commands.financial;

import java.time.Duration;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.loans.AvaliableLoan;
import ZenaCraft.objects.loans.Loan;
import net.milkbowl.vault.economy.Economy;

public class TakeLoan extends TemplateCommand{

    public TakeLoan() {super(2);}

    @Override
    protected boolean run() {

        Integer loan;
        loan = formatInt(args[1]);
        if(loan == null) return invalidSyntax(player);
        loan--;

        Faction faction = null;

        for (Faction mf : App.factionIOstuff.getFactionList()){
            if (mf.getName().equals(args[0])) faction = mf;
        }

        if(faction == null) return factionNoExist(player);

        AvaliableLoan chosen = faction.getAvaliableLoans().get(loan);
        Loan l = faction.assignLoan(player, chosen);

        Economy econ = App.getEconomy();
        econ.depositPlayer(player, chosen.getInitAmount());

        Duration d = l.getTimeTillExpire();

        String timeString = formatDateTime(d);

        player.sendMessage(App.zenfac + ChatColor.GREEN + "You took out a loan with " + 
            faction.getPrefix() + ChatColor.GREEN + " worth " + formatMoney(chosen.getInitAmount()) +
            ChatColor.GREEN + " Be sure to pay it back in " + ChatColor.RED + timeString);

        return true;
    }  
}