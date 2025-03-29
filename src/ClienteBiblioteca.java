import Biblioteca.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

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

            ClienteBiblioteca cliente = new ClienteBiblioteca();

            while (true) {
                int opcion = 0;

                opcion = cliente.menu(opcion);
                switch (opcion) {
                    case 1:
                        cliente.buscarLibro(gestionBiblioteca);
                        break;
                    case 2:
                        cliente.prestarLibro(gestionBiblioteca);
                        break;
                    case 3:
                        cliente.devolverLibro(gestionBiblioteca);
                        break;
                    case 4:
                        cliente.mostrarLibros(gestionBiblioteca);
                        break;
                    case 5:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace(System.out);
        }
    }

    public int menu(int opcion) {
        System.out.println("-------Menú-------");
        System.out.println("1. Buscar libro");
        System.out.println("2. Prestar libro");
        System.out.println("3. Devolver libro");
        System.out.println("4. Mostrar libros");
        System.out.println("5. Salir");

        System.out.println("Ingrese una opción: ");
        opcion = Integer.parseInt(System.console().readLine());

        return opcion;
    }

    public void buscarLibro(GestionBiblioteca gestionBiblioteca) {
        System.out.println("Ingrese el título del libro a buscar: ");
        String tituloLibro = System.console().readLine();

        Libro libro = gestionBiblioteca.buscarLibro(tituloLibro);
        if (libro != null) {
            System.out.println("Libro encontrado: " + libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN);

            System.out.println("Desea prestar el libro? (s/n)");
            String respuesta = System.console().readLine();

            if (respuesta.equals("s")) {
                boolean resultadoPrestamo = gestionBiblioteca.prestarLibro(libro.ISBN);
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

    public void prestarLibro(GestionBiblioteca gestionBiblioteca) {
        System.out.println("Ingrese el ISBN del libro a prestar: ");
        String ISBN = System.console().readLine();
        boolean resultadoPrestamo = gestionBiblioteca.prestarLibro(ISBN);
        if (resultadoPrestamo) {
            System.out.println("Libro prestado con éxito.");
        } else {
            System.out.println("El libro no está disponible para préstamo.");
        }
    }

    public void devolverLibro(GestionBiblioteca gestionBiblioteca) {
        System.out.println("Ingrese el ISBN del libro a devolver: ");
        String ISBN = System.console().readLine();
        boolean resultadoDevolucion = gestionBiblioteca.devolverLibro(ISBN);
        if (resultadoDevolucion) {
            System.out.println("Libro devuelto con éxito.");
        } else {
            System.out.println("El libro no se encuentra en la base de datos.");
        }
    }

    public void mostrarLibros(GestionBiblioteca gestionBiblioteca) {
        Libro[] libros = gestionBiblioteca.mostrarLibros();
        for (Libro libro : libros) {
            System.out.println(libro.titulo + ", " + libro.autor + ", ISBN: " + libro.ISBN +
                    ", Disponible: " + (libro.estaDisponible ? "Sí" : "No") +
                    ", Cantidad: " + libro.cantidad);
        }
    }
}