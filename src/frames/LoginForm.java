package frames;

import util.DBManager;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class LoginForm extends JFrame {
    private JFrame frame;
    private final JPasswordField passwordField = new JPasswordField();
    private JTextField textField;

    public LoginForm() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 576, 326);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Добре дошли!");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblNewLabel.setBounds(151, 21, 258, 77);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Потребител:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1.setBounds(30, 99, 121, 42);
        frame.getContentPane().add(lblNewLabel_1);
        passwordField.setBounds(360, 156, 166, 42);
        frame.getContentPane().add(passwordField);

        JLabel lblNewLabel_1_1 = new JLabel("Парола:");
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1_1.setBounds(30, 151, 121, 42);
        frame.getContentPane().add(lblNewLabel_1_1);

        textField = new JTextField();
        textField.setBounds(360, 108, 166, 41);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Вход");
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton.setBounds(192, 211, 156, 42);
        frame.getContentPane().add(btnNewButton);
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                String password = new String(passwordField.getPassword());
                String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
                if (validInput(username, encodedPassword)) {
                    SuccessfulLogin successfulLogin = new SuccessfulLogin();
                    String loggedInCustomer = username;

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new CustomerOrderInfoFrame(loggedInCustomer).setVisible(true);
                        }
                    });
                } else {
                    FailedLogin failedLogin = new FailedLogin();
                }
            }
        });


        JButton btnNewButton_1 = new JButton("Нова регистрация");
        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton_1.setBounds(10, 211, 166, 42);
        frame.getContentPane().add(btnNewButton_1);
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                RegistrationForm registrationScreen = new RegistrationForm();
            }
        });


        JButton btnNewButton_2 = new JButton("Забравена парола");
        btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton_2.setBounds(370, 211, 167, 42);
        frame.getContentPane().add(btnNewButton_2);
        btnNewButton_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ForgottenPasswordForm forgottenPasswordScreen = new ForgottenPasswordForm();
            }
        });

        frame.setVisible(true);
    }

    public boolean validInput(String username, String encodedPassword) {
        DBManager manager;
        try {
            manager = DBManager.getInstance();
            DataSource dataSource = manager.getDataSource();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name, encryptedPassword FROM customers");
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(username) && (resultSet.getString(2).equals(encodedPassword))) {
                    return true;
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}