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

    //conexion a la base de datos
    public void conectar() {
        try {
            conn = DriverManager.getConnection(AppConfig.DB_URL, AppConfig.DB_USERNAME, AppConfig.DB_PASSWORD);
            System.out.println("Conexion exitosa");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Carga los datos de la vista 'vista_estudiantes' en un DefaultTableModel.
     *
     * @return DefaultTableModel con los datos de la vista 'vista_estudiantes'.
     */
    public DefaultTableModel cargaDataGrid() {
        // Crear un modelo de tabla por defecto para almacenar los datos de la consulta
        DefaultTableModel model = new DefaultTableModel();
        PreparedStatement statement = null; // Inicializar el PreparedStatement
        ResultSet result = null; // Inicializar el ResultSet

        try {
            // Definir la consulta SQL para seleccionar todos los datos de la vista 'vista_estudiantes'
            String sql = "SELECT * FROM vista_estudiantes";
            statement = conn.prepareStatement(sql); // Preparar la consulta
            result = statement.executeQuery(); // Ejecutar la consulta y obtener los resultados

            // Obtener los metadatos de los resultados de la consulta
            ResultSetMetaData metaData = result.getMetaData();

            // Obtener el número de columnas en los resultados
            int columnCount = metaData.getColumnCount();

            // Agregar las columnas al modelo usando los nombres de las columnas obtenidos de los metadatos
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Llenar el modelo con los datos obtenidos de la consulta
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
            e.printStackTrace(); // Manejar cualquier excepción SQL que ocurra
        } finally {
            // Cerrar los recursos en el bloque 'finally' para asegurarse de que siempre se cierren
            try {
                if (result != null) {
                    result.close(); // Cerrar el ResultSet
                }
                if (statement != null) {
                    statement.close(); // Cerrar el PreparedStatement
                }
                if (conn != null) {
                    conn.close(); // Cerrar la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Manejar cualquier excepción SQL que ocurra durante el cierre de recursos
            }
        }

        return model; // Devolver el modelo de tabla lleno con los datos de la consulta
    }

    /**
     * Verifica las credenciales de un usuario en la base de datos.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    public boolean verificarCredenciales(String username, String password) {
        boolean credencialesValidas = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Definir la consulta SQL para verificar las credenciales del usuario
            String sql = "SELECT * FROM Users WHERE username = ? AND password_user = ?";
            stmt = conn.prepareStatement(sql); // Preparar la consulta
            stmt.setString(1, username); // Establecer el nombre de usuario en la consulta
            stmt.setString(2, password); // Establecer la contraseña en la consulta

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            // Verificar si se encontró una coincidencia en la base de datos
            if (rs.next()) {
                credencialesValidas = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt); // Cerrar los recursos utilizados
        }
        return credencialesValidas;
    }

    /**
     * Elimina un usuario de la base de datos utilizando su cédula.
     *
     * @param cedula La cédula del usuario a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarUsuario(String cedula) {
        boolean eliminacionExitosa = false;
        PreparedStatement stmt = null;
        try {
            // Definir la consulta SQL para eliminar al usuario por su cédula
            String sql = "DELETE FROM estudiantes WHERE Cedula = ?";
            stmt = conn.prepareStatement(sql); // Preparar la consulta
            stmt.setString(1, cedula); // Establecer la cédula en la consulta

            // Ejecutar la consulta de eliminación
            int filasAfectadas = stmt.executeUpdate();

            // Verificar si se afectó alguna fila en la base de datos
            if (filasAfectadas > 0) {
                eliminacionExitosa = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, stmt); // Cerrar los recursos utilizados
        }
        return eliminacionExitosa;
    }

    /**
     * Agrega un nuevo usuario a la base de datos.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean agregarUsuario(String username, String password) {
        PreparedStatement stmt = null;
        try {
            // Definir la consulta SQL para insertar un nuevo usuario
            String sql = "INSERT INTO Users (username, password_user) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql); // Preparar la consulta
            stmt.setString(1, username); // Establecer el nombre de usuario en la consulta
            stmt.setString(2, password); // Establecer la contraseña en la consulta

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

    /**
     * Modifica los datos de un estudiante en la base de datos.
     *
     * @param nombre El nuevo nombre del estudiante.
     * @param apellido El nuevo apellido del estudiante.
     * @param edad La nueva edad del estudiante.
     * @param cedula La cédula del estudiante.
     * @param estado_civil_id El nuevo estado civil del estudiante.
     * @param documento_id El nuevo tipo de documento del estudiante.
     * @param estado_id El nuevo estado del estudiante.
     * @return true si los datos fueron modificados correctamente, false en caso
     * contrario.
     */
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

    /**
     * Agrega un nuevo estudiante a la base de datos utilizando un procedimiento
     * almacenado.
     *
     * @param nombre El nombre del estudiante.
     * @param Apellido El apellido del estudiante.
     * @param Edad La edad del estudiante.
     * @param Cedula La cédula del estudiante.
     * @param estado_civil_id El estado civil del estudiante.
     * @param documento_id El tipo de documento del estudiante.
     * @param estado_id El estado del estudiante.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean agregarEstudiante(String nombre, String Apellido, String Edad, String Cedula, String estado_civil_id, String documento_id, String estado_id) {
        CallableStatement cstmt = null;
        boolean listo = false;

        try {
            // Definir la llamada al procedimiento almacenado para insertar un nuevo estudiante
            String sql = "{CALL ingresarAlumno(?, ?, ?, ?, ?, ?, ?)}";
            cstmt = conn.prepareCall(sql); // Preparar la llamada al procedimiento almacenado
            cstmt.setString(1, nombre); // Establecer el nombre del estudiante
            cstmt.setString(2, Apellido); // Establecer el apellido del estudiante
            cstmt.setString(3, Edad); // Establecer la edad del estudiante
            cstmt.setString(4, Cedula); // Establecer la cédula del estudiante
            cstmt.setString(5, estado_civil_id); // Establecer el estado civil del estudiante
            cstmt.setString(6, documento_id); // Establecer el tipo de documento del estudiante
            cstmt.setString(7, estado_id); // Establecer el estado del estudiante

            // Ejecutar la consulta de inserción
            cstmt.execute();

            listo = true; // Suponemos que la inserción fue exitosa si no hubo excepciones
        } catch (SQLException ex) {
            ex.printStackTrace(); // Imprimir el stack trace para depuración
            System.out.println("Error en la inserción: " + ex.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Error de formato en los datos proporcionados: " + e.getMessage());
        } finally {
            // Cerrar los recursos
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

    /**
     * Verifica si un estudiante con la cédula proporcionada existe en la base
     * de datos.
     *
     * @param cedula La cédula del estudiante.
     * @return true si el estudiante existe, false en caso contrario.
     */
    public boolean verificarExistenciaPorCedula(String cedula) {
        boolean existe = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Definir la consulta SQL para contar los estudiantes con la cédula proporcionada
            String sql = "SELECT COUNT(*) FROM estudiantes WHERE Cedula = ?";
            stmt = conn.prepareStatement(sql); // Preparar la consulta
            stmt.setString(1, cedula); // Establecer la cédula en la consulta

            // Ejecutar la consulta
            rs = stmt.executeQuery();

            // Verificar si el resultado contiene algún registro
            if (rs.next()) {
                int count = rs.getInt(1);
                existe = count > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt); // Cerrar los recursos utilizados
        }
        return existe;
    }

    /**
     * Busca un estudiante en la base de datos utilizando su cédula.
     *
     * @param cedula La cédula del estudiante a buscar.
     * @return Un arreglo de strings con los valores del estudiante encontrado,
     * o null si no se encontró ningún estudiante.
     */
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

    /**
     * Cierra los recursos de ResultSet y PreparedStatement.
     *
     * @param rs El ResultSet a cerrar.
     * @param stmt El PreparedStatement a cerrar.
     */
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
