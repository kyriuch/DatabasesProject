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
public class PatientsPanel extends JPanel {

    private final JScrollPane scrollPane;
    private final Connection connection;
    private JTable jTable;
    ArrayList<String> aDoctors = new ArrayList<>();
    ArrayList<String> aTels = new ArrayList<>();
    ArrayList<Integer> aNumbers = new ArrayList<>();
    ArrayList<String> aAddresses = new ArrayList<>();
    String orderBy = "baza.Pacjent.nazwisko";
    String[] ascDesc = {" ASC", " DESC"};
    int pointer = 0;

    private void fillTable() {

        aDoctors.clear();
        aTels.clear();
        aNumbers.clear();
        aAddresses.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT baza.Pacjent.nr_ubezp, baza.Pacjent.nazwisko, baza.Pacjent.imie, baza.Pacjent.adres, baza.Pacjent.telefon "
                + "FROM baza.Pacjent ORDER BY " + orderBy + ascDesc[pointer];

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Nie znaleziono żadnego.Pacjenta", "Informacja", 1);
            } else {
                String doctorName, patientName;

                while (rs.next()) {
                    doctorName = rs.getString(2) + " " + rs.getString(3);

                    aNumbers.add(rs.getInt(1));
                    aDoctors.add(doctorName);
                    aAddresses.add(rs.getString(4));
                    aTels.add(rs.getString(5));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać pacjentów", "Informacja", 1);
            return;
        }

        if (!aNumbers.isEmpty() && !aDoctors.isEmpty() && !aTels.isEmpty() && !aAddresses.isEmpty()) {
            Integer[] numbers = aNumbers.toArray(new Integer[aNumbers.size()]);
            String[] doctors = aDoctors.toArray(new String[aDoctors.size()]);
            String[] addresses = aAddresses.toArray(new String[aAddresses.size()]);
            String[] tels = aTels.toArray(new String[aTels.size()]);

            String[] columnNames = {"Nr ubezp", "Nazwisko i imię", "Adres", "Telefon"};
            Object[][] data = new Object[aDoctors.size()][4];

            int i = 0;

            for (Integer numb : numbers) {
                data[i][0] = numb;
                i++;
            }

            i = 0;

            for (String doc : doctors) {
                data[i][1] = doc;
                i++;
            }

            i = 0;

            for (String address : addresses) {
                data[i][2] = address;
                i++;
            }

            i = 0;

            for (String tel : tels) {
                data[i][3] = tel;
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
                        if (orderBy.equals("baza.Pacjent.nr_ubezp")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "baza.Pacjent.nr_ubezp";
                        }
                        fillTable();
                    } else if (col == 1) {
                        if (orderBy.equals("baza.Pacjent.nazwisko")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "baza.Pacjent.nazwisko";
                        }
                        fillTable();
                    } else if (col == 2) {
                        if (orderBy.equals("baza.Pacjent.adres")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "baza.Pacjent.adres";
                        }
                        fillTable();
                    } else {
                        if (orderBy.equals("baza.Pacjent.telefon")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "baza.Pacjent.telefon";
                        }
                        fillTable();
                    }
                }
            });

        }
    }

    public PatientsPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(20, 660, 150, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bDel = new JButton("Usuń");
        bDel.setBounds(180, 660, 150, 40);
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

        bDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jTable.getSelectedRow() >= 0) {
                    Statement st;

                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "DELETE FROM baza.Pacjent WHERE baza.Pacjent.nr_ubezp = " + aNumbers.get(jTable.getSelectedRow());
                    
                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się usunąć pacjenta", "Informacja", 1);
                        return;
                    }
                    
                    fillTable();
                    JOptionPane.showMessageDialog(null, "Pomyślnie usunięto pacjenta", "Informacja", 1);
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
