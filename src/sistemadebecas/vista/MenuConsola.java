

public class MenuConsola {
    private GestorBecas gestor;
    private PersistenciaCSV persistencia;
    private Scanner scanner;
    private CargarDatosBecas cargadorBecas;
    
    

    public MenuConsola(GestorBecas gestor, PersistenciaCSV persistencia, CargarDatosBecas cargar) {
        this.gestor = gestor;
        this.persistencia = persistencia;
        this.scanner = new Scanner(System.in);
        cargadorBecas=cargar ;
    }

    public void iniciar() {
        // Carga los datos de las becas postulables
        HashMap<String, Beca> mapaBecas = cargadorBecas.leerArchivo("archivo.csv");
        // Carga Batch al iniciar el programa
        persistencia.cargarDatosBatch(gestor);

        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            procesarOpcion(opcion);
        } while (opcion != 0);

        // Guardado Batch al salir del programa
        persistencia.guardarDatosBatch(gestor);
        System.out.println("¡Datos guardados exitosamente! Programa finalizado.");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n========== SISTEMA DE GESTIÓN DE BECAS (SIA) ==========");
        System.out.println("1. Listar todas las Becas");
        System.out.println("2. Registrar Beneficiario y postulación a una beca");
        System.out.println("3. Listar postulaciones de un Beneficiario");
        System.out.println("4. Eliminar postulacion de un Beneficiario");
        System.out.println("5. Generar reporte");
        System.out.println("6. Buscar postulacion");
        System.out.println("7. algo");
        System.out.println("0. Salir y Guardar Cambios");
        System.out.println("=========================================================");
    }

    private void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    listarBecas();
                    break;
                case 2:
                    registrarBeneficiario() ;
                    break;
                case 3:
                    listarBeneficiarioPost();
                    break;
                case 4:
                    eliminarPostulacionBeneficiario();
                    break;
                case 5:
                    generarReporte() ;
                    break;
                case 6:
                    buscarPostulacion() ;
                    break;
                case 7:
                    //algo
                    break;
                case 0:
                    System.out.println("Cerrando sesión y procesando persistencia de datos...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        } catch (BecaNoEncontradaException | RequisitoNoCumplidoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    // Muestra las becas postulables disponibles junto a su descripción
    private void listarBecas() {
        System.out.println("Becas postulables: ") ;
        for (Beca beca : cargadorBecas.mapaBecas.values()) {
            System.out.print(beca.getIdBeca() + ". ") ;
            System.out.println(beca.getNombreBeca()) ;
            System.out.println("Descripción: " + beca.getdescripcionBeca()) ;
        }
    }
    
    // postulacion  a una beca a partir de un Beneficiario
    private void postularBeca(Beneficiario b) {
        listarBecas() ;
        String opcion ;
        do {
            System.out.println("Ingrese número de beca a la que desea postular: ") ;
            opcion=scanner.nextLine() ;
            opcion= "0" + opcion ;
            
            if (!cargadorBecas.mapaBecas.containsKey(opcion)) {
                System.out.println("Beca ingresada incorrectamente, intente nuevamente.") ;
            }
            
            
        } while ( !cargadorBecas.mapaBecas.containsKey(opcion) ) ;
        
        Beca beca=cargadorBecas.mapaBecas.get(opcion) ;
        beca.cambioEstado() ;
        String estado=beca.getEstado() ;
        if (estado.equals("Cerrado")) {
            System.out.println("La beca ya no se encuentra abierta a postulaciones.") ;
            return ;
        }
        
        Postulacion post=new Postulacion(b, beca) ;
        gestor.agregarBeneficiario(b, post) ;
            
        System.out.println("Beca postulada exitosamente.") ;
        
    }

    // Registro de un Beneficiario nuevo o de una nueva postulación
    // para uno ya existente
    private void registrarBeneficiario() {
        System.out.println("Ingrese su rut: ") ;
        String rut=scanner.nextLine() ;
        
        Beneficiario b=gestor.buscarBeneficiario(rut) ;
        if (b!=null) {
            postularBeca(b) ;
            return ;
        }
        
        System.out.println("Ingrese su nombre: ") ;
        String nombre=scanner.nextLine() ;
        
        System.out.println("Ingrese quintil socioeconómico: ") ;
        int quintil=Integer.parseInt(scanner.nextLine()) ;
        
        System.out.println("¿Su vivienda se encuentra en la región de Valparaíso? SI/NO: ") ;
        String respuestaVivienda=scanner.nextLine() ;
        boolean vivienda ;
        if (respuestaVivienda.equalsIgnoreCase("si")) {
            vivienda=true ;
        }
        else { vivienda=false ; }
        
        System.out.println("Ingrese su promedio académico actual: ") ;
        double promedio=Double.parseDouble(scanner.nextLine()) ;
        
        System.out.println("Ingrese su puntaje paes: ") ;
        int puntajePaes=Integer.parseInt(scanner.nextLine()) ;
        
        Beneficiario b=new Beneficiario(nombre, rut, promedio, quintil, puntajePaes, vivienda) ;
        postularBeca(b) ;
    }

    // Muestra las postulaciones de un Beneficiario
    private void listarBeneficiarioPost() {
        Beneficiario b=buscarUnBeneficiario() ;
            
        gestor.mostrarPostulaciones(b) ;
        
    }

    // Elimina una postulacion de un beneficiario especifico
    private void eliminarPostulacionBeneficiario() {
        Beneficiario b=buscarUnBeneficiario() ;
        
        gestor.mostrarPostulaciones(b) ;
        int opcion ;
        Postulacion p ;
        do {
            System.out.println("Ingrese número de la postulación que desea eliminar: ") ;
            opcion=Integer.parseInt(scanner.nextLine()) ;
            p=gestor.obtenerPostulacion(opcion, b) ;
            
            if (p==null) {
                System.out.println("Postulación ingresada incorrectamente, intente nuevamente.") ;
            }
            
        } while (p==null) ;
        gestor.eliminarPostulacion(b, p) ;
        System.out.println("Postulación eliminada correctamente.") ;
    }
    
    
    public void generarReporte() {
        Beneficiario b=buscarUnBeneficiario() ;
        
        gestor.mostrarPostulaciones(b) ;
        int opcion ;
        Postulacion p ;
        do {
            System.out.println("Ingrese número de la postulación a la cual desea generar el reporte: ") ;
            opcion=Integer.parseInt(scanner.nextLine()) ;
            p=gestor.obtenerPostulacion(opcion, b) ;
            
            if (p==null) System.out.println("Postulación ingresada incorrectamente, intente nuevamente.") ;
            
        } while (p==null) ;
        
        p.generarReporte() ;
        
    }
    
    
    public void buscarPostulacion(){
        Beneficiario b=buscarUnBeneficiario() ;
        
        gestor.mostrarPostulaciones(b) ;
        int opcion ;
        Postulacion p ;
        do {
            System.out.println("Ingrese número de la postulación que desea buscar: ") ;
            opcion=Integer.parseInt(scanner.nextLine()) ;
            p=gestor.obtenerPostulacion(opcion, b) ;
            
            if (p==null) {
                System.out.println("Postulación ingresada incorrectamente, intente nuevamente.") ;
            }
        } while (p==null) ;
        
        if (!gestor.buscarPostulacion(b, p)) {
            System.out.println("Usted no ha postulado a esta beca.") ;
            return ;
        }
        System.out.println("Usted tiene una postulación realizada a la beca "+ p.getBecaSolicitada()) ;
        
    }
    
    public Beneficiario buscarUnBeneficiario() {
        Beneficiario b ;
        do {
            System.out.println("Ingrese su rut: ") ;
            String rut=scanner.nextLine() ;
        
            b=gestor.buscarBeneficiario(rut) ;
            if (b==null) {
                System.out.println("Beneficiario no encontrado, intente nuevamente.") ;
            }
        } while (b==null) ;
        
        return b ;
    }


    // --- MÉTODOS DE LECTURA Y VALIDACIÓN ---

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    private int leerEnteroPositivo(String mensaje) {
        while (true) {
            int val = leerEntero(mensaje);
            if (val >= 0) return val;
            System.out.println("El valor no puede ser negativo.");
        }
    }

    private int leerEnteroRango(String mensaje, int min, int max) {
        while (true) {
            int val = leerEntero(mensaje);
            if (val >= min && val <= max) return val;
            System.out.println("El número debe estar entre " + min + " y " + max + ".");
        }
    }

    private double leerDoublePositivo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                double val = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
                if (val >= 0) return val;
                System.out.println("El valor no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número decimal válido (ej. 5.5).");
            }
        }
    }
}
