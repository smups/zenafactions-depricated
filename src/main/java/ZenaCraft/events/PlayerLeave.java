package ZenaCraft.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ZenaCraft.App;
import ZenaCraft.objects.FactionQChunk;

public class PlayerLeave implements Listener{
    
    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        Location location = player.getLocation();
        double[] borderLocs = new double[] {0, 1600, -1600};

        for (double offsetX : borderLocs){
            for (double offsetZ : borderLocs){

                double[] playerLoc = new double[2];
                playerLoc[0] = location.getX() + offsetX;
                playerLoc[1] = location.getZ() + offsetZ;
        
                int[] chunkLoc = new int[2];
                chunkLoc[0] = location.getChunk().getX() + (int) offsetX/16;
                chunkLoc[1] = location.getChunk().getZ() + (int) offsetZ/16;
        
                String fQchunkName = "";
        
                int fQx = chunkLoc[0]/100;
                int fQz = chunkLoc[1]/100;
        
                fQchunkName += ("X" + String.valueOf(fQx));
                fQchunkName += ("Z" + String.valueOf(fQz));
        
                FactionQChunk factionQChunk = (FactionQChunk) App.loadedFQChunks.get(fQchunkName);

                if ((int) factionQChunk.getOnlinePlayers().size() == 1){
                    factionQChunk.removeOnlinePlayer(player);
                    factionQChunk.saveFQChunkData();
                    App.loadedFQChunks.remove(fQchunkName);
                }
                else{
                    factionQChunk.removeOnlinePlayer(player);
                }
            }
        }
    }
}