package sistemadebecas.modelo;

public class Persona {
    protected String nombre;
    protected String rut;
    protected String fechaNacimiento;
    protected String genero;

    public Persona() {
    }

    public Persona(String nombre, String rut, String fechaNacimiento, String genero) {
        this.nombre = nombre;
        this.rut = rut;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
    }

    public String obtenerDetalleFormateado() {
        return "RUT: " + rut + " | Nombre: " + nombre + " | F.Nac: " + fechaNacimiento + " | Género: " + genero;
    }

    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero() { return genero; }
}
