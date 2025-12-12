package es.edu.ull.esit;

import es.edu.ull.esit.algorithm.AstarAlgorithm;
import es.edu.ull.esit.algorithm.DijkstraAlgorithm;
import es.edu.ull.esit.algorithm.SearchAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests para verificar el manejo de interrupciones en los algoritmos de búsqueda.
 */
public class AlgorithmInterruptTest {

    private Node start, end;
    private Node[][] grid;
    private final int WIDTH = 10;
    private final int HEIGHT = 10;

    @BeforeEach
    void setUp() {
        grid = new Node[WIDTH][HEIGHT];
        
        // Initialize all nodes
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                grid[i][j] = new Node(15 + i * 35, 15 + j * 35);
            }
        }
        
        // Set up directional connections between nodes
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Node up = null, down = null, left = null, right = null;
                if (j > 0) up = grid[i][j - 1];
                if (j < HEIGHT - 1) down = grid[i][j + 1];
                if (i > 0) left = grid[i - 1][j];
                if (i < WIDTH - 1) right = grid[i + 1][j];
                grid[i][j].setDirections(left, right, up, down);
            }
        }
        
        start = grid[0][0];
        end = grid[WIDTH - 1][HEIGHT - 1];
    }

    @Test
    void testAstarInterruption() throws InterruptedException {
        runAlgorithmWithInterruption(new AstarAlgorithm());
    }

    @Test
    void testDijkstraInterruption() throws InterruptedException {
        runAlgorithmWithInterruption(new DijkstraAlgorithm());
    }

    private void runAlgorithmWithInterruption(SearchAlgorithm algorithm) throws InterruptedException {
        // Usamos un CountDownLatch para asegurarnos de que el hilo ha empezado
        CountDownLatch latch = new CountDownLatch(1);
        
        Thread searchThread = new Thread(() -> {
            latch.countDown();
            // Usamos un tiempo de espera largo para asegurar que le dé tiempo a entrar en sleep
            algorithm.search(start, end, WIDTH, HEIGHT, 1000);
        });

        searchThread.start();
        
        // Esperamos a que el hilo empiece
        latch.await();
        
        // Le damos un momento para que empiece a buscar y entre en sleep
        Thread.sleep(100);
        
        // Interrumpimos el hilo
        searchThread.interrupt();
        
        // Esperamos a que termine
        searchThread.join(2000);
        
        // Verificamos que el hilo terminó (no se quedó colgado)
        assertTrue(!searchThread.isAlive(), "El hilo debería haber terminado tras la interrupción");
        
        // Nota: Verificar Thread.interrupted() aquí es difícil porque el estado de interrupción
        // pertenece al hilo searchThread, y al terminar el hilo, ese estado se pierde o no es accesible fácilmente
        // desde fuera. Sin embargo, si el código captura InterruptedException y hace Thread.currentThread().interrupt(),
        // el hilo debería salir del sleep y continuar o terminar.
        // Lo importante es que no lance excepción al exterior y termine limpiamente.
    }
}
