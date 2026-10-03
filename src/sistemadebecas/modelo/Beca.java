/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

import java.util.*;
import java.time.LocalDate;

public abstract class Beca {
    private String idBeca;
    private String nombreBeca;
    private String descripcionBeca;
    private double montoMensual;
    private List<Beneficiario> listaBeneficiarios;
    private String estado ;
    private LocalDate inicioPost ;
    private LocalDate cierrePost ;
    private LocalDate cierreRev ;

    public Beca(String idBeca, String nombreBeca, String desc, double montoMensual, LocalDate inicio,
                LocalDate cierre, LocalDate rev) {
        this.idBeca = idBeca;
        this.nombreBeca = nombreBeca;
        this.descripcionBeca=desc;
        this.montoMensual = montoMensual;
        this.listaBeneficiarios = new ArrayList<>();
        estado="Revision" ;
        inicioPost=inicio ;
        cierrePost=cierre ;
        cierreRev=rev ;
    }

    // Metodo abstracto para evaluar si un postulante cumple con los
    // requisitos de la beca.
    public abstract boolean evaluarPostulante(Beneficiario b) ;

    // Agrega al beneficiario en la lista de postulados de una beca especifica
    public boolean agregarBeneficiario(Beneficiario b) {
        return listaBeneficiarios.add(b);
    }

    public boolean eliminarBeneficiario(String rut) {
        return listaBeneficiarios.removeIf(b -> b.getRut().equalsIgnoreCase(rut));
    }

    public Beneficiario buscarBeneficiario(String rut) {
        for (Beneficiario b : listaBeneficiarios) {
            if (b.getRut().equalsIgnoreCase(rut)) {
                return b;
            }
        }
        return null;
    }
    public void mostrarBeneficiarios(){
        for (int i=0; i>listaBeneficiarios.size(); i++){
            System.out.println(listaBeneficiarios.get(i));
        }
    }


    // Getters y Setters
    public String getIdBeca() { return idBeca; }
    public String getNombreBeca() { return nombreBeca; }
    public String getDescripcionBeca() { return descripcionBeca; }
    public double getMontoMensual() { return montoMensual; }
    public String getEstado() { return estado ; }
    public LocalDate getCierreRev() { return cierreRev; }

    public List<Beneficiario> getListaBeneficiarios() {
        return Collections.unmodifiableList(listaBeneficiarios);
    }


    // Metodo para verificar si la beca esta abierta a postulaciones, en revisión de
    // las postulaciones ya realizadas o cerrada.
    public void cambioEstado() {
        if ( ( (LocalDate.now()).isAfter(inicioPost) || LocalDate.now().isEqual(inicioPost) ) && LocalDate.now().isBefore(cierrePost)) {
            estado="Abierto" ;
        }
        else if ( (LocalDate.now()).isAfter(cierrePost) && LocalDate.now().isBefore(cierreRev)) {
            estado="Revision" ;

        }
        else {
            estado="Cerrado" ;
        }
    }
}
