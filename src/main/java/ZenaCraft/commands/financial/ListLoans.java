package ZenaCraft.commands.financial;

import java.util.Map.Entry;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.loans.AvaliableLoan;

public class ListLoans extends TemplateCommand{

    public ListLoans() {super(0);}

    @Override
    protected boolean run() {
        String response = App.zenfac + "Avaliable loans:\n";

        for(Faction f : App.factionIOstuff.getFactionList()){
            response += ChatColor.WHITE + "[" + f.getPrefix() + ChatColor.WHITE + "]:\n";
            
            int i = 1;
            for(AvaliableLoan l : f.getAvaliableLoans()){
                response += "Loan [" + String.valueOf(i) + "] ";
                response += formatMoney(l.getInitAmount());
                response += ChatColor.BOLD + " " + ChatColor.RED + "@" + formatPercent(l.getInterest());
                response += ChatColor.RESET + ",\n";
                i++;
            }
        }

        player.sendMessage(response);
        return true;
    }
}