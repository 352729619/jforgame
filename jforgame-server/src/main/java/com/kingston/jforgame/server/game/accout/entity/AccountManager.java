package com.kingston.jforgame.server.game.accout.entity;

import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.dbmanager.LoadDBManager;
import com.kingston.jforgame.server.game.player.PlayerManager;

public class AccountManager  {

	private static AccountManager instance = new AccountManager();

	public static AccountManager getInstance() {
		return instance;
	}


	public Account getOrCreate(long accountId) {
		Account account =LoadDBManager.getInstance().getEntity(accountId,Account.class);
		if (account != null) {
			return account;
		}
		
		Account newAccount = new Account();
		newAccount.setId(accountId);
		DbService.getInstance().insertOrUpdate(newAccount);
		PlayerManager.getInstance().addAccountProfile(newAccount);
		return newAccount;
	}
	
}
