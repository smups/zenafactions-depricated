package ZenaCraft.commands.financial;

import java.util.List;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.loans.Loan;

public class MyLoans extends TemplateCommand{

    public MyLoans() {super(0);}

    @Override
    protected boolean run() {
        List<Loan> loanlist = App.factionIOstuff.getPlayerLoans(player);

        if(loanlist == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't owe anyone!");
            return true;
        }

        String resp = App.zenfac + ChatColor.WHITE + "Running loans (" + String.valueOf(loanlist.size()) + "): ";

        for(Loan l : loanlist){
            resp += "[" + l.getFaction().getPrefix() + ChatColor.RESET + "] ";
            resp += "Loaned amount: " + formatMoney(l.getInitAmount()) + ChatColor.RESET + " ";
            resp += "Amount due: " + formatMoney(l.getCurrentPrice()) + ChatColor.RESET + " ";
            resp += "Due in: " + formatDateTime(l.getTimeTillExpire()) +  " ";
        }

        player.sendMessage(resp);
        
        return true;
    }
}