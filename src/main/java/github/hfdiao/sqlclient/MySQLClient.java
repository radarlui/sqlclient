package github.hfdiao.sqlclient;

/**
 * @author dhf
 */
public class MySQLClient extends SQLClient {
    private boolean driverLoaded = false;

    @Override
    public boolean driverLoaded() {
        return driverLoaded;
    }

    public void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        driverLoaded = true;
    }

    public String makeConnectUrl(String host, int port, String database) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

}
