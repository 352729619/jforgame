package com.kingston.jforgame.server.game.database.user.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.server.db.BaseEntity;
import com.kingston.jforgame.server.game.function.model.Function;
import com.kingston.jforgame.server.game.login.model.Platform;
import com.kingston.jforgame.server.game.player.PlayerSerializerUtil;
import com.kingston.jforgame.server.game.vip.model.VipRight;
import com.kingston.jforgame.server.utils.IdGenerator;
import com.kingston.jforgame.socket.mina.MinaSessionProperties;
import com.kingston.jforgame.socket.session.SessionManager;
import com.kingston.jforgame.socket.task.Distributable;

/**
 * 玩家实体
 *
 * @author kingston
 */
@Entity
public class Player extends BaseEntity implements Distributable {

    private static final long serialVersionUID = 8913056963732639062L;

    @Id
    @Column(length = 32)
    private long id;

    @Column(length = 32)
    private long accountId;

    @Column(length = 128)
    private String name;

    /**
     * 职业
     */
    @Column(length = 6)
    private int job;

    @Column(length = 11)
    private int level;

    @Column(length = 32)
    private long exp;

    /**
     * 上一次每日重置的时间戳
     */
    @Column
    private long lastDailyReset;

    private VipRight vipRight;

    @Column
    private String vipRightJson;


    private Platform platform;

    @Column
    private String platformJson;

    //	@Column
    private String functionJson;

    private Function function;

    public Player() {
        this.id = IdGenerator.getNextId();
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getLastDailyReset() {
        return lastDailyReset;
    }

    public void setLastDailyReset(long lastDailyReset) {
        this.lastDailyReset = lastDailyReset;
    }


    public VipRight getVipRight() {
        return vipRight;
    }

    public void setVipRight(VipRight vipRight) {
        this.vipRight = vipRight;
    }

    public String getVipRightJson() {
        return vipRightJson;
    }

    public void setVipRightJson(String vipRightJson) {
        this.vipRightJson = vipRightJson;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getPlatformJson() {
        return platformJson;
    }

    public void setPlatformJson(String platformJson) {
        this.platformJson = platformJson;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function functions) {
        this.function = functions;
    }

    public String getFunctionJson() {
        return functionJson;
    }

    public void setFunctionJson(String functionJson) {
        this.functionJson = functionJson;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void doAfterInit() {
        PlayerSerializerUtil.deserialize(this);
    }

    @Override
    public void doBeforeSave() {
        PlayerSerializerUtil.serialize(this);
    }

    @Override
    public int distributeKey() {
//		IoSession session = SessionManager.INSTANCE.getSessionBy(id);
//		return (int)session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
        return 0;
    }

    @Override
    public String toString() {
        return "Player [id=" + id + ", name=" + name + ", job=" + job + ", level=" + level + ", exp=" + exp
                + ", lastDailyReset=" + lastDailyReset + ", vipRight=" + vipRight + ", platform=" + platform + "]";
    }

}
