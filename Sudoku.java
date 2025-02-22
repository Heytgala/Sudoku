import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Sudoku {
    class Tile extends JButton {
        int r;
        int c;
        Tile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int boardWidth = 600;
    int boardHeight = 650;
    JFrame frame = new JFrame("Sudoku");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JButton numSelected = null;
    int errors = 0;
    boolean gameCompleted = false;

    int[][] board = new int[9][9];      // To store the puzzle (with some numbers removed)
    int[][] solution = new int[9][9];   // To store the complete solution (no numbers removed)
    int cellsToRemove;                  // To dynamically store the number of cells to remove

    public Sudoku(String difficulty) {
        setDifficulty(difficulty);
        generatePuzzle();
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Sudoku: 0");

        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9, 9));
        setupTiles();
        frame.add(boardPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout(1, 9));
        setupButtons();
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 20));
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                errors = 0;
                textLabel.setText("Sudoku: 0");
                gameCompleted = false;
                generatePuzzle();
                resetBoard();
            }
        });
        frame.add(resetButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    void setDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                cellsToRemove = 30;  
                break;
            case "medium":
                cellsToRemove = 40;  
                break;
            case "hard":
                cellsToRemove = 50;  
                break;
            default:
                cellsToRemove = 40; 
        }
    }

    void setupTiles() {
        boardPanel.removeAll();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = new Tile(r, c);
                if (board[r][c] != 0) {
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setText(String.valueOf(board[r][c]));
                    tile.setBackground(Color.lightGray);
                    tile.setEnabled(false); 
                } else {
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                }
                tile.setFocusable(false);
                boardPanel.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameCompleted) {
                            return; 
                        }
                        Tile tile = (Tile) e.getSource();
                        int r = tile.r;
                        int c = tile.c;
                        if (numSelected != null) {
                            if (tile.getText() != "") {
                                return;
                            }
                            String numSelectedText = numSelected.getText();
                            int enteredNum = Integer.parseInt(numSelectedText);

                            if (enteredNum == solution[r][c]) {
                                tile.setText(numSelectedText);
                                board[r][c] = enteredNum;
                                if (checkGameCompletion()) {
                                    textLabel.setText("You Win!");
                                    gameCompleted = true;
                                }
                            } else {
                                errors += 1;
                                textLabel.setText("Sudoku: " + String.valueOf(errors));
                            }
                        }
                    }
                });
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    void setupButtons() {
        for (int i = 1; i < 10; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setText(String.valueOf(i));
            button.setFocusable(false);
            button.setBackground(Color.white);
            buttonsPanel.add(button);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (numSelected != null) {
                        numSelected.setBackground(Color.white);
                    }
                    numSelected = button;
                    numSelected.setBackground(Color.lightGray);
                }
            });
        }
    }

    void resetBoard() {
        setupTiles();
    }

    void generatePuzzle() {
        fillBoard(); 
        createPuzzle();  
    }

    void fillBoard() {
        fillBoardHelper(0, 0);  
        copySolution();  
    }

    boolean fillBoardHelper(int row, int col) {
        if (row == 9) {
            return true;
        }
        if (col == 9) {
            return fillBoardHelper(row + 1, 0);
        }
        if (board[row][col] != 0) {
            return fillBoardHelper(row, col + 1);
        }

        for (int num = 1; num <= 9; num++) {
            if (isSafeToPlace(row, col, num)) {
                board[row][col] = num;
                if (fillBoardHelper(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    boolean isSafeToPlace(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    void createPuzzle() {
        Random rand = new Random();
        int cellsToRemoveLocal = cellsToRemove;  
        while (cellsToRemoveLocal > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);

            if (board[row][col] != 0) {
                board[row][col] = 0;
                cellsToRemoveLocal--;
            }
        }
    }

    void copySolution() {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(board[i], 0, solution[i], 0, 9);
        }
    }

    boolean checkGameCompletion() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != solution[r][c]) {
                    return false; 
                }
            }
        }
        return true; 
    }
}
