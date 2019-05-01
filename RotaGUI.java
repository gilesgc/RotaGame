import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;
public class RotaGUI extends JPanel
{
    private static final int GUIWidth = 600;
    private static final int GUIHeight = 600;
    private Rota rota = new Rota();
    private Chip[] chips = initChips();
    private Chip selectedChip;
    private Rota.ChipPlayer winner;
    private int chipsPlaced = 0;
    public RotaGUI(Color backColor) {
        setBackground(backColor);
        addMouseListener(new PanelListener());
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 5F));

        for(Chip c : chips)
            c.draw(g);
        
        winner = rota.getWinner();
        if(winner != Rota.ChipPlayer.NONE) {
            g.drawString((winner == Rota.ChipPlayer.PLAYER_1 ? "Red" : "Blue") + " wins!!", 0, 50);
        }
    }
    public static void main(String[] args) {
        JFrame gui = new JFrame();
        gui.setTitle("Rota GUI");
        gui.setSize(GUIWidth, GUIHeight);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        RotaGUI panel = new RotaGUI(Color.white);
        gui.getContentPane().add(panel);
        gui.setVisible(true);
    }
    private void updateChips() {
        for(Rota.Chip chip : rota.getChips())
            chips[chip.getPos() - 1].setPlayer(chip.getPlayer());
    }
    private void setChips(ChipMode fromMode, ChipMode toMode) {
        for(Chip c : chips) {
            if(c.getMode() == fromMode) c.setMode(toMode);
        }
    }
    private void clickChip(Chip c) {
        switch (c.getMode()) {
            case EMPTY: {
                if(rota.getWinner() != Rota.ChipPlayer.NONE)
                    break;
                if(chipsPlaced < 6) {
                    Rota.ChipPlayer chipPlayer = chipsPlaced++ % 2 == 0 ?
                        Rota.ChipPlayer.PLAYER_1 : Rota.ChipPlayer.PLAYER_2;
                    c.setPlayer(chipPlayer);
                    rota.getChip(c.num).setPlayer(chipPlayer);
                }
                break;
            }
            case FILLED: {
                if(chipsPlaced < 6 || rota.getWinner() != Rota.ChipPlayer.NONE)
                    break;
                selectedChip = c;
                setChips(ChipMode.SELECTED, ChipMode.FILLED);
                setChips(ChipMode.MOVE, ChipMode.EMPTY);
                c.setMode(ChipMode.SELECTED);
                for(int chip : rota.getPossibleMoves(c.num)) {
                    chips[chip - 1].setMode(ChipMode.MOVE);
                }
                break;
            }
            case SELECTED: {
                c.setMode(ChipMode.FILLED);
                setChips(ChipMode.MOVE, ChipMode.EMPTY);
                break;
            }
            case MOVE: {
                rota.moveChip(selectedChip.num, c.num);
                updateChips();
            }
        }
    }
    private Chip[] initChips() {
        Chip[] chips = new Chip[9];
        
        for(int i = 0; i < 8; i++) {
            chips[i] = new Chip(
                i + 1,
                280 + (int)(250*Math.sin(Math.PI/4 * i)),
                280 + (int)(250*Math.cos(Math.PI/4 * i))
            );
        }
        chips[8] = new Chip(9, 280, 280);
        
        return chips;
    }
    private class PanelListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if(winner != Rota.ChipPlayer.NONE) {
                rota.newGame();
                updateChips();
                chipsPlaced = 0;
                return;
            }
            for(Chip c : chips) {
                if(c.containsPoint(e.getX(), e.getY())) {
                    clickChip(c);
                }
            }
        }
    }
    private enum ChipMode {
        SELECTED, FILLED, EMPTY, MOVE
    }
    private class Chip extends Circle {
        private int x, y, num;
        private ChipMode chipMode;
        private Rota.ChipPlayer chipPlayer;
        public Chip(int num, int x, int y) {
            super(x, y, 25, Color.blue);
            this.x = x;
            this.y = y;
            this.num = num;
            this.chipMode = ChipMode.EMPTY;
            this.chipPlayer = Rota.ChipPlayer.NONE;
        }
        public void draw(Graphics g) {
            if(chipPlayer == Rota.ChipPlayer.PLAYER_1 || chipPlayer == Rota.ChipPlayer.PLAYER_2) {
                setColor(chipPlayer == Rota.ChipPlayer.PLAYER_1 ? Color.red : Color.blue);
                
                if(chipMode == ChipMode.FILLED)
                    setRadius(25);
                else if(chipMode == ChipMode.SELECTED)
                    setRadius(30);
                    
                super.fill(g);
            } else {
                if(chipMode == ChipMode.EMPTY) {
                    setRadius(10);
                    setColor(Color.gray);
                    super.draw(g);
                }
                else if(chipMode == ChipMode.MOVE) {
                    setRadius(15);
                    setColor(Color.green);
                    super.fill(g);
                }
            }
        }
        public void setMode(ChipMode cm) {
            chipMode = cm;
            repaint();
        }
        public void setPlayer(Rota.ChipPlayer cp) {
            chipPlayer = cp;
            chipMode = (chipPlayer == Rota.ChipPlayer.NONE) ? ChipMode.EMPTY : ChipMode.FILLED;
            repaint();
        }
        public ChipMode getMode() {
            return chipMode;
        }
    }
}
