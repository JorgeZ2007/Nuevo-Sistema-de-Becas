

package sistemadebecas.modelo;
import java.time.LocalDate;

public class Postulacion {
    private Beneficiario postulado ;
    private String estadoPostulacion ;
    private Beca becaSolicitada ;

    public Postulacion() {
    }
    public Postulacion(Beneficiario pos, Beca beca) {
        postulado=pos ;
        estadoPostulacion="Revision" ;
        becaSolicitada=beca ;
    }

    public void setPostulado(Beneficiario b) { postulado=b ;}
    public Beneficiario getPostulado() { return postulado ; }

    public void setEstadoPostulacion(String estado) { estadoPostulacion=estado ; }
    public String getEstadoPostulacion() { return estadoPostulacion ; }

    public void setBecaSolicitada(TipoBeca beca) { becaSolicitada=beca ; }
    public TipoBeca getBecaSolicitada() { return becaSolicitada ; }


    public void estadoPos() {
        if (becaSolicitada.getEstado().equals("Revision")) {
            estadoPostulacion="Revision";
        }
        else {
            RequisitosBeca req= becaSolicitada.getRequisitos() ;
            estadoPostulacion="Aceptado" ;
            if (postulado.getDecil()>req.getDecilRequerido()) {
                estadoPostulacion="Rechazado" ;
            }
            if (!postulado.getAscendencia().equals(req.getAscendenciaRequerida()) || postulado.getAscendencia().equals("NO")) {
                estadoPostulacion="Rechazado" ;
            }
            if (!postulado.getColegioEgresado().equals(req.getColegioEgrRequerido()) || postulado.getColegioEgresado().equals("NO")) {
                estadoPostulacion="Rechazado" ;
            }
            if (postulado.getNem()!=0 && postulado.getNem()<req.getNemRequerido()) {
                estadoPostulacion="Rechazado" ;
            }
            if (postulado.getPuntajePaes()!=0 && postulado.getPuntajePaes()<req.getPuntajePaesReq()) {
                estadoPostulacion="Rechazado" ;
            }

        }
        estadoPostulacion="Revision" ;
    }
}
