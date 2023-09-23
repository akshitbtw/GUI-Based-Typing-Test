/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package typingtestmain;

import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Ak
 */
public class highscoreFrame extends javax.swing.JFrame {

    /**
     * Creates new form highscoreFrame
     */
    public highscoreFrame() {
        initComponents();
//        jScrollPane1.setPreferredSize(new Dimension(jScrollPane1.getPreferredSize().width, easyTable.getRowHeight() * easyTable.getRowCount()));
        // setting font of easyTable, mediumTable, hardTable header
        JTableHeader tableHeader1 = easyTable.getTableHeader();
        Font f = new Font("Tahoma", Font.PLAIN, 22);
        tableHeader1.setFont(f);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        JTableHeader tableHeader2 = mediumTable.getTableHeader();
        tableHeader2.setFont(f);
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        JTableHeader tableHeader3 = hardTable.getTableHeader();
        tableHeader3.setFont(f);
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int x = 0; x < 2; x++) {
            easyTable.getColumnModel().getColumn(x).setHeaderRenderer(centerRenderer);
            easyTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
            mediumTable.getColumnModel().getColumn(x).setHeaderRenderer(centerRenderer);
            mediumTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
            hardTable.getColumnModel().getColumn(x).setHeaderRenderer(centerRenderer);
            hardTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
        fetchEasyScores();
    }

    private void fetchEasyScores() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/typingTestDB", "root", "root");

            // fill easy table
            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT \n"
                    + "  username,\n"
                    + "  score\n"
                    + "FROM (\n"
                    + "  SELECT DISTINCT \n"
                    + "    username, \n"
                    + "    score\n"
                    + "  FROM EASY_SCORES\n"
                    + "  ORDER BY score DESC\n"
                    + "  FETCH FIRST 5 ROWS ONLY\n"
                    + ") AS easy");

            while (rs1.next()) {
                DefaultTableModel model = (DefaultTableModel) easyTable.getModel();
                model.addRow(new Object[]{rs1.getString(1), rs1.getInt(2)});
            }

            // fill medium table
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT \n"
                    + "  username,\n"
                    + "  score\n"
                    + "FROM (\n"
                    + "  SELECT DISTINCT \n"
                    + "    username, \n"
                    + "    score\n"
                    + "  FROM MEDIUM_SCORES\n"
                    + "  ORDER BY score DESC\n"
                    + "  FETCH FIRST 5 ROWS ONLY\n"
                    + ") AS medium");

            while (rs2.next()) {
                DefaultTableModel model = (DefaultTableModel) mediumTable.getModel();
                model.addRow(new Object[]{rs2.getString(1), rs2.getInt(2)});
            }

            // fill hard table
            Statement st3 = con.createStatement();
            ResultSet rs3 = st2.executeQuery("SELECT \n"
                    + "  username,\n"
                    + "  score\n"
                    + "FROM (\n"
                    + "  SELECT DISTINCT \n"
                    + "    username, \n"
                    + "    score\n"
                    + "  FROM HARD_SCORES\n"
                    + "  ORDER BY score DESC\n"
                    + "  FETCH FIRST 5 ROWS ONLY\n"
                    + ") AS hard");

            while (rs3.next()) {
                DefaultTableModel model = (DefaultTableModel) hardTable.getModel();
                model.addRow(new Object[]{rs3.getString(1), rs3.getInt(2)});
            }

        } catch (Exception ex) {
            System.out.println("Driver Class not found " + ex);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        easyTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mediumTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        hardTable = new javax.swing.JTable();
        highscoreLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        easyTable.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        easyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "USERNAME", "SCORE"
            }
        ));
        easyTable.setRowHeight(26);
        jScrollPane1.setViewportView(easyTable);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 36)); // NOI18N
        jLabel1.setText("EASY");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 36)); // NOI18N
        jLabel2.setText("MEDIUM");

        mediumTable.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        mediumTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "USERNAME", "SCORE"
            }
        ));
        mediumTable.setRowHeight(26);
        jScrollPane2.setViewportView(mediumTable);

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 36)); // NOI18N
        jLabel3.setText("HARD");

        hardTable.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        hardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "USERNAME", "SCORE"
            }
        ));
        hardTable.setRowHeight(26);
        jScrollPane3.setViewportView(hardTable);

        highscoreLabel.setFont(new java.awt.Font("Impact", 0, 72)); // NOI18N
        highscoreLabel.setText(" HIGHSCORES ");
        highscoreLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highscoreLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(highscoreLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(highscoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(highscoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(highscoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(highscoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new highscoreFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable easyTable;
    private javax.swing.JTable hardTable;
    private javax.swing.JLabel highscoreLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable mediumTable;
    // End of variables declaration//GEN-END:variables
}
