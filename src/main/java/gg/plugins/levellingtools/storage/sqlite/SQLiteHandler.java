package gg.plugins.levellingtools.storage.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import gg.plugins.levellingtools.storage.PlayerData;
import gg.plugins.levellingtools.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
    public void pullData(UUID uuid) {
        try {
            SQLitePlayerData user = accountDao.queryForId(uuid.toString());
            if (user == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                user = new SQLitePlayerData();
                user.setUuid(uuid.toString());
                user.setUsername(offlinePlayer.getName());
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
