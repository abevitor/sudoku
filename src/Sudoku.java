import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;
import javax.sound.sampled.*;
import java.util.List;

public class Sudoku {
    Clip clip;
    FloatControl controleVolume;
    boolean isMuted = false;
    int musicaAtualIndex = -1; // Inicializado em -1 para começar no índice 0 na primeira música
    JSlider volumeSlider = new JSlider(-80, 6);

    List<String> playlist = Arrays.asList(
        "/audio/joao.wav",
        "/audio/kagefumi.wav",
        "/audio/ikwudls.wav",
        "/audio/logo_eu.wav",
        "/audio/kingston.wav"
    );

    // ✅ Botão customizado que armazena posição (linha, coluna)
    class Tile extends JButton {
        int linha;
        int coluna;

        Tile(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }
    }

    int largura = 800;
    int altura = 650;

    // ✅ Elementos da interface
    JFrame frame = new JFrame("Sudoku");
    JLabel textoLabel = new JLabel();
    JPanel textoPainel = new JPanel();
    JPanel painelQuadrado = new JPanel();
    JPanel botaoNumeros = new JPanel();
    JPanel painelSom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton botaoMute = new JButton("🔇");

    JButton numeroSelecionado = null;
    int errors = 0;
    int acertos = 0; // 🆕 Contador total de acertos, incluindo campos fixos

    // ✅ Tabuleiro incompleto
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

    // ✅ Solução completa
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

    Sudoku() {
        tocarProximaMusica();

        frame.setSize(largura, altura);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        textoLabel.setFont(new Font("Serif", Font.BOLD, 30));
        textoLabel.setHorizontalAlignment(JLabel.CENTER);
        textoLabel.setText("Sudoku: 0");

        textoPainel.setLayout(new BorderLayout());
        textoPainel.add(textoLabel, BorderLayout.CENTER);
        frame.add(textoPainel, BorderLayout.NORTH);

        painelQuadrado.setLayout(new GridLayout(9, 9));
        setupTiles();

        contarAcertosIniciais();
        frame.add(painelQuadrado, BorderLayout.CENTER);

        botaoNumeros.setLayout(new GridLayout(1, 9));
        setupButtons();
        frame.add(botaoNumeros, BorderLayout.SOUTH);

        botaoMute.setFocusable(false);
        botaoMute.addActionListener(e -> {
    if (controleVolume == null) return; // evita NullPointerException

    isMuted = !isMuted;

    if (isMuted) {
        controleVolume.setValue(controleVolume.getMinimum()); // volume mínimo (silêncio)
        botaoMute.setText("🔈");
    } else {
        controleVolume.setValue(volumeSlider.getValue());
        botaoMute.setText("🔇");
    }
});

volumeSlider.addChangeListener(e -> {
    if (!isMuted && controleVolume != null) {
        controleVolume.setValue(volumeSlider.getValue());
    }
});

        volumeSlider.setValue(-10);
        volumeSlider.addChangeListener(e -> {
            if (!isMuted && controleVolume != null) {
                controleVolume.setValue(volumeSlider.getValue());
            }
        });

        painelSom.add(botaoMute);
        painelSom.add(volumeSlider);
        textoPainel.add(painelSom, BorderLayout.EAST);

        frame.setVisible(true);
    }

    void contarAcertosIniciais() {
        for (int linha = 0; linha < 9; linha++) {
            for (int coluna = 0; coluna < 9; coluna++) {
                if (puzzle[linha].charAt(coluna) != '-') {
                    acertos++;
                }
            }
        }
    }

    void setupTiles() {
        for (int linha = 0; linha < 9; linha++) {
            for (int coluna = 0; coluna < 9; coluna++) {
                Tile tile = new Tile(linha, coluna);
                char tileChar = puzzle[linha].charAt(coluna);

                if (tileChar != '-') {
                    tile.setFont(new Font("Serif", Font.BOLD, 20));
                    tile.setText(String.valueOf(tileChar));
                    tile.setFocusable(false);
                    tile.setEnabled(false);
                    tile.setBackground(Color.lightGray);
                } else {
                    tile.setFont(new Font("Serif", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                    tile.setText(" ");
                }

                tile.setFont(new Font("SansSerif", Font.BOLD, 20));
                tile.setFocusPainted(false);

                if ((linha == 2 && coluna == 2) || (linha == 2 && coluna == 5)
                        || (linha == 5 && coluna == 2) || (linha == 5 && coluna == 5)) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 5, Color.black));
                } else if (linha == 2 || linha == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 5, 1, Color.black));
                } else if (coluna == 2 || coluna == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 5, Color.black));
                } else {
                    tile.setBorder(BorderFactory.createLineBorder(Color.black));
                }

                painelQuadrado.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Tile tile = (Tile) e.getSource();
                        int linha = tile.linha;
                        int coluna = tile.coluna;

                        if (numeroSelecionado != null) {
                            if (!tile.getText().equals(" ")) {
                                return;
                            }

                            String numeroSelecionadoTexto = numeroSelecionado.getText();
                            String solucao = String.valueOf(solution[linha].charAt(coluna));

                            if (solucao.equals(numeroSelecionadoTexto)) {
                                tile.setText(numeroSelecionadoTexto);
                                tile.setForeground(Color.BLUE);
                                acertos++;

                                if (acertos == 81) {
                                    mostrarParabens();
                                }
                            } else {
                                errors++;
                                textoLabel.setText("Sudoku: " + errors);
                            }
                        }
                    }
                });
            }
        }
    }

    void setupButtons() {
        for (int i = 0; i < 10; i++) {
            JButton botao = new JButton();
            botao.setFont(new Font("Serif", Font.BOLD, 20));
            botao.setText(String.valueOf(i));
            botao.setFocusable(false);
            botao.setBackground(Color.white);

            if (i == 0) {
                botao.setEnabled(false);
            }

            botaoNumeros.add(botao);

            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton botao = (JButton) e.getSource();
                    if (numeroSelecionado != null) {
                        numeroSelecionado.setBackground(Color.white);
                    }
                    numeroSelecionado = botao;
                    numeroSelecionado.setBackground(Color.lightGray);
                }
            });
        }
    }

    void mostrarParabens() {
        JOptionPane.showMessageDialog(frame, "🎉 Parabéns! Você completou o Sudoku!");

        JFrame frameSolucao = new JFrame("Solução do Sudoku");
        frameSolucao.setSize(400, 400);
        frameSolucao.setLocationRelativeTo(null);
        frameSolucao.setLayout(new GridLayout(9, 9));

        for (int linha = 0; linha < 9; linha++) {
            for (int coluna = 0; coluna < 9; coluna++) {
                JLabel label = new JLabel(
                        String.valueOf(solution[linha].charAt(coluna)),
                        SwingConstants.CENTER);
                label.setFont(new Font("Serif", Font.BOLD, 18));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                frameSolucao.add(label);
            }
        }

        frameSolucao.setVisible(true);
    }

    private void tocarProximaMusica() {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }

            musicaAtualIndex = (musicaAtualIndex + 1) % playlist.size();
            String musica = playlist.get(musicaAtualIndex);

            InputStream is = getClass().getResourceAsStream(musica);
            if (is == null) {
                System.err.println("❌ Música não encontrada: " + musica);
                return;
            }
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(Integer.MAX_VALUE);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                controleVolume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (!isMuted) {
                    controleVolume.setValue(volumeSlider.getValue());
                }
            } else {
                controleVolume = null;
            }

            clip.start();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.stop();
                    clip.close();
                    tocarProximaMusica();
                }
            });

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Sudoku();
    }
}
