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

public class Main {
    static String archivoUsuarios = "usuarios.txt";
    static List<String[]> usuarios = new ArrayList<>();
    static String codigoSala = null;
    static List<String[]> usuariosEnSala = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] usuarioActual = null;
        boolean sesionIniciada = false;

        cargarUsuarios();

        while (true) {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Crear sala de llamadas");
            System.out.println("4. Unirse a sala");
            System.out.println("5. Iniciar chat por turnos");
            System.out.println("6. Salir");
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

                    if (correoExiste(correo)) {
                        System.out.println("⚠ El correo ya está registrado.");
                    } else {
                        registrarUsuario(nombre, correo, contrasena);
                        System.out.println("✅ Registro exitoso.");
                    }
                    break;

                case "2":
                    System.out.print("Correo: ");
                    String loginCorreo = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String loginContrasena = scanner.nextLine();

                    usuarioActual = iniciarSesion(loginCorreo, loginContrasena);
                    if (usuarioActual != null) {
                        sesionIniciada = true;
                        System.out.println("✅ Bienvenido, " + usuarioActual[0]);
                    } else {
                        System.out.println("❌ Datos incorrectos.");
                    }
                    break;

                case "3":
                    if (!sesionIniciada) {
                        System.out.println("⚠ Debes iniciar sesión primero.");
                    } else {
                        codigoSala = generarCodigo();
                        System.out.println("✅ Sala creada exitosamente.");
                        System.out.println("Código de la sala: " + codigoSala);
                    }
                    break;

                case "4":
                    if (!sesionIniciada) {
                        System.out.println("⚠ Debes iniciar sesión primero.");
                    } else if (codigoSala == null) {
                        System.out.println("⚠ No hay salas creadas aún.");
                    } else {
                        System.out.print("Ingrese el código de la sala: ");
                        String codigoIngresado = scanner.nextLine().toUpperCase();
                        if (codigoIngresado.equals(codigoSala)) {
                            if (!usuariosEnSala.contains(usuarioActual)) {
                                usuariosEnSala.add(usuarioActual);
                            }
                            System.out.println("✅ Has ingresado a la sala.");
                        } else {
                            System.out.println("❌ Código incorrecto.");
                        }
                    }
                    break;

                case "5":
                    if (!sesionIniciada || usuarioActual == null) {
                        System.out.println("⚠ Debes iniciar sesión primero.");
                    } else if (codigoSala == null || !usuariosEnSala.contains(usuarioActual)) {
                        System.out.println("⚠ Debes estar en una sala.");
                    } else {
                        iniciarChat(scanner);
                    }
                    break;

                case "6":
                    System.out.println("👋 Hasta luego.");
                    scanner.close();
                    return;

                default:
                    System.out.println("⚠ Opción no válida.");
            }
        }
    }

    static void cargarUsuarios() {
        File file = new File(archivoUsuarios);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    usuarios.add(partes);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error al cargar usuarios.");
        }
    }

    static void registrarUsuario(String nombre, String correo, String contrasena) {
        try (FileWriter fw = new FileWriter(archivoUsuarios, true)) {
            fw.write(nombre + "," + correo + "," + contrasena + "\n");
            usuarios.add(new String[]{nombre, correo, contrasena});
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el usuario.");
        }
    }

    static boolean correoExiste(String correo) {
        for (String[] u : usuarios) {
            if (u[1].equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    static String[] iniciarSesion(String correo, String contrasena) {
        for (String[] u : usuarios) {
            if (u[1].equalsIgnoreCase(correo) && u[2].equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    static String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int indice = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(indice));
        }
        return codigo.toString();
    }

    static void iniciarChat(Scanner scanner) {
        if (usuariosEnSala.isEmpty()) {
            System.out.println("⚠ No hay usuarios en la sala.");
            return;
        }

        System.out.println("🟢 Chat iniciado. Escribe 'salir' para terminar.");

        boolean chatActivo = true;
        int turno = 0;

        while (chatActivo) {
            String[] usuario = usuariosEnSala.get(turno);
            System.out.print(usuario[0] + " >> ");
            String mensaje = scanner.nextLine();

            if (mensaje.equalsIgnoreCase("salir")) {
                System.out.println("🔚 Chat finalizado.");
                chatActivo = false;
            } else {
                System.out.println(usuario[0] + " dijo: " + mensaje);
                turno = (turno + 1) % usuariosEnSala.size();
            }
        }
    }
}
