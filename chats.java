package com.example.anil.chat;

/**
 * Created by anil3 on 18-04-2018.
 */

public class chats
{

    private String user_status;
    public  chats()
    {

    }

    public chats(String user_status)
    {
        this.user_status = user_status;
    }

    public String getUser_status()
    {
        return user_status;
    }

    public void setUser_status(String user_status)
    {
        this.user_status = user_status;
    }
}
