package com.example.passwordmanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PasswordMan
{
    private final StringProperty Username;
    private final StringProperty Password;
    private final StringProperty Description;

    public PasswordMan()
    {
        Username = new SimpleStringProperty(this, "Username");
        Password = new SimpleStringProperty(this, "Password");
        Description = new SimpleStringProperty(this, "Description");
    }


    public StringProperty usernameProperty() { return Username; }
    public String getUsername() { return Username.get(); }
    public void setUsername(String newName) { Username.set(newName); }

    public StringProperty passwordProperty() { return Password; }
    public String getPassword() { return Password.get(); }
    public void setPassword(String newMobile) { Password.set(newMobile); }

    public StringProperty descProperty() { return Description; }
    public String getDesc() { return Description.get(); }
    public void setDesc(String newCourse) { Description.set(newCourse); }
}
