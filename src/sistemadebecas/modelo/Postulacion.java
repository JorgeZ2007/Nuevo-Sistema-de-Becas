

package sistemadebecas.modelo;
import java.time.LocalDate;

// Clase postulacion para gestionar las postulaciones de los beneficiarios a
// becas especificas.
public class Postulacion {
    private Beneficiario postulado ;
    private Beca becaSolicitada ;

    public Postulacion() {
    }
    public Postulacion(Beneficiario pos, Beca beca) {
        postulado=pos ;
        becaSolicitada=beca ;
    }

    public void setPostulado(Beneficiario b) { postulado=b ;}
    public Beneficiario getPostulado() { return postulado ; }

    public void setBecaSolicitada(Beca beca) { becaSolicitada=beca ; }
    public Beca getBecaSolicitada() { return becaSolicitada ; }

    // Metodo para generar un reporte indicando el estado actual de la postulacion de un
    // beneficiario. Muestra si la beca ha sido aceptada, rechazada o si aun esta en proceso
    // de revision.
    public void generarReporte() {

        System.out.println("Reporte: "+ becaSolicitada) ;
        String estado=becaSolicitada.getEstado() ;
        if (estado.equals("Revision")) { // Si la beca aun esta en periodo de revision:
            System.out.println("Beca en proceso de revisión, consulte nuevamente más tarde.");
            LocalDate plazo=becaSolicitada.getCierreRev() ;
            System.out.println("El estado de revisión tiene plazo máximo hasta: " + plazo);
            return ;
        }
        else if (becaSolicitada.evaluarPostulante(postulado)) {
            System.out.println("Usted se ha vuelto beneficiario de la " + becaSolicitada.getNombre() +".") ;
            return ;
        }
        else {
            System.out.println("Usted no ha sido aceptado como beneficiario de la " + becaSolicitada.getNombre() +".") ;
            //razones rechazo
            return ;
        }
    }

}
