/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.servicio;

import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;
import sistemadebecas.excepciones.BecaNoEncontradaException;
import sistemadebecas.excepciones.RequisitoNoCumplidoException;

import java.util.*;

public class GestorBecas {
    private Map<String, Beca> mapaBecas; 

    public GestorBecas() {
        this.mapaBecas = new HashMap<>();
    }


    public boolean agregarBeca(Beca beca) {
        if (mapaBecas.containsKey(beca.getIdBeca())) {
            return false;
        }
        mapaBecas.put(beca.getIdBeca(), beca);
        return true;
    }

    public Beca buscarBeca(String idBeca) throws BecaNoEncontradaException {
        Beca b = mapaBecas.get(idBeca);
        if (b == null) {
            throw new BecaNoEncontradaException(idBeca);
        }
        return b;
    }

    public boolean eliminarBeca(String idBeca) throws BecaNoEncontradaException {
        buscarBeca(idBeca); 
        mapaBecas.remove(idBeca);
        return true;
    }

    // Evita retornar el HashMap por referencia
    public Map<String, Beca> getMapaBecas() {
        return Collections.unmodifiableMap(mapaBecas);
    }


    public boolean agregarBeneficiarioABeca(String idBeca, Beneficiario beneficiario) 
            throws BecaNoEncontradaException, RequisitoNoCumplidoException {
        Beca beca = buscarBeca(idBeca);
        
        if (beneficiario.getPromedioNotas() < 4.0) {
            throw new RequisitoNoCumplidoException("El postulante no cumple con el promedio mínimo requerido (4.0).");
        }
        
        boolean agregado = beca.agregarBeneficiario(beneficiario);
        if (!agregado) {
            throw new RequisitoNoCumplidoException("La beca '" + beca.getNombreBeca() + "' no tiene cupos disponibles.");
        }
        return true;
    }

    public boolean eliminarBeneficiarioDeBeca(String idBeca, String rut) throws BecaNoEncontradaException {
        Beca beca = buscarBeca(idBeca);
        return beca.eliminarBeneficiario(rut);
    }

    public List<Beneficiario> obtenerBeneficiarios(String idBeca) throws BecaNoEncontradaException {
        Beca beca = buscarBeca(idBeca);
        return beca.getListaBeneficiarios();
    }

    public List<Beneficiario> obtenerBeneficiarios(String idBeca, int quintilMaximo) throws BecaNoEncontradaException {
        Beca beca = buscarBeca(idBeca);
        List<Beneficiario> filtrados = new ArrayList<>();
        for (Beneficiario b : beca.getListaBeneficiarios()) {
            if (b.getQuintilSocioeconomico() <= quintilMaximo) {
                filtrados.add(b);
            }
        }
        return Collections.unmodifiableList(filtrados);
    }

    
    
    public List<Beneficiario> obtenerPostulantesPrioritarios(String idBeca) throws BecaNoEncontradaException {
        Beca beca = buscarBeca(idBeca);
        List<Beneficiario> prioritarios = new ArrayList<>();
        
        for (Beneficiario b : beca.getListaBeneficiarios()) {
            if (b.getQuintilSocioeconomico() <= 2 && b.getPromedioNotas() >= 5.5) {
                prioritarios.add(b);
            }
        }
        return Collections.unmodifiableList(prioritarios);
    }
}
