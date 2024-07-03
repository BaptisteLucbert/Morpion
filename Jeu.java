import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Jeu extends JFrame implements ActionListener {
    private JButton[] lesBoutons;
    private char currentPlayer = 'X';
    private int scoreX = 0;
    private int scoreO = 0;

    private String playerX;
    private String playerO;

    private JButton btnJouer = new JButton("Rejouer");
    private JButton btnQuitter = new JButton("Quitter");
    private JLabel labelTour = new JLabel("Tour de: X", JLabel.CENTER);
    private JLabel labelScore = new JLabel("Score - X: 0 | O: 0", JLabel.CENTER);

    private JPanel panelGrille = new JPanel();
    private JPanel panelBoutons = new JPanel();
    private JPanel panelInfos = new JPanel();

    private ImageIcon iconX;
    private ImageIcon iconO;

    public Jeu(int taille) {
        playerX = JOptionPane.showInputDialog(this, "Entrez le prénom du joueur X:");
        playerO = JOptionPane.showInputDialog(this, "Entrez le prénom du joueur O:");

        if (playerX == null || playerX.isEmpty()) {
            playerX = "X";
        }
        if (playerO == null || playerO.isEmpty()) {
            playerO = "O";
        }

        try {
            iconX = new ImageIcon(ImageIO.read(new File("path/to/x.png")));
            iconO = new ImageIcon(ImageIO.read(new File("path/to/o.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setTitle("Morpion");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (taille < 3 || taille > 8) {
            taille = 3;
        }
        this.setBounds(200, 200, 100 * taille, 100 * taille + 150);
        this.setLayout(new GridBagLayout());

        lesBoutons = new JButton[taille * taille];

        // Panel pour la grille du morpion
        panelGrille.setLayout(new GridLayout(taille, taille));
        panelGrille.setBackground(new Color(70, 130, 180));
        this.setResizable(false);

        for (int i = 0; i < taille * taille; i++) {
            lesBoutons[i] = new JButton("-");
            lesBoutons[i].setFont(new Font("Arial", Font.BOLD, 40));
            lesBoutons[i].setBackground(Color.WHITE);
            lesBoutons[i].addActionListener(this);
            panelGrille.add(lesBoutons[i]);
        }

        // Panel pour les informations
        panelInfos.setLayout(new GridLayout(2, 1));
        panelInfos.add(labelTour);
        panelInfos.add(labelScore);
        labelTour.setFont(new Font("Arial", Font.BOLD, 20));
        labelScore.setFont(new Font("Arial", Font.BOLD, 20));
        labelTour.setOpaque(true);
        labelTour.setBackground(new Color(70, 130, 180));
        labelScore.setOpaque(true);
        labelScore.setBackground(new Color(70, 130, 180));

        // Panel pour les boutons
        panelBoutons.setLayout(new GridBagLayout());
        panelBoutons.setBackground(new Color(70, 130, 180));

        btnJouer.setFont(new Font("Arial", Font.PLAIN, 16));
        btnJouer.setMargin(new Insets(5, 20, 5, 20));
        btnJouer.addActionListener(this);
        btnQuitter.setFont(new Font("Arial", Font.PLAIN, 16));
        btnQuitter.setMargin(new Insets(5, 20, 5, 20));
        btnQuitter.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelBoutons.add(btnJouer, gbc);
        gbc.gridx = 1;
        panelBoutons.add(btnQuitter, gbc);

        // Ajouter les panels à la fenêtre principale
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        this.add(panelInfos, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        this.add(panelGrille, gbcMain);

        gbcMain.gridy = 2;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 0;
        gbcMain.weighty = 0;
        this.add(panelBoutons, gbcMain);

        this.setVisible(true);
        updateTourLabel();
        updateScore(); // Mise à jour du score dès le début du jeu
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

        if (source.equals(btnJouer)) {
            // Réinitialiser le jeu
            resetGame();
        } else if (source.equals(btnQuitter)) {
            // Quitter l'application
            System.exit(0);
        } else {
            // Gérer le clic sur un bouton de jeu
            for (int i = 0; i < lesBoutons.length; i++) {
                if (source.equals(lesBoutons[i]) && lesBoutons[i].getText().equals("-")) {
                    lesBoutons[i].setText(String.valueOf(currentPlayer));
                    lesBoutons[i].setIcon(currentPlayer == 'X' ? iconX : iconO);
                    lesBoutons[i].setEnabled(false);
                    if (checkWin()) {
                        JOptionPane.showMessageDialog(this, "Joueur " + (currentPlayer == 'X' ? playerX : playerO) + " a gagné !");
                        if (currentPlayer == 'X') {
                            scoreX++;
                        } else {
                            scoreO++;
                        }
                        updateScore();
                        resetGame();
                    } else if (checkTie()) {
                        JOptionPane.showMessageDialog(this, "Match nul !");
                        resetGame();
                    } else {
                        // Changer de joueur
                        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        updateTourLabel();
                    }
                }
            }
        }
    }

    private void resetGame() {
        // Réinitialiser le plateau de jeu
        for (JButton bouton : lesBoutons) {
            bouton.setText("-");
            bouton.setIcon(null);
            bouton.setEnabled(true);
        }
        currentPlayer = 'X';
        updateTourLabel();
    }

    private void updateScore() {
        labelScore.setText("Score - " + playerX + " (X): " + scoreX + " | " + playerO + " (O): " + scoreO);
    }

    private void updateTourLabel() {
        labelTour.setText("Tour de: " + (currentPlayer == 'X' ? playerX : playerO));
    }

    private boolean checkWin() {
        int size = (int) Math.sqrt(lesBoutons.length);
        return checkRows(size) || checkColumns(size) || checkDiagonals(size);
    }

    private boolean checkRows(int size) {
        for (int i = 0; i < lesBoutons.length; i += size) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (!lesBoutons[i + j].getText().equals(String.valueOf(currentPlayer))) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }
        return false;
    }

    private boolean checkColumns(int size) {
        for (int i = 0; i < size; i++) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (!lesBoutons[i + size * j].getText().equals(String.valueOf(currentPlayer))) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }
        return false;
    }

    private boolean checkDiagonals(int size) {
        boolean win1 = true;
        boolean win2 = true;
        for (int i = 0; i < size; i++) {
            if (!lesBoutons[i * (size + 1)].getText().equals(String.valueOf(currentPlayer))) {
                win1 = false;
            }
            if (!lesBoutons[(i + 1) * (size - 1)].getText().equals(String.valueOf(currentPlayer))) {
                win2 = false;
            }
        }
        return win1 || win2;
    }

    private boolean checkTie() {
        for (JButton bouton : lesBoutons) {
            if (bouton.getText().equals("-")) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new Jeu(3);
    }
}
