package cn.linkey.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Email_Autherticator extends Authenticator {

    String username = "";
    String password = "";

    public Email_Autherticator() {
        super();
    }

    public Email_Autherticator(String user, String pwd) {
        super();
        username = user;
        password = pwd;
    }

    //Authenticator是抽象类，必须实现它的getPasswordAuthentication
    //方法返回一个PasswordAuthentication对象
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}