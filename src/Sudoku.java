import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class Sudoku {
    class Tile extends JButton {
        int linha;
        int coluna;
        Tile(int linha, int coluna){
            this.linha = linha;
            this.coluna = coluna;
        }

    }

    int largura = 800;
    int altura = 650;

    JFrame frame = new JFrame("Sudoku");
    JLabel textoLabel = new JLabel();
    JPanel textoPainel = new JPanel();
    JPanel painelQuadrado = new JPanel(); 


   String[] puzzle = {
        "53--7----",
        "6--195---",
        "-98----6-",
        "8---6---3",
        "4--8-3--1",
        "7---2---6",
        "-6----28-",
        "---419--5",
        "----8--79"
    };

    String[] solution = {
        "534678912",
        "672195348",
        "198342567",
        "859761423",
        "426853791",
        "713924856",
        "961537284",
        "287419635",
        "345286179"
    };



    Sudoku(){
        //JANELA(FRAME)
        frame.setVisible(true);//visivel na tela
        frame.setSize(largura, altura);// tamanho
        frame.setResizable(false);//não da para mudar o tamanho
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//indica que o programa inteiro 
        //será encerrado quando o usuário clicar no X da janela.
        frame.setLocationRelativeTo(null);//centraliza a janela ao abrir
        frame.setLayout(new BorderLayout());//Esse layout divide a janela em 5 
        //regiões: NORTH, SOUTH, EAST, WEST e CENTER.
    

        //TEXTO
        textoLabel.setFont(new Font("Serif", Font.BOLD, 30));//fonte /estilo/ tamanho
        textoLabel.setHorizontalAlignment(JLabel.CENTER); //centralizar o texto no meio
        textoLabel.setText("Sudoku: 0");// texto

        //PAINEL
        textoPainel.setLayout(new BorderLayout());//possibilita organizar por regiões
        textoPainel.add(textoLabel, BorderLayout.CENTER);// adciona o textoLabel no centro do painel
        frame.add(textoPainel, BorderLayout.NORTH);// coloca o painel na parte superior


        //BOARD
        painelQuadrado.setLayout(new GridLayout(9,9));
        setupTiles();
        frame.add(painelQuadrado, BorderLayout.CENTER);
        frame.setVisible(true);
    }

   void setupTiles(){
    for (int linha = 0; linha < 9; linha++){
        for(int coluna = 0; coluna < 9; coluna++){
            Tile tile = new Tile(linha, coluna);
            char tileChar = puzzle[linha].charAt(coluna);

            if (tileChar != '-') {
                tile.setText(String.valueOf(tileChar));
                tile.setEnabled(false); // campo fixo, não editável
            } else {
                tile.setText("-"); // campo vazio para o jogador preencher
            }

            tile.setFont(new Font("SansSerif", Font.BOLD, 20));
            tile.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // borda visível
            painelQuadrado.add(tile);
            
        }
    }
}

}
