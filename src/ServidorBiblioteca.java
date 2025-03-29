import Biblioteca.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.HashMap;
import java.util.Map;

class GestionBibliotecaImpl extends GestionBibliotecaPOA {
    private ORB orb;
    private Map<String, Libro> libros = new HashMap<>();

    public GestionBibliotecaImpl(ORB orb) {
        this.orb = orb;
        // Inicializar algunos libros en el sistema
        libros.put("1234", new Libro("El principito", "Antoine de Saint-Exupéry", "1234", true, 5));
        // Agregar más libros según sea necesario
        libros.put("5678", new Libro("El señor de los anillos", "J.R.R. Tolkien", "5678", true,5));
        libros.put("91011", new Libro("Cien años de soledad", "Gabriel García Márquez", "91011", true,5));
        libros.put("121314", new Libro("Don Quijote de la Mancha", "Miguel de Cervantes", "121314", true,5));
        libros.put("151617", new Libro("La Odisea", "Homero", "151617", true,5));
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    // Implementar los métodos de la interfaz GestionBiblioteca
    @Override
    public Libro buscarLibro(String titulo) {
        System.out.println("Buscando libro con título: " + titulo + "...");
        return libros.values().stream()
                .filter(libro -> libro.titulo.equals(titulo) && libro.estaDisponible)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean prestarLibro(String ISBN) {
        if (libros.containsKey(ISBN) && libros.get(ISBN).estaDisponible) {
            libros.get(ISBN).cantidad--;
            System.out.println("Prestado libro con ISBN: " + ISBN + " quedan: " +libros.get(ISBN).cantidad);
            if (libros.get(ISBN).cantidad == 0) {
                libros.get(ISBN).estaDisponible = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean devolverLibro(String ISBN) {
        if (libros.containsKey(ISBN)) {
            libros.get(ISBN).cantidad++;
            libros.get(ISBN).estaDisponible = true;
            System.out.println("Devuelto el libro con ISBN: " + ISBN+ " quedan: " + libros.get(ISBN).cantidad);
            return true;
        }
        return false;
    }

    @Override
    public Libro[] mostrarLibros() {
        System.out.println("Mostrando todos los libros...");
        // Solo los libros disponibles
        return libros.values().stream()
                .filter(libro -> libro.estaDisponible)
                .toArray(Libro[]::new);
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

