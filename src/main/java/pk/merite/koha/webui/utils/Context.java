package pk.merite.koha.webui.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


/**
 * @author MohsinA
 *
 */

public class Context {
    
    protected Connection connection;
    protected Properties properties;
    
    public Connection openConnection() throws ClassNotFoundException, IOException, SQLException {
        if(connection == null) {
            Class.forName("org.gjt.mm.mysql.Driver");
            String hostname = getProperty("hostname");
            String database = getProperty("database");
            String user = getProperty("user");
            String password = getProperty("pass");
            if(hostname.indexOf(':') < 0) {
                String port = getProperty("port");
                if(port == null) {
                    port = "3306";
                }
                hostname = hostname + ":" + port; 
            }
            connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + database, user, password);
        }
        return connection;
    }
    
    public void closeConnection() throws SQLException {
        if(connection != null) {
            connection.close();
            connection = null;
        }
    }
    
    public PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException, IOException {
        return openConnection().prepareStatement(sql);
    }
    
    public String getProperty(String key) throws IOException {
        if(properties == null) {
            properties = new Properties();
            if("Y".equals(System.getProperty("login.view"))) {
                properties = System.getProperties();
            } else {
                properties.load(new FileInputStream("/etc/koha.conf"));
            }
        }
        return properties.getProperty(key);
    }    
}
