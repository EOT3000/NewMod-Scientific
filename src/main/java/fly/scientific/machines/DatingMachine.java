package fly.scientific.machines;

import fly.newmod.NewMod;
import fly.newmod.bases.ModItem;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static org.bukkit.Material.*;

public class DatingMachine extends ModItem implements Listener {
    private static List<Material> AUTO_AGED = Arrays.asList(
            WOODEN_AXE, WOODEN_HOE, WOODEN_PICKAXE, WOODEN_SHOVEL, WOODEN_SWORD,
            LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS,

            STONE_AXE, STONE_HOE, STONE_PICKAXE, STONE_SHOVEL, STONE_SWORD,
            CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,

            GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE, GOLDEN_SHOVEL, GOLDEN_SWORD,
            GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS,

            IRON_AXE, IRON_HOE, IRON_PICKAXE, IRON_SHOVEL, IRON_SWORD,
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,

            DIAMOND_AXE, DIAMOND_HOE, DIAMOND_PICKAXE, DIAMOND_SHOVEL, DIAMOND_SWORD,
            DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS,

            NETHERITE_AXE, NETHERITE_HOE, NETHERITE_PICKAXE, NETHERITE_SHOVEL, NETHERITE_SWORD,
            NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS,

            SHEARS, SHIELD, BOW, CROSSBOW, FISHING_ROD, WRITABLE_BOOK, MAP
            );

    public static NamespacedKey DATE_NAMESPACE = new NamespacedKey(NewMod.get(), "time-of-creation");
    public static NamespacedKey SIGNING_NAMESPACE = new NamespacedKey(NewMod.get(), "time-of-signing");
    public static NamespacedKey EDITING_NAMESPACE = new NamespacedKey(NewMod.get(), "time-of-editing");

    public static NamespacedKey CRAFTER_NAMESPACE = new NamespacedKey(NewMod.get(), "uuid-of-crafter");

    public static NamespacedKey LINE_1_NAMESPACE = new NamespacedKey(NewMod.get(), "line1");
    public static NamespacedKey LINE_2_NAMESPACE = new NamespacedKey(NewMod.get(), "line2");
    public static NamespacedKey LINE_3_NAMESPACE = new NamespacedKey(NewMod.get(), "line3");
    public static NamespacedKey LINE_4_NAMESPACE = new NamespacedKey(NewMod.get(), "line4");
    public static NamespacedKey COLOR_NAMESPACE = new NamespacedKey(NewMod.get(), "color");

    public static NamespacedKey PLAYERS_NAMESPACE = new NamespacedKey(NewMod.get(), "players");

    private Random random = new Random();

    public DatingMachine() {
        super(Material.DROPPER, "&4Dating Machine", "dating_machine");
    }

    public static <T> ItemStack tag(T object, ItemStack stack, NamespacedKey key, PersistentDataType<T, T> type) {
        ItemMeta meta = stack.getItemMeta();

        meta.getPersistentDataContainer().set(key, type, object);

        stack.setItemMeta(meta);

        return stack;
    }

    private static <T> ItemMeta tagA(T object, ItemMeta meta, NamespacedKey key, PersistentDataType<T, T> type) {
        meta.getPersistentDataContainer().set(key, type, object);

        return meta;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if(AUTO_AGED.contains(event.getRecipe().getResult().getType())) {
            tag(System.currentTimeMillis(), event.getInventory().getResult(), DATE_NAMESPACE, PersistentDataType.LONG);
            tag(event.getWhoClicked().getUniqueId().toString(), event.getInventory().getResult(), CRAFTER_NAMESPACE, PersistentDataType.STRING);
        }

        if(event.getInventory().getResult().getType().equals(WRITTEN_BOOK)) {
            for(ItemStack stack : event.getInventory()) {
                if(stack != null && stack.getType().equals(WRITABLE_BOOK)) {
                    tag(stack.getItemMeta().getPersistentDataContainer().getOrDefault(DATE_NAMESPACE, PersistentDataType.LONG, -1L), event.getInventory().getResult(), DATE_NAMESPACE, PersistentDataType.LONG);
                }
            }
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

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        long x = Math.abs(((long)random.nextInt()))+1000000000L;

        for(ItemStack stack : event.getLoot()) {
            if(AUTO_AGED.contains(stack.getType())) {
                tag(Math.abs(x + random.nextInt(1000000)), stack, DATE_NAMESPACE, PersistentDataType.LONG);
            }
        }
    }

    @EventHandler
    public void onBookSign(PlayerEditBookEvent event) {
        BookMeta oldMeta = event.getPreviousBookMeta();
        BookMeta newMeta = event.getNewBookMeta();

        System.out.println("AASD");

        if(event.isSigning()) {
            event.setNewBookMeta((BookMeta) tagA(System.currentTimeMillis(), newMeta, SIGNING_NAMESPACE, PersistentDataType.LONG));
        }

        event.setNewBookMeta((BookMeta) tagA(addOne(newMeta, System.currentTimeMillis()), newMeta, EDITING_NAMESPACE, PersistentDataType.LONG_ARRAY));
    }

    private static long[] addOne(ItemMeta meta, long arg) {
        long[] array = meta.getPersistentDataContainer().get(EDITING_NAMESPACE, PersistentDataType.LONG_ARRAY);

        Set<Long> set = new HashSet<>();

        if(array != null) {

            for (long l : array) {
                set.add(l);
            }
        }

        set.add(arg/25000000);

        return ArrayUtils.toPrimitive(set.toArray(new Long[0]));
    }
}
