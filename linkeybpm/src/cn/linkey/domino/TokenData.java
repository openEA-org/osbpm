package cn.linkey.domino;

import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class TokenData {
    public static final SimpleTimeZone utcTimeZone = new SimpleTimeZone(0, "UTC");

    public String username;
    public GregorianCalendar tokenCreated;
    public GregorianCalendar tokenExpiration;

    public TokenData() {
        username = "";
        tokenCreated = new GregorianCalendar(utcTimeZone);
        tokenCreated.setTimeInMillis(0);
        tokenExpiration = new GregorianCalendar(utcTimeZone);
        tokenExpiration.setTimeInMillis(0);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("[ username:").append(username).append(", tokenCreated: ").append(tokenCreated.getTime().toString());
        buf.append(", tokenExpiration: ").append(tokenExpiration.getTime().toString()).append(" ]");

        return buf.toString();
    }
}
