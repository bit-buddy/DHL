package frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import util.DBManager;

public class CustomerOrderInfoFrame extends JFrame {
    private String loggedInCustomer;
    private JComboBox<String> orderComboBox;
    private JTextArea orderInfoTextArea;
    private JButton showDescriptionButton;
    private JButton editDestinationButton;
    private JButton deleteOrderButton;

    public CustomerOrderInfoFrame(String loggedInCustomer) {
        this.loggedInCustomer = loggedInCustomer;

        setTitle("Customer Order Information");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();

        orderComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayOrderInfo(loggedInCustomer, (String) orderComboBox.getSelectedItem());
            }
        });

        showDescriptionButton = new JButton("Show Detailed Description");
        showDescriptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDetailedDescription(loggedInCustomer, (String) orderComboBox.getSelectedItem());
            }
        });

        editDestinationButton = new JButton("Edit Destination");
        editDestinationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editOrderDestination(loggedInCustomer, (String) orderComboBox.getSelectedItem());
            }
        });

        deleteOrderButton = new JButton("Delete Order");
        deleteOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmDeleteOrder(loggedInCustomer, (String) orderComboBox.getSelectedItem());
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(showDescriptionButton);
        buttonPanel.add(editDestinationButton);
        buttonPanel.add(deleteOrderButton);

        add(buttonPanel, BorderLayout.SOUTH);

        populateOrderComboBox(loggedInCustomer);
    }

    private void initUI() {
        setLayout(new BorderLayout());

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
                            "WHERE customers.name = ? AND shipments.is_deleted = false"
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
                    "WHERE customers.name = ? AND shipments.trackingNumber = ? AND shipments.is_deleted = false";

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

    private void showDetailedDescription(String customerName, String trackingNumber) {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            String sqlQuery = "SELECT shipments.detailed_description " +
                    "FROM shipments " +
                    "JOIN customers ON shipments.customerId = customers.id " +
                    "WHERE customers.name = ? AND shipments.trackingNumber = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, trackingNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String detailedDescription = resultSet.getString("detailed_description");
                JOptionPane.showMessageDialog(this, "Detailed Description:\n" + detailedDescription,
                        "Order Detailed Description", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No detailed description available for the selected order.",
                        "Order Detailed Description", JOptionPane.INFORMATION_MESSAGE);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void editOrderDestination(String customerName, String trackingNumber) {

        JFrame editDestinationFrame = new JFrame("Edit Destination");
        JPanel editPanel = new JPanel(new GridLayout(3, 2));

        JLabel newDestinationLabel = new JLabel("Enter new Destination:");
        JTextField newDestinationField = new JTextField();
        JButton updateButton = new JButton("Update");

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newDestination = newDestinationField.getText();
                updateDestinationInDatabase(customerName, trackingNumber, newDestination);
                editDestinationFrame.dispose();
            }
        });

        editPanel.add(newDestinationLabel);
        editPanel.add(newDestinationField);
        editPanel.add(updateButton);

        editDestinationFrame.getContentPane().add(BorderLayout.CENTER, editPanel);
        editDestinationFrame.setSize(300, 150);
        editDestinationFrame.setVisible(true);
    }

    private void updateDestinationInDatabase(String customerName, String trackingNumber, String newDestination) {

        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            String sqlQuery = "UPDATE shipments SET destination = ? " +
                    "WHERE customerId = (SELECT id FROM customers WHERE name = ?) " +
                    "AND trackingNumber = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, newDestination);
            preparedStatement.setString(2, customerName);
            preparedStatement.setString(3, trackingNumber);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Destination updated successfully",
                        "Update Success", JOptionPane.INFORMATION_MESSAGE);

                displayOrderInfo(customerName, trackingNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update destination",
                        "Update Failure", JOptionPane.ERROR_MESSAGE);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void confirmDeleteOrder(String customerName, String trackingNumber) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this order?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            deleteOrder(customerName, trackingNumber);
        }
    }

    private void deleteOrder(String customerName, String trackingNumber) {
        try {
            Connection connection = DBManager.getInstance().getDataSource().getConnection();
            String sqlQuery = "UPDATE shipments SET is_deleted = true " +
                    "WHERE customerId = (SELECT id FROM customers WHERE name = ?) " +
                    "AND trackingNumber = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, trackingNumber);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Order deleted successfully",
                        "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
                populateOrderComboBox(loggedInCustomer); // Refresh the order list after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete order",
                        "Deletion Failure", JOptionPane.ERROR_MESSAGE);
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
