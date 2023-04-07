package com.example.passwordmanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import com.example.passwordmanager.HomeController;

import com.example.passwordmanager.PasswordMan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FXMLDocumentController implements Initializable {


    @FXML
    private Label label;

    @FXML
    private TextField username;

    @FXML
    private TextField pass_text;

    @FXML
    private PasswordField pass_field;

    @FXML
    private TextArea desc;

    @FXML
    private TableView<PasswordMan> Details;

    @FXML
    private TableColumn<PasswordMan,String> descCol;

    @FXML
    private TableColumn<PasswordMan,String> passCol;

    @FXML
    private TableColumn<PasswordMan,String> userCol;


    @FXML
    private CheckBox checker;

    public FXMLDocumentController() throws Exception {
    }

    @FXML
    void Add(ActionEvent event) throws Exception {


        if(username.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid username!");

            alert.showAndWait();
            return;
        }

        if(pass_field.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid password!");

            alert.showAndWait();
            return;
        }

        if(desc.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid description!");

            alert.showAndWait();
            return;
        }

        String Username,Password,description;
        Username = username.getText();
        Password = aesEncryption.encrypt(pass_field.getText());
//        System.out.println(Password);
        description = desc.getText();
        try
        {
//            pst = con.prepareStatement("insert into registration(Username,Password,Description)values(?,?,?)");

            //**ADDING DATA TO THE LOGGED IN USER'S TABLE**//
            String tableName = homeController.name_login;
            String sql = "INSERT INTO " + tableName + " (Username, Password, Description) VALUES (?, ?, ?)";


            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, Username);
            pstmt.setString(2, Password);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            System.out.println("Record inserted successfully!");
            new log(homeController.name_login+ "'s Record inserted successfully!");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Record Added!");

            alert.showAndWait();

            table();

            username.setText("");
            pass_field.setText("");
            desc.setText("");
            username.requestFocus();
        }
        catch(SQLIntegrityConstraintViolationException e){
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "username already exits", e);
            new log("username already exits");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Database Error");
            alert.setContentText("Username already exists!");

            alert.showAndWait();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error occurred in the database while adding data into database", ex);
            new log("Error occurred in the database while adding data into database");
        }
    }


    public void table()
    {

        Connect();
        ObservableList<PasswordMan> students = FXCollections.observableArrayList();

//        System.out.println(homeController.name_login);
//        new log(homeController.name_login+" logged in");

        try
        {

            //**CHANGED THE WAY OF GETTING THE DATA FROM THE TABLE**//
            String tableName = homeController.name_login;
            String sql = "SELECT Username, Password, Description FROM " + tableName;

            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                PasswordMan st = new PasswordMan();
                st.setUsername(rs.getString("Username"));
                st.setPassword(rs.getString("Password"));
                st.setDesc(rs.getString("Description"));
                students.add(st);
            }

            Details.setItems(students);
            userCol.setCellValueFactory(f -> f.getValue().usernameProperty());
            passCol.setCellValueFactory(f -> f.getValue().passwordProperty());
            descCol.setCellValueFactory(f -> f.getValue().descProperty());
            new log(homeController.name_login+ "'s table refreshed successfully!");
        }

        catch (SQLException ex)
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error occurred while setting up table.", ex);
            new log("Error occurred while setting up table.");
        }

        Details.setRowFactory( tv -> {
            TableRow<PasswordMan> myRow = new TableRow<>();
            myRow.setOnMouseClicked (event ->
            {
                if (event.getClickCount() == 1 && (!myRow.isEmpty()))
                {
                    myIndex =  Details.getSelectionModel().getSelectedIndex();
                    username.setText(Details.getItems().get(myIndex).getUsername());
                    try {
                        pass_field.setText(aesEncryption.decrypt(Details.getItems().get(myIndex).getPassword()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    desc.setText(Details.getItems().get(myIndex).getDesc());

                }
            });
            return myRow;
        });


    }

    @FXML
    void Delete(ActionEvent event) {
        myIndex = Details.getSelectionModel().getSelectedIndex();
         id = Details.getItems().get(myIndex).getUsername();

        if(username.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid username!");

            alert.showAndWait();
            return;
        }

        if(pass_field.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid password!");

            alert.showAndWait();
            return;
        }

        if(desc.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid description!");

            alert.showAndWait();
            return;
        }



        try
        {
            String tableName = homeController.name_login;
            String sql = "DELETE FROM " + tableName + " WHERE Username = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully!");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Deleted!");

            alert.showAndWait();
            table();
            new log(homeController.name_login+ "'s Record deleted successfully!");
            username.clear();
            pass_field.clear();
            pass_text.clear();
            desc.clear();
            username.requestFocus();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error occurred while deleting.", ex);
            new log("Error occurred while deleting.");
        }
    }

    @FXML
    void Update(ActionEvent event) throws Exception {

        String Username,Password,description;

        myIndex = Details.getSelectionModel().getSelectedIndex();
        id = Details.getItems().get(myIndex).getUsername();

        Username = username.getText();
        Password = aesEncryption.encrypt(pass_field.getText());
        description = desc.getText();

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

        if(desc.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Please enter a valid description!");

            alert.showAndWait();
            return;
        }


        try
        {
            String tableName = homeController.name_login;
            String sql = "UPDATE " + tableName + " SET Username = ?, Password = ?, Description = ? WHERE Username = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, Username);
            pstmt.setString(2, Password);
            pstmt.setString(3, description);
            pstmt.setString(4, id);
            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
            new log(homeController.name_login+ "'s Record updated successfully!");


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Manager");

            alert.setHeaderText("Password Manager");
            alert.setContentText("Updated!");

            alert.showAndWait();
            table();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error occurred while updating row.", ex);
            new log("Error occurred while updating row.");
        }
    }

    @FXML
    void generateRand(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Please enter length of the password:");
        dialog.setContentText("Length");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();
        pass_field.setText(generateRandomPassword.generatePassword(Integer.parseInt(result.get())));
        new log(homeController.name_login+ "'s random password generated successfully!");
    }

    @FXML
    void checkFun(ActionEvent event){
        if(checker.isSelected()){
            pass_text.setText(pass_field.getText());
            pass_text.setVisible(true);
            pass_field.setVisible(false);
            return;
        }
        pass_field.setText(pass_text.getText());
        pass_field.setVisible(true);
        pass_text.setVisible(false);
    }



    Connection con;
    PreparedStatement pst;
    int myIndex;
    String id;
    AesEncryption aesEncryption = new AesEncryption("thisisakey");

    HomeController homeController = new HomeController();



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



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connect();
        table();
    }

}

class generateRandomPassword{
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*";

    private static final String ALL_CHARACTERS = LOWER_CASE + UPPER_CASE + NUMBERS + SYMBOLS;

    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();

        // Initialize an array list with all the characters to be used in the password
        ArrayList<Character> allCharactersList = new ArrayList<>();
        for (char c : ALL_CHARACTERS.toCharArray()) {
            allCharactersList.add(c);
        }

        // Shuffle the characters to ensure randomization
        Collections.shuffle(allCharactersList, random);

        // Generate the password by picking characters randomly from the shuffled list
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allCharactersList.size());
            passwordBuilder.append(allCharactersList.get(randomIndex));
        }

        return passwordBuilder.toString();
    }
}


class AesEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static String key;

    AesEncryption(String key){
        this.key = String.format("%-16s", key).replace(' ', '\0');
    }

    public static String encrypt(String input) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encrypted) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decryptedBytes);
    }
}
 class log{
    private static final Logger logger = Logger.getLogger("MyLog");


    log(String text) {
        FileHandler fh;
        try {
            fh = new FileHandler("mylog.log",true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.info(text);

            fh.close();
        } catch (Exception e) {

            logger.warning("Failed to create log file: " + e.getMessage());
        }


    }
}




