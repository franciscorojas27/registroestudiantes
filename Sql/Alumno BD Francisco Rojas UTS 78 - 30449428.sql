-- Este script crea una base de datos que se implementó en el software de registro de alumnos para la actividad de programación.
-- Elimina la base de datos 'RegistroEstudiante' si ya existe.
DROP DATABASE IF EXISTS RegistroEstudiante;

-- Crea una nueva base de datos 'RegistroEstudiante'.
CREATE DATABASE RegistroEstudiante;

-- Utiliza la base de datos 'RegistroEstudiante'.
USE RegistroEstudiante;

-- Crea la tabla 'Users' para almacenar información de usuarios.
CREATE TABLE Users (
  id_user INT AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_user VARCHAR(50) NOT NULL,
  PRIMARY KEY (id_user)
);

-- Crea la tabla 'estado_civil' para almacenar diferentes estados civiles.
CREATE TABLE estado_civil (
  estado_civil_id INT PRIMARY KEY AUTO_INCREMENT,
  Estado_Name VARCHAR(50) NOT NULL
);

-- Crea la tabla 'documentos' para almacenar diferentes tipos de documentos.
CREATE TABLE documentos (
  documento_id INT PRIMARY KEY AUTO_INCREMENT,
  Document_Name VARCHAR(50) NOT NULL
);

-- Crea la tabla 'estados' para almacenar diferentes estados/regiones.
CREATE TABLE estados (
  estado_id INT PRIMARY KEY AUTO_INCREMENT,
  Estado_Name VARCHAR(50) NOT NULL
);

-- Crea la tabla 'logs' para almacenar registros de eventos en el sistema.
CREATE TABLE logs (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crea la tabla 'estudiantes' para almacenar información de estudiantes.
-- Incluye claves foráneas para relacionar con las tablas 'estado_civil', 'documentos' y 'estados'.
CREATE TABLE estudiantes (
  estudiante_id INT PRIMARY KEY AUTO_INCREMENT,
  Nombre VARCHAR(50) NOT NULL,
  Apellido VARCHAR(50) NOT NULL,
  Edad INT NOT NULL,
  Cedula INT(8) NOT NULL,
  estado_civil_id INT,
  documento_id INT,
  estado_id INT,
  FOREIGN KEY (estado_civil_id) REFERENCES estado_civil(estado_civil_id),
  FOREIGN KEY (documento_id) REFERENCES documentos(documento_id),
  FOREIGN KEY (estado_id) REFERENCES estados(estado_id)
);

-- Inserta valores predeterminados en la tabla 'estado_civil'.
START TRANSACTION;
BEGIN;
    INSERT INTO estado_civil (Estado_Name)
    VALUES 
      ('Soltero'),
      ('Casado'),
      ('Divorciado'),
      ('Viudo');
COMMIT;

-- Inserta valores predeterminados en la tabla 'documentos'.
START TRANSACTION;
BEGIN;
    INSERT INTO documentos (Document_Name) 
    VALUES 
    ('Copia Cedula'), 
    ('Titulo Bachiller'),
    ('Notas Certificadas');
COMMIT;

-- Inserta valores predeterminados en la tabla 'estados'.
START TRANSACTION;
BEGIN;
    INSERT INTO estados (Estado_Name)
    VALUES 
      ('Distrito Capital'),
      ('Miranda'),
      ('Zulia'),
      ('Carabobo'),
      ('Merida');
COMMIT;

-- Crea una vista 'vista_estudiantes' que muestra la información combinada de los estudiantes y sus relaciones.
CREATE VIEW vista_estudiantes AS
SELECT e.Nombre, e.Apellido, e.Edad, e.Cedula, ec.Estado_Name AS EstadoCivil, d.Document_Name AS Documento, es.Estado_Name AS Estado
FROM estudiantes e
JOIN estado_civil ec ON e.estado_civil_id = ec.estado_civil_id
JOIN documentos d ON e.documento_id = d.documento_id
JOIN estados es ON e.estado_id = es.estado_id;

-- Crea un procedimiento almacenado 'ingresarAlumno' para insertar un nuevo estudiante en la tabla 'estudiantes'.
DELIMITER //
CREATE PROCEDURE ingresarAlumno(
    IN p_Nombre VARCHAR(50),
    IN p_Apellido VARCHAR(50),
    IN p_Edad INT,
    IN p_Cedula INT,
    IN p_estado_civil_id INT,
    IN p_documento_id INT,
    IN p_estado_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;
    INSERT INTO estudiantes (Nombre, Apellido, Edad, Cedula, estado_civil_id, documento_id, estado_id)
    VALUES (p_Nombre, p_Apellido, p_Edad, p_Cedula, p_estado_civil_id, p_documento_id, p_estado_id);
    COMMIT;
END //
DELIMITER ;

-- Crea un procedimiento almacenado 'BuscarAlumno' para buscar un estudiante por su cédula en la tabla 'estudiantes'.
DELIMITER //
CREATE PROCEDURE BuscarAlumno(
    IN p_Cedula INT
)
BEGIN
    DECLARE tmp_Nombre VARCHAR(50);
    DECLARE tmp_Apellido VARCHAR(50);
    DECLARE tmp_Edad INT;
    DECLARE tmp_Cedula INT;
    DECLARE tmp_estado_civil_id INT;
    DECLARE tmp_documento_id INT;
    DECLARE tmp_estado_id INT;
    
    SELECT Nombre, Apellido, Edad, Cedula, estado_civil_id, documento_id, estado_id
    INTO tmp_Nombre, tmp_Apellido, tmp_Edad, tmp_Cedula, tmp_estado_civil_id, tmp_documento_id, tmp_estado_id
    FROM estudiantes
    WHERE Cedula = p_Cedula;

    SELECT tmp_Nombre AS Nombre, tmp_Apellido AS Apellido, tmp_Edad AS Edad, tmp_Cedula AS Cedula, 
           tmp_estado_civil_id AS EstadoCivilID, tmp_documento_id AS DocumentoID, tmp_estado_id AS EstadoID;
END //
DELIMITER ;

-- Crea un trigger 'after_estudiantes_insert' para insertar un registro en la tabla 'logs' después de un insert en 'estudiantes'.
DELIMITER //
CREATE TRIGGER after_estudiantes_insert
AFTER INSERT ON estudiantes
FOR EACH ROW
BEGIN
    INSERT INTO logs (description, created_at) VALUES (CONCAT('Insertado estudiante ID: ', NEW.estudiante_id), NOW());
END //
DELIMITER ;

-- Crea un trigger 'after_estudiantes_delete' para insertar un registro en la tabla 'logs' después de un delete en 'estudiantes'.
DELIMITER //
CREATE TRIGGER after_estudiantes_delete
AFTER DELETE ON estudiantes
FOR EACH ROW
BEGIN
    INSERT INTO logs (description, created_at) VALUES (CONCAT('Eliminado estudiante ID: ', OLD.estudiante_id), NOW());
END //
DELIMITER ;

-- Inserciones de prueba usando transacciones
START TRANSACTION;
BEGIN;
    -- Llamadas de prueba al procedimiento 'ingresarAlumno' para insertar varios estudiantes.
    CALL ingresarAlumno('Test', 'Student', 25, 99999999, 1, 1, 1);
    CALL ingresarAlumno('Juan', 'Perez', 20, 12345678, 1, 1, 1);
    CALL ingresarAlumno('Maria', 'Lopez', 22, 87654321, 2, 2, 2);
    CALL ingresarAlumno('Pedro', 'Gomez', 18, 65432187, 1, 3, 3);
    CALL ingresarAlumno('Laura', 'Gonzalez', 21, 98765432, 2, 1, 4);
    CALL ingresarAlumno('Carlos', 'Rodriguez', 19, 54321678, 1, 2, 5);
    CALL ingresarAlumno('Ana', 'Martinez', 23, 23456789, 3, 3, 1);
    CALL ingresarAlumno('Luis', 'Garcia', 20, 34567890, 4, 1, 2);
    CALL ingresarAlumno('Sofia', 'Fernandez', 24, 45678901, 1, 2, 3);
    CALL ingresarAlumno('Miguel', 'Hernandez', 22, 56789012, 2, 3, 4);
    CALL ingresarAlumno('Lucia', 'Ramirez', 21, 67890123, 3, 1, 5);
COMMIT;

DELETE FROM estudiantes WHERE Cedula = 99999999;

-- Llama al procedimiento 'BuscarAlumno' para buscar un estudiante por su cédula.
CALL BuscarAlumno(12345678);

-- Selecciona todos los registros de la vista 'vista_estudiantes'.
SELECT * FROM vista_estudiantes;

-- Selecciona todos los registros de la tabla 'logs'.
SELECT * FROM logs;
