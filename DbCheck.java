import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DbCheck {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String pass = "z&ntr@SIF&N";

        try {
            System.out.println("Intentando conectar a PostgreSQL...");
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Éxito!");
            ResultSet rs = conn.createStatement().executeQuery("SELECT version()");
            while (rs.next()) {
                System.out.println("Versión de PostgreSQL: " + rs.getString(1));
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("FALLO EN LA CONEXIÓN:");
            e.printStackTrace();
        }
    }
}
