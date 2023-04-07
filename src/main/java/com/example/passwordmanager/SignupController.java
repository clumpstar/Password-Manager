package com.example.passwordmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignupController {

    Connection con;
    PreparedStatement pst;
    Sha512Hash sha512Hash = new Sha512Hash();

    @FXML
    private Button signupBTN;

    @FXML
    private PasswordField pass_field;

    @FXML
    private TextField pass_text;

    @FXML
    private TextField username;

    @FXML
    private CheckBox checkbtn;

    @FXML
    private Button backBTN;

    @FXML
    void checkFun(ActionEvent event) {
        if(checkbtn.isSelected()){
            pass_text.setText(pass_field.getText());
            pass_text.setVisible(true);
            pass_field.setVisible(false);
            return;
        }
        pass_field.setText(pass_text.getText());
        pass_field.setVisible(true);
        pass_text.setVisible(false);
    }

    @FXML
    void signupbtn(ActionEvent event) {
        Connect();
        int count = 0 ;

        if(username.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid username!");

            alert.showAndWait();
            return;
        }

        if(pass_field.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid password!");

            alert.showAndWait();
            return;
        }

        try
        {

            pst = con.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            pst.setString(1, username.getText());
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                count = rs.getInt(1);
            }

            if(count == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Manager");

                alert.setHeaderText("Database Error");
                alert.setContentText("Username already exists!");

                alert.showAndWait();

                username.clear();
                pass_field.clear();
                username.requestFocus();
                return;
            }

            pst = con.prepareStatement("insert into users(name,password)values(?,?)");
            pst.setString(1, username.getText());
            pst.setString(2, sha512Hash.hashWithSHA512(pass_field.getText()));
            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Record Added!");

            alert.showAndWait();

            //**TABLE CREATION FOR EVERY NEW USER**//
            String tableName = username.getText();

            String sql = "CREATE TABLE " + tableName + " (" +
                    "Username VARCHAR(25) PRIMARY KEY, " +
                    "Password VARCHAR(50) NOT NULL, " +
                    "Description VARCHAR(100)" +
                    ")";

            try {
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                System.out.println("Table created successfully!");

                new log("Table created successfully! for "+username.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Stage window = (Stage) signupBTN.getScene().getWindow();
            window.setScene(new Scene(root,750,500));
        }
        catch(SQLIntegrityConstraintViolationException e){
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "username already exits", e);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Database Error");
            alert.setContentText("Username already exists!");

            alert.showAndWait();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error occurred in the database while adding data into database", ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void backFun(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Stage window = (Stage) backBTN.getScene().getWindow();
        window.setScene(new Scene(root,750,500));
    }

    public void Connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_connection","root","pass");
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}

class Sha512Hash {

    public static String hashWithSHA512(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
