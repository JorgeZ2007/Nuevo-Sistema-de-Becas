/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

public class Beneficiario extends Persona {
    private String carrera;
    private double promedioNotas;
    private int quintilSocioeconomico;

    public Beneficiario(String nombre, String rut, String fechaNacimiento, String genero, 
                        String carrera, double promedioNotas, int quintilSocioeconomico) {
        super(nombre, rut, fechaNacimiento, genero);
        this.carrera = carrera;
        this.promedioNotas = promedioNotas;
        this.quintilSocioeconomico = quintilSocioeconomico;
    }

    @Override
    public String obtenerDetalleFormateado() {
        return super.obtenerDetalleFormateado() + 
               " | Carrera: " + carrera + 
               " | Promedio: " + promedioNotas + 
               " | Quintil: " + quintilSocioeconomico;
    }

    public String getCarrera() { return carrera; }
    public double getPromedioNotas() { return promedioNotas; }
    public int getQuintilSocioeconomico() { return quintilSocioeconomico; }
}
