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

public class HomeController {

    Connection con;
    PreparedStatement pst;
    Sha512Hashing sha512Hashing = new Sha512Hashing();
    PasswordMan passwordMan = new PasswordMan();

    static String name_login;

    @FXML
    private CheckBox check_pass;

    @FXML
    private PasswordField pass_field;

    @FXML
    private TextField pass_text;

    @FXML
    private TextField username;

    @FXML
    private Button loginBTN;

    @FXML
    private Button signupBTN;

    @FXML
    private Button remBTN;

    @FXML
    void loginbtn(ActionEvent event) {

        Connect();

        int count=0;
        String pass="";

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

        try{
            pst = con.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            pst.setString(1, username.getText());
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                count = rs.getInt(1);
            }

            if(count == 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Manager");

                alert.setHeaderText("Database Error");
                alert.setContentText("Username does not exists!");

                alert.showAndWait();

                username.clear();
                pass_field.clear();
                username.requestFocus();
                return;
            }
            if(count == 1){

                pst = con.prepareStatement("SELECT * FROM users WHERE name = ?");
                pst.setString(1, username.getText());
                ResultSet rs1 = pst.executeQuery();


                while(rs1.next()){
                    if(sha512Hashing.hashWithSHA512(pass_field.getText()).equals(rs1.getString("password"))){

//                        passwordMan.setUsername(username.getText());

                        //**GETTING THE NAME OF THE USER WHILE LOGGING IN**//
                        name_login = username.getText();
//                        System.out.println(name_login);

                        new log(name_login+ "logged in!");

                        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                        Stage window = (Stage) loginBTN.getScene().getWindow();
                        window.setScene(new Scene(root,750,500));
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void signupbtn(ActionEvent event) throws IOException {
        Connect();
        Parent root = FXMLLoader.load(getClass().getResource("Signup.fxml"));
        Stage window = (Stage) loginBTN.getScene().getWindow();
        window.setScene(new Scene(root,750,500));
    }

    @FXML
    void rembtn(ActionEvent event) {
        Connect();

        int count = 0;

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
        try{
            pst = con.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            pst.setString(1, username.getText());
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                count = rs.getInt(1);
            }

            if(count == 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Manager");

                alert.setHeaderText("Database Error");
                alert.setContentText("Username does not exists!");

                alert.showAndWait();

                username.clear();
                pass_field.clear();
                username.requestFocus();
                return;
            }
            if(count == 1){

                pst = con.prepareStatement("DELETE FROM users WHERE name = ?");
                pst.setString(1, username.getText());
                pst.executeUpdate();

                String tableName = username.getText();
                PreparedStatement pst = con.prepareStatement("DROP TABLE IF EXISTS " + tableName);
                pst.executeUpdate();


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Manager");

                alert.setHeaderText("Password Manager");
                alert.setContentText("Removed the user!");

                alert.showAndWait();
                new log(username.getText()+" removed successfully");
                username.clear();
                pass_field.clear();
                username.requestFocus();

                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void checkFun(ActionEvent event) {
        if(check_pass.isSelected()){
            pass_text.setText(pass_field.getText());
            pass_text.setVisible(true);
            pass_field.setVisible(false);
            return;
        }
        pass_field.setText(pass_text.getText());
        pass_field.setVisible(true);
        pass_text.setVisible(false);
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

class Sha512Hashing {

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
