/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

public class BecaSocioeconomica extends Beca {
    private int quintilCorte;

    public BecaSocioeconomica(String idBeca, String nombreBeca, double montoMensual, int cuposMaximos, int quintilMaximo) {
        super(idBeca, nombreBeca, montoMensual, cuposMaximos, new Requisito(1.0, quintilMaximo));
        this.quintilCorte = quintilMaximo;
    }

    @Override
    public String getTipoBeca() {
        return "Socioeconómica (Quintil Máximo: " + quintilCorte + ")";
    }
}
