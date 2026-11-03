/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   BancoDeFlujo - Semana 6

   Cinco experimentos sobre las estructuras de la semana:
     1. Pila de acciones (LIFO): deshacer.
     2. Cola lineal sin reciclar: se llena y descarta lecturas.
     3. Cola circular: recicla el espacio y no descarta.
     4. Lista ordenada vs montículo: costo al crecer el numero de alertas.
     5. Empates de severidad: criterio de desempate.
   ============================================================ */

package experiment;

import control.ColaDeAlertas;
import control.PilaDeAcciones;
import ingest.ColaDeIngesta;
import model.Alerta;
import model.Alerta.Severidad;
import model.LecturaSensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BancoDeFlujo {

    private static final int CAPACIDAD_INGESTA = 1000;
    private static final int TAMANO_RAFAGA = 1000;

    public static void main(String[] args) {
        experimento1PilaDeAcciones();
        System.out.println();
        experimento2ColaLinealSinReciclar();
        System.out.println();
        experimento3ColaCircular();
        System.out.println();
        experimento4ListaVsMonticulo();
        System.out.println();
        experimento5Empate();
    }

    /** Exp 1: deshacer las ultimas tres acciones del operador. */
    public static void experimento1PilaDeAcciones() {
        System.out.println("=== Exp 1. La pila ===");
        PilaDeAcciones<String> pila = new PilaDeAcciones<>();
        pila.apilar("Activar EST-001");
        pila.apilar("Activar EST-003");
        pila.apilar("Activar EST-005");
        for (int i = 0; i < 3; i++) {
            pila.apilar("Desactivar EST-007");
        }
        for (int i = 1; i <= 3; i++) {
            System.out.println("deshacer " + i + " -> " + pila.desapilar());
        }
        System.out.println("Acciones que quedan: " + pila.tamano());
    }

    /**
     * Exp 2: cola lineal (arreglo sin reciclar). Cada posicion se usa una
     * sola vez, asi que cuando "fin" llega al final todo lo nuevo se descarta,
     * aunque el frente ya haya liberado espacio.
     */
    public static void experimento2ColaLinealSinReciclar() {
        System.out.println("=== Exp 2. La cola que se llena vacia ===");
        LecturaSensor[] buffer = new LecturaSensor[CAPACIDAD_INGESTA];
        int frente = 0;
        int fin = 0;
        int llegaron = 0;
        int procesadas = 0;
        int descartadas = 0;

        for (int ronda = 0; ronda < 5; ronda++) {
            for (int i = 0; i < TAMANO_RAFAGA; i++) {
                llegaron++;
                LecturaSensor lectura = generarLectura(llegaron);
                if (fin < CAPACIDAD_INGESTA) {
                    buffer[fin++] = lectura;
                } else {
                    descartadas++;
                }
            }
            while (frente < fin) {
                buffer[frente++] = null;
                procesadas++;
            }
        }
        System.out.println("Llegaron:        " + llegaron);
        System.out.println("Procesadas:      " + procesadas);
        System.out.println("DESCARTADAS:     " + descartadas);
        System.out.println("Frente=" + frente + " Fin=" + fin);
    }

    /** Exp 3: la misma rafaga de llegadas, pero con ColaDeIngesta circular. */
    public static void experimento3ColaCircular() {
        System.out.println("=== Exp 3. Cola circular corregida ===");
        ColaDeIngesta cola = new ColaDeIngesta(CAPACIDAD_INGESTA);
        int llegaron = 0;
        int procesadas = 0;
        long inicio = System.nanoTime();

        for (int ronda = 0; ronda < 200; ronda++) {
            for (int i = 0; i < TAMANO_RAFAGA; i++) {
                llegaron++;
                cola.encolar(generarLectura(llegaron));
            }
            while (cola.desencolar() != null) {
                procesadas++;
            }
        }
        long ms = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("Llegaron:    " + llegaron);
        System.out.println("Procesadas:  " + procesadas);
        System.out.println("DESCARTADAS: " + cola.descartadas());
        System.out.println("Tiempo total: ~" + ms + " ms");
    }

    /** Exp 4: lista ordenada vs montículo con distintas cantidades de alertas. */
    public static void experimento4ListaVsMonticulo() {
        System.out.println("=== Exp 4. Lista vs. montículo ===");
        System.out.printf("%-10s %-18s %-10s %-18s %-10s %s%n",
                "Alertas", "Lista (comp.)", "ms", "Heap (comp.)", "ms", "Mismo orden");
        int[] tamanos = {10_000, 50_000, 100_000};
        for (int n : tamanos) {
            List<Alerta> alertas = generarAlertas(n, 42L);

            ColaDeAlertas lista = new ColaDeAlertas.ListaOrdenada();
            long t0 = System.nanoTime();
            List<Alerta> ordenLista = vaciar(lista, alertas);
            long msLista = (System.nanoTime() - t0) / 1_000_000;

            ColaDeAlertas heap = new ColaDeAlertas.Monticulo();
            long t1 = System.nanoTime();
            List<Alerta> ordenHeap = vaciar(heap, alertas);
            long msHeap = (System.nanoTime() - t1) / 1_000_000;

            System.out.printf("%-10d %-18d %-10d %-18d %-10d %s%n",
                    n, lista.comparaciones(), msLista,
                    heap.comparaciones(), msHeap,
                    ordenLista.equals(ordenHeap));
        }
    }

    /** Exp 5: cinco alertas, tres de ellas criticas. Llegan en el orden del mapa mental. */
    public static void experimento5Empate() {
        System.out.println("=== Exp 5. El empate ===");
        List<Alerta> llegadas = new ArrayList<>();
        llegadas.add(new Alerta("EST-002", Severidad.CRITICA, "zona escolar"));
        llegadas.add(new Alerta("EST-005", Severidad.ADVERTENCIA, "zona comercial"));
        llegadas.add(new Alerta("EST-004", Severidad.CRITICA, "zona hospitalaria"));
        llegadas.add(new Alerta("EST-009", Severidad.INFORMATIVA, "zona parque"));
        llegadas.add(new Alerta("EST-001", Severidad.CRITICA, "zona residencial"));

        for (ColaDeAlertas cola : new ColaDeAlertas[] {
                new ColaDeAlertas.Monticulo(), new ColaDeAlertas.ListaOrdenada()}) {
            System.out.println(cola.getClass().getSimpleName() + ":");
            for (Alerta a : vaciar(cola, llegadas)) {
                System.out.println("  " + a);
            }
        }
    }

    /** Inserta todas las alertas y las extrae en orden de prioridad. */
    private static List<Alerta> vaciar(ColaDeAlertas cola, List<Alerta> alertas) {
        for (Alerta a : alertas) {
            cola.encolar(a);
        }
        List<Alerta> orden = new ArrayList<>(alertas.size());
        while (!cola.estaVacia()) {
            orden.add(cola.desencolar());
        }
        return orden;
    }

    private static List<Alerta> generarAlertas(int n, long semilla) {
        Random rnd = new Random(semilla);
        Severidad[] niveles = Severidad.values();
        List<Alerta> alertas = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String id = String.format("EST-%03d", rnd.nextInt(50) + 1);
            alertas.add(new Alerta(id, niveles[rnd.nextInt(niveles.length)], "evento " + i));
        }
        return alertas;
    }

    private static LecturaSensor generarLectura(int numero) {
        return new LecturaSensor("EST-001", "2026-11-02 10:00", 20.0, 50.0, numero % 100);
    }
}
