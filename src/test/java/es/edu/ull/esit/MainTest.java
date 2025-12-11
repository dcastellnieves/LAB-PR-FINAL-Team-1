package es.edu.ull.esit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private Main mainApp;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mainApp = new Main();
        
        // Initialize nodeList manually to avoid AWT init() issues
        Field nodeListField = Main.class.getDeclaredField("nodeList");
        nodeListField.setAccessible(true);
        Node[][] nodes = new Node[28][19];
        nodeListField.set(mainApp, nodes); // NODES_WIDTH=28, NODES_HEIGHT=19
        
        // Populate nodes for tests that expect them
        mainApp.createNodes(false);
        
        // Initialize statics used by listeners
        Field runTimeMainField = Main.class.getDeclaredField("runTimeMain");
        runTimeMainField.setAccessible(true);
        runTimeMainField.set(null, mainApp);

        Field algorithmField = Main.class.getDeclaredField("algorithm");
        algorithmField.setAccessible(true);
        algorithmField.set(null, new Algorithm());
        
        // Initialize MazeGenerator
        Field mazeGenField = Main.class.getDeclaredField("mazeGenerator");
        mazeGenField.setAccessible(true);
        mazeGenField.set(null, new MazeGenerator(28, 19, nodes));
        
        // Initialize start and target for search algorithms
        Field startField = Main.class.getDeclaredField("start");
        startField.setAccessible(true);
        startField.set(null, mainApp.getNodeAt(15, 15)); // 0,0

        Field targetField = Main.class.getDeclaredField("target");
        targetField.setAccessible(true);
        targetField.set(null, mainApp.getNodeAt(50, 15)); // 1,0
    }

    @Test
    void testCreateNodes() throws NoSuchFieldException, IllegalAccessException {
        // Access private nodeList
        Field nodeListField = Main.class.getDeclaredField("nodeList");
        nodeListField.setAccessible(true);
        Node[][] nodeList = (Node[][]) nodeListField.get(mainApp);

        assertNotNull(nodeList);
        assertTrue(nodeList.length > 0);
        assertTrue(nodeList[0].length > 0);
        assertNotNull(nodeList[0][0]);
        
        // Test resetting
        mainApp.createNodes(true);
        assertNotNull(nodeList[0][0]);
    }

    @Test
    void testGetNodeAt() {
        // Based on logic: x -= 15; x /= 35; y -= 15; y /= 35;
        // Node at 0,0 corresponds to pixel 15, 15
        Node node = mainApp.getNodeAt(15, 15);
        assertNotNull(node);
        assertEquals(0, node.getX());
        assertEquals(0, node.getY());

        // Test out of bounds
        // 0,0 maps to index 0,0 due to integer division (-15/35 = 0)
        // We need a value that results in < 0 index, e.g., -20 -> -35/35 = -1
        assertNull(mainApp.getNodeAt(-20, -20));
    }

    @Test
    void testIsMazeValid() throws NoSuchFieldException, IllegalAccessException {
        // Access private static start and target
        Field startField = Main.class.getDeclaredField("start");
        startField.setAccessible(true);
        Field targetField = Main.class.getDeclaredField("target");
        targetField.setAccessible(true);

        // Initially null (or whatever state previous tests left it in)
        startField.set(null, null);
        targetField.set(null, null);
        assertFalse(mainApp.isMazeValid());

        // Set start
        startField.set(null, new Node(0, 0));
        assertFalse(mainApp.isMazeValid());

        // Set target
        targetField.set(null, new Node(1, 1));
        assertTrue(mainApp.isMazeValid());
    }
    
    @Test
    void testResetCosts() throws NoSuchFieldException, IllegalAccessException {
        Field nodeListField = Main.class.getDeclaredField("nodeList");
        nodeListField.setAccessible(true);
        Node[][] nodeList = (Node[][]) nodeListField.get(mainApp);
        
        nodeList[0][0].setgCost(10.0);
        mainApp.resetCosts();
        assertEquals(Double.MAX_VALUE, nodeList[0][0].getgCost());
    }
    
    @Test
    void testClearSearchResults() throws NoSuchFieldException, IllegalAccessException {
        Field nodeListField = Main.class.getDeclaredField("nodeList");
        nodeListField.setAccessible(true);
        Node[][] nodeList = (Node[][]) nodeListField.get(mainApp);
        
        // Use a node that is NOT start or end (start is 0,0; target is 1,0)
        Node testNode = nodeList[5][5];
        
        // Simulate a searched node
        testNode.setColor(Color.BLUE); 
        assertTrue(testNode.isSearched());
        
        mainApp.clearSearchResults();
        assertFalse(testNode.isSearched());
        assertEquals(Color.LIGHT_GRAY, testNode.getColor());
    }

    @Test
    void testMousePressed() throws NoSuchFieldException, IllegalAccessException {
        // Ensure start/target are null or different to avoid self-clearing logic
        Field startField = Main.class.getDeclaredField("start");
        startField.setAccessible(true);
        startField.set(null, null);
        
        Field targetField = Main.class.getDeclaredField("target");
        targetField.setAccessible(true);
        targetField.set(null, null);

        // Simulate mouse click at 15, 15 (Node 0,0)
        // Button 1: Wall
        MouseEvent e1 = new MouseEvent(mainApp, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON1);
        mainApp.mousePressed(e1);
        
        Node node = mainApp.getNodeAt(15, 15);
        assertTrue(node.isWall());
        
        // Click again to clear
        mainApp.mousePressed(e1);
        assertFalse(node.isWall());
        
        // Button 2: Start
        MouseEvent e2 = new MouseEvent(mainApp, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 15, 15, 1, false, MouseEvent.BUTTON2);
        mainApp.mousePressed(e2);
        assertTrue(node.isStart());
        
        // Button 3: End
        MouseEvent e3 = new MouseEvent(mainApp, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 50, 15, 1, false, MouseEvent.BUTTON3); // Node 1,0
        mainApp.mousePressed(e3);
        Node node2 = mainApp.getNodeAt(50, 15);
        assertTrue(node2.isEnd());
    }

    @Test
    void testMenuActions() {
        JFrame frame = new JFrame();
        Main.SetupMenu(frame);
        
        JMenuBar bar = null;
        
        // Check content pane
        for (Component c : frame.getContentPane().getComponents()) {
            if (c instanceof JMenuBar) {
                bar = (JMenuBar) c;
                break;
            }
        }
        
        // If not found, check root pane's layered pane (sometimes added there in null layout?)
        if (bar == null) {
             for (Component c : frame.getRootPane().getLayeredPane().getComponents()) {
                if (c instanceof JMenuBar) {
                    bar = (JMenuBar) c;
                    break;
                }
            }
        }
        
        // If still not found, check frame components directly
        if (bar == null) {
            for (Component c : frame.getComponents()) {
                if (c instanceof JMenuBar) {
                    bar = (JMenuBar) c;
                    break;
                }
            }
        }
        
        assertNotNull(bar, "JMenuBar should be found in the frame");
        
        JMenu boardMenu = bar.getMenu(1);
        JMenu algorithmsMenu = bar.getMenu(2);
        
        // Test "New Board" (index 0 in Board menu)
        JMenuItem newGrid = boardMenu.getItem(0);
        newGrid.getActionListeners()[0].actionPerformed(new ActionEvent(newGrid, ActionEvent.ACTION_PERFORMED, "New Board"));
        
        // Test "Generate Maze" (index 1 in Board menu)
        JMenuItem generateMaze = boardMenu.getItem(1);
        generateMaze.getActionListeners()[0].actionPerformed(new ActionEvent(generateMaze, ActionEvent.ACTION_PERFORMED, "Generate Maze"));
        
        // Test "Clear Search Results" (index 2 in Board menu)
        JMenuItem clearSearch = boardMenu.getItem(2);
        clearSearch.getActionListeners()[0].actionPerformed(new ActionEvent(clearSearch, ActionEvent.ACTION_PERFORMED, "Clear Search Results"));
        
        // Test Algorithms (BFS at index 0)
        JMenuItem bfsItem = algorithmsMenu.getItem(0);
        bfsItem.getActionListeners()[0].actionPerformed(new ActionEvent(bfsItem, ActionEvent.ACTION_PERFORMED, "BFS"));
        
        // Test DFS (index 1)
        JMenuItem dfsItem = algorithmsMenu.getItem(1);
        dfsItem.getActionListeners()[0].actionPerformed(new ActionEvent(dfsItem, ActionEvent.ACTION_PERFORMED, "DFS"));
        
        // Test A* (index 2)
        JMenuItem astarItem = algorithmsMenu.getItem(2);
        astarItem.getActionListeners()[0].actionPerformed(new ActionEvent(astarItem, ActionEvent.ACTION_PERFORMED, "A*"));
        
        // Test Dijkstra (index 3)
        JMenuItem dijkstraItem = algorithmsMenu.getItem(3);
        dijkstraItem.getActionListeners()[0].actionPerformed(new ActionEvent(dijkstraItem, ActionEvent.ACTION_PERFORMED, "Dijkstra"));
        
        // Test Greedy (index 4)
        JMenuItem greedyItem = algorithmsMenu.getItem(4);
        greedyItem.getActionListeners()[0].actionPerformed(new ActionEvent(greedyItem, ActionEvent.ACTION_PERFORMED, "Greedy"));
        
        // Test Bidirectional (index 5)
        JMenuItem bidirectionalItem = algorithmsMenu.getItem(5);
        bidirectionalItem.getActionListeners()[0].actionPerformed(new ActionEvent(bidirectionalItem, ActionEvent.ACTION_PERFORMED, "Bidirectional"));
    }

    @Test
    void testRender() {
        BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        mainApp.render(g2d);
        g2d.dispose();
    }

    @Test
    void testUnusedMouseEvents() {
        MouseEvent e = new MouseEvent(mainApp, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false);
        mainApp.mouseClicked(e);
        mainApp.mouseEntered(e);
        mainApp.mouseExited(e);
        mainApp.mouseReleased(e);
    }
}
