package ZenaCraft.events;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ZenaCraft.App;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;

public class ModifyClaim implements Listener{

    @EventHandler
    void onCreateClaim(ClaimModifiedEvent event){
        Player player = (Player) event.getModifier();
        Claim claim = event.getTo();

        int condition = 0;

        for (Chunk chunk : claim.getChunks()){
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();
            String FQCName = App.factionIOstuff.calcFQCName(chunkX, chunkZ, null, null);

            byte[][] chunkData = App.factionIOstuff.getFQC(FQCName).getChunkData();
            int ownerID = chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)];
            byte playerFaction = player.getMetadata("factionID").get(0).asByte();
            if (ownerID != playerFaction) condition += 1;
        }

        if (condition != 0){
            event.setCancelled(true);
            player.sendMessage(App.zenfac + ChatColor.RED + "Modified claim not within faction borders!");
        }
    }
    
}
