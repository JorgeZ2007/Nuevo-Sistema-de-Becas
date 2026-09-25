/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.excepciones;

public class BecaNoEncontradaException extends Exception {
    public BecaNoEncontradaException(String idBeca) {
        super("Error: La beca con el ID '" + idBeca + "' no existe en el sistema.");
    }
}
