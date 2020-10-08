package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

import static sample.Main.objectOutputStream;

class Empty extends ChessManClass {
    Empty(){
        color=null;
        imageview=new ImageView();
        borderPane=new BorderPane(imageview);
    }
    @Override
    protected ChessManClass clone()  {
        return new Empty();
    }
    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        return false;
    }


    @Override
    public void finalMove(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board,
                          GridPane gridPane, boolean send_data) {

    }

    @Override
    public Image getChess_picture() {
        return null;
    }

    @Override
    public void setOnMouseClick(int i, int j, ChessManClass[][] chessboard, GridPane gridPane) {
    }

    @Override
    public char getChessPieceName() {
        return 0;
    }
}
class Bishop extends ChessManClass {

    Bishop(Color color) {
        this.color = color;
        if(color==Color.White)
            chess_picture = new Image("7WBishop.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BBishop.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        //imageview.autosize();
        borderPane=new BorderPane(imageview);
    }

    @Override
    protected ChessManClass clone() {
        return new Bishop(color);
    }

    @Override
    public char getChessPieceName() {
        return 'B';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
            if (Math.abs(i_dest - i_src) == Math.abs(j_dest - j_src) &&
                    chess_board[i_dest][j_dest].getColor()!=color) {
                if(i_dest>i_src && j_dest>j_src) {
                    for (int i = i_src+1 , j=j_src+1; i <i_dest && j<j_dest; i++ , j++) {
                        if(chess_board[i][j].getColor()!=null) {
                            return false;
                        }
                    }
                    return true;
                }else if(i_dest>i_src && j_dest<j_src) {
                    for (int i = i_src+1 , j=j_src-1; i <i_dest && j>j_dest; i++ , j--) {
                        if(chess_board[i][j].getColor()!=null) {
                            return false;
                        }
                    }
                    return true;
                }else if(i_dest<i_src && j_dest<j_src) {
                    for (int i = i_src-1 , j=j_src-1; i >i_dest && j>j_dest; i-- , j--) {
                        if(chess_board[i][j].getColor()!=null) {
                            return false;
                        }
                    }
                    return true;
                }else if(i_dest<i_src && j_dest>j_src) {
                    for (int i = i_src-1 , j=j_src+1; i >i_dest && j<j_dest; i-- , j++) {
                        if(chess_board[i][j].getColor()!=null) {
                            return false;
                        }
                    }
                    return true;
                }
            } return false;
    }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}


class King extends ChessManClass {

    King(Color color) {
        this.color = color;
        if(color==Color.White)
        chess_picture = new Image("7WKing.png", 35, 35, false, false);
        else
            chess_picture = new Image("7BKing.png", 35, 35, false, false);
        imageview.setImage(chess_picture);
        borderPane=new BorderPane(imageview);
    }
    @Override
    protected ChessManClass clone() {
        return new King(color);
    }

    @Override
    public char getChessPieceName() {
        return 'K';
    }


    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                && Math.abs(i_dest - i_src) <= 1 && Math.abs(j_dest - j_src) <= 1
                && !(i_dest == i_src && j_dest == j_src);
    }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}

class Knight extends ChessManClass {

    Knight(Color color) {
        this.color = color;

        if(color==Color.White)
            chess_picture = new Image("7WKnight.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BKnight.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane=new BorderPane(imageview);
    }

    @Override
    protected ChessManClass clone() {
        return new Knight(color);
    }

    @Override
    public char getChessPieceName() {
        return 'N';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        return chess_board[i_dest][j_dest].getColor() != color &&
                (Math.abs(i_dest - i_src) == 1 && Math.abs(j_dest - j_src) == 2 ||
                        Math.abs(i_dest - i_src) == 2 && Math.abs(j_dest - j_src) == 1);
    }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}

class Pawn extends ChessManClass {

    private boolean en_passent=false;
    Pawn(Color color) {
        this.color = color;
        if(color==Color.White)
            chess_picture = new Image("7WPawn.png", 40, 40, false, false);
        else
        chess_picture = new Image("7BPawn.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane=new BorderPane(imageview);
    }
    @Override
    protected ChessManClass clone() {
        return new Pawn(color);
    }

    @Override
    public char getChessPieceName() {
        return 0;
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        if(chess_board[i_dest][j_dest].getColor()!=chess_board[i_src][j_src].getColor()&&
                chess_board[i_dest][j_dest]instanceof Pawn&& i_dest==i_src &&Math.abs(j_dest-j_src)==1){
            if (((Pawn) chess_board[i_dest][j_dest]).en_passent)
                return true;
        }
        if (chess_board[i_dest][j_dest].getColor() ==Color.Black){
            return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                    && i_dest == i_src - 1 && (j_dest + 1 == j_src || j_dest - 1 == j_src);
        }
        else if (chess_board[i_dest][j_dest].getColor() ==Color.White){
            return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                    && i_dest - 1 == i_src && (j_dest + 1 == j_src || j_dest - 1 == j_src);
        }
        else if(color==Color.White) {
            return i_src == 6 && i_dest == 4 && j_dest == j_src || i_dest + 1 == i_src && j_dest == j_src;
        }
        else if(color==Color.Black) {
            return i_src == 1 && i_dest == 3 && j_dest == j_src || i_dest - 1 == i_src && j_dest == j_src;
        }
        return false;
    }

    @Override
    public void finalMove(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board
            , GridPane gridPane, boolean send_data) {
        gridPane.getChildren().remove(chess_board[i_dest][j_dest].borderPane);
        gridPane.getChildren().remove(chess_board[i_src][j_src].borderPane);
        boolean x=!(chess_board[i_dest][j_dest] instanceof Empty);
        if(i_dest==0 || i_dest==7){
            chess_board[i_dest][j_dest] = new Queen(color);
        }else
            chess_board[i_dest][j_dest] = new Pawn(color);
        en_passent = (i_src == 7 && i_dest == 5) || (i_src == 1 && i_dest == 3);
        chess_board[i_src][j_src] = new Empty();
        gridPane.add(chess_board[i_src][j_src].borderPane, j_src, i_src);
        gridPane.add(chess_board[i_dest][j_dest].borderPane, j_dest, i_dest);
        if(send_data) {
            sendData(i_src, j_src, i_dest, j_dest, chess_board,x);
            Chess.nullClicks(chess_board);
        }
        }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}

class Queen extends ChessManClass {

    Queen(Color color) {
        this.color = color;
        if(color==Color.White)
            chess_picture = new Image("7WQueen.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BQueen.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane=new BorderPane(imageview);
    }

    @Override
    protected ChessManClass clone() {
        return new Queen(color);
    }

    @Override
    public char getChessPieceName() {
        return 'Q';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        if (Math.abs(i_dest - i_src) == Math.abs(j_dest - j_src) &&
                chess_board[i_dest][j_dest].getColor()!=color) {
            if ((new Bishop(color)).canMoveNormal(i_src,j_src,i_dest,j_dest,chess_board)){
                return true;
            }
        } if((i_dest==i_src ^ j_dest==j_src)&&chess_board[i_dest][j_dest].getColor()!=color){
            return (new Rook(color)).canMoveNormal(i_src, j_src, i_dest, j_dest, chess_board);
        }
        return false;
    }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}


class Rook extends ChessManClass {

    Rook(Color color) {
        this.color = color;
        if(color==Color.White)
            chess_picture = new Image("7WRook.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BRook.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane=new BorderPane(imageview);
    }

    @Override
    public ChessManClass clone() {
        return new Rook(color);
    }

    @Override
    public char getChessPieceName() {
        return 'R';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board) {
        if((i_dest==i_src ^ j_dest==j_src)&&chess_board[i_dest][j_dest].getColor()!=color){
            if(i_dest<i_src){
                for (int i = i_src-1; i >i_dest ; i--) {
                    if(chess_board[i][j_dest].getColor()!=null) {
                        return false;
                    }
                }
                return true;
            }
            if(i_dest>i_src){
                for (int i = i_src+1; i <i_dest ; i++) {
                    if(chess_board[i][j_dest].getColor()!=null) {
                        return false;
                    }
                }
                return true;
            }if(j_dest<j_src){
                for (int j = j_src-1; j >j_dest ; j--) {
                    if(chess_board[i_dest][j].getColor()!=null) {
                        return false;
                    }
                }
                return true;
            }if(j_dest>j_src){
                for (int j = j_src+1; j <j_dest ; j++) {
                    if(chess_board[i_dest][j].getColor()!=null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Image getChess_picture() {
        return chess_picture;
    }
}

public class Chess extends Thread {
    Color color;
    private GridPane gridPane;
    public ChessManClass[][] chessboard = new ChessManClass[8][8];

    Chess(Color color, GridPane gridPane) {
        this.color = color;this.gridPane=gridPane;
    }

    @Override
    public void run() {
        //Platform.runLater(this::setBoard);
        setBoard();
            if(color!=null && color==Color.White)
            setClicks(color);
        super.run();
    }


    private boolean checkLose(Color color){
        boolean mat=true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chessboard[i][j].getColor())
                    if (chessboard[i][j] instanceof King) {
                        mat = false;
                        break;
                    }
            }
        }
        if(!mat && chessboard[0][0].IsProtectedByOpponent(chessboard,color)) {
            mat=true;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    for (int k = 0; k <8 ; k++) {
                        for (int l = 0; l <8 ; l++) {
                            if(chessboard[i][j].getColor()==color &&chessboard[i][j].move(i,j,k,l,chessboard)){
                                mat=false;
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if(mat){
            Thread send=new Thread(()->{
                try {
                        objectOutputStream.writeUTF("player checkmate");
                        objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            send.start();
            try {
                send.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mat;
    }
    public void setClicks(Color color){
        if(checkLose(color)){
            return;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(chessboard[i][j].color==color) {
                    chessboard[i][j].setOnMouseClick(i, j, chessboard, gridPane);
                }
            }
        }
    }

    static void nullClicks(ChessManClass[][] chessboard){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j].borderPane.setOnMouseClicked(null);
            }
        }
    }
    private void setBoard() {
            for (int i = 0; i < 8; i++) {
                chessboard[6][i] = new Pawn(Color.White);
            }
            chessboard[7][0] = new Rook(Color.White);
            chessboard[7][7] = new Rook(Color.White);
            chessboard[7][1] = new Bishop(Color.White);
            chessboard[7][6] = new Bishop(Color.White);
            chessboard[7][2] = new Knight(Color.White);
            chessboard[7][5] = new Knight(Color.White);
            chessboard[7][3] = new Queen(Color.White);
            chessboard[7][4] = new King(Color.White);
            for (int i = 2; i < 6; i++) {
                for (int j = 0; j < 8; j++) {
                    chessboard[i][j] = new Empty();
                }
            }
            for (int i = 0; i < 8; i++) {
                chessboard[1][i] = new Pawn(Color.Black);
            }
            chessboard[0][0] = new Rook(Color.Black);
            chessboard[0][7] = new Rook(Color.Black);
            chessboard[0][1] = new Bishop(Color.Black);
            chessboard[0][6] = new Bishop(Color.Black);
            chessboard[0][2] = new Knight(Color.Black);
            chessboard[0][5] = new Knight(Color.Black);
            chessboard[0][3] = new Queen(Color.Black);
            chessboard[0][4] = new King(Color.Black);
            try {
                setCells();
            }catch (IllegalStateException ignored){}
        }

    private void setCells() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gridPane.add(chessboard[i][j].borderPane, j, i);
            }
        }
    }
}
