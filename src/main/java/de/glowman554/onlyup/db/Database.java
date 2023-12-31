package de.glowman554.onlyup.db;

import java.sql.SQLException;

public interface Database {
    void saveTime(Time time) throws SQLException;
    Time[] loadBestTime(int amount) throws  SQLException;

    record Time(String player, int timeTick) {
    }
}