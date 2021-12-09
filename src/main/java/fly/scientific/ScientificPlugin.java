package fly.scientific;

import fly.newmod.NewMod;
import fly.newmod.bases.ModItem;
import fly.scientific.machines.DatingMachine;
import fly.scientific.utils.PlayerIntMapWrapper;
import fly.scientific.utils.PlayerTimeDataType;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class ScientificPlugin extends NewMod.ModExtension implements Listener {
    @Override
    public void load() {

    }

    private void updateArmor(Player player, ItemStack stack, int ticks) {
        if(stack == null) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();

        if(ticks % 100 == 0) {
            PlayerIntMapWrapper wrapper = meta.getPersistentDataContainer().getOrDefault(DatingMachine.PLAYERS_NAMESPACE, PlayerTimeDataType.PLAYER_TIME_TYPE, new PlayerIntMapWrapper(new HashMap<>()));

            wrapper.add(player);

            meta.getPersistentDataContainer().set(DatingMachine.PLAYERS_NAMESPACE, PlayerTimeDataType.PLAYER_TIME_TYPE, wrapper);
        }

        stack.setItemMeta(meta);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer cont = event.getItemInHand().getItemMeta().getPersistentDataContainer();

        if(cont.has(DatingMachine.LINE_1_NAMESPACE, PersistentDataType.STRING)) {
            Sign sign = ((Sign) event.getBlock().getState());

            //TODO: fix deprecation

            sign.setLine(0, cont.get(DatingMachine.LINE_1_NAMESPACE, PersistentDataType.STRING));
            sign.setLine(1, cont.get(DatingMachine.LINE_2_NAMESPACE, PersistentDataType.STRING));
            sign.setLine(2, cont.get(DatingMachine.LINE_3_NAMESPACE, PersistentDataType.STRING));
            sign.setLine(3, cont.get(DatingMachine.LINE_4_NAMESPACE, PersistentDataType.STRING));
            sign.setColor(DyeColor.valueOf(cont.get(DatingMachine.COLOR_NAMESPACE, PersistentDataType.STRING)));

            sign.getPersistentDataContainer().set(DatingMachine.DATE_NAMESPACE, PersistentDataType.LONG, cont.get(DatingMachine.DATE_NAMESPACE, PersistentDataType.LONG));

            sign.update();
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        BlockState state = event.getBlock().getState();
        CoreProtectAPI coApi = CoreProtect.getInstance().getAPI();

        if(state instanceof Sign && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
            PersistentDataContainer signContainer = ((Sign) state).getPersistentDataContainer();

            String l1 = ((Sign) state).getLine(0);
            String l2 = ((Sign) state).getLine(1);
            String l3 = ((Sign) state).getLine(2);
            String l4 = ((Sign) state).getLine(3);

            long age = -1;

            if(signContainer.has(DatingMachine.DATE_NAMESPACE, PersistentDataType.LONG)) {
                age = signContainer.get(DatingMachine.DATE_NAMESPACE, PersistentDataType.LONG);
            }

            for(String[] s : coApi.blockLookup(event.getBlock(), Integer.MAX_VALUE)) {
                CoreProtectAPI.ParseResult result = coApi.new ParseResult(s);

                if(result.getActionId() == 1 && age == -1) {
                    age = ((long) result.getTime()*1000);
                    System.out.println(result.getTime());
                    break;
                }
            }
            ItemStack sign = DatingMachine.tag(age, new ItemStack(Material.valueOf(event.getBlock().getType().name().replaceFirst("WALL_", ""))), DatingMachine.DATE_NAMESPACE, PersistentDataType.LONG);
            ItemMeta meta = sign.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();


            container.set(DatingMachine.LINE_1_NAMESPACE, PersistentDataType.STRING, l1);
            container.set(DatingMachine.LINE_2_NAMESPACE, PersistentDataType.STRING, l2);
            container.set(DatingMachine.LINE_3_NAMESPACE, PersistentDataType.STRING, l3);
            container.set(DatingMachine.LINE_4_NAMESPACE, PersistentDataType.STRING, l4);
            container.set(DatingMachine.COLOR_NAMESPACE, PersistentDataType.STRING, ((Sign) state).getColor().name());

            sign.setItemMeta(meta);
            event.setDropItems(false);
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), sign);
        }
    }
}
