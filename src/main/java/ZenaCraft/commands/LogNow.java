package ZenaCraft.commands;

import ZenaCraft.App;

public class LogNow extends TemplateCommand{

    public LogNow() {super(0);}

    @Override
    protected boolean run() {
        if (!player.isOp()) return opCommand(player);

        App.perfThread.interrupt();
        player.sendMessage(App.zenfac + "Saved log files!");
        return true;
    }
}