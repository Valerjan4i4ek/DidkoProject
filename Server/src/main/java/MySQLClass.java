import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class MySQLClass {
    private final static String fileName = "database.properties";

    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public MySQLClass(){
        baseCreate();
        tableWordsCreate();
    }

    public Connection getConnection() throws SQLException{
        Properties props = new Properties();
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)){
            if(in != null){
                props.load(in);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }
    public void baseCreate(){
        try{
            Connection conn = null;
            Statement st = null;

            try{
                conn = getConnection();
                st = conn.createStatement();
                st.executeUpdate("CREATE DATABASE IF NOT EXISTS WordsParsing");
            }
            finally {
                connectionClose(conn, st);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void tableWordsCreate(){
        try{
            Connection conn = null;
            Statement st = null;

            try{
                conn = getConnection();
                st = conn.createStatement();
                st.executeUpdate("CREATE TABLE IF NOT EXISTS WordsParsing.words " +
                        "(id INT NOT NULL, wordName VARCHAR(20) NOT NULL, wordCount INT NOT NULL, link LONGTEXT NOT NULL)");
//                        "(id INT NOT NULL, wordName VARCHAR(20) NOT NULL, wordCount INT NOT NULL)");
            }
            finally {
                connectionClose(conn, st);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void addWords(Words words){
        try{
            Connection conn = null;
            PreparedStatement ps = null;

            try{
                conn = getConnection();
//                ps = conn.prepareStatement("INSERT INTO words (id, wordName, wordCount) VALUES (?, ?, ?)");
                ps = conn.prepareStatement("INSERT INTO words (id, wordName, wordCount, link) VALUES (?, ?, ?, ?)");
                ps.setInt(1, words.getId());
                ps.setString(2, words.getWordName());
                ps.setInt(3, words.getWordCount());
//                ps.setString(4, words.getLink());
                ps.executeUpdate();
            } finally {
                connectionClose(conn, ps);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void replaceWord(Words words){
        try{
            Connection conn = null;
            PreparedStatement ps = null;

            try{
                conn = getConnection();
//                ps = conn.prepareStatement("UPDATE words SET id = ?, wordCount = ? WHERE wordName = ?");
                ps = conn.prepareStatement("UPDATE words SET id = ?, wordCount = ?, link = ? WHERE wordName = ?");
                ps.setInt(1, words.getId());
                ps.setInt(2, words.getWordCount());
                ps.setString(3, words.getLink());
                ps.setString(4, words.getWordName());
                ps.executeUpdate();
            } finally {
                connectionClose(conn, ps);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void connectionClose(Connection conn, Statement st){
        try{
            if(conn != null){
                conn.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(st != null){
                st.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
