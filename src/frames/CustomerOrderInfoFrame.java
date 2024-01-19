package frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import util.DBManager;

public class CustomerOrderInfoFrame extends JFrame {
    private String loggedInCustomer;
    private JComboBox<String> orderComboBox;  // Add a JComboBox for orders
    private JTextArea orderInfoTextArea;

    public CustomerOrderInfoFrame(String loggedInCustomer) {
        this.loggedInCustomer = loggedInCustomer;

        setTitle("Customer Order Information");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();

        // Display the order info based on the selected order in the JComboBox
        orderComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Selected item: " + orderComboBox.getSelectedItem());
                displayOrderInfo(loggedInCustomer, (String) orderComboBox.getSelectedItem());
            }
        });


        // Populate the JComboBox with the customer's orders
        populateOrderComboBox(loggedInCustomer);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Initialize and add JComboBox
        orderComboBox = new JComboBox<>();
        add(orderComboBox, BorderLayout.NORTH);

        orderInfoTextArea = new JTextArea();
        orderInfoTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(orderInfoTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateOrderComboBox(String customerName) {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT DISTINCT shipments.trackingNumber " +
                            "FROM shipments " +
                            "JOIN customers ON shipments.customerId = customers.id " +
                            "WHERE customers.name = ?"
            );
            preparedStatement.setString(1, customerName);

            ResultSet resultSet = preparedStatement.executeQuery();

            orderComboBox.removeAllItems();  // Clear existing items before adding new ones

            while (resultSet.next()) {
                orderComboBox.addItem(resultSet.getString("trackingNumber"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void displayOrderInfo(String customerName, String trackingNumber) {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            String sqlQuery = "SELECT shipments.trackingNumber, shipments.origin, shipments.destination, parcels.weight, parcels.description " +
                    "FROM shipments " +
                    "JOIN customers ON shipments.customerId = customers.id " +
                    "JOIN parcels ON parcels.shipmentId = shipments.id " +
                    "WHERE customers.name = ? AND shipments.trackingNumber = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, trackingNumber);

            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder orderInfo = new StringBuilder();

            while (resultSet.next()) {
                orderInfo.append("Tracking Number: ").append(resultSet.getString("trackingNumber")).append("\n");
                orderInfo.append("Origin: ").append(resultSet.getString("origin")).append("\n");
                orderInfo.append("Destination: ").append(resultSet.getString("destination")).append("\n");
                orderInfo.append("Weight: ").append(resultSet.getDouble("weight")).append(" kg\n");
                orderInfo.append("Description: ").append(resultSet.getString("description")).append("\n\n");
            }

            orderInfoTextArea.setText(orderInfo.toString());

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
