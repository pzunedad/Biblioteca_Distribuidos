package Biblioteca;


/**
* Biblioteca/GestionBibliotecaPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Biblioteca.idl
* viernes 28 de marzo de 2025 18H47' CET
*/


// Define la interfaz para el sistema de gestión de la biblioteca
public abstract class GestionBibliotecaPOA extends org.omg.PortableServer.Servant
 implements Biblioteca.GestionBibliotecaOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("buscarLibro", new java.lang.Integer (0));
    _methods.put ("prestarLibro", new java.lang.Integer (1));
    _methods.put ("devolverLibro", new java.lang.Integer (2));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  // Busca un libro por título y devuelve los detalles del libro
       case 0:  // Biblioteca/GestionBiblioteca/buscarLibro
       {
         String titulo = in.read_string ();
         Biblioteca.Libro $result = null;
         $result = this.buscarLibro (titulo);
         out = $rh.createReply();
         Biblioteca.LibroHelper.write (out, $result);
         break;
       }


  // Presta un libro, cambiando su estado a no disponible
       case 1:  // Biblioteca/GestionBiblioteca/prestarLibro
       {
         String ISBN = in.read_string ();
         boolean $result = false;
         $result = this.prestarLibro (ISBN);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }


  // Devuelve un libro, cambiando su estado a disponible
       case 2:  // Biblioteca/GestionBiblioteca/devolverLibro
       {
         String ISBN = in.read_string ();
         boolean $result = false;
         $result = this.devolverLibro (ISBN);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:Biblioteca/GestionBiblioteca:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public GestionBiblioteca _this() 
  {
    return GestionBibliotecaHelper.narrow(
    super._this_object());
  }

  public GestionBiblioteca _this(org.omg.CORBA.ORB orb) 
  {
    return GestionBibliotecaHelper.narrow(
    super._this_object(orb));
  }


} // class GestionBibliotecaPOA
