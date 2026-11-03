import control.ColaDeAlertas;
import model.Alerta;
import model.Alerta.Severidad;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ColaDeAlertasTest {

    private Alerta alerta(String id, Severidad s) {
        return new Alerta(id, s, "prueba");
    }

    @Test
    public void testCriticaSaleAntesQueAdvertencia() {
        ColaDeAlertas heap = new ColaDeAlertas.Monticulo();
        heap.encolar(alerta("A", Severidad.ADVERTENCIA));
        heap.encolar(alerta("B", Severidad.CRITICA));
        heap.encolar(alerta("C", Severidad.INFORMATIVA));
        assertEquals("B", heap.desencolar().getIdSensor());
        assertEquals("A", heap.desencolar().getIdSensor());
        assertEquals("C", heap.desencolar().getIdSensor());
        assertNull(heap.desencolar());
    }

    @Test
    public void testEmpateEnSeveridadSaleFIFO() {
        for (ColaDeAlertas cola : new ColaDeAlertas[] {
                new ColaDeAlertas.Monticulo(), new ColaDeAlertas.ListaOrdenada()}) {
            cola.encolar(alerta("PRIMERA", Severidad.CRITICA));
            cola.encolar(alerta("SEGUNDA", Severidad.CRITICA));
            cola.encolar(alerta("TERCERA", Severidad.CRITICA));
            assertEquals("PRIMERA", cola.desencolar().getIdSensor());
            assertEquals("SEGUNDA", cola.desencolar().getIdSensor());
            assertEquals("TERCERA", cola.desencolar().getIdSensor());
        }
    }

    @Test
    public void testVaciaDevuelveNull() {
        ColaDeAlertas heap = new ColaDeAlertas.Monticulo();
        assertTrue(heap.estaVacia());
        assertNull(heap.desencolar());
    }

    /**
     * Con cantidades distintas, el montículo y la lista ordenada deben dar
     * exactamente la misma secuencia, y la severidad nunca debe subir.
     */
    @Test
    public void testMonticuloCoincideConListaParaVariasCantidades() {
        int[] tamanos = {1, 2, 3, 15, 16, 17, 100, 1000};
        for (int n : tamanos) {
            Random rnd = new Random(n);
            List<Alerta> entrada = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Severidad s = Severidad.values()[rnd.nextInt(3)];
                entrada.add(alerta("E" + i, s));
            }
            List<Alerta> salidaHeap = drenar(new ColaDeAlertas.Monticulo(), entrada);
            List<Alerta> salidaLista = drenar(new ColaDeAlertas.ListaOrdenada(), entrada);

            assertEquals(n, salidaHeap.size());
            assertEquals(salidaLista, salidaHeap, "tamano " + n);
            for (int i = 1; i < salidaHeap.size(); i++) {
                assertTrue(salidaHeap.get(i - 1).getSeveridad().ordinal()
                        >= salidaHeap.get(i).getSeveridad().ordinal());
            }
        }
    }

    private List<Alerta> drenar(ColaDeAlertas cola, List<Alerta> entrada) {
        for (Alerta a : entrada) {
            cola.encolar(a);
        }
        List<Alerta> salida = new ArrayList<>();
        while (!cola.estaVacia()) {
            salida.add(cola.desencolar());
        }
        return salida;
    }
}
