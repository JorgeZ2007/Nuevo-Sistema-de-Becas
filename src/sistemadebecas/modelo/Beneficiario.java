/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

public class Beneficiario extends Persona {
    private double promedioNotas;
    private int quintilSocioeconomico;
    private int puntajePaes ;
    private boolean viviendaValparaiso ;

    public Beneficiario(String nombre, String rut, double promedioNotas, int quintilSocioeconomico, 
                        int puntaje, boolean vivienda) {
        super(nombre, rut);
        this.promedioNotas = promedioNotas;
        this.quintilSocioeconomico = quintilSocioeconomico;
        puntajePaes=puntaje ;
        viviendaValparaiso=vivienda ;
    }

    // Sobrecarga de constructor para transformar posibles datos decimales a int
    public Beneficiario(String nombre, String rut, double promedioNotas, float quintilSocioeconomico, 
                        float puntaje, boolean vivienda) {
        super(nombre, rut);
        this.promedioNotas = promedioNotas;
        this.quintilSocioeconomico = (int) quintilSocioeconomico;
        puntajePaes=(int) puntaje ;
        viviendaValparaiso=vivienda ;
    }

    @Override
    public String obtenerDetalleFormateado() {
        return super.obtenerDetalleFormateado() + 
               " | Carrera: " + carrera + 
               " | Promedio: " + promedioNotas + 
               " | Quintil: " + quintilSocioeconomico;
    }

    public double getPromedioNotas() { return promedioNotas; }
    public int getQuintilSocioeconomico() { return quintilSocioeconomico; }
    public int getPuntajePaes() { return puntajePaes; }
    public boolean getViviendaValparaiso() { return viviendaValparaiso; }



    // Sobreescrituras para asegurar el funcionamiento correcto de ciertos
    // métodos de HashMap, como el containskey().

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true ;
        if (obj == null || getClass() != obj.getClass()) {
            return false ;
        }
        Beneficiario otro=(Beneficiario) obj ;
        return Objects.equals(this.rut, otro.rut) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rut);
    }

    
}
