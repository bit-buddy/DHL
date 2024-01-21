package frames;

import util.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CustomerOrderInfoWindow extends JFrame {
    private JComboBox<String> customerComboBox;
    private JTextArea orderInfoTextArea;

    public CustomerOrderInfoWindow() {
        setTitle("Customers orders");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();

        populateCustomerComboBox();

        customerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCustomer = (String) customerComboBox.getSelectedItem();
                if (selectedCustomer != null) {
                    displayOrderInfo(selectedCustomer);
                }
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Customer:"));

        customerComboBox = new JComboBox<>();
        topPanel.add(customerComboBox);

        add(topPanel, BorderLayout.NORTH);

        orderInfoTextArea = new JTextArea();
        orderInfoTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(orderInfoTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateCustomerComboBox() {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM customers");

            while (resultSet.next()) {
                customerComboBox.addItem(resultSet.getString("name"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayOrderInfo(String customerName) {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT shipments.trackingNumber, shipments.origin, shipments.destination, parcels.weight, parcels.description " +
                            "FROM shipments " +
                            "JOIN customers ON shipments.customerId = customers.id " +
                            "JOIN parcels ON parcels.shipmentId = shipments.id " +
                            "WHERE customers.name = ?"
            );
            preparedStatement.setString(1, customerName);

            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder orderInfo = new StringBuilder();

            orderInfoTextArea.setText("");  // Clear existing text

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerOrderInfoWindow().setVisible(true);
            }
        });
    }
}
