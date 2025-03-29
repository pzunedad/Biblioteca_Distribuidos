import Biblioteca.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Scanner;

public class ClienteBiblioteca {
    public static void main(String args[]) {
        try {
            // Inicializar el ORB (Object Request Broker)
            ORB orb = ORB.init(args, null);

            // Obtener referencia al servicio de nombres
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Buscar la referencia del objeto (servidor) en el servicio de nombres
            String name = "GestionBiblioteca";
            GestionBiblioteca gestionBiblioteca = GestionBibliotecaHelper.narrow(ncRef.resolve_str(name));

            Scanner sc = new Scanner(System.in);
            int opcion;

            do {
                System.out.println("\n=== MENÚ BIBLIOTECA ===");
                System.out.println("1. Buscar libro por título");
                System.out.println("2. Prestar libro");
                System.out.println("3. Devolver libro");
                System.out.println("4. Mostrar todos los libros");
                System.out.println("5. Mostrar autor de un libro");
                System.out.println("6. Buscar libros por autor");
                System.out.println("7. Mostrar libros no disponibles");
                System.out.println("8. Ver detalles de libro");
                System.out.println("9. Salir");
                System.out.print("Seleccione una opción: ");

                opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer

                switch(opcion) {
                    case 1:
                        buscarLibro(gestionBiblioteca, sc);
                        break;
                    case 2:
                        prestarLibro(gestionBiblioteca, sc);
                        break;
                    case 3:
                        devolverLibro(gestionBiblioteca, sc);
                        break;
                    case 4:
                        mostrarLibros(gestionBiblioteca);
                        break;
                    case 5:
                        mostrarAutor(gestionBiblioteca, sc);
                        break;
                    case 6:
                        buscarPorAutor(gestionBiblioteca, sc);
                        break;
                    case 7:
                        mostrarNoDisponibles(gestionBiblioteca);
                        break;
                    case 8:
                        verDetallesLibro(gestionBiblioteca, sc);
                        break;
                    case 9:
                        System.out.println("Saliendo del sistema...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } while(opcion != 9);

        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace(System.out);
        }
    }

    private static void buscarLibro(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese título a buscar: ");
        String tituloLibro = sc.nextLine();
        Libro libro = gb.buscarLibro(tituloLibro);

        if (libro != null) {
            System.out.println("Libro encontrado: " + libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN);

            System.out.println("Desea prestar el libro? (s/n)");
            String respuesta = sc.nextLine();

            if (respuesta.equalsIgnoreCase("s")) {
                boolean resultadoPrestamo = gb.prestarLibro(libro.ISBN);
                if (resultadoPrestamo) {
                    System.out.println("Libro prestado con éxito.");
                } else {
                    System.out.println("El libro no está disponible para préstamo.");
                }
            }
        } else {
            System.out.println(tituloLibro + " no encontrado.");
        }
    }

    private static void prestarLibro(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese ISBN del libro a prestar: ");
        String ISBN = sc.nextLine();
        boolean resultadoPrestamo = gb.prestarLibro(ISBN);
        if (resultadoPrestamo) {
            System.out.println("Libro prestado con éxito.");
        } else {
            System.out.println("El libro no está disponible para préstamo.");
        }
    }

    private static void devolverLibro(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese ISBN del libro a devolver: ");
        String ISBN = sc.nextLine();
        boolean resultadoDevolucion = gb.devolverLibro(ISBN);
        if (resultadoDevolucion) {
            System.out.println("Libro devuelto con éxito.");
        } else {
            System.out.println("El libro no se encuentra en la base de datos.");
        }
    }

    private static void mostrarLibros(GestionBiblioteca gb) {
        System.out.println("\n=== LIBROS DISPONIBLES ===");
        Libro[] libros = gb.mostrarLibros();
        for (Libro libro : libros) {
            System.out.println(libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN +
                    ", Disponible: " + (libro.estaDisponible ? "Sí" : "No") +
                    ", Cantidad: " + libro.cantidad);
        }
    }

    private static void mostrarAutor(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese título del libro: ");
        String autor = gb.obtenerAutor(sc.nextLine());
        System.out.println("Autor: " + (autor != null ? autor : "No encontrado"));
    }

    private static void buscarPorAutor(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese autor a buscar: ");
        Libro[] libros = gb.buscarPorAutor(sc.nextLine());
        System.out.println("\n=== RESULTADOS ===");
        if (libros != null && libros.length > 0) {
            for (Libro libro : libros) {
                System.out.println(libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN);
            }
        } else {
            System.out.println("No se encontraron libros de este autor.");
        }
    }

    private static void mostrarNoDisponibles(GestionBiblioteca gb) {
        System.out.println("\n=== LIBROS PRESTADOS ===");
        Libro[] libros = gb.obtenerLibrosNoDisponibles();
        if (libros != null && libros.length > 0) {
            for (Libro libro : libros) {
                System.out.println(libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN);
            }
        } else {
            System.out.println("Todos los libros están disponibles.");
        }
    }

    private static void verDetallesLibro(GestionBiblioteca gb, Scanner sc) {
        System.out.print("Ingrese ISBN del libro: ");
        String detalles = gb.verDetallesLibro(sc.nextLine());
        System.out.println("\n=== DETALLES DEL LIBRO ===");
        System.out.println(detalles != null ? detalles : "Libro no encontrado");
    }
}