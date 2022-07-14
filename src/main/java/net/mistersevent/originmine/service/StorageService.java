package net.mistersevent.originmine.service;

import net.mistersevent.core.other.services.Model;
import net.mistersevent.originmine.account.Account;

public interface StorageService extends Model<String, Account> {
    void load();
}
