package com.kingston.jforgame.server.dbmanager;

import com.kingston.jforgame.server.cache.BaseCacheService;
import com.kingston.jforgame.server.db.BaseEntity;
import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.accout.entity.Account;
import com.kingston.jforgame.server.game.database.user.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JieYu.W    Q:352729619
 * Created on 2020/5/13
 * Describe :
 */
public class LoadDBManager {

    private LoadDBManager() {
        register();
    }

    private static LoadDBManager instance = new LoadDBManager();

    public static LoadDBManager getInstance() {
        return instance;
    }

    public Map<Class<? extends BaseEntity>, BaseCacheService> cacheMap = new ConcurrentHashMap<>();

    private void register() {
        cacheMap.put(Player.class, new PlayerCache());
        cacheMap.put(Account.class, new AccountCache());
    }

    /**
     *  通过主键 和 实体 类信息，获取对象
     * @param idKey
     * @param clazz
     * @param <T>
     * @return
     */
    public final <T extends BaseEntity> T getEntity(Long idKey, Class<T> clazz) {
        BaseCacheService baseCacheService = cacheMap.get(clazz);
        return (T) baseCacheService.get(idKey);
    }

    /**
     * 手动移除缓存
     * @param idKey
     * @param clazz
     * @param <T>
     */
    public final <T extends BaseEntity> void remove(Long idKey, Class<T> clazz) {
        BaseCacheService baseCacheService = cacheMap.get(clazz);
        baseCacheService.remove(idKey);
    }

    /**
     * 手动加入缓存
     * @param idKey
     * @param clazz
     * @param <T>
     */
    public final  <T extends BaseEntity> void  putCache(Long idKey, T clazz){
        BaseCacheService baseCacheService = cacheMap.get(clazz);
        baseCacheService.put(idKey,clazz);
    }



    public class PlayerCache extends BaseCacheService<Long, Player> {

        public Player load(Long playerId) throws Exception {
            String sql = "SELECT * FROM Player where Id = ? ";
            Player player = DbUtils.queryOneById(DbUtils.DB_USER, sql, Player.class, String.valueOf(playerId));
            if (player != null) {
                player.doAfterInit();
            }
            return player;
        }
    }

    public class AccountCache extends BaseCacheService<Long, Account> {
        @Override
        public Account load(Long accountId) throws Exception {
            String sql = "SELECT * FROM account where id = ? ";
            Account account = DbUtils.queryOneById(DbUtils.DB_USER, sql, Account.class, String.valueOf(accountId));
            return account;
        }
    }

}
