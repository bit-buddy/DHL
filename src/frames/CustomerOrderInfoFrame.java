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

        // Initialize and add JButton
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

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(showDescriptionButton);
        buttonPanel.add(editDestinationButton);

        // Add buttonPanel to the South region of BorderLayout
        add(buttonPanel, BorderLayout.SOUTH);

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
        // Create a separate window for editing destination
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
                editDestinationFrame.dispose();  // Close the window after updating
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
        // Implement the logic to update the destination in the database
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
                // Update the displayed order information after the update
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
}
