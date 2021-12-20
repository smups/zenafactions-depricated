package ZenaCraft.listeners;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class BeaconEffect implements Listener{

    @EventHandler
    public void onBeaconEffect(BeaconEffectEvent e){
        Player p = e.getPlayer();
        Faction f = App.factionIOstuff.getPlayerFaction(p);

        Chunk c = e.getBlock().getChunk();

        byte[][] FQCData = App.factionIOstuff.getFQC(
            App.factionIOstuff.calcFQCName(c.getX(), c.getZ(), null, null)
        ).getChunkData();

        if(f.getID() != FQCData[Math.abs(c.getX()) % 100][Math.abs(c.getZ()) % 100]) e.setCancelled(true);
    }
}
