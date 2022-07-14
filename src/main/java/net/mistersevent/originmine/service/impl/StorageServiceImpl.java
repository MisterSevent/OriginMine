package net.mistersevent.originmine.service.impl;

import net.mistersevent.core.spigot.api.configuration.ConfigAPI;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.service.StorageService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class StorageServiceImpl implements StorageService {

    private final Set<Account> set = new HashSet<>();
    private final ConfigAPI storage = new ConfigAPI("storage", OriginMine.getInstance());

    public void create(Account account) {
        if (this.storage.getConfigurationSection("accounts") == null || !this.storage.contains("accounts." + account.getName()) || this.storage.get("accounts." + account.getName()) == null) {
            set.add(account);
            storage.put("accounts." + account.getName(), account.serialize().toJSONString());
            storage.save();
        }
    }

    public void remove(String s) {
        if (get(s) != null) {
            set.remove(get(s));
            storage.put("accounts." + s, (Object) null);
            storage.save();
        }
    }

    public Account get(String s) {
        return (Account) search(s).findFirst().orElse(null);
    }

    public Stream<Account> search(String s) {
        return set.stream().filter((account) -> {
            return account.getName().equalsIgnoreCase(s);
        });
    }

    public void load() {
        if (storage.getConfigurationSection("accounts") != null) {
            storage.getConfigurationSection("accounts").getKeys(false).forEach((s) -> {
                set.add(Account.deserialize(storage.getString("accounts." + s)));
            });
        }
    }

    
}
