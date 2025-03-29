import Biblioteca.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class GestionBibliotecaImpl extends GestionBibliotecaPOA {
    private ORB orb;
    private Map<String, Libro> libros = new HashMap<>();

    public GestionBibliotecaImpl(ORB orb) {
        this.orb = orb;
        // Inicializar algunos libros en el sistema
        libros.put("1234", new Libro("El principito", "Antoine de Saint-Exupéry", "1234", true, 5));
        libros.put("5678", new Libro("El señor de los anillos", "J.R.R. Tolkien", "5678", true, 5));
        libros.put("91011", new Libro("Cien años de soledad", "Gabriel García Márquez", "91011", true, 5));
        libros.put("121314", new Libro("Don Quijote de la Mancha", "Miguel de Cervantes", "121314", true, 5));
        libros.put("151617", new Libro("La Odisea", "Homero", "151617", true, 5));
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    // Implementar los métodos de la interfaz GestionBiblioteca
    @Override
    public Libro buscarLibro(String titulo) {
        System.out.println("Buscando libro con título: " + titulo + "...");
        return libros.values().stream()
                .filter(libro -> libro.titulo.equalsIgnoreCase(titulo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean prestarLibro(String ISBN) {
        if (libros.containsKey(ISBN)){
            Libro libro = libros.get(ISBN);
            if (libro.estaDisponible && libro.cantidad > 0) {
                libro.cantidad--;
                System.out.println("Prestado libro con ISBN: " + ISBN + " - Copias restantes: " + libro.cantidad);
                if (libro.cantidad == 0) {
                    libro.estaDisponible = false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean devolverLibro(String ISBN) {
        if (libros.containsKey(ISBN)) {
            Libro libro = libros.get(ISBN);
            libro.cantidad++;
            libro.estaDisponible = true;
            System.out.println("Devuelto libro con ISBN: " + ISBN + " - Copias disponibles: " + libro.cantidad);
            return true;
        }
        return false;
    }

    @Override
    public Libro[] mostrarLibros() {
        System.out.println("Mostrando todos los libros...");
        return libros.values().toArray(new Libro[0]);
    }

    // Nuevas implementaciones de métodos añadidos
    @Override
    public String obtenerAutor(String titulo) {
        Libro libro = buscarLibro(titulo);
        return libro != null ? libro.autor : "Autor no encontrado";
    }

    @Override
    public Libro[] buscarPorAutor(String autor) {
        System.out.println("Buscando libros del autor: " + autor);
        return libros.values().stream()
                .filter(libro -> libro.autor.equalsIgnoreCase(autor))
                .toArray(Libro[]::new);
    }

    @Override
    public Libro[] obtenerLibrosNoDisponibles() {
        System.out.println("Obteniendo libros no disponibles...");
        return libros.values().stream()
                .filter(libro -> !libro.estaDisponible)
                .toArray(Libro[]::new);
    }

    @Override
    public String verDetallesLibro(String ISBN) {
        System.out.println("Mostrando detalles del libro con ISBN: " + ISBN);
        if (libros.containsKey(ISBN)) {
            Libro libro = libros.get(ISBN);
            return String.format(
                    "Título: %s\nAutor: %s\nISBN: %s\nDisponible: %s\nCopias disponibles: %d",
                    libro.titulo, libro.autor, libro.ISBN,
                    libro.estaDisponible ? "Sí" : "No", libro.cantidad
            );
        }
        return "Libro no encontrado";
    }

    @Override
    public boolean anadirLibro(Libro libro) {
        if (!libros.containsKey(libro.ISBN)) {
            libros.put(libro.ISBN, libro);
            System.out.println("Añadido nuevo libro: " + libro.titulo);
            return true;
        }
        return false;
    }

    @Override
    public boolean eliminarLibro(String ISBN) {
        if (libros.containsKey(ISBN)) {
            System.out.println("Eliminado libro con ISBN: " + ISBN);
            libros.remove(ISBN);
            return true;
        }
        return false;
    }

    @Override
    public boolean actualizarLibro(Libro libro) {
        if (libros.containsKey(libro.ISBN)) {
            libros.put(libro.ISBN, libro);
            System.out.println("Actualizado libro con ISBN: " + libro.ISBN);
            return true;
        }
        return false;
    }
}

public class ServidorBiblioteca {
    public static void main(String args[]) {
        try {
            // Crear e inicializar el ORB
            ORB orb = ORB.init(args, null);

            // Obtener referencia a rootpoa y activar el POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Crear el servicio e inscribirlo en el ORB
            GestionBibliotecaImpl gestionBiblioteca = new GestionBibliotecaImpl(orb);
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(gestionBiblioteca);
            GestionBiblioteca href = GestionBibliotecaHelper.narrow(ref);

            // Obtener referencia al servicio de nombres
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Vincular la referencia del objeto en el servicio de nombres
            String name = "GestionBiblioteca";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);

            System.out.println("El servidor de la biblioteca está listo y esperando ...");

            // Esperar llamadas de los clientes
            orb.run();
        } catch (Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace(System.out);
        }
    }
}