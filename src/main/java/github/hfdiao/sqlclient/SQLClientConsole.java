package github.hfdiao.sqlclient;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jline.console.ConsoleReader;

/**
 * @author dhf
 */
public class SQLClientConsole {
    public static void main(String[] args) throws ClassNotFoundException,
            EOFException, UnsupportedEncodingException, IOException,
            SQLException {
        showHelp();

        ConsoleReader reader = new ConsoleReader();
        reader.setHistoryEnabled(true);
        String[] splits = reader.readLine("connect>").split(" ");
        if (splits.length != 6) {
            showHelp();
            return;
        }

        int i = -1;
        String dbtype = splits[++i];
        String host = splits[++i];
        int port = Integer.parseInt(splits[++i]);
        String database = splits[++i];
        String username = splits[++i];
        String password = splits[++i];

        SQLClient client = makeClient(dbtype);
        Connection conn = client.getConnection(host, port, database, username,
                password);
        try {
            reader.getHistory().clear();
            while (true) {
                String line = reader.readLine("sql>");
                if ("quit".equalsIgnoreCase(line)
                        || "bye".equalsIgnoreCase(line)
                        || "exit".equalsIgnoreCase(line)) {
                    exit();
                    break;
                }
                try {
                    Object result = client.doSql(conn, line);
                    if (result instanceof Integer) {
                        System.out.println("affected lines: " + result);
                    } else if (result instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> list = (List<Map<String, Object>>) result;
                        for (Map<String, Object> map: list) {
                            System.out.println(map);
                        }
                    } else {
                        System.out.println(result);
                    }
                } catch (Exception e) {
                    System.out
                            .println("execute sql fail, please check your sql statement: "
                                    + line);
                    e.printStackTrace();
                }
            }
        } finally {
            closeConnection(conn);
        }
    }

    private static void showHelp() {
        System.out.println("usage:");
        System.out.println("\t dbtype host port database username password");
        System.out.println("example:");
        System.out
                .println("\t mysql 127.0.0.1 3306 mydatabase myusername mypassword");
        System.out.println("dbtypes:");
        StringBuilder dbtypes = new StringBuilder();
        for (String dbtype: CLIENT_MAP.keySet()) {
            if (dbtypes.length() != 0) {
                dbtypes.append(", ");
            }
            dbtypes.append(dbtype);
        }

        System.out.println("\t " + dbtypes);
    }

    private static Map<String, SQLClient> loadClientMap() throws IOException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        InputStream in = ClassLoader
                .getSystemResourceAsStream("sqlclients.txt");
        Map<String, SQLClient> clientMap = new HashMap<String, SQLClient>();
        try {
            if (null != in) {
                Properties p = new Properties();
                p.load(in);

                for (Entry<Object, Object> entry: p.entrySet()) {
                    String dbtype = ((String) entry.getKey()).trim();
                    String className = ((String) entry.getValue()).trim();

                    clientMap.put(dbtype, (SQLClient) Class.forName(className)
                            .newInstance());
                }
            }
            return clientMap;
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    private static SQLClient getSQLClient(String dbtype) {
        return CLIENT_MAP.get(dbtype);
    }

    private static SQLClient makeClient(String dbtype)
            throws ClassNotFoundException {
        SQLClient client = getSQLClient(dbtype);
        if (null == client) {
            throw new IllegalArgumentException("unknow db type: " + dbtype);
        }
        if (!client.driverLoaded()) {
            client.loadDriver();
        }
        return client;
    }

    private static void exit() {
        System.exit(0);
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, SQLClient> CLIENT_MAP;
    static {
        try {
            CLIENT_MAP = loadClientMap();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
