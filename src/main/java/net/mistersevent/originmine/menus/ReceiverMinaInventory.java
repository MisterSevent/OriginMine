package net.mistersevent.originmine.menus;

import net.mistersevent.core.other.dabatase.redis.Redis;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.command.MineCommands;
import net.mistersevent.originmine.rpacket.AccountStoragePacket;
import net.mistersevent.originmine.util.ItemBuilder;
import net.mistersevent.pick.Main;
import net.mistersevent.pick.model.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;

public class ReceiverMinaInventory implements Listener {

    private final Redis redis = (Redis) (Redis) Services.get(Redis.class);

    public void showInventory(Player p) {

        Inventory inv = Bukkit.createInventory(null, OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.size"), OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.title").replace("&", "§"));

        Iterator iter = OriginMine.getInstance().getConfig().getConfigurationSection("menu-inventorys.inventory-mina-sair.items").getKeys(false).iterator();

        while (iter.hasNext()) {
            String key = (String) iter.next();
            boolean glow = OriginMine.getInstance().getConfig().getBoolean("menu-inventorys.inventory-mina-sair.items." + key + ".Glow");

            ItemStack builder = glow ? new ItemBuilder(
                    Material.getMaterial(OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".ID")), 1
                    , OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".Data"))
                    .setDisplayName(OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Name"))
                    .setLore(OriginMine.getInstance().getConfig().getStringList("menu-inventorys.inventory-mina-sair.items." + key + ".Lore"))
                    .addEnchantment(Enchantment.ARROW_DAMAGE, 10)
                    .hideAllFlags().build() : new ItemBuilder(
                    Material.getMaterial(OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".ID")), 1
                    , OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".Data"))
                    .setDisplayName(OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Name"))
                    .setLore(OriginMine.getInstance().getConfig().getStringList("menu-inventorys.inventory-mina-sair.items." + key + ".Lore"))
                    .build();

            inv.setItem(OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".Slot"), builder);
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onClickReceiverMinaInventory(InventoryClickEvent e) {

        if (e.getWhoClicked() instanceof Player) {
            if (e.getInventory().getTitle().equals(OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.title").replace("&", "§"))) {

                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                e.setCancelled(true);

                Player p = (Player) e.getWhoClicked();
                Iterator iter = OriginMine.getInstance().getConfig().getConfigurationSection("menu-inventorys.inventory-mina-sair.items").getKeys(false).iterator();

                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    boolean glow = OriginMine.getInstance().getConfig().getBoolean("menu-inventorys.inventory-mina-sair.items." + key + ".Glow");

                    ItemStack builder = glow ? new ItemBuilder(
                            Material.getMaterial(OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".ID")), 1
                            , OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".Data"))
                            .setDisplayName(OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Name"))
                            .setLore(OriginMine.getInstance().getConfig().getStringList("menu-inventorys.inventory-mina-sair.items." + key + ".Lore"))
                            .addEnchantment(Enchantment.ARROW_DAMAGE, 10)
                            .hideAllFlags().build() : new ItemBuilder(
                            Material.getMaterial(OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".ID")), 1
                            , OriginMine.getInstance().getConfig().getInt("menu-inventorys.inventory-mina-sair.items." + key + ".Data"))
                            .setDisplayName(OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Name"))
                            .setLore(OriginMine.getInstance().getConfig().getStringList("menu-inventorys.inventory-mina-sair.items." + key + ".Lore"))
                            .build();

                    if (e.getCurrentItem().isSimilar(builder)) {

                        if (OriginMine.getInstance().getConfig().getBoolean("menu-inventorys.inventory-mina-sair.items." + key + ".Close-inventory")) {
                            p.closeInventory();
                        }

                        String type = OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Execute");

                        switch (type) {
                            case "return":

                                UserModel userModel = Main.getInstance().getUserCache().getById(p.getUniqueId());
                                if (userModel != null) {
                                    userModel.removeIfContains(p.getInventory());
                                }
                                p.sendMessage(OriginMine.getInstance().getConfig().getString("connecting-server-padrao").replace("&", "§"));
                                p.setMetadata("spawnCommand", new FixedMetadataValue(OriginMine.getInstance(), true));
                                ReceiverMinaInventory.this.redis.sendPacket(new AccountStoragePacket(new Account(p)), "mines.sender");
                                Bukkit.getScheduler().scheduleSyncDelayedTask(OriginMine.getInstance(), () -> {
                                OriginMine.getInstance().getBungeeChannelApi().connect(p, OriginMine.getInstance().getConfig().getString("Server.server-name-default"));
                                }, 60L);
                                break;

                            case "command":
                                String command = OriginMine.getInstance().getConfig().getString("menu-inventorys.inventory-mina-sair.items." + key + ".Command");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&", "§").replace("<player>", p.getName()));
                                break;
                        }
                    }
                }
            }
        }
    }
}
