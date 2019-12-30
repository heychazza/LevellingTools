package com.codeitall.tools.storage.mysql;

import com.codeitall.tools.storage.PlayerData;
import com.codeitall.tools.storage.StorageHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.UUID;

public class MySQLHandler implements StorageHandler {

    private ConnectionSource connectionSource;
    private Dao<MySQLPlayerData, String> accountDao;

    public MySQLHandler(String prefix, String host, int port, String database, String username, String password) {
        try {
            connectionSource = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            accountDao = DaoManager.createDao(connectionSource, MySQLPlayerData.class);
            TableUtils.createTableIfNotExists(connectionSource, MySQLPlayerData.class);
            connectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pullData(String name, UUID uuid) {
        try {
            MySQLPlayerData user = accountDao.queryForId(uuid.toString());
            if (user == null) {
                user = new MySQLPlayerData();
                user.setUuid(uuid.toString());
                user.setUsername(name);
                user.setLevel(1);
                PlayerData.get().put(uuid, user);
                //accountDao.create(user);
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
            accountDao.createOrUpdate((MySQLPlayerData) playerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
