package ZenaCraft.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class LogNow extends TemplateCommand{

    public LogNow() {super(0);}

    private CommandSender sender;

    @Override
    protected boolean getSender(CommandSender sender) {
        this.sender = sender;

        if (sender instanceof Player){
            this.player = (Player) sender;
            return true;
        }
        return true;
    }

    @Override
    protected boolean run() {
        if (player != null && !player.isOp()) return opCommand(player);

        App.perfThread.interrupt();
        sender.sendMessage(App.zenfac + "Saved log files!");
        return true;
    }
}