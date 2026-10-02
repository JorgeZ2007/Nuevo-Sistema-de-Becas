/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistemadebecas.modelo;

public class BecaEstudio extends Beca {
    private double promedioMin ;
    private int quintilRequerido ;

    public BecaEstudio(String id, String nombre, String desc, int monto, double promedio, int quintil, 
                        LocalDate inicio, LocalDate cierre, LocalDate rev) {
        super(id, nombre, desc, monto, inicio, cierre, rev) ;
        promedioMin=promedio ;
        quintilRequerido=quintil ;
    }

    // Sobreescritura metodo de Beca evaluarPostulante.
    // Para obtener la beca el Beneficiario debe cumplir con el promedio mínimo
    // y el quintil requerido
    @Override
    public boolean evaluarPostulante(Beneficiario b) {
        if (b.getPromedioNotas()>=promedioMin && b.getQuintilSocioeconomico()<=quintilRequerido) {
            return true ;
        }
        return false ;
    }

    public double getPromedioMin() {return promedioMin ;} ;
    public int getQuintil() {return quintilRequerido ;}

}
