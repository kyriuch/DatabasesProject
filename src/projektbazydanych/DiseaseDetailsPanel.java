/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tomek
 */
public class DiseaseDetailsPanel extends JPanel {

    private final Connection connection;
    private final JLabel lName;
    private final JTextArea tRecomendation;
    private final JComboBox cCure;
    private final ArrayList<String> aCure = new ArrayList<>();
    private final ArrayList<Integer> aCureCodes = new ArrayList<>();
    private final ArrayList<Integer> aUsedCures = new ArrayList<>();
    private final JScrollPane scrollPane;
    private final DefaultTableModel model;
    private final JTable jTable;

    private void fillData() {
        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT c.nazwa FROM baza.Choroba c WHERE c.numer = " + ProjektBazyDanych.currentDiseaseId;

        ResultSet rs;

        try {
            rs = st.executeQuery(myQuery);
            rs.next();
            lName.setText(rs.getString(1));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się znaleźć wizyty", "Informacja", 1);
            return;
        }

        myQuery = "SELECT l.zalecenie, l.id FROM baza.Leczenie l WHERE l.nr_choroba = " + ProjektBazyDanych.currentDiseaseId;

        int id = -1;

        try {
            rs = st.executeQuery(myQuery);
            rs.next();
            tRecomendation.setText(rs.getString(1));
            id = rs.getInt(2);
        } catch (SQLException ex) {
            tRecomendation.setText("Tutaj wpisz zalecane leczenie.");
        }

        if (id != -1) {
            myQuery = "SELECT z.kod_lekarstwo, l.nazwa, z.jak_czesto, z.ile FROM baza.Zalecane_leki z "
                    + "JOIN baza.Lekarstwo l ON l.kod = z.kod_lekarstwo "
                    + "WHERE z.id_leczenie = " + id;

            try {
                rs = st.executeQuery(myQuery);

                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        aUsedCures.add(rs.getInt(1));
                        model.addRow(new Object[]{rs.getString(2), rs.getInt(3), rs.getInt(4)});
                    }
                }
            } catch (SQLException ex) {
            }
        }
    }

    private void fillCures() {
        aCure.clear();
        aCureCodes.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT baza.Lekarstwo.kod, baza.Lekarstwo.nazwa FROM baza.Lekarstwo "
                + " ORDER BY baza.Lekarstwo.nazwa ASC";

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Żadna choroba nie występuje w bazie", "Informacja", 1);
            } else {
                while (rs.next()) {
                    aCureCodes.add(rs.getInt(1));
                    aCure.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać chorób", "Informacja", 1);
            return;
        }

        if (!aCure.isEmpty()) {
            String[] cures = aCure.toArray(new String[aCure.size()]);

            cCure.removeAllItems();
            cCure.addItem("Brak");

            for (String cure : cures) {
                cCure.addItem(cure);
            }
        }
    }

    private void showMessageDialog(String text, String title) {
        JTextArea jta = new JTextArea(text);
        Font textFont = new Font(jta.getFont().getName(), Font.PLAIN, 15);
        jta.setFont(textFont);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setEnabled(false);

        JScrollPane jsp = new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };

        JOptionPane.showMessageDialog(null, jsp, title, 1);
    }

    public DiseaseDetailsPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(20, 650, 120, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bAddCure = new JButton("+");
        bAddCure.setBounds(430, 595, 60, 40);
        bAddCure.setFont(buttonFont);
        add(bAddCure);

        JButton bDelCure = new JButton("-");
        bDelCure.setBounds(870, 530, 60, 40);
        bDelCure.setFont(buttonFont);
        add(bDelCure);

        JButton bCheckDisease = new JButton("?");
        bCheckDisease.setBounds(300, 15, 60, 40);
        bCheckDisease.setFont(buttonFont);
        add(bCheckDisease);

        JButton bCheckInteractions = new JButton("Sprawdź interakcje");
        bCheckInteractions.setBounds(610, 530, 250, 40);
        bCheckInteractions.setFont(buttonFont);
        add(bCheckInteractions);

        JButton bDel = new JButton("Usuń");
        bDel.setBounds(170, 650, 150, 40);
        bDel.setFont(buttonFont);
        add(bDel);

        JButton bUpdate = new JButton("Akutalizuj");
        bUpdate.setBounds(340, 650, 150, 40);
        bUpdate.setFont(buttonFont);
        add(bUpdate);

        // Labels
        JLabel lDisease = new JLabel("Choroba: ");
        Font labelFont = new Font(lDisease.getFont().getName(), Font.PLAIN, 20);
        lDisease.setBounds(20, 20, 300, 20);
        lDisease.setFont(labelFont);
        add(lDisease);

        lName = new JLabel();
        lName.setBounds(110, 20, 250, 20);
        lName.setFont(labelFont);
        add(lName);

        JLabel lCure = new JLabel("Lek: ");
        lCure.setBounds(20, 600, 150, 20);
        lCure.setFont(labelFont);
        add(lCure);

        // Text Areas
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        tRecomendation = new JTextArea();
        Font textFont = new Font(tRecomendation.getFont().getName(), Font.PLAIN, 20);
        tRecomendation.setBounds(20, 80, 470, 480);
        tRecomendation.setBorder(border);
        tRecomendation.setLineWrap(true);
        tRecomendation.setWrapStyleWord(true);
        tRecomendation.setFont(textFont);
        add(tRecomendation);

        cCure = new JComboBox();
        Font comboFont = new Font(cCure.getFont().getName(), Font.PLAIN, 20);
        cCure.setBounds(170, 595, 250, 40);
        cCure.setFont(comboFont);
        add(cCure);

        // Scroll Pane
        scrollPane = new JScrollPane();
        scrollPane.setBounds(530, 20, 400, 500);
        scrollPane.setBorder(border);
        add(scrollPane);

        // Table
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        jTable = new JTable(model);
        model.addColumn("Nazwa");
        model.addColumn("Jak często");
        model.addColumn("Ile");
        Font tableFont = new Font(jTable.getFont().getName(), Font.PLAIN, 20);
        jTable.setFillsViewportHeight(true);
        jTable.setFont(tableFont);
        jTable.setRowHeight(40);
        jTable.setShowGrid(false);
        jTable.getColumnModel().getColumn(0).setPreferredWidth(210);
        scrollPane.getViewport().add(jTable);

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pDiseases");

                model.setRowCount(0);
                aCure.clear();
                aCureCodes.clear();
                aUsedCures.clear();
            }

        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                fillData();
                fillCures();
            }
        });

        bCheckDisease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "SELECT baza.Choroba.nazwa, baza.Choroba.opis, baza.Choroba.objawy "
                        + "FROM baza.Choroba "
                        + "WHERE baza.Choroba.numer = " + ProjektBazyDanych.currentDiseaseId;

                ResultSet rs;
                String[] scores = new String[3];

                try {
                    rs = st.executeQuery(myQuery);
                    rs.next();
                    scores[0] = rs.getString(1);
                    scores[1] = rs.getString(2);
                    scores[2] = rs.getString(3);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się znaleźć choroby", "Informacja", 1);
                    return;
                }

                String message = "Nazwa: " + scores[0] + "\n\nOpis: " + scores[1] + "\n\nObjawy: " + scores[2];

                showMessageDialog(message, "Informacje o chorobie");
            }
        });

        bAddCure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (cCure.getSelectedIndex() > 0) {
                    if (!aUsedCures.contains(aCureCodes.get(cCure.getSelectedIndex() - 1))) {
                        aUsedCures.add(aCureCodes.get(cCure.getSelectedIndex() - 1));
                        model.addRow(new Object[]{cCure.getSelectedItem(), "0", "0"});
                    }
                }
            }
        });

        bDelCure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (jTable.getSelectedRow() >= 0) {
                    aUsedCures.remove(aUsedCures.get(jTable.getSelectedRow()));
                    model.removeRow(jTable.getSelectedRow());
                }
            }
        });

        bCheckInteractions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String returnString = "";

                Statement st;

                try {
                    st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                int rows = jTable.getRowCount();

                for (int i = 0; i < rows - 1; i++) {
                    for (int j = i + 1; j < rows; j++) {
                        int code_1, code_2;

                        code_1 = aUsedCures.get(i);
                        code_2 = aUsedCures.get(j);

                        String myQuery = "SELECT c1.nazwa, c1.kod, c2.nazwa, c2.kod, i.Interakcja from baza.Interakcje i JOIN "
                                + "baza.Lekarstwo c1 ON c1.kod = " + code_1 + " JOIN baza.Lekarstwo c2 ON c2.kod = " + code_2 + " WHERE "
                                + "i.kod_lek1 = c1.kod AND i.kod_lek2 = c2.kod";

                        ResultSet rs;
                        String[] scores = new String[4];

                        try {
                            rs = st.executeQuery(myQuery);
                            rs.next();
                            returnString += "Lek1: " + rs.getString(1) + "  Lek 2: " + rs.getString(3) + "\n"
                                    + "Interakcja: " + rs.getString(5) + "\n\n\n";
                        } catch (SQLException ex) {
                        }
                    }

                }

                for (int i = rows - 1; i >= 1; i--) {
                    for (int j = i - 1; j >= 0; j--) {
                        int code_1, code_2;

                        code_1 = aUsedCures.get(i);
                        code_2 = aUsedCures.get(j);

                        String myQuery = "SELECT c1.nazwa, c1.kod, c2.nazwa, c2.kod, i.Interakcja from baza.Interakcje i JOIN "
                                + "baza.Lekarstwo c1 ON c1.kod = " + code_1 + " JOIN baza.Lekarstwo c2 ON c2.kod = " + code_2 + " WHERE "
                                + "i.kod_lek1 = c1.kod AND i.kod_lek2 = c2.kod";

                        ResultSet rs;
                        String[] scores = new String[4];

                        try {
                            rs = st.executeQuery(myQuery);
                            rs.next();
                            returnString += "Lek1: " + rs.getString(1) + "  Lek 2: " + rs.getString(3) + "\n"
                                    + "Interakcja: " + rs.getString(5) + "\n\n\n";
                        } catch (SQLException ex) {
                        }
                    }

                }
                    
                if(returnString.isEmpty()) {
                    returnString = "Nie wykryto interakcji";
                }
                
                showMessageDialog(returnString, "Interakcje");
            }
        });

        bDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "DELETE FROM baza.Leczenie WHERE baza.Leczenie.nr_choroba = " + ProjektBazyDanych.currentDiseaseId;

                try {
                    st.executeUpdate(myQuery);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się usunąć zalecenia", "Informacja", 1);
                    return;
                }

                JOptionPane.showMessageDialog(null, "Usunięto zalecenie", "Informacja", 1);
                cLayout.show(pContainer, "pDiseases");

                model.setRowCount(0);

                aCure.clear();
                aCureCodes.clear();
                aUsedCures.clear();
            }

        });

        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Integer> often = new ArrayList<>();
                ArrayList<Integer> many = new ArrayList<>();

                int rows = jTable.getRowCount();

                for (int i = 0; i < rows; i++) {
                    try {
                        often.add(Integer.parseInt(model.getValueAt(i, 1).toString()));
                        many.add(Integer.parseInt(model.getValueAt(i, 2).toString()));
                    } catch (NumberFormatException exNumber) {
                        JOptionPane.showMessageDialog(null, "Wprowadź liczby", "Błąd", 0);
                        return;
                    }

                    if (many.get(i) <= 0 || often.get(i) <= 0) {
                        JOptionPane.showMessageDialog(null, "Uzupełnij pola", "Błąd", 0);
                        return;
                    }
                }

                String recomendation = tRecomendation.getText().trim().replaceAll(" +", " ");

                if (recomendation.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                } else {
                    Statement st;

                    try {
                        st = connection.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "INSERT INTO baza.Leczenie(nr_choroba, zalecenie) VALUES(" + ProjektBazyDanych.currentDiseaseId
                            + ", \'" + recomendation + "\')";

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        myQuery = "UPDATE baza.Leczenie SET baza.Leczenie.zalecenie = \'" + recomendation + "\'"
                                + " WHERE baza.Leczenie.nr_choroba = " + ProjektBazyDanych.currentDiseaseId;

                        try {
                            st.executeUpdate(myQuery);
                        } catch (SQLException ex2) {
                            JOptionPane.showMessageDialog(null, "Nie udało się uaktualnić wizyty", "Informacja", 1);
                        }
                    }

                    myQuery = "SELECT l.id FROM baza.Leczenie l WHERE l.nr_choroba = " + ProjektBazyDanych.currentDiseaseId;

                    int id = -1;
                    ResultSet rs;
                    
                    try {
                        rs = st.executeQuery(myQuery);
                        rs.next();
                        id = rs.getInt(1);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się pobrać indeksu zalecenia", "Informacja", 1);
                    }
                    
                    if(id == -1) {
                        return;
                    }

                    if (!aUsedCures.isEmpty()) {
                        for (int i = 0; i < aUsedCures.size(); i++) {
                            myQuery = "INSERT INTO baza.Zalecane_leki(id_leczenie, kod_lekarstwo, "
                                    + "jak_czesto, ile) VALUES(" + id + ", "
                                    + aUsedCures.get(i) + ", " + often.get(i) + ", " + many.get(i) + ")";

                            try {
                                st.executeUpdate(myQuery);
                            } catch (SQLException ex) {
                                myQuery = "UPDATE baza.Zalecane_leki SET baza.Zalecane_leki.jak_czesto = "
                                        + often.get(i) + ", baza.Zalecane_leki.ile = "
                                        + many.get(i) + " WHERE baza.Zalecane_leki.id_leczenie = "
                                        + id + " AND baza.Zalecane_leki.kod_lekarstwo = "
                                        + aUsedCures.get(i);

                                try {
                                    st.executeUpdate(myQuery);
                                } catch (SQLException ex2) {
                                    JOptionPane.showMessageDialog(null, "Nie udało się uaktualnić zalecenia", "Informacja", 1);
                                }
                            }
                        }
                    }

                    myQuery = "SELECT baza.Zalecane_leki.kod_lekarstwo FROM baza.Zalecane_leki "
                            + "WHERE baza.Zalecane_leki.id_leczenie = " + id;
                    
                    try {
                        st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    try {
                        rs = st.executeQuery(myQuery);

                        if (rs.isBeforeFirst()) {
                            try {
                                st = connection.createStatement();
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                                return;
                            }
                            while (rs.next()) {
                                if (aUsedCures.indexOf(rs.getInt(1)) < 0) {
                                    myQuery = "DELETE FROM baza.Zalecane_leki WHERE baza.Zalecane_leki.id_leczenie = "
                                            + id + " AND "
                                            + "baza.Zalecane_leki.kod_lekarstwo = " + rs.getInt(1);

                                    try {
                                        st.executeUpdate(myQuery);
                                    } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(null, "Nie udało się usunąć leku", "Informacja", 1);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się usunąć lekarstw", "Informacja", 1);
                        return;
                    }
                    JOptionPane.showMessageDialog(null, "Pomyślnie zaktualizowano zalecenie", "Informacja", 1);
                }
            }
        });
    }

}
