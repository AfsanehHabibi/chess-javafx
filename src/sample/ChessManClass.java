package sample;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import static sample.Main.objectOutputStream;

public abstract class ChessManClass {
    public BorderPane borderPane = new BorderPane();
    Color color;
    Image chess_picture;
    ImageView imageview = new ImageView();

    abstract boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board);

    void finalMove(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board,
                          GridPane gridPane, boolean send_data) {
        gridPane.getChildren().remove(chess_board[i_dest][j_dest].borderPane);
        gridPane.getChildren().remove(chess_board[i_src][j_src].borderPane);
        boolean x=!(chess_board[i_dest][j_dest] instanceof Empty);
        chess_board[i_dest][j_dest] = chess_board[i_src][j_src].clone();
        chess_board[i_src][j_src] = new Empty();
        gridPane.add(chess_board[i_src][j_src].borderPane, j_src, i_src);
        gridPane.add(chess_board[i_dest][j_dest].borderPane, j_dest, i_dest);
        if (send_data) {
            sendData(i_src, j_src, i_dest, j_dest,chess_board,x);
            Chess.nullClicks(chess_board);
        }
    }

    public abstract Image getChess_picture();

    public Color getColor() {
        if (getChess_picture() == null)
            return null;
        return color;
    }

    @Override
    protected abstract ChessManClass clone() ;

boolean move(int i_src,int j_src,int i_dest,int j_dest,ChessManClass[][] chess_board){
        if(IsProtectedByOpponent(chess_board,color)){
            return ReIsProtectedByOpponent(chess_board,color,i_src,j_src,i_dest,j_dest);
        }
        return canMoveNormal(i_src,j_src,i_dest,j_dest,chess_board);
    }

    private void turnOffBorder() {
        borderPane.setStyle(null);
    }

    public void setOnMouseClick(int i, int j, ChessManClass[][] chessboard, GridPane gridPane) {
        int finalJ = j;
        int finalI = i;
        chessboard[i][j].borderPane.setOnMouseClicked((event) -> {
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    chessboard[k][l].turnOffBorder();
                }
            }
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    if (chessboard[finalI][finalJ].move(finalI, finalJ, k, l, chessboard) ) {
                        chessboard[k][l].borderPane.setStyle(
                                " -fx-border-color: black ;-fx-border-width: 4;-fx-border-style: solid;");
                        System.out.println(finalI + " " + finalJ + " " + k + " " + l);
                        int finalL = l;
                        int finalK = k;
                        chessboard[k][l].borderPane.setOnMouseClicked((event1) -> {
                            System.out.println("second");
                            chessboard[finalI][finalJ].finalMove(finalI, finalJ, finalK, finalL, chessboard
                                    , gridPane, true);
                        });
                    }
                }
            }
        });
    }

    private int positionJKing(ChessManClass[][] chess_board, Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chess_board[i][j].getColor())
                    if (chess_board[i][j] instanceof King)
                        return j;
            }
        }
        return 0;
    }
    private int positionIKing(ChessManClass[][] chess_board, Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chess_board[i][j].getColor())
                    if (chess_board[i][j] instanceof King)
                        return i;
            }
        }
        return 0;
    }
    private boolean ReIsProtectedByOpponent(ChessManClass[][] chess_board, Color color, int i_src, int j_src, int i_dest, int j_dest) {
        if( !canMoveNormal(i_src,j_src,i_dest,j_dest,chess_board))
            return false;
        ChessManClass[][] copy = new ChessManClass[8][8];
        GridPane gridPane = new GridPane();
        for (int i = 0; i <8 ; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j]=chess_board[i][j].clone();
            }
        }
        copy[i_src][j_src].finalMove(i_src, j_src, i_dest, j_dest, copy, gridPane, false);
        return !IsProtectedByOpponent(copy, color);

    }

   public boolean IsProtectedByOpponent(ChessManClass[][] chess_board, Color color){
        int i_king= positionIKing(chess_board,color);
        int j_king= positionJKing(chess_board,color);
       for (int i = 0; i < 8; i++) {
           for (int j = 0; j < 8; j++) {
               if (chess_board[i][j].canMoveNormal(i,j,i_king,j_king,chess_board)&&
                       chess_board[i][j].getColor()!=color){
                   return true;
               }
           }
       }
       return false;
   }

   private char findName(int j_dest){
        return (char)(j_dest+'a');
   }
   public abstract char getChessPieceName();
    void sendData(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board,boolean isCaptured){
        String notation="";
        notation+=chess_board[i_dest][j_dest].getChessPieceName();
        if(isCaptured)
            notation+='x';
        notation+=chess_board[i_dest][j_dest].findName(j_dest);
        System.out.println(chess_board[i_dest][j_dest].findName(j_dest));
        int temp=8-i_dest;
        notation+=temp;
        Controller.copy_notations.setText(Controller.copy_notations.getText()+" "+notation);
        try {
            objectOutputStream.writeUTF(i_src+""+j_src+""+i_dest+""+j_dest+"@"+notation);
            objectOutputStream.flush();
            Controller.count = !Controller.count;
            System.out.println("send "+i_src+j_src+i_dest+j_dest+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
