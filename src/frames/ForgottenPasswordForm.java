package frames;

import util.DBManager;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;

public class ForgottenPasswordForm extends JFrame {
    private JFrame frame;
    private JTextField textField;
    private JPasswordField passwordField;

    public ForgottenPasswordForm() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 561, 344);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Смяна на парола");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblNewLabel.setBounds(150, 35, 258, 54);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Име:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1.setBounds(55, 138, 142, 25);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_1_1 = new JLabel("Нова парола:");
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1_1.setBounds(55, 200, 142, 25);
        frame.getContentPane().add(lblNewLabel_1_1);

        textField = new JTextField();
        textField.setBounds(342, 138, 142, 32);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(342, 191, 142, 35);
        frame.getContentPane().add(passwordField);

        JButton btnNewButton = new JButton("Запази промените");
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnNewButton.setBounds(179, 257, 220, 40);
        frame.getContentPane().add(btnNewButton);
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());
                String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
                changePassword(textField.getText(), encodedPassword);
                frame.dispose();
                LoginForm loginScreen = new LoginForm();
            }
        });


        frame.setVisible(true);
    }

    public int changePassword(String username, String newPassword){
        DBManager manager;
        try {
            manager = DBManager.getInstance();
            DataSource dataSource = manager.getDataSource();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.executeQuery("UPDATE customers SET customers.encryptedPassword = '" + newPassword + "' WHERE customers.name = '" + username+"'");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

}