package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Usuario {
    private String nombre;
    private String correo;
    private String contrasena;

    public Usuario(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }

    public String toCSV() {
        return nombre + "," + correo + "," + contrasena;
    }

    public static Usuario fromCSV(String linea) {
        String[] partes = linea.split(",");
        if (partes.length != 3) return null;
        return new Usuario(partes[0], partes[1], partes[2]);
    }
}

class UsuarioService {
    private List<Usuario> usuarios = new ArrayList<>();
    private final String archivo = "usuarios.txt";

    public UsuarioService() {
        cargarUsuarios();
    }

    public boolean registrar(String nombre, String correo, String contrasena) {
        if (correoExiste(correo)) return false;

        Usuario nuevo = new Usuario(nombre, correo, contrasena);
        usuarios.add(nuevo);
        guardarUsuario(nuevo);
        return true;
    }

    public boolean iniciarSesion(String correo, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo) && u.getContrasena().equals(contrasena)) {
                System.out.println("✅ Bienvenido, " + u.getNombre());
                return true;
            }
        }
        return false;
    }

    private boolean correoExiste(String correo) {
        return usuarios.stream().anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
    }

    private void guardarUsuario(Usuario usuario) {
        try (FileWriter fw = new FileWriter(archivo, true)) {
            fw.write(usuario.toCSV() + "\n");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el usuario.");
        }
    }

    private void cargarUsuarios() {
        File file = new File(archivo);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Usuario u = Usuario.fromCSV(linea);
                if (u != null) usuarios.add(u);
            }
        } catch (IOException e) {
            System.out.println("❌ Error al cargar usuarios.");
        }
    }
}

public class Main {
    private static String codigoSala = null;

    public static String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(indice));
        }
        return codigo.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioService usuarioService = new UsuarioService();

        boolean sesionIniciada = false;

        while (true) {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Crear sala de llamadas");
            System.out.println("4. Unirse a sala");
            System.out.println("5. Salir");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Correo: ");
                    String correo = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String contrasena = scanner.nextLine();

                    if (usuarioService.registrar(nombre, correo, contrasena)) {
                        System.out.println("✅ Registro exitoso.");
                    } else {
                        System.out.println("⚠️ El correo ya está registrado.");
                    }
                    break;

                case "2":
                    System.out.print("Correo: ");
                    String loginCorreo = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String loginContrasena = scanner.nextLine();

                    if (usuarioService.iniciarSesion(loginCorreo, loginContrasena)) {
                        sesionIniciada = true;
                    } else {
                        System.out.println("❌ Datos incorrectos.");
                    }
                    break;

                case "3":
                    if (!sesionIniciada) {
                        System.out.println("⚠️ Debes iniciar sesión primero.");
                    } else {
                        codigoSala = generarCodigo();
                        System.out.println("✅ Sala creada exitosamente.");
                        System.out.println("Código de la sala: " + codigoSala);
                    }
                    break;

                case "4":
                    if (!sesionIniciada) {
                        System.out.println("⚠️ Debes iniciar sesión primero.");
                    } else if (codigoSala == null) {
                        System.out.println("⚠️ No hay salas creadas aún. Crea una sala primero.");
                    } else {
                        System.out.print("Ingrese el código de la sala: ");
                        String codigoIngresado = scanner.nextLine().toUpperCase();

                        if (codigoIngresado.equals(codigoSala)) {
                            System.out.println("✅ Has ingresado a la sala correctamente.");
                        } else {
                            System.out.println("❌ Código incorrecto.");
                        }
                    }
                    break;

                case "5":
                    System.out.println("👋 Hasta luego.");
                    scanner.close();
                    return;

                default:
                    System.out.println("⚠️ Opción no válida.");
            }
        }
    }
}
