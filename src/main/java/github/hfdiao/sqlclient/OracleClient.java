package github.hfdiao.sqlclient;

/**
 * @author dhf
 */
public class OracleClient extends SQLClient {
    private boolean driverLoaded = false;

    @Override
    public boolean driverLoaded() {
        return driverLoaded;
    }

    public void loadDriver() throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        driverLoaded = true;
    }

    public String makeConnectUrl(String host, int port, String database) {
        return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
    }
}
