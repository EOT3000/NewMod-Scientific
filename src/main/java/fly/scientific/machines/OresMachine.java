package fly.scientific.machines;

import com.google.common.collect.Lists;
import fly.newmod.bases.ModItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OresMachine extends ModItem {
    private final List<Material> STONES = Lists.asList(Material.STONE, new Material[]
            {Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.CALCITE, Material.DEEPSLATE, Material.TUFF,
            Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.IRON_ORE, Material.GOLD_ORE, Material.COPPER_ORE, Material.COAL_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE,
            Material.BUDDING_AMETHYST});

    public OresMachine() {
        super(Material.HOPPER, "&4Ores Machine", "ores_machine");
    }

    @Override
    public void tick(Location location, int c) {
        Block block = location.getBlock();

        Hopper hopper = (Hopper) block.getBlockData();

        Map<Material, Integer> map = new HashMap<>();

        if(!hopper.isEnabled()) {
            Location locationBlock = location.clone();

            while (locationBlock.getBlockY() > 1) {
                locationBlock.subtract(0, 1, 0);

                map.put(locationBlock.getBlock().getType(), map.getOrDefault(locationBlock.getBlock().getType(), 0)+1);
            }
        }

        for(Material material : map.keySet()) {
            if(STONES.contains(material)) {
                for(Player player : location.getNearbyPlayers(5)) {
                    player.sendMessage(material + " : " + map.get(material));
                }
            }
        }
    }
}
