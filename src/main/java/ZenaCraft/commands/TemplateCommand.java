package ZenaCraft.commands;

import java.time.Duration;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.Common;

public class TemplateCommand implements CommandExecutor{

    private final Common common;
    protected final int argSize;

    protected Player player;
    protected Logger log;
    protected String[] args;

    public TemplateCommand(int argSize){
        super();
        this.argSize = argSize;
        common = App.getCommon();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!getSender(sender)) return true;
        if (!checkArgSize(player, args)) return true;

        this.args = args;
        
        long start = System.nanoTime();
        boolean result = run();
        long stop = System.nanoTime();

        if(!App.logging) return result;

        App.perfThread.logcommand(this.getClass().getName(), (stop - start)/1000);

        return result;
    }

    protected boolean getSender(CommandSender sender){
        if (!(sender instanceof Player)) return false;
        player = (Player) sender;
        return true;
    }

    protected boolean checkArgSize(Player player, String[] args){
        if (argSize == 0) return true;
        if (args.length != argSize){
            common.invalidSyntax(player);
            return false;
        }
        return true;
    }

    protected boolean weakCheckArgSize(Player player, String[] args){
        return true;
    }
    
    //this one you need to override!
    protected boolean run(){
        return true;
    }

    protected boolean invalidSyntax(Player player){
        return common.invalidSyntax(player);
    }

    protected boolean invalidRank(Player player, int rankReq){
        return common.invalidRank(player, rankReq);
    }

    protected boolean insufficientFunds(Player player){
        return common.insufficientFunds(player);
    }

    protected boolean insufficientFactionFunds(Player player){
        return common.insufficientFactionFunds(player);
    }

    protected boolean playerNoExist(Player player, String pName){
        return common.playerNoExist(player, pName);
    }
    protected boolean playerNoExist(Player player){
        return common.playerNoExist(player, null);
    }

    protected boolean factionNoExist(Player player, String fName){
        return common.factionNoExist(player, fName);
    }
    protected boolean factionNoExist(Player player){
        return common.factionNoExist(player, null);
    }

    protected boolean noMember(Player player){
        return common.noMember(player);
    }

    protected Double formatDouble(String st){
        return common.formatDouble(st, player);
    }
    protected Double formatDouble(String st, Player player){
        return common.formatDouble(st, player);
    }

    protected Integer formatInt(String st){
        return common.formatInt(st, null);
    }
    protected Integer formatInt(String st, Player player){
        return common.formatInt(st, player);
    }

    protected String formatMoney(double amount){
        return common.formatMoney(amount);
    }

    protected String formatPercent(double amount){
        return common.formatPercent(amount);
    }

    protected String formatDateTime(Duration d){
        return common.formatDateTime(d);
    }
}