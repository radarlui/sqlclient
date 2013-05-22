package github.hfdiao.sqlclient;

/**
 *
 * @author dhf
 *
 */
public class OracleClient extends SQLClient{
    public void loadDriver() throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }
    
    public String makeConnectUrl(String host, int port,
            String database) {
        return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
    }
}
