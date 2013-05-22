package github.hfdiao.sqlclient;


/**
 * @author dhf
 */
public class MySQLClient extends SQLClient {
    public void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }

    public String makeConnectUrl(String host, int port, String database) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

}
