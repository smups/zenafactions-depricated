package ZenaCraft.objects;

import java.io.Serializable;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class pBanner implements Serializable{
    //versie van Location die wel persistent is
    static final long serialVersionUID = 100L;

    private List<Pattern> pList;
    private DyeColor baseColor;

    public pBanner(ItemStack is) throws ExceptionInInitializerError{
        BannerMeta bm = (BannerMeta) is.getItemMeta();
        pList = bm.getPatterns();
    }
}
