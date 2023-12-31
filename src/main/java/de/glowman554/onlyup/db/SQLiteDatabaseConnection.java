package de.glowman554.onlyup.db;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteDatabaseConnection implements Database {
    private final Connection connection;

    public SQLiteDatabaseConnection(File database) throws SQLException {
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", database.getPath()));

        Statement statement = connection.createStatement();
        statement.execute("create table if not exists plays (player text not null, time int not null)");
        statement.close();
    }

    @Override
    public void saveTime(Time time) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into plays (player, time) values (?, ?)");
        statement.setString(1, time.player());
        statement.setInt(2, time.timeTick());
        statement.execute();
        statement.close();
    }

    @Override
    public Time[] loadBestTime(int amount) throws SQLException {
        ArrayList<Time> times = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("select player, time from plays order by time");
        ResultSet resultSet = statement.executeQuery();

        while (amount > 0 && resultSet.next()) {
            amount--;
            times.add(new Time(resultSet.getString("player"), resultSet.getInt("time")));
        }

        resultSet.close();
        statement.close();

        return times.toArray(Time[]::new);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
