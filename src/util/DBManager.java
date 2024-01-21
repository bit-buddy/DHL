package util;

import javax.sql.DataSource;
import java.sql.SQLException;

import org.mariadb.jdbc.MariaDbDataSource;

public class DBManager {
    private static DBManager instance;
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public DBManager() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();

        dataSource.setUrl("jdbc:mariadb://localhost:3306/dhl");
        dataSource.setUser("root");
        dataSource.setPassword(null);
        this.dataSource = dataSource;
    }

    public static synchronized DBManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}