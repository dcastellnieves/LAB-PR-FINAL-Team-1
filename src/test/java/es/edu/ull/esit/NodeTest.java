package es.edu.ull.esit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import java.util.List;

/**
 * Test unitarios para la clase {@link Node}.
 * <p>
 * Valida el funcionamiento de:
 * <ul>
 *     <li>Constructores y manejo de posiciones</li>
 *     <li>Costos (gCost y fCost)</li>
 *     <li>Cambio de color mediante interacciones</li>
 *     <li>Métodos booleanos de estado</li>
 *     <li>Obtención de nodos vecinos válidos</li>
 *     <li>Operaciones básicas como setX, setY, clearNode, etc.</li>
 * </ul>
 */
public class NodeTest {

    /**
     * Prueba que el constructor con parámetros asigna correctamente
     * las posiciones X e Y y que los métodos getX() y getY()
     * realizan la conversión adecuada.
     */
    @Test
    public void testConstructorAndPositions() {
        Node n = new Node(50, 100);
        assertEquals((50 - 15) / 35, n.getX());
        assertEquals((100 - 15) / 35, n.getY());
    }

    /**
     * Verifica que los métodos setX() y setY() actualicen correctamente
     * las coordenadas internas del nodo.
     */
    @Test
    public void testSetXandY() {
        Node n = new Node();
        n.setX(70).setY(140);
        assertEquals((70 - 15) / 35, n.getX());
        assertEquals((140 - 15) / 35, n.getY());
    }

    /**
     * Comprueba que getgCost() y setgCost() funcionen correctamente.
     */
    @Test
    public void testgCost() {
        Node n = new Node();
        n.setgCost(12.5);
        assertEquals(12.5, n.getgCost());
    }

    /**
     * Comprueba que getFCost() y setFCost() funcionen correctamente.
     */
    @Test
    public void testFCost() {
        Node n = new Node();
        n.setFCost(9.9);
        assertEquals(9.9, n.getFCost());
    }

    /**
     * Verifica que el método estático distance() calcule correctamente
     * la distancia euclidiana entre dos nodos.
     */
    @Test
    public void testDistance() {
        Node a = new Node(0, 0);
        Node b = new Node(3, 4);
        assertEquals(5.0, Node.distance(a, b));
    }

    /**
     * Verifica que el método estático distance() maneje correctamente
     * valores que causarían desbordamiento de entero en la resta.
     */
    @Test
    public void testDistanceOverflow() {
        Node a = new Node();
        a.setX(Integer.MIN_VALUE).setY(0);
        Node b = new Node();
        b.setX(Integer.MAX_VALUE).setY(0);
        
        // La distancia debería ser (double)MAX - (double)MIN
        // 2147483647.0 - (-2147483648.0) = 4294967295.0
        double expected = (double)Integer.MAX_VALUE - (double)Integer.MIN_VALUE;
        assertEquals(expected, Node.distance(a, b), 0.001);
    }

    /**
     * Prueba que el método Clicked() cambie el color del nodo
     * dependiendo del código de botón recibido.
     */
    @Test
    public void testClickedChangesColor() {
        Node n = new Node();

        n.Clicked(1); // wall
        assertEquals(Color.BLACK, n.getColor());

        n.Clicked(2); // start
        assertEquals(Color.GREEN, n.getColor());

        n.Clicked(3); // end
        assertEquals(Color.RED, n.getColor());

        n.Clicked(4); // clear
        assertEquals(Color.LIGHT_GRAY, n.getColor());
    }

    /**
     * Verifica que setAsWall() establezca correctamente el color BLACK
     * y que isWall() lo detecte.
     */
    @Test
    public void testSetAsWall() {
        Node n = new Node();
        n.setAsWall();
        assertEquals(Color.BLACK, n.getColor());
        assertTrue(n.isWall());
    }

    /**
     * Verifica que clearNode() restablezca el color LIGHT_GRAY.
     */
    @Test
    public void testClearNode() {
        Node n = new Node();
        n.setColor(Color.RED);
        n.clearNode();
        assertEquals(Color.LIGHT_GRAY, n.getColor());
    }

    /**
     * Valida el funcionamiento de los métodos booleanos que identifican
     * el estado del nodo según su color.
     */
    @Test
    public void testColorFlags() {
        Node n = new Node();

        n.setColor(Color.BLACK);
        assertTrue(n.isWall());
        assertFalse(n.isStart());
        assertFalse(n.isEnd());

        n.setColor(Color.GREEN);
        assertTrue(n.isStart());

        n.setColor(Color.RED);
        assertTrue(n.isEnd());
        assertTrue(n.isPath());

        n.setColor(Color.LIGHT_GRAY);
        assertTrue(n.isPath());

        n.setColor(Color.BLUE);
        assertTrue(n.isSearched());

        n.setColor(Color.ORANGE);
        assertTrue(n.isSearched());

        n.setColor(Color.CYAN);
        assertTrue(n.isSearched());
    }

    /**
     * Prueba que getNeighbours() solo devuelva los nodos configurados
     * como caminos válidos (isPath() == true).
     */
    @Test
    public void testGetNeighbours() {
        Node center = new Node();

        Node left = new Node();
        Node right = new Node();
        Node up = new Node();
        Node down = new Node();
        Node blocked = new Node();

        left.setColor(Color.LIGHT_GRAY);
        right.setColor(Color.RED);     // Red es path
        up.setColor(Color.BLACK);      // Bloqueado
        down.setColor(Color.LIGHT_GRAY);
        blocked.setColor(Color.BLACK);

        center.setDirections(left, right, up, down);

        List<Node> neighbours = center.getNeighbours();

        assertEquals(3, neighbours.size());
        assertTrue(neighbours.contains(left));
        assertTrue(neighbours.contains(right));
        assertTrue(neighbours.contains(down));
        assertFalse(neighbours.contains(up));
        assertFalse(neighbours.contains(blocked));
    }

    /**
     * Prueba el método render de Node.
     * Se utiliza un BufferedImage para simular el contexto gráfico.
     */
    @Test
    public void testRender() {
        Node n = new Node(15, 15);
        n.setColor(Color.BLUE);
        
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = image.createGraphics();
        
        // Ejecutar render
        n.render(g2d);
        
        g2d.dispose();
        // No podemos verificar fácilmente los píxeles dibujados sin lógica compleja,
        // pero aseguramos que no lance excepciones.
    }

}