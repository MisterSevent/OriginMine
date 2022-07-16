package net.mistersevent.originmine.listener.player;

import com.sun.management.VMOption;
import net.mistersevent.core.other.dabatase.redis.Redis;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.core.spigot.api.title.Title;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.json.SerializeInventory;
import net.mistersevent.originmine.rpacket.AccountStoragePacket;
import net.mistersevent.originmine.service.StorageService;
import net.mistersevent.pick.Main;
import net.mistersevent.pick.model.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerListener implements Listener {

    private final StorageService storageService = (StorageService) Services.get(StorageService.class);
    private final Redis redis = (Redis) Services.get(Redis.class);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Account account = (Account) this.storageService.get(p.getName());

        if (account != null) {
            p.getInventory().clear();
            p.getInventory().setArmorContents((ItemStack[]) null);
            p.setLevel(0);
            p.setExp(0.0F);
            if (!account.isClearInventory()) {
                p.setLevel((int) account.getLevel());
                p.setExp((float) p.getExp());
                Bukkit.getScheduler().scheduleSyncDelayedTask(OriginMine.getInstance(), () -> {
                    try {
                        p.getInventory().setArmorContents(SerializeInventory.itemStackArrayFromBase64(account.getPlayerInventory()[1]));
                        SerializeInventory.fromBase64(account.getPlayerInventory()[0]).forEach((itemStack) -> {
                            if (itemStack != null && itemStack.getType() != Material.AIR) {
                                p.getInventory().addItem(itemStack);
                            }
                        });
                        p.updateInventory();
                    }catch (IOException var1) {
                        var1.printStackTrace();
                        p.kickPlayer("§cNão foi possível carregar seu inventário, contate um administrador!");
                    }
                }, 40L);
            }
        }

        if (OriginMine.RECEIVER) {
            if (OriginMine.getInstance().getConfig().getBoolean("Server.title.use")) {
                Title.sendTitle(p, OriginMine.getInstance().getConfig().getString("Server.title.line_1").replace("&", "§"), OriginMine.getInstance().getConfig().getString("Server.title.line_2").replace("&", "§"));
            }
            p.teleport(getRandomLocationInsideWorldBorder(Bukkit.getWorld(OriginMine.getInstance().getConfig().getString("Server.world-mine"))));
        }
        this.storageService.remove(p.getName());
        e.setJoinMessage(null);
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (OriginMine.RECEIVER) {
            if (!p.hasMetadata("spawnCommand")) {
                redis.sendPacket(new AccountStoragePacket(new Account(p)), "mines.sender");
            } else {
                p.removeMetadata("spawnCommand", OriginMine.getInstance());
            }
        } else if (p.hasMetadata("mineCommand")) {
            p.removeMetadata("mineCommand", OriginMine.getInstance());
        }

        storageService.remove(p.getName());
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        World world = p.getWorld();
        if (e.getFrom().getName().equalsIgnoreCase(OriginMine.getInstance().getConfig().getString("Server.world-mine")) && OriginMine.getInstance().getConfig().getBoolean("Server.block-potion-effect-dig-speed-and-night-vision-in-world-mine")) {
            if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                p.removePotionEffect(PotionEffectType.FAST_DIGGING);
            }
        }
    }

    @EventHandler
    public void EntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (p.getWorld().getName().equalsIgnoreCase(OriginMine.getInstance().getConfig().getString("Server.block-potion-effect-dig-speed-and-night-vision-in-world-mine"))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory() == null) {
                return;
            }

            if (e.getCurrentItem() == null || e.getCurrentItem().getTypeId() == 0) {
                return;
            }

            if (!e.getClickedInventory().equals(p.getInventory()) && (p.hasMetadata("mineCommand") || p.hasMetadata("spawnCommand"))) {
                p.sendMessage(OriginMine.getInstance().getConfig().getString("interacting-connecting").replace("&", "§"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata("mineCommand") || p.hasMetadata("spawnCommand")) {
            p.sendMessage(OriginMine.getInstance().getConfig().getString("interacting-connecting").replace("&", "§"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata("mineCommand") || p.hasMetadata("spawnCommand")) {
            p.sendMessage(OriginMine.getInstance().getConfig().getString("dropping-connecting").replace("&", "§"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata("mineCommand") || p.hasMetadata("spawnCommand")) {
            p.sendMessage(OriginMine.getInstance().getConfig().getString("digiting-connecting").replace("&", "§"));
            e.setCancelled(true);
        }
    }
    private Location getRandomLocationInsideWorldBorder(World world) {
        double randomX = ThreadLocalRandom.current().nextDouble(0.0D, world.getWorldBorder().getSize() / 2.0D);
        double randomY = ThreadLocalRandom.current().nextDouble(0.0D, world.getWorldBorder().getSize() / 2.0D);
        return new Location(world, randomX, (double)world.getHighestBlockYAt((int)randomX, (int)randomY), randomY);
    }
}
