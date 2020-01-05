package com.codeitforyou.tools.storage.sqlite;

import com.codeitforyou.tools.storage.PlayerData;
import com.codeitforyou.tools.storage.StorageHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.UUID;

public class SQLiteHandler implements StorageHandler {

    private ConnectionSource connectionSource;
    private Dao<SQLitePlayerData, String> accountDao;

    public SQLiteHandler(String path) {
        try {
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path + "/playerdata.db");
            accountDao = DaoManager.createDao(connectionSource, SQLitePlayerData.class);
            TableUtils.createTableIfNotExists(connectionSource, SQLitePlayerData.class);
            connectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pullData(String name, UUID uuid) {
        try {
            SQLitePlayerData user = accountDao.queryForId(uuid.toString());
            if (user == null) {
                user = new SQLitePlayerData();
                user.setUuid(uuid.toString());
                user.setUsername(name);
                user.setLevel(1);
                PlayerData.get().put(uuid, user);
            } else {
                PlayerData.get().put(uuid, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushData(UUID player) {
        PlayerData playerData = PlayerData.get().get(player);
        try {
            accountDao.createOrUpdate((SQLitePlayerData) playerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
