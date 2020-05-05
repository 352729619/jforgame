package com.kingston.jforgame.server.db;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import com.kingston.jforgame.common.utils.BlockingUniqueQueue;
import com.kingston.jforgame.orm.OrmBridge;
import com.kingston.jforgame.orm.OrmProcessor;
import com.kingston.jforgame.orm.utils.SqlUtils;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 用户数据异步持久化的服务
 *
 * @author kingston
 */
public class DbService {

    private static volatile DbService instance;

    public static DbService getInstance() {
        if (instance == null) {
            synchronized (DbService.class) {
                if (instance == null) {
                    instance = new DbService();
                }
            }
        }
        return instance;
    }

    private Worker playerWorker = new Worker();

    private Worker commonWorker = new Worker();

    /**
     * start consumer thread
     */
    public void init() throws SQLException {
        createTable();
        new NamedThreadFactory("db-player-service").newThread(playerWorker).start();
        new NamedThreadFactory("db-common-service").newThread(commonWorker).start();
    }

    private final AtomicBoolean run = new AtomicBoolean(true);

    /**
     * 自动插入或者更新数据
     *
     * @param entity
     */
    public void insertOrUpdate(BaseEntity entity) {
        if (entity instanceof Player) {
            playerWorker.addToQueue(entity);
        } else {
            commonWorker.addToQueue(entity);
        }
    }

    /**
     * 删除数据
     *
     * @param entity
     */
    public void delete(BaseEntity entity) {
        entity.setDelete();
        if (entity instanceof Player) {
            playerWorker.addToQueue(entity);
        } else {
            commonWorker.addToQueue(entity);
        }
    }

    private class Worker implements Runnable {

        private BlockingQueue<BaseEntity> queue = new BlockingUniqueQueue<>();

        void addToQueue(BaseEntity ent) {
            this.queue.add(ent);
        }

        @Override
        public void run() {
            while (run.get()) {
                BaseEntity entity = null;
                try {
                    entity = queue.take();
                    saveToDb(entity);
                } catch (Exception e) {
                    LoggerUtils.error("", e);
                    // 有可能是并发抛错，重新放入队列
                    insertOrUpdate(entity);
                }
            }
        }

        void shutDown() {
            for (; ; ) {
                if (!queue.isEmpty()) {
                    saveAllBeforeShutDown();
                } else {
                    break;
                }
            }
        }

        void saveAllBeforeShutDown() {
            while (!queue.isEmpty()) {
                Iterator<BaseEntity> it = queue.iterator();
                while (it.hasNext()) {
                    BaseEntity next = it.next();
                    it.remove();
                    saveToDb(next);
                }
            }
        }
    }

    /**
     * 启服的时候创建表
     */
    public void createTable() throws SQLException {
        Map<Class<?>, OrmBridge> ormBridgeMap = OrmProcessor.INSTANCE.getClassOrmMapperr();
        for (Map.Entry<Class<?>, OrmBridge> entry : ormBridgeMap.entrySet()) {
            OrmBridge bridge = entry.getValue();
            String tableName = bridge.getTableName();
            //以Config开头的表默认是策划表，不主动创建
            if (tableName.startsWith("config")) {
                continue;
            }
            String saveSql = SqlUtils.getCreateTableSql(bridge);
            DbUtils.executeSql(saveSql);
        }
    }

    /**
     * 数据真正持久化
     *
     * @param entity
     */
    private void saveToDb(BaseEntity entity) {
        entity.doBeforeSave();
        String saveSql = entity.getSaveSql();
        try {
            if (DbUtils.executeSql(saveSql)) {
                entity.resetDbStatus();
            }
        } catch (Exception e) {
            LoggerUtils.error("", e);
        }
    }

    public void shutDown() {
        run.getAndSet(false);
        playerWorker.shutDown();
        commonWorker.shutDown();

        LoggerUtils.error("[Db4Common] 执行全部命令后关闭");
    }

}
