import util.DBManager;
import frames.LoginForm;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        connectionWithDataBase();
        LoginForm loginScreen = new LoginForm();
    }

    public static void connectionWithDataBase(){
        DBManager manager;
        try {
            manager = DBManager.getInstance();
            DataSource dataSource = manager.getDataSource();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()){
                System.out.print(resultSet.getString(1) + " ");
                System.out.print(resultSet.getString(2) + " ");
                System.out.print(resultSet.getString(3) + " ");
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}