/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConnectionBD;

/**
 *
 * @author franc
 */
import EnviromentVariables.AppConfig;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ConsultaBD {

    private Connection conn;

    public void conectar() {
        try {
            conn = DriverManager.getConnection(AppConfig.DB_URL, AppConfig.DB_USERNAME, AppConfig.DB_PASSWORD);
            System.out.println("Conexion exitosa");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public DefaultTableModel cargaDataGrid() {
        DefaultTableModel model = new DefaultTableModel();
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            String sql = "SELECT * FROM vista_estudiantes"; // Utiliza el nombre de la vista en lugar de la consulta completa
            statement = conn.prepareStatement(sql);
            result = statement.executeQuery();

            // Obtener los metadatos de la consulta
            ResultSetMetaData metaData = result.getMetaData();

            // Obtener el número de columnas
            int columnCount = metaData.getColumnCount();

            // Agregar las columnas al modelo usando los nombres de las columnas de los metadatos
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Llenar el modelo con los datos de la consulta
            while (result.next()) {
                // Crear un arreglo para almacenar los valores de cada fila
                Object[] rowData = new Object[columnCount];

                // Obtener los valores de cada columna y agregarlos al arreglo
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = result.getObject(i);
                }

                // Agregar la fila al modelo
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return model;
    }

    public boolean verificarCredenciales(String username, String password) {
        boolean credencialesValidas = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM Users WHERE username = ? AND password_user = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            // Verificar si se encontró una coincidencia en la base de datos
            if (rs.next()) {
                credencialesValidas = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt);
        }
        return credencialesValidas;
    }

    public boolean eliminarUsuario(String cedula) {
        boolean eliminacionExitosa = false;
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM estudiantes WHERE Cedula = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cedula);

            // Ejecutar la consulta de eliminación
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                eliminacionExitosa = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, stmt);
        }
        return eliminacionExitosa;
    }

    public boolean agregarUsuario(String username, String password) {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO Users (username, password_user) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Ejecutar la inserción
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            // Cerrar la declaración
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean modificarDatosEstudiante(String nombre, String apellido, String edad, String cedula, String estado_civil_id, String documento_id, String estado_id) {
        PreparedStatement stmt = null;

        try {
            // Conectar a la base de datos
            conectar();

            // Preparar la consulta SQL
            String query = "UPDATE estudiantes SET nombre = ?, apellido = ?, edad = ?, estado_civil_id = ?, documento_id = ?, estado_id = ? WHERE cedula = ?";
            stmt = conn.prepareStatement(query);

            // Establecer los valores de los parámetros
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, edad);
            stmt.setString(4, estado_civil_id);
            stmt.setString(5, documento_id);
            stmt.setString(6, estado_id);
            stmt.setString(7, cedula);

            // Ejecutar la consulta
            int filasAfectadas = stmt.executeUpdate();

            // Verificar si se modificaron los datos correctamente
            if (filasAfectadas > 0) {
                return true; // Se modificaron los datos correctamente
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar los recursos
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false; // No se pudieron modificar los datos
    }

    public boolean agregarEstudiante(String nombre, String Apellido, String Edad, String Cedula, String estado_civil_id, String documento_id, String estado_id) {
        CallableStatement cstmt = null;
        boolean listo = false;

        try {

            String sql = "{CALL ingresarAlumno(?, ?, ?, ?, ?, ?, ?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, nombre);
            cstmt.setString(2, Apellido);
            cstmt.setString(3, Edad);
            cstmt.setString(4, Cedula);
            cstmt.setString(5, estado_civil_id);
            cstmt.setString(6, documento_id);
            cstmt.setString(7, estado_id);

            // Ejecutar la consulta de inserción
            cstmt.execute();

            listo = true; // Suponemos que la inserción fue exitosa si no hubo excepciones
        } catch (SQLException ex) {
            ex.printStackTrace();  // Imprimir el stack trace para depuración
            System.out.println("Error en la inserción: " + ex.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Error de formato en los datos proporcionados: " + e.getMessage());
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return listo;
    }

    public boolean verificarExistenciaPorCedula(String cedula) {
        boolean existe = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) FROM estudiantes WHERE Cedula = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cedula);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                existe = count > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt);
        }
        return existe;
    }

    public String[] buscarEstudiantePorCedula(String cedula) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Conectar a la base de datos
            conectar();

            // Preparar la consulta SQL
            String query = "{CALL BuscarAlumno(?)}";
            stmt = conn.prepareStatement(query);

            // Establecer el valor del parámetro
            stmt.setString(1, cedula);

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            // Verificar si se encontraron resultados
            if (rs.next()) {
                // Crear un arreglo de strings para almacenar los valores encontrados
                String[] valoresEstudiante = new String[7];

                // Obtener los valores de las columnas y guardarlos en el arreglo
                valoresEstudiante[0] = rs.getString("cedula");
                valoresEstudiante[1] = rs.getString("nombre");
                valoresEstudiante[2] = rs.getString("apellido");
                valoresEstudiante[3] = rs.getString("edad");
                valoresEstudiante[4] = rs.getString("EstadoCivilID");
                valoresEstudiante[5] = rs.getString("DocumentoID");
                valoresEstudiante[6] = rs.getString("EstadoID");

                return valoresEstudiante;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar los recursos
            try {
                cerrarRecursos(rs, stmt);
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null; // No se encontró ningún estudiante
    }

    private void cerrarRecursos(ResultSet rs, PreparedStatement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
