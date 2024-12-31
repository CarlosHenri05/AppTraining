import javazoom.jl.player.Player;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class AppPlayer implements ActionListener {

    JButton showQueueButton = new JButton("Mostrar fila de músicas");
    JButton queueButton = new JButton("Criar fila de músicas ");
    JButton selectButton = new JButton("Selecione uma música mp3");
    JButton playButton = new JButton();
    JButton pauseButton = new JButton();
    JButton resumeButton = new JButton();
    JButton stopButton = new JButton();

    JLabel songName;
    JFileChooser fileChooser;
    FileInputStream fileInputStream;
    BufferedInputStream bufferedInputStream;
    File myFile = null;
    String filename, filePath;
    long totalLength, pauseLength, actualLength;
    Player player;
    Thread playThread, resumeThread;
    Queue<File> musicQueue;

    public AppPlayer() {
        musicQueue = new LinkedList<>();
        initUI();
        adicionarAcoes();

        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);
    }

    public void initUI() {
        songName = new JLabel(" ", SwingConstants.CENTER);

        JPanel painelPlayer = new JPanel(); // Painel de seleção de musicas
        JPanel painelControle = new JPanel(); // Painel de controle das músicas

        //  Criando os botões

        ImageIcon playIcon = new ImageIcon("music-player-icons\\play-button.png");
        ImageIcon pauseIcon = new ImageIcon("music-player-icons\\pause-button.png");
        ImageIcon stopIcon = new ImageIcon("music-player-icons\\stop-button.png");
        ImageIcon resumeIcon = new ImageIcon("music-player-icons\\resume-button.png");
        ImageIcon appIcon = new ImageIcon("music-player-icons\\song-icon.png");

        playButton = new JButton(playIcon);
        pauseButton = new JButton(pauseIcon);
        resumeButton = new JButton(resumeIcon);
        stopButton = new JButton(stopIcon);


        // Arrumando o layout do player mp3

        painelPlayer.setLayout(new GridLayout(4, 1));

        // Adicionando componentes no painel do player
        painelPlayer.add(selectButton); // Adicionando o botão de selecionar músicas
        painelPlayer.add(songName); // Adicionando nome da música
        painelPlayer.add(queueButton);
        painelPlayer.add(showQueueButton);

        // Arrumando o painel de controle
        painelControle.setLayout(new GridLayout(1, 4));

        // Adicionando componentes no painel de controle
        painelControle.add(playButton);
        painelControle.add(pauseButton);
        painelControle.add(resumeButton);
        painelControle.add(stopButton);

        //Cor do background dos botões
        playButton.setBackground(Color.WHITE);
        stopButton.setBackground(Color.white);
        resumeButton.setBackground(Color.white);
        pauseButton.setBackground(Color.white);


        // Criação da janela e seu título
        JFrame frame = new JFrame();

        frame.setTitle("Tocador de música do Carlin");
        frame.setIconImage(appIcon.getImage());

        // Adicionando os paineis de controle e de player na janela criada
        frame.add(painelPlayer, BorderLayout.NORTH);
        frame.add(painelControle, BorderLayout.SOUTH);


        // Colocando informações, cores e atributos no frame (também conhecido como uma janela)
        frame.setBackground(Color.white);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    // Adicionar acoes

    public void adicionarAcoes() {
        selectButton.addActionListener(this);
        stopButton.addActionListener(this);
        playButton.addActionListener(this);
        resumeButton.addActionListener(this);
        pauseButton.addActionListener(this);
        queueButton.addActionListener(this);

    }


    // Metodo para performar as ações
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(queueButton)) {
            addToQueue();
        }

        if (e.getSource().equals(selectButton)) {
            selectMusic();
        }

        if (e.getSource().equals(playButton)) {
            playMusic();
        }

        if (e.getSource().equals(resumeButton)) {
            resumeMusic();
        }

        if (e.getSource().equals(pauseButton)) {
            pauseMusic();
        }

        if (e.getSource().equals(stopButton)) {
            stopMusic();
        }
        if (e.getSource().equals(showQueueButton)) {
            showQueue();
        }
    }


    // Thread que irá rodar o metodo de resume
    Runnable runnableResume = new Runnable() {
        @Override
        public void run() {
            try {
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);

                fileInputStream.skip(totalLength - pauseLength);

                player = new Player(bufferedInputStream);

                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // Thread de dar play
    Runnable runnablePlay = new Runnable() {
        @Override
        public void run() {
            try {
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                totalLength = fileInputStream.available();
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // Metodo que vai tocar a música
    public void playMusic() {
        if (myFile != null) {
            playThread.start();
            songName.setText("Música sendo tocada agora: " + filename);
        } else {
            songName.setText("Sem músicas selecionadas.");
        }
    }

    // Metodo que vai retomar a musica de onde foi pausada
    public void resumeMusic() {
        if (myFile != null) {
            resumeThread = new Thread(runnableResume);
            resumeThread.start();

            songName.setText("Música retomada");
        } else {
            songName.setText("Sem músicas selecionadas.");
        }
    }

    public void pauseMusic() {
        if (player != null) {
            try {
                pauseLength = fileInputStream.available();
                player.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void stopMusic() {
        if (player != null && filename != null) {
            player.close();
            songName.setText("");
        }
    }

    // Adicionar músicas a fila
    // Código com erro ou incompleto
    public void addToQueue() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Downloads"));
        fileChooser.setDialogTitle("Escolha a faixa mp3 a ser adicionada na fila");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter nome = new FileNameExtensionFilter("Arquivos MP3", "mp3");
        fileChooser.setFileFilter(nome);
        if (fileChooser.showOpenDialog(queueButton) == JFileChooser.APPROVE_OPTION) {
            myFile = fileChooser.getSelectedFile();
            filename = myFile.getName();
            filePath = myFile.getPath();
            if (filename.endsWith("mp3")) {
                musicQueue.add(myFile);
                songName.setText("Música " + filename + " adicionada com sucesso.");
            }
        }

    }

    public void selectMusic() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Downloads"));
        fileChooser.setDialogTitle("Escolha sua faixa mp3");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter nome = new FileNameExtensionFilter("ARQUIVOS MP3", "mp3");
        fileChooser.setFileFilter(nome);
        if (fileChooser.showOpenDialog(selectButton) == JFileChooser.APPROVE_OPTION) {
            myFile = fileChooser.getSelectedFile();
            filename = myFile.getName();
            filePath = myFile.getPath();
            songName.setText("Música selecionada: " + filename);
        }
    }

    // Código com erro ou incompleto
    public void playNextSong() {
        if (!musicQueue.isEmpty()) {
            myFile = musicQueue.poll();
            filename = myFile.getName();
            filePath = myFile.getPath();
            playMusic();
        } else {
            JOptionPane.showMessageDialog(null, "Deu erro parceirin, a fila tá vazia.");
        }
    }

    // Código com erro ou incompleto
     // To-do: Mostrar a fila de forma certa num pop-up ou possivelmente no app normal.
    public void showQueue() {
        if (musicQueue.isEmpty()) {
            songName.setText("Fila vazia.");
        } else {
            StringBuilder queueDisplay = new StringBuilder();
            for (File music : musicQueue) {
                queueDisplay.append(music.getName()).append("\n");
            }
            //queueArea.setText(queueDisplay.toString());
        }
    }

    public static void main(String[] args) {
        AppPlayer app = new AppPlayer();
    }
}
