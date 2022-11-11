
import java.util.LinkedList;  //libreria para usar listas
import java.util.Queue;  //libreria para usar cola
import java.util.Random; //libreria para usar RANDOM
import java.util.Scanner; //libreria para ingresar datos en consola


//////////////////NOTAS//////////////////////////
//  .equals  se usa como el = pero solo en Strings.
// Integer.parseInt se usa para cambiar el tipo de dato a entero
// String.valueOf se usa para cambiar el tipo de datos a cadena

public class ProyectoJuegoDeCartas { //clase
    //Letras que no son validas
    public static String letrasNoPermitidas[] = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","ñ","o","p","q","r","s","t","u","v","w","x","y","z"};
    //cartas de numeros
    public static int totalCartasNumeros = 120; //total de cartas sin comodines
    public static String[] numerosDisponibles = {"0","1","2","3","4","5","6","7","8","9"}; //numeros validos
    public static int totalCartasRepartir = 7; //numero de cartas que se van a repartir

    //cartas de comportamiento
    public static int totalCartasBloqueo = 4;
    public static int totalCartasReversa = 4;

    //Jugadores y Bots
    public static int totalJugadores = 4;
    public static int totalBots = 3;

    //contador de las cartas repartidas
    public static int contadorCartasRepartidas = 0;
    public static String ultimaCartaTirada = null;
    public static boolean primeraCarta = true;

    // se crea un arreglo con el total de cartas
    public static String baraja[] = new String[totalCartasNumeros + totalCartasBloqueo + totalCartasReversa];
    public static Queue<String> barajaFinal = new LinkedList<>(); //cola
    public static int cartasRepartidas[] = new int[totalCartasNumeros + totalCartasBloqueo + totalCartasReversa]; //asignamos tamaño del arreglo

    //turnos
    public static String turnos[];
    public static String reverseTurnos[];

    //cartas de participantes
    public static String cartasEntregadasJugadores[][];
    public static String cartasEntregadasBots[][];

    //participantes
    public static int jugadores[];
    public static int bots[];

    public static void main(String args[]) {  //metodo main

        crearCartas(); //se manda a llamar a la funcion

        boolean terminar = false;
        //do-while para repetir los procesos
        do {
            crearJugadores();
            crearBots();
            repartirCartas();    //se manda a llamar a las funciones
            crearOrdenTurnos();
            barejear();

            String cartaTirada = null;
            int contadorTurnos = 0;  //variable contador
            boolean turnoSiguiente = true;
            while(turnoSiguiente) {
                String[] quienTira = turnos[contadorTurnos].toLowerCase().split(" "); //convertimos el arreglo a mayusculas con el "toLowerCase"
                //split es para cortar lo que sigue

                cartaTirada = tiroCarta(turnos[contadorTurnos], quienTira[1], quienTira[0]); //asignamos valor a "cartaTirada"

                if(cartaTirada.equals("BLOQUEO")) { //comparacion para el bloqueo
                    contadorTurnos += 2;

                    if(contadorTurnos > (turnos.length-1) && (contadorTurnos-(turnos.length-1)) == 2) {
                        contadorTurnos = 1;
                    }
                    if(contadorTurnos > (turnos.length-1)) {
                        contadorTurnos = 0;
                    }
                }else {
                    if(cartaTirada.equals("REVERSA")) {
                        ordenarTurnos(contadorTurnos); //ordena los turnos
                        contadorTurnos = 0;
                    }else {
                        if(contadorTurnos == (turnos.length-1)) { // Si son iguales, inicia de nuevo el primer jugador, porque ya dio una ronda
                            contadorTurnos = 0;
                        } else { // si no son iguales tira el siguiente
                            ++contadorTurnos;
                        }
                    }
                }

                turnoSiguiente = validarFinalJugador();
                if(turnoSiguiente){                   //METODOS QUE VALIDAN AL GANADOR
                    turnoSiguiente=validarFinalbot();
                }
            }

        } while (terminar); //while


        System.out.println("ERES EL GANADOR... JUEGO TERMINADO!"); //MENSAJE PARA MOSTRAR EL GANADOR

        /*while (barajaFinal.peek() != null) {
            System.out.println("BARAJA: " + barajaFinal.poll()); //SABER QUE SE FUERON AL FINAL DE LA BARAJA EN LA COLA
        }*/      //BARAJA, DONDE EL "POLL" devulve y elimina el elemento del arreglo

    }

    public static void ordenarTurnos(int posicion) {  //metodo para ordenar los turnos
        int contadorReversaPrimero = turnos.length - 1; //caso si es reversa
        int contadorReversaUltimo = turnos.length - 2;  //caso reversa
        int contadorIntermedio = posicion - 1;  //caso reversa
        reverseTurnos = new String[turnos.length]; //se asignan los turnos en el arreglo
        for(int i=0; i < reverseTurnos.length; i++) {

            if(posicion == 0) {  //comparaciones
                reverseTurnos[i] = turnos[contadorReversaPrimero];
                --contadorReversaPrimero;
            }
            if(posicion == (turnos.length - 1)) {
                if(contadorReversaUltimo < 0) {
                    reverseTurnos[i] = turnos[turnos.length - 1];
                }else{
                    reverseTurnos[i] = turnos[contadorReversaUltimo];
                }
                --contadorReversaUltimo;
            }
            if(posicion < (turnos.length - 1) && posicion > 0) {
                if(contadorIntermedio < 0) {
                    contadorIntermedio = (turnos.length - 1);
                    reverseTurnos[i] = turnos[contadorIntermedio];
                }else {
                    reverseTurnos[i] = turnos[contadorIntermedio];
                }
                --contadorIntermedio;
            }
        }

        System.out.println("Orden de turnos");
        for(int i = 0 ; i < turnos.length; i++) { //itera los turnos
            turnos[i] = reverseTurnos[i];
            System.out.println((i+1)+". "+(turnos[i])); //se imprimen los turnos
        }

    }

    public static boolean validarFinalJugador() { //validar jugador ganador
        boolean terminar = true;
        int contador = 0;
        for(int i = 0; i < cartasEntregadasJugadores.length; i++) { // filas jugadores
            for(int j = 0; j < cartasEntregadasJugadores[i].length; j++) { // columnas cartas
                if(cartasEntregadasJugadores[i][j] == null) {
                    ++contador;
                }
            }
            if(contador == 100) {
                terminar = false;   //se cambia el valor del booleano
                break;
            }
            contador=0; //contador = 0 en caso de no cumplir el if
        }

        return terminar; //regresa el valor
    }

    public static boolean validarFinalbot() { //validar bot ganador
        boolean terminar = true;
        int contador = 0;
        for(int i = 0; i < cartasEntregadasBots.length; i++) { // filas bots
            for(int j = 0; j < cartasEntregadasBots[i].length; j++) { // columnas cartas
                if(cartasEntregadasBots[i][j] == null) {
                    ++contador;
                }
            }
            if(contador == 100) {
                terminar = false;  //se cambia el valor del booleano
                break;
            }
            contador=0;
        }

        return terminar; //regresa el valor
    }

    public static int mostrarCartasJugdaores(String jugador, String tipo) { //metodo para mostrar las cartas
        int posicionTurno = Integer.valueOf(jugador);
        posicionTurno = posicionTurno - 1; // indice del arreglo
        int contadorUltimaCarta = 0;
        for(int i = 0; i < cartasEntregadasJugadores.length; i++) { // filas
            System.out.print("Cartas : ");
            for(int j = 0; j < cartasEntregadasJugadores[i].length; j++) { // columnas
                if(cartasEntregadasJugadores[i][j] != null && posicionTurno == i && tipo.equals("jugador")) {
                    System.out.print(" "+cartasEntregadasJugadores[i][j]+" ");
                }else {
                    if(cartasEntregadasJugadores[i][j] == null) {
                        ++contadorUltimaCarta;
                        System.out.print("");
                    }else {
                        System.out.print(" X "); //muestra las otras cartas con una X
                    }
                }
            }
            System.out.println(""); //dar espacio
        }

        return contadorUltimaCarta; //regresa el valor
    }

    public static int mostrarCartasBots(String bot, String tipo) {  //muestra las cartas de los bots
        int posicionTurno = Integer.valueOf(bot);
        posicionTurno = posicionTurno - 1; // indice del arreglo
        int contadorUltimaCarta = 0;
        for(int i = 0; i < cartasEntregadasBots.length; i++) { // filas
            System.out.print("Cartas : ");
            for(int j = 0; j < cartasEntregadasBots[i].length; j++) { // columnas
                if(cartasEntregadasBots[i][j] != null && posicionTurno == i && tipo.equals("bot")) {
                    System.out.print(" "+cartasEntregadasBots[i][j]+" ");
                }else {
                    if(cartasEntregadasBots[i][j] == null) {
                        ++contadorUltimaCarta;
                        System.out.print("");
                    }else {
                        System.out.print(" X ");  //muestra las X
                    }

                }
            }
            System.out.println("");
        }
        return contadorUltimaCarta;  //regresa el valor
    }

    public static void repartir(int totalArreglo, String tipoJugador) {  //metodo para repartir cartas
        dibujarLineaAsteriscos();

        for(int j = 0; j < totalArreglo; j++) {
            int contadorCartas = 0;
            boolean terminarRepartirCartas = true;
            do {
                boolean cartaUsada = false;
                Random r = new Random();
                int numeroRandom = r.nextInt(totalCartasNumeros + totalCartasBloqueo + totalCartasReversa);
                //System.out.println("Repartiendo cartas al "+tipoJugador+" "+(j+1)+"..... index: "+numeroRandom);
                System.out.println("Repartiendo cartas al "+tipoJugador+" "+(j+1));
                for(int i = 0; i < cartasRepartidas.length; i++) {
                    if(numeroRandom == cartasRepartidas[i]) {
                        System.out.println("Carta usada.....");
                        //System.out.println("Carta usada.....index:"+numeroRandom);
                        cartaUsada = true;  //se cambia el valor del booleano
                        break;
                    }
                }

                if(!cartaUsada) { // si la carta no esta usada
                    cartasRepartidas[contadorCartasRepartidas]= numeroRandom;

                    if(tipoJugador.equals("jugador")) {
                        cartasEntregadasJugadores[j][contadorCartas] = baraja[numeroRandom];
                    }else {
                        cartasEntregadasBots[j][contadorCartas] = baraja[numeroRandom];  //asigna las cartas
                    }

                    baraja[numeroRandom] = null;
                    ++contadorCartas;
                    ++contadorCartasRepartidas;  //contador para las cartas
                }
                    //muestra que se termino de repartir las cartas
                if(contadorCartas == totalCartasRepartir) { // si contador cartas es igual a total de cartas a repartir(7)
                    dibujarLineaAsteriscos();
                    System.out.println("**************** Termino de repartir al "+tipoJugador+" "+(j+1)+" *****************");
                    dibujarLineaAsteriscos();
                    terminarRepartirCartas = false;
                }
            }while(terminarRepartirCartas);
        }

    }


     public static void repartirCartas() {  //metodo para repartir las cartas

        cartasEntregadasJugadores = new String[jugadores.length][100];  //damos un valor de 100 al arreglo
        cartasEntregadasBots = new String[bots.length][100];            // para tener espacios

        repartir(jugadores.length, "jugador");
        repartir( bots.length, "bot");

    }


    public static boolean validarLetras(String cadena) {  //validamos que sean letras permitidas
        boolean letrasInvalidas = false;
        for(int j=0; j < letrasNoPermitidas.length; j++) {
            if(letrasNoPermitidas[j].toUpperCase().equals(cadena.toUpperCase())) {
                System.out.println("No se permiten letras");
                letrasInvalidas = true;
                break; // terminar este for
            }
        }
        return letrasInvalidas; //regresa el valor
    }

    public static void crearJugadores() {   //metodo para crear los jugadores
        boolean jugadoresIncompletos = true;
        do {
            dibujarLineaAsteriscos();
            Scanner s = new Scanner(System.in);
            System.out.println("....¿Cuantos jugadores (1-4)?....");  //pide cuantos jugadores
            String stringJugadores = s.next();
            //letras
            boolean letrasInvalidas = validarLetras(stringJugadores);

            if(!letrasInvalidas) {   //if en caso de no ingresar no de jugadores correctos
                // numeros
                int numeroJugadores = Integer.parseInt(stringJugadores);
                if(numeroJugadores > totalJugadores || numeroJugadores < 1) {
                    System.out.println("....Deben ser de 1 a 4 jugadores....");
                }else {
                    jugadores = new int[numeroJugadores];
                    for(int i=1; i <= numeroJugadores; i++) {
                        jugadores[i-1] = i;
                    }
                    jugadoresIncompletos = false;
                }
            }

        }while(jugadoresIncompletos);
    }

    public static void crearBots() {  //crea los bots
        boolean botsIncompletos = true;
        do {
            dibujarLineaAsteriscos();
            Scanner s = new Scanner(System.in);
            System.out.println("....¿Cuantos bots (1-3)?....");  //pide cuantos bots
            String stringBots = s.next();
            //letras
            boolean letrasInvalidas = validarLetras(stringBots);  //valida las letras

            if(!letrasInvalidas) {
                // numeros
                int numeroBots = Integer.parseInt(stringBots);
                if(numeroBots > totalBots || numeroBots < 1) {
                    System.out.println(".....Deben ser de 1 a 3 bots....");
                }else {
                    bots = new int[numeroBots];
                    for(int i=1; i <= numeroBots; i++) {
                        bots[i-1] = i;
                    }
                    botsIncompletos = false;
                }
            }

        }while(botsIncompletos);
    }

    public static void crearCartas() {   //metodo para crear las cartas
        boolean barajaIncompleta = true;
        int contador = 0;
        while(barajaIncompleta) {
            for(int j=0; j < numerosDisponibles.length; j++) {
                baraja[contador] =	numerosDisponibles[j];
                ++contador;
            }
            if(contador == totalCartasNumeros) {
                barajaIncompleta = false;     //se cambia el booleano cuando la baraja esta completada
            }
        }
        //120 < 128
        if(contador < (totalCartasNumeros + totalCartasBloqueo + totalCartasReversa)) {
            for(int i=0; i < totalCartasBloqueo; i++) {
                baraja[contador] = "BLOQUEO";
                ++contador;
            }

            for(int i=0; i < totalCartasReversa; i++) {
                baraja[contador] = "REVERSA";
                ++contador;
            }
        }
    }

    public static void crearOrdenTurnos() {  //metodo para crear el orden de turnos
        turnos = new String[jugadores.length+bots.length];
        for(int i=0; i< jugadores.length; i++) { //acomoda a jugadores
            turnos[i] = "Jugador "+(i+1);
        }
        for(int i=0; i < bots.length; i++) { // acomoda a bots
            turnos[i+jugadores.length] = "Bot "+(i+1);
        }
        dibujarLineaAsteriscos();
        System.out.println("..Orden de turnos..");
        for(int i=0; i< turnos.length; i++) {
            System.out.println((i+1)+". "+(turnos[i]));
        }
    }

    public static void barejear() {  //barajea la baraja
        dibujarLineaAsteriscos();
        System.out.println("Barajeando....");
        Random r = new Random();
        boolean barajear = true;
        while(barajear) {
            int random = r.nextInt(baraja.length);
            if(baraja[random] != null) {
                barajaFinal.add(baraja[random]);

                int cartasUsadas = (totalCartasRepartir * (jugadores.length + bots.length));
                int cartasRestantes = (totalCartasNumeros + totalCartasBloqueo + totalCartasReversa);
                if(barajaFinal.size() == (cartasRestantes - cartasUsadas)) {
                    barajear = false;
                }
            }
        }
        System.out.println("Termino de barejear....");

    }


    public static String tiroCarta(String turno, String posicion, String tipoJugador) { //metodo para tirar cartas
        int posicionTurno = Integer.valueOf(posicion);
        posicionTurno = posicionTurno - 1; // indice del arreglo

        int ultimaCartaJugador = mostrarCartasJugdaores(posicion, tipoJugador);
        System.out.println("");
        int ultimaCartaBot = mostrarCartasBots(posicion, tipoJugador);

        String cartaTirada = null;
        dibujarLineaAsteriscos();
        System.out.println("Turno del "+turno);

        do{
            String entrada=null;
            System.out.println(".....¿Que carta deseas tirar?....");
            System.out.println("....Para comer carta escriba COMER....");
            System.out.println(".........Ultima tirada: "+ultimaCartaTirada);
            if (tipoJugador.equals("bot")) {
                entrada=tirarBot(posicionTurno);
            }else{
                Scanner s = new Scanner(System.in);
                entrada = s.next();
            }

            if(entrada.equals("COMER")) {
                if(tipoJugador.equals("jugador")) {
                    for(int i = 0; i < cartasEntregadasJugadores[posicionTurno].length; i++) {
                        if(cartasEntregadasJugadores[posicionTurno][i] == null) {
                            cartasEntregadasJugadores[posicionTurno][i] = barajaFinal.poll();
                            cartaTirada = "COMIO";
                            break; //termina for
                        }
                    }
                }else {
                    for(int i = 0; i < cartasEntregadasBots[posicionTurno].length; i++) {
                        if(cartasEntregadasBots[posicionTurno][i] == null) {
                            cartasEntregadasBots[posicionTurno][i] = barajaFinal.poll();
                            cartaTirada = "COMIO";
                            break; //termina for
                        }
                    }
                }
            }else {
                boolean letrasInvalidas = validarLetras(entrada);
                boolean numerosValidos = validarNumeros(entrada);
                int numeroEntero=0;
                if(numerosValidos) {
                    numeroEntero = Integer.parseInt(entrada);
                }
                if(!letrasInvalidas || numerosValidos) {
                    if(primeraCarta || entrada.equals("BLOQUEO") || entrada.equals("REVERSA") || ultimaCartaTirada.equals("BLOQUEO") || ultimaCartaTirada.equals("REVERSA") ||  entrada.equals(ultimaCartaTirada)
                    || (numeroEntero+1)==Integer.parseInt(ultimaCartaTirada) || (numeroEntero-1)==Integer.parseInt(ultimaCartaTirada)
                    || (numeroEntero+9)==Integer.parseInt(ultimaCartaTirada) || (numeroEntero-9)==Integer.parseInt(ultimaCartaTirada)) {
                        if(tipoJugador.equals("jugador")) {
                            for(int i = 0; i < cartasEntregadasJugadores[posicionTurno].length; i++) {
                                if(cartasEntregadasJugadores[posicionTurno][i] != null &&
                                        cartasEntregadasJugadores[posicionTurno][i].equals(entrada)) {
                                    cartasEntregadasJugadores[posicionTurno][i] = null;
                                    cartaTirada = entrada;
                                    ultimaCartaTirada = entrada;
                                    primeraCarta = false;
                                    break;
                                }
                            }
                        }else {
                            for(int i = 0; i < cartasEntregadasBots[posicionTurno].length; i++) {
                                if(cartasEntregadasBots[posicionTurno][i] != null
                                        && cartasEntregadasBots[posicionTurno][i].equals(entrada)) {
                                    cartasEntregadasBots[posicionTurno][i] = null;
                                    cartaTirada = entrada;
                                    ultimaCartaTirada = entrada;
                                    primeraCarta = false;
                                    break;
                                }
                            }
                        }

                    }
                }

                if(cartaTirada == null){
                    System.out.println("...Cartas no valida...");
                }
            }
        }while(cartaTirada == null);

        if((ultimaCartaJugador+1) == 99 && tipoJugador.equals("jugador")) {
            System.out.println("Al "+posicion+" "+tipoJugador+" le queda una carta");
        }

        if((ultimaCartaBot+1) == 99 && tipoJugador.equals("bot")) {
            System.out.println("Al "+posicion+" "+tipoJugador+" le queda una carta");
        }

        return cartaTirada; //regresa la carta tirada

    }


    public static String tirarBot(int posicion){ //metodo tirar carta del bot
        String carta="COMER";
        for(int i=0; i<cartasEntregadasBots[posicion].length; i++){ //columnas
            if(cartasEntregadasBots[posicion][i]!=null){
                boolean esNumero=validarNumeros(cartasEntregadasBots[posicion][i]);
                int entero = 0;
                if(esNumero){
                    entero=Integer.parseInt(cartasEntregadasBots[posicion][i]);
                }
                //if para saber que carta tirar, si no se cumple ninguna regresa el valor de carta que es igual a "COMER"
                if(cartasEntregadasBots[posicion][i].equals("BLOQUEO") //posicion=bot e i=columnas=cartas
                        || cartasEntregadasBots[posicion][i].equals("REVERSA")
                        || cartasEntregadasBots[posicion][i].equals(ultimaCartaTirada)
                        || ultimaCartaTirada.equals("BLOQUEO") || ultimaCartaTirada.equals("REVERSA")
                        || (entero+1)==Integer.parseInt(ultimaCartaTirada) || (entero-1)==Integer.parseInt(ultimaCartaTirada)
                        || (entero+9)==Integer.parseInt(ultimaCartaTirada) || (entero-9)==Integer.parseInt(ultimaCartaTirada)){
                    carta=cartasEntregadasBots[posicion][i];
                    break;
                }
            }
        }
        return carta; //REGRESA VALOR DE LA CARTA
    }


    public static boolean validarNumeros(String numeroString) {  //METODO PARA LA VALIDACION DE NUMEROS
        boolean noValido = false;
        for(int i=0; i < numerosDisponibles.length; i++) {
            if(numeroString.equals(numerosDisponibles[i])) {
                noValido = true;
                break;
            }
        }
        return noValido;
    }

    //metodos para dibujar lineas
    public static void dibujarLinea() {
        System.out.println("----------------------------------------------");
    }
    public static void dibujarLineaAsteriscos() {
        System.out.println("*******************************************************************");
    }


}

