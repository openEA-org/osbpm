package cn.linkey.domino;

public class LtpaData {
    public String ltpaSecret;
    public String tokenDomain;
    public int tokenExpiration;

    public LtpaData() {
        ltpaSecret = "";
        tokenDomain = "";
        tokenExpiration = 0;
    }

    public LtpaData(String ltpaSecret, int tokenExpiration, String tokenDomain) {
        super();
        this.ltpaSecret = ltpaSecret;
        this.tokenExpiration = tokenExpiration;
        this.tokenDomain = tokenDomain;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LTPAData {Secret=" + ltpaSecret + ", expiration=" + tokenExpiration + ", domain=" + tokenDomain + "}";
    }
}
