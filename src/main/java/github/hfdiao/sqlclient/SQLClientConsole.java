package github.hfdiao.sqlclient;

import java.io.EOFException;
import java.io.File;
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
import java.util.regex.Pattern;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;

/**
 * @author dhf
 */
public class SQLClientConsole {
    private static final String HISTORY_FILE = ".sqlhistory";

    public static void main(String[] args) throws ClassNotFoundException,
            EOFException, UnsupportedEncodingException, IOException,
            SQLException {
        if (args.length != 6) {
            showHelp();
            return;
        }

        int i = -1;
        String dbtype = args[++i];
        String host = args[++i];
        int port = Integer.parseInt(args[++i]);
        String database = args[++i];
        String username = args[++i];
        String password = args[++i];

        ConsoleReader reader = new ConsoleReader();
        reader.setHistoryEnabled(true);
        FileHistory history = initHistory(HISTORY_FILE);
        if (null != history) {
            reader.setHistory(history);
        }

        SQLClient client = makeClient(dbtype);
        Connection conn = client.getConnection(host, port, database, username,
                password);
        try {
            StringBuilder buffer = new StringBuilder();
            while (true) {
                String line = null;
                if (buffer.length() == 0) {
                    line = reader.readLine("sql>");
                } else {
                    line = reader.readLine("   >");
                }
                if (null == line || "".equals(line.trim())) {
                    continue;
                }

                buffer.append(line);
                if (buffer.charAt(buffer.length() - 1) == '\\') {
                    buffer.setCharAt(buffer.length() - 1, ' ');
                    continue;
                }
                String sql = buffer.toString();
                buffer.setLength(0);

                if (isExitCommand(sql)) {
                    exit();
                    break;
                }
                try {
                    Object result = client.doSql(conn, sql);
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
                                    + sql);
                    e.printStackTrace();
                }
                if (null != history) {
                    history.flush();
                }
            }
        } finally {
            closeConnection(conn);
        }
    }

    private static void showHelp() {
        System.out.println("usage: " + SQLClientConsole.class.getSimpleName()
                + "dbtype host port database username [password]");
        System.out.println("example: " + SQLClientConsole.class.getSimpleName()
                + " mysql 127.0.0.1 3306 mydatabase myusername mypassword");
        StringBuilder dbtypes = new StringBuilder();
        for (String dbtype: CLIENT_MAP.keySet()) {
            if (dbtypes.length() != 0) {
                dbtypes.append(", ");
            }
            dbtypes.append(dbtype);
        }
        System.out.println("supported dbtypes: " + dbtypes);
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

    private static FileHistory initHistory(String historyFilePath)
            throws IOException {
        File historyFile = new File(historyFilePath);
        if (!historyFile.exists()) {
            File parent = historyFile.getAbsoluteFile().getParentFile();
            if (null == parent) {
                return null;
            }
            if (!parent.exists() && !parent.mkdirs()) {
                return null;
            }
            if (!historyFile.createNewFile()) {
                return null;
            }
        }
        if (historyFile.isDirectory() || !historyFile.canRead()
                || !historyFile.canWrite()) {
            return null;
        }

        return new FileHistory(historyFile);
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

    private static final Pattern EXIT_COMMAND = Pattern.compile(
            "\\s*(quit|exit|bye)[;\\s]*", Pattern.CASE_INSENSITIVE);

    private static boolean isExitCommand(String command) {
        if (null == command) {
            return false;
        }
        return EXIT_COMMAND.matcher(command).matches();
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
