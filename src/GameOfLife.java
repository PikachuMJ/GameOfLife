import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOfLife extends JFrame {
    private JPanel mainPanel;
    private JButton[][] cells;
    private boolean[][] grid;
    private int width;
    private int height;
    private Timer timer;
    private boolean isRunning;

    public GameOfLife(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new boolean[width][height];

        setTitle("Game of Life");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(width, height));
        add(mainPanel, BorderLayout.CENTER);

        initializeCells();
        setupButtons();

        setVisible(true);
    }

    private void initializeCells() {
        cells = new JButton[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new JButton();
                cells[i][j].setBackground(Color.WHITE);
                final int x = i;
                final int y = j;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        toggleCell(x, y);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e) && isRunning) {
                            toggleCell(x, y);
                        }
                    }
                });
                mainPanel.add(cells[i][j]);
            }
        }
    }

    private void setupButtons() {
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JButton clearButton = new JButton("Clear");
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 200);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation(speedSlider.getValue());
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearGrid();
            }
        });

        speedSlider.setMajorTickSpacing(200);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        controlPanel.add(new JLabel("Speed:"));
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void toggleCell(int x, int y) {
        grid[x][y] = !grid[x][y];
        cells[x][y].setBackground(grid[x][y] ? Color.BLACK : Color.WHITE);
    }

    private void startSimulation(int delay) {
        if (isRunning)
            return;

        isRunning = true;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextGeneration();
            }
        });
        timer.start();
    }

    private void stopSimulation() {
        if (timer != null) {
            timer.stop();
            isRunning = false;
        }
    }

    private void clearGrid() {
        stopSimulation();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = false;
                cells[i][j].setBackground(Color.WHITE);
            }
        }
    }

    private void nextGeneration() {
        boolean[][] nextGen = new boolean[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int neighbors = countNeighbors(i, j);
                if (grid[i][j]) {
                    nextGen[i][j] = (neighbors == 2 || neighbors == 3);
                } else {
                    nextGen[i][j] = (neighbors == 3);
                }
            }
        }

        grid = nextGen;
        updateCells();
    }

    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int neighborX = x + i;
                int neighborY = y + j;
                if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                    if (i != 0 || j != 0) {
                        if (grid[neighborX][neighborY]) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    private void updateCells() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j].setBackground(grid[i][j] ? Color.BLACK : Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameOfLife(30, 30);
            }
        });
    }
}
