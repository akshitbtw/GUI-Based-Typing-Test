/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package typingtestmain;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;

public class TypingTestProject extends javax.swing.JFrame implements ActionListener {

    public TypingTestProject() {
        initComponents();
        sampleTextArea.setEditable(false);
        sampleTextArea.setLineWrap(true);
        sampleTextArea.setWrapStyleWord(true);
        fileName = "easy.txt";
        sampleTextArea.setText(loadSampleText());
        sampleTextArea.setCaretPosition(0);
        sampleWordsLen = sampleTextArea.getText().split("\\s").length;

        userInput = new StringBuilder();

        timerProgressBar.setValue(60);
        timerProgressBar.setString("Time Remaining: " + 60 + " seconds");

        // add radio buttons to a group
        ButtonGroup group = new ButtonGroup();
        group.add(easyRadioButton);
        group.add(mediumRadioButton);
        group.add(hardRadioButton);

        setDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (easyRadioButton.isSelected()) {
                    selectDifficulty.setText("Selected Difficulty : EASY");
                    fileName = "easy.txt";
                } else if (mediumRadioButton.isSelected()) {
                    selectDifficulty.setText("Selected Difficulty : MEDIUM");
                    fileName = "medium.txt";
                } else if (hardRadioButton.isSelected()) {
                    selectDifficulty.setText("Selected Difficulty : HARD");
                    fileName = "hard.txt";
                }
                sampleTextArea.setText(loadSampleText());
                sampleTextArea.setCaretPosition(0);
                sampleWordsLen = sampleTextArea.getText().split("\\s").length;
            }
        });

        userInputTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == KeyEvent.VK_SPACE && inputWordIndex != sampleWordsLen) {
                    userInput.append(userInputTextField.getText().trim() + " ");
                    // split the sample text into words
                    String[] sampleWords = sampleTextArea.getText().split("\\s");

                    DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(Color.LIGHT_GRAY);

                    String currentWord = sampleWords[inputWordIndex];
                    
                    // highlight the word to be typed
                    if (inputWordIndex == 1) {
                        start = sampleWords[inputWordIndex-1].length()+1;
                        end = start + sampleWords[inputWordIndex].length();
                    }
                    else{
                        end = start + currentWord.length();
                    }
                    
                    // increment the input word index
                    inputWordIndex++;

                    try {
                        sampleTextArea.getHighlighter().removeAllHighlights();
                        sampleTextArea.getHighlighter().addHighlight(start, end, highlightPainter);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    
                    start = end +1;
                    
                    
                    userInputTextField.setText(null);
                    
                } else if (c == KeyEvent.VK_SPACE && inputWordIndex == sampleWordsLen) {
                    userInput.append(userInputTextField.getText().trim());
                    userInputTextField.setText(null);
                }
            }
        });

        startButton.addActionListener(this);
        resetButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            userInputTextField.setEditable(true);
            startButton.setEnabled(false);
            resetButton.setEnabled(true);
            inputWordIndex = 1;
            
            String[] sampleWords = sampleTextArea.getText().split("\\s");

            Highlighter highlighter = sampleTextArea.getHighlighter();
            highlighter.removeAllHighlights();
            DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(Color.LIGHT_GRAY);
            // highlight the first word
            try {
                highlighter.addHighlight(0, sampleWords[0].length(), highlightPainter);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }

            if (!isTimerRunning) {
                // start the timer
                isTimerRunning = true;
                timeLeft = 60;
                timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        timeLeft--;
                        timerProgressBar.setValue(timeLeft);
                        timerProgressBar.setString(timeLeft + " seconds remaining");
                        if (timeLeft == 0 || sampleTextArea.getText().equals(userInput.toString())) {
                            timer.stop();
                            isTimerRunning = false;
                            timeLeft = 60;
                            timerProgressBar.setValue(timeLeft);
                            timerProgressBar.setString("Time Remaining: " + timeLeft + " seconds");
                            resetButton.setEnabled(false);
                            showResults();
                        }
                    }
                });
                timer.start();

                // disable the start button
                startButton.setEnabled(false);

                // set the focus to the user input area
                userInputTextField.requestFocus();

            }
        }
        if (e.getSource() == resetButton) {
            // reset the text in the user input text area
            userInputTextField.setText("");
            userInput.setLength(0);
            sampleTextArea.getHighlighter().removeAllHighlights();
            // stop the timer and reset the timer label
            timer.stop();
            timeLeft = 60;
            isTimerRunning = false;
            timerProgressBar.setValue(timeLeft);
            timerProgressBar.setString("Time Remaining: " + timeLeft + " seconds");

            // enable the start button
            resetButton.setEnabled(false);
            startButton.setEnabled(true);
            timerProgressBar.setStringPainted(true);
            timerProgressBar.setString("60 seconds remaining");
        }
    }

    private void showResults() {
        // count the number of words in the sample text
        String[] sampleWords = sampleTextArea.getText().split("\\s+");

        // count the number of words in the user input
        String[] userWords = userInput.toString().split("\\s+");
        int userWordCount = userWords.length;

        // count the number of correct & incorrect words
        int correctWordCount = 0;
        int incorrectWordCount = 0;
        for (int i = 0; i < userWordCount; i++) {
            if (sampleWords[i].equals(userWords[i])) {
                correctWordCount++;
            } else {
                incorrectWordCount++;
            }
        }

        // calculate the accuracy
        double accuracy = (double) correctWordCount / (correctWordCount + incorrectWordCount) * 100;

        String wpmLabel = "WPM: " + correctWordCount;
        String correctWordsLabel = "Correct Words: " + correctWordCount;
        String incorrectWordsLabel = "Incorrect Words: " + incorrectWordCount;
        String accuracyLabel = "Accuracy: " + String.format("%.2f", accuracy) + "%";

        JOptionPane.showMessageDialog(this, wpmLabel + "\n" + correctWordsLabel + "\n" + incorrectWordsLabel + "\n" + accuracyLabel , "RESULTS", JOptionPane.INFORMATION_MESSAGE);

        // if user has logged in, then save the results in the database
        if (checkLogin) {
            try {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/typingTestDB", "root", "root");

                PreparedStatement st = null;
                if (fileName.equals("easy.txt")) {
                    st = (PreparedStatement) con.prepareStatement("INSERT INTO EASY_SCORES VALUES (? , ?)");
                } else if (fileName.equals("medium.txt")) {
                    st = (PreparedStatement) con.prepareStatement("INSERT INTO MEDIUM_SCORES VALUES (? , ?)");
                } else if (fileName.equals("hard.txt")) {
                    st = (PreparedStatement) con.prepareStatement("INSERT INTO HARD_SCORES VALUES (? , ?)");
                }

                st.setString(1, username);
                st.setInt(2, correctWordCount);
                st.executeUpdate();

            } catch (Exception ex) {
                System.out.println("Driver Class not found " + ex);
            }
        }

        userInputTextField.setText("");
        sampleTextArea.getHighlighter().removeAllHighlights();
        startButton.setEnabled(true);
        timerProgressBar.setValue(60);
        userInput.setLength(0);

    }

    private String loadSampleText() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> words = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineWords = line.split(" ");
                for (String word : lineWords) {
                    words.add(word);
                }
            }
            reader.close();
            inputStream.close();
            Collections.shuffle(words);
            return String.join(" ", words);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        optionsPanel = new javax.swing.JPanel();
        loginButton = new javax.swing.JButton();
        registerButton = new javax.swing.JButton();
        easyRadioButton = new javax.swing.JRadioButton();
        mediumRadioButton = new javax.swing.JRadioButton();
        hardRadioButton = new javax.swing.JRadioButton();
        selectDifficulty = new javax.swing.JLabel();
        setDifficultyButton = new javax.swing.JButton();
        statsButton = new javax.swing.JButton();
        highscoreButton = new javax.swing.JButton();
        typingPanel = new javax.swing.JPanel();
        sampleTextPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sampleTextArea = new javax.swing.JTextArea();
        userInputPanel = new javax.swing.JPanel();
        userInputTextField = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        timerProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Typing Test");
        setPreferredSize(new java.awt.Dimension(1366, 806));
        setResizable(false);

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Welcome, Guest", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 36))); // NOI18N

        loginButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        registerButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        registerButton.setText("Register");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        easyRadioButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        easyRadioButton.setSelected(true);
        easyRadioButton.setText(" Easy");

        mediumRadioButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        mediumRadioButton.setText(" Medium");

        hardRadioButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        hardRadioButton.setText(" Hard");

        selectDifficulty.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        selectDifficulty.setText("Selected Difficulty : EASY");

        setDifficultyButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        setDifficultyButton.setText("SET");

        statsButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        statsButton.setText("STATS");
        statsButton.setEnabled(false);
        statsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statsButtonActionPerformed(evt);
            }
        });

        highscoreButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        highscoreButton.setText("HIGHSCORES");
        highscoreButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));
        highscoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highscoreButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(statsButton))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(easyRadioButton)
                            .addComponent(mediumRadioButton)
                            .addComponent(hardRadioButton)))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionsPanelLayout.createSequentialGroup()
                                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(highscoreButton, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(optionsPanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(selectDifficulty))))
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(setDifficultyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(registerButton))
                .addGap(70, 70, 70)
                .addComponent(selectDifficulty)
                .addGap(18, 18, 18)
                .addComponent(easyRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mediumRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hardRadioButton)
                .addGap(18, 18, 18)
                .addComponent(setDifficultyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statsButton)
                .addGap(67, 67, 67)
                .addComponent(highscoreButton)
                .addGap(77, 77, 77))
        );

        typingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TYPING TEST", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 36))); // NOI18N

        sampleTextPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sample Text", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        sampleTextArea.setColumns(20);
        sampleTextArea.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        sampleTextArea.setRows(5);
        jScrollPane1.setViewportView(sampleTextArea);

        javax.swing.GroupLayout sampleTextPanelLayout = new javax.swing.GroupLayout(sampleTextPanel);
        sampleTextPanel.setLayout(sampleTextPanelLayout);
        sampleTextPanelLayout.setHorizontalGroup(
            sampleTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 904, Short.MAX_VALUE)
                .addContainerGap())
        );
        sampleTextPanelLayout.setVerticalGroup(
            sampleTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        userInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Type Here", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        userInputTextField.setEditable(false);
        userInputTextField.setBackground(new java.awt.Color(255, 255, 255));
        userInputTextField.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        javax.swing.GroupLayout userInputPanelLayout = new javax.swing.GroupLayout(userInputPanel);
        userInputPanel.setLayout(userInputPanelLayout);
        userInputPanelLayout.setHorizontalGroup(
            userInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userInputTextField)
                .addContainerGap())
        );
        userInputPanelLayout.setVerticalGroup(
            userInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userInputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        startButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        startButton.setText("Start");

        resetButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        resetButton.setText("Reset");
        resetButton.setEnabled(false);

        timerProgressBar.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        timerProgressBar.setForeground(new java.awt.Color(153, 0, 255));
        timerProgressBar.setMaximum(60);
        timerProgressBar.setStringPainted(true);

        javax.swing.GroupLayout typingPanelLayout = new javax.swing.GroupLayout(typingPanel);
        typingPanel.setLayout(typingPanelLayout);
        typingPanelLayout.setHorizontalGroup(
            typingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(typingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sampleTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(userInputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(typingPanelLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(startButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton)
                        .addGap(18, 18, 18)
                        .addComponent(timerProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        typingPanelLayout.setVerticalGroup(
            typingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sampleTextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(typingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(typingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(startButton)
                        .addComponent(resetButton))
                    .addComponent(timerProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(typingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(typingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        // TODO add your handling code here:
        RegisterFrame rf = new RegisterFrame();
        rf.setVisible(true);
    }//GEN-LAST:event_registerButtonActionPerformed

    private void statsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButtonActionPerformed
        // TODO add your handling code here:
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/typingTestDB", "root", "root");
            PreparedStatement st1 = (PreparedStatement) con.prepareStatement("SELECT \n"
                    + "  MIN(score) as min_score,\n"
                    + "  AVG(score) as avg_score,\n"
                    + "  MAX(score) as max_score\n"
                    + "FROM EASY_SCORES\n"
                    + "WHERE username = ?\n"
                    + "UNION\n"
                    + "SELECT \n"
                    + "  MIN(score) as min_score,\n"
                    + "  AVG(score) as avg_score,\n"
                    + "  MAX(score) as max_score\n"
                    + "FROM MEDIUM_SCORES\n"
                    + "WHERE username = ?\n"
                    + "UNION\n"
                    + "SELECT \n"
                    + "  MIN(score) as min_score,\n"
                    + "  AVG(score) as avg_score,\n"
                    + "  MAX(score) as max_score\n"
                    + "FROM HARD_SCORES\n"
                    + "WHERE username = ?");
            st1.setString(1, username);
            st1.setString(2, username);
            st1.setString(3, username);

            ResultSet rs1 = st1.executeQuery();
            int i = 0;
            while (rs1.next()) {
                previousScores[i][0] = rs1.getInt(1);
                previousScores[i][1] = rs1.getInt(2);
                previousScores[i][2] = rs1.getInt(3);
                i++;
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        int[] min = new int[3];
        int[] max = new int[3];
        int[] avg = new int[3];

        // storing values of previous scores, 0 index for easy, 1 index for medium and 2 index for hard
        for (int i = 0; i < 3; i++) {
            min[i] = previousScores[i][0];
            avg[i] = previousScores[i][1];
            max[i] = previousScores[i][2];
        }

        JOptionPane.showMessageDialog(this, "EASY : Lowest WPM : " + min[2] + " | Average WPM : " + avg[2] + " | Highest WPM : " + max[2] + "\n"
                + "MEDIUM : Lowest WPM : " + min[1] + " | Average WPM : " + avg[1] + " | Highest WPM : " + max[1] + "\n"
                + "HARD : Lowest WPM : " + min[0] + " | Average WPM : " + avg[0] + " | Highest WPM : " + max[0]
        );
    }//GEN-LAST:event_statsButtonActionPerformed

    private void highscoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highscoreButtonActionPerformed
        // TODO add your handling code here:
        highscoreFrame hf = new highscoreFrame();
        hf.setVisible(true);
    }//GEN-LAST:event_highscoreButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // TODO add your handling code here:
        if(checkLogin==false){
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
            // event listener to check if login frame was closed or not
            lf.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent evt) {
                    checkLogin = lf.isLogin();
                    if (checkLogin == true) {
                        registerButton.setEnabled(false);
//                        loginButton.setEnabled(false);
                        loginButton.setText("Logout");
                        statsButton.setEnabled(true);
                        username = lf.getUsername();
                        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Welcome, " + username, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 36)));
                    }
                }
            });
        }
        else{
            checkLogin = false;
            username = "";
            System.out.println(username);
            loginButton.setText("Login");
            optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Welcome, Guest ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 36)));
            registerButton.setEnabled(true);
            statsButton.setEnabled(false);
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TypingTestProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TypingTestProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TypingTestProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TypingTestProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>

        /*
         javax.swing.plaf.metal.MetalLookAndFeel
         javax.swing.plaf.nimbus.NimbusLookAndFeel
         com.sun.java.swing.plaf.motif.MotifLookAndFeel
         com.sun.java.swing.plaf.windows.WindowsLookAndFeel
         com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
         */
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 20));
        } catch (Exception e) {
            System.out.println("Look and Feel not set");
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TypingTestProject().setVisible(true);
            }
        });
    }

    private Timer timer;
    private int timeLeft;
    private final int[][] previousScores = new int[3][3];
    private boolean isTimerRunning, checkLogin = false;
    private int inputWordIndex = 1, sampleWordsLen, startSearchFrom = 0, start=0, end;
    private String fileName, username;
    private StringBuilder userInput;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton easyRadioButton;
    private javax.swing.JRadioButton hardRadioButton;
    private javax.swing.JButton highscoreButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton mediumRadioButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JButton registerButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JTextArea sampleTextArea;
    private javax.swing.JPanel sampleTextPanel;
    private javax.swing.JLabel selectDifficulty;
    private javax.swing.JButton setDifficultyButton;
    private javax.swing.JButton startButton;
    private javax.swing.JButton statsButton;
    private javax.swing.JProgressBar timerProgressBar;
    private javax.swing.JPanel typingPanel;
    private javax.swing.JPanel userInputPanel;
    private javax.swing.JTextField userInputTextField;
    // End of variables declaration//GEN-END:variables
}
