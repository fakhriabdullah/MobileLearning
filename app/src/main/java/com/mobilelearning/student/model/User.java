package com.mobilelearning.student.model;

/**
 * Created by Taofik Muhammad on 22/10/2016.
 */
public class User {
    private int user_id;
    private String username;
    private String email;
    private String full_name;
    private int userType;

    public void setUserId(int user_id)
    {
        this.user_id=user_id;
    }

    public int getUserId()
    {
        return this.user_id;
    }

    public void setUsername(String username)
    {
        this.username=username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setFullName(String full_name)
    {
        this.full_name=full_name;
    }

    public String getFullName()
    {
        return this.full_name;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
