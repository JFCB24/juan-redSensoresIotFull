import ingest.ColaDeIngesta;
import model.LecturaSensor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColaDeIngestaTest {

    private LecturaSensor lectura(int n) {
        return new LecturaSensor("EST-001", "2026-11-02 10:00", 20.0 + n, 50.0, n);
    }

    @Test
    public void testDescartaSoloCuandoEstaLlena() {
        ColaDeIngesta cola = new ColaDeIngesta(3);
        assertTrue(cola.encolar(lectura(1)));
        assertTrue(cola.encolar(lectura(2)));
        assertTrue(cola.encolar(lectura(3)));
        assertFalse(cola.encolar(lectura(4)));
        assertEquals(1, cola.descartadas());
        assertEquals(3, cola.tamano());
    }

    @Test
    public void testReciclaEspacioDespuesDeDesencolar() {
        ColaDeIngesta cola = new ColaDeIngesta(3);
        cola.encolar(lectura(1));
        cola.encolar(lectura(2));
        cola.encolar(lectura(3));
        assertEquals(1, cola.desencolar().getPm25());
        // Ahora hay un hueco al frente: debe aceptar y envolver al inicio
        assertTrue(cola.encolar(lectura(4)));
        assertEquals(2, cola.desencolar().getPm25());
        assertEquals(3, cola.desencolar().getPm25());
        assertEquals(4, cola.desencolar().getPm25());
        assertNull(cola.desencolar());
        assertEquals(0, cola.descartadas());
    }

    @Test
    public void testMantieneFIFOAVariasVueltas() {
        ColaDeIngesta cola = new ColaDeIngesta(5);
        int siguienteEsperado = 0;
        int siguienteLlegada = 0;
        for (int ronda = 0; ronda < 50; ronda++) {
            for (int i = 0; i < 4; i++) {
                assertTrue(cola.encolar(lectura(siguienteLlegada++)));
            }
            for (int i = 0; i < 4; i++) {
                assertEquals(siguienteEsperado++, cola.desencolar().getPm25());
            }
        }
        assertEquals(0, cola.descartadas());
        assertTrue(cola.estaVacia());
    }

    @Test
    public void testCapacidadInvalidaLanza() {
        assertThrows(IllegalArgumentException.class, () -> new ColaDeIngesta(0));
    }
}
