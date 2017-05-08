/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tomek
 */
public class DiseasesPanel extends JPanel {

    private final JScrollPane scrollPane;
    private final Connection connection;
    private JTable jTable;
    ArrayList<Integer> aNumbers = new ArrayList<>();
    ArrayList<String> aNames = new ArrayList<>();
    ArrayList<String> aCures = new ArrayList<>();
    String orderBy = "c.numer";
    String[] ascDesc = {" ASC", " DESC"};
    int pointer = 0;

    private void fillTable() {

        aNumbers.clear();
        aNames.clear();
        aCures.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT c.numer, c.nazwa "
                + "FROM baza.Choroba c ORDER BY " + orderBy + ascDesc[pointer];

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Nie znaleziono żadnej choroby", "Informacja", 1);
                return;
            } else {
                String doctorName, patientName;

                while (rs.next()) {
                    aNumbers.add(rs.getInt(1));
                    aNames.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać chorób", "Informacja", 1);
            return;
        }

        for (int number : aNumbers) {
            myQuery = "SELECT z.nr_choroba, c.numer "
                    + "FROM baza.Choroba c, baza.Leczenie z WHERE "
                    + "z.nr_choroba = c.numer AND c.numer = " + number;

            try {
                ResultSet rs = st.executeQuery(myQuery);

                if (!rs.isBeforeFirst()) {
                    aCures.add("Nie");
                } else {
                    aCures.add("Tak");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Nie udało się pobrać zaleceń", "Informacja", 1);
                return;
            }
        }

        if (!aNumbers.isEmpty() && !aNames.isEmpty() && !aCures.isEmpty()) {
            Integer[] numbers = aNumbers.toArray(new Integer[aNumbers.size()]);
            String[] names = aNames.toArray(new String[aNames.size()]);
            String[] cures = aCures.toArray(new String[aCures.size()]);

            String[] columnNames = {"Numer", "Nazwa", "Zalecenia"};
            Object[][] data = new Object[aNumbers.size()][3];

            int i = 0;

            for (Integer numb : numbers) {
                data[i][0] = numb;
                i++;
            }

            i = 0;

            for (String name : names) {
                data[i][1] = name;
                i++;
            }

            i = 0;

            for (String cure : cures) {
                data[i][2] = cure;
                i++;
            }

            jTable = new JTable(data, columnNames);
            Font tableFont = new Font(jTable.getFont().getName(), Font.PLAIN, 20);
            jTable.setFillsViewportHeight(true);
            jTable.setFont(tableFont);
            jTable.setRowHeight(40);
            jTable.setShowGrid(false);
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            jTable.setModel(tableModel);
            scrollPane.getViewport().add(jTable);

            jTable.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = jTable.columnAtPoint(e.getPoint());
                    if (col == 0) {
                        if (orderBy.equals("c.numer")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "c.numer";
                        }
                        fillTable();
                    } else if (col == 1) {
                        if (orderBy.equals("c.nazwa")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "c.nazwa";
                        }
                        fillTable();
                    }
                }
            });

        }
    }

    public DiseasesPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(20, 660, 150, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bGo = new JButton("Idź");
        bGo.setBounds(180, 660, 150, 40);
        bGo.setFont(buttonFont);
        add(bGo);
        
        JButton bDel = new JButton("Usuń");
        bDel.setBounds(340, 660, 150, 40);
        bDel.setFont(buttonFont);
        add(bDel);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Scroll Pane
        scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 20, 970, 600);
        scrollPane.setBorder(border);
        add(scrollPane);

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
            }

        });

        bGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jTable.getSelectedRow() >= 0) {
                    ProjektBazyDanych.currentDiseaseId = aNumbers.get(jTable.getSelectedRow());
                    cLayout.show(pContainer, "pDiseaseDetails");
                }
            }
        });
        
        bDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jTable.getSelectedRow() >= 0) {
                    Statement st;
                    
                    try {
                        st = connection.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "DELETE FROM baza.Choroba WHERE baza.Choroba.numer = " + aNumbers.get(jTable.getSelectedRow());
                            
                    try {
                        st.executeUpdate(myQuery);
                        fillTable();
                        JOptionPane.showMessageDialog(null, "Pomyślnie usunięto chorobę", "Informacja", 1);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się usunąć choroby", "Informacja", 1);
                    }
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                fillTable();
            }
        });
    }
}
