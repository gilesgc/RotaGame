import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
public class Rota
{
    private Chip[] chips;
    private ChipPlayer previousMove;
    public Rota() {
        newGame();
    }
    public Chip getChip(int c) {
        return chips[c - 1];
    }
    public Chip[] getChips() {
        return chips;
    }
    public void newGame() {
        chips = initializeChips();
        previousMove = ChipPlayer.PLAYER_2;
    }
    public boolean moveChip(int fromPos, int toPos) {
        Chip chip = getChip(fromPos);
        Chip hole = getChip(toPos);
        
        if(!isMoveable(fromPos, toPos))
            return false;
        
        previousMove = chip.getPlayer();
        hole.setPlayer(chip.getPlayer());
        chip.setPlayer(ChipPlayer.NONE);
        
        return true;
    }
    public boolean allowedToMove(int chip) {
        return (previousMove != getChip(chip).getPlayer()) && getChip(chip).isFilled();
    }
    public boolean isMoveable(int chip) {
        if(!allowedToMove(chip))
            return false;
            
        return getPossibleMoves(chip).size() > 0;
    }
    public boolean isMoveable(int fromChip, int toChip) {
        if(!allowedToMove(fromChip))
            return false;
            
        return getPossibleMoves(fromChip).contains(toChip);
    }
    public int cwChip(int pos) {
        //return next clockwise chip
        if(pos == 9)
            return 9;
        else if(pos == 8)
            return 1;
        else
            return pos + 1;
    }
    public int ccwChip(int pos) {
        //return next counter-clockwise chip
        if(pos == 9)
            return 9;
        else if(pos == 1)
            return 8;
        else
            return pos - 1;
    }
    public int oppositeChip(int pos) {
        if(pos == 9)
            return 9;
        else if(pos >= 5)
            return pos - 4;
        else
            return pos + 4;
    }
    public ChipPlayer getWinner() {
        for(int c = 1; c <= 8; c++)
            if(isInRow(c))
                return getChip(c).getPlayer();
                
        return ChipPlayer.NONE;
    }
    private boolean isInRow(int c) {
        ChipPlayer cwPlayer =   getChip(cwChip(c)).getPlayer();
        ChipPlayer ccwPlayer =  getChip(ccwChip(c)).getPlayer();
        ChipPlayer oppPlayer =  getChip(oppositeChip(c)).getPlayer();
        ChipPlayer midPlayer =  chips[8].getPlayer();
        ChipPlayer chipPlayer = getChip(c).getPlayer();
        
        if(ccwPlayer == chipPlayer && chipPlayer == cwPlayer && chipPlayer != ChipPlayer.NONE)
            return true;
        if(oppPlayer == chipPlayer && midPlayer == chipPlayer && chipPlayer != ChipPlayer.NONE)
            return true;
        
        return false;
    }
    public List<Integer> getAdjacentMoves(int chip) {
        if(chip == 9)
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        else
            return Arrays.asList(cwChip(chip), ccwChip(chip), 9);
    }
    public List<Integer> getPossibleMoves(int chip) {
        List<Integer> moves = new ArrayList<>();
        
        if(!allowedToMove(chip))
            return moves;
        
        for(int m : getAdjacentMoves(chip)) {
            if(getChip(m).isEmpty())
                moves.add(m);
        }
        
        return moves;
    }
    public String toString() {
        String str = "";
        for(Chip c : chips)
            str += c.isFilled() + "   ";
        return str;
    }
    public Chip[] initializeChips() {
        Chip[] chips = new Chip[9];
        for(int i = 0; i < 9; i++)
            chips[i] = new Chip(i + 1);
        return chips;
    }
    enum ChipPlayer {
        NONE, PLAYER_1, PLAYER_2
    }
    class Chip {
        private int position;
        private ChipPlayer chipPlayer;
        public Chip(int position, ChipPlayer cp) {
            this.position = position;
            this.chipPlayer = cp;
        }
        public Chip(int position) {
            this(position, ChipPlayer.NONE);
        }
        public int getPos() {
            return position;
        }
        public boolean isFilled() {
            return chipPlayer != ChipPlayer.NONE;
        }
        public boolean isEmpty() {
            return chipPlayer == ChipPlayer.NONE;
        }
        public void setPlayer(ChipPlayer cp) {
            chipPlayer = cp;
        }
        public ChipPlayer getPlayer() {
            return chipPlayer;
        }
    }
}
