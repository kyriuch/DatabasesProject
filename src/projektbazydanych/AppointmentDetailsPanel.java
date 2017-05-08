/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 *
 * @author Tomek
 */
public class AppointmentDetailsPanel extends JPanel {

    private final Connection connection;
    private final JLabel lDoctorName;
    private final JLabel lPatientName;
    private final JTextArea tDate;
    private final JTextArea tHour;
    private final JComboBox cDisease;
    private final JComboBox cCure;
    private final ArrayList<String> aDisease = new ArrayList<>();
    private final ArrayList<Integer> aDiseaseNumber = new ArrayList<>();
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

        String myQuery = "SELECT baza.Lekarz.nazwisko, baza.Lekarz.imie, baza.Pacjent.nazwisko, baza.Pacjent.imie, "
                + "baza.Wizyta.data, baza.Wizyta.godzina FROM baza.Lekarz, baza.Pacjent, baza.Wizyta "
                + "WHERE baza.Wizyta.id = " + ProjektBazyDanych.currentAppointmentId + " AND baza.Wizyta.nr_pacjent"
                + " = baza.Pacjent.nr_ubezp AND baza.Wizyta.nr_lekarz = baza.Lekarz.numer";

        ResultSet rs;
        String[] scores = new String[4];

        try {
            rs = st.executeQuery(myQuery);
            rs.next();
            scores[0] = rs.getString(1) + " " + rs.getString(2);
            scores[1] = rs.getString(3) + " " + rs.getString(4);
            scores[2] = rs.getString(5);
            scores[3] = rs.getString(6);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się znaleźć wizyty", "Informacja", 1);
            return;
        }

        lDoctorName.setText(scores[0]);
        lPatientName.setText(scores[1]);
        tDate.setText(scores[2]);
        tHour.setText(scores[3]);

        myQuery = "SELECT c.nazwa FROM baza.Choroba c JOIN baza.Rozpoznanie r ON r.nr_choroba = c.numer WHERE "
                + "r.id_wizyta = " + ProjektBazyDanych.currentAppointmentId;

        try {
            rs = st.executeQuery(myQuery);
            rs.next();
            cDisease.setSelectedItem(rs.getString(1));
        } catch (SQLException ex) {
        }

        myQuery = "SELECT p.kod_lekarstwo, l.nazwa, p.jak_czesto, p.ile FROM baza.Przypisane_leki p "
                + "JOIN baza.Lekarstwo l ON l.kod = p.kod_lekarstwo "
                + "WHERE p.id_wizyta = " + ProjektBazyDanych.currentAppointmentId;

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

    private void fillDiseases() {
        aDisease.clear();
        aDiseaseNumber.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT baza.Choroba.numer, baza.Choroba.nazwa from baza.Choroba "
                + " ORDER BY baza.Choroba.nazwa ASC";

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Żadna choroba nie występuje w bazie", "Informacja", 1);
            } else {
                while (rs.next()) {
                    aDiseaseNumber.add(rs.getInt(1));
                    aDisease.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać chorób", "Informacja", 1);
            return;
        }

        if (!aDisease.isEmpty()) {
            String[] diseases = aDisease.toArray(new String[aDisease.size()]);

            cDisease.removeAllItems();
            cDisease.addItem("Brak");

            for (String disease : diseases) {
                cDisease.addItem(disease);
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
                JOptionPane.showMessageDialog(null, "Żadne lekarstwo nie występuje w bazie", "Informacja", 1);
            } else {
                while (rs.next()) {
                    aCureCodes.add(rs.getInt(1));
                    aCure.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać lekarstw", "Informacja", 1);
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

    public AppointmentDetailsPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(40, 360, 80, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bCheckDisease = new JButton("?");
        bCheckDisease.setBounds(400, 215, 60, 40);
        bCheckDisease.setFont(buttonFont);
        add(bCheckDisease);

        JButton bAddCure = new JButton("+");
        bAddCure.setBounds(400, 265, 60, 40);
        bAddCure.setFont(buttonFont);
        add(bAddCure);

        JButton bDelCure = new JButton("-");
        bDelCure.setBounds(870, 530, 60, 40);
        bDelCure.setFont(buttonFont);
        add(bDelCure);

        JButton bCheckInteractions = new JButton("Sprawdź interakcje");
        bCheckInteractions.setBounds(610, 530, 250, 40);
        bCheckInteractions.setFont(buttonFont);
        add(bCheckInteractions);

        JButton bDelAppointment = new JButton("Odwołaj");
        bDelAppointment.setBounds(140, 360, 150, 40);
        bDelAppointment.setFont(buttonFont);
        add(bDelAppointment);

        JButton bUpdate = new JButton("Akutalizuj");
        bUpdate.setBounds(310, 360, 150, 40);
        bUpdate.setFont(buttonFont);
        add(bUpdate);

        JButton bPrint = new JButton("Drukuj receptę");
        bPrint.setBounds(740, 670, 250, 40);
        bPrint.setFont(buttonFont);
        add(bPrint);

        // Labels
        JLabel lDoctor = new JLabel("Lekarz: ");
        Font labelFont = new Font(lDoctor.getFont().getName(), Font.PLAIN, 20);
        lDoctor.setBounds(20, 20, 300, 20);
        lDoctor.setFont(labelFont);
        add(lDoctor);

        JLabel lPatient = new JLabel("Pacjent: ");
        lPatient.setBounds(20, 70, 300, 20);
        lPatient.setFont(labelFont);
        add(lPatient);

        lDoctorName = new JLabel();
        lDoctorName.setBounds(140, 20, 250, 20);
        lDoctorName.setFont(labelFont);
        add(lDoctorName);

        lPatientName = new JLabel();
        lPatientName.setBounds(140, 70, 250, 20);
        lPatientName.setFont(labelFont);
        add(lPatientName);

        JLabel lDate = new JLabel("Data: ");
        lDate.setBounds(20, 120, 150, 20);
        lDate.setFont(labelFont);
        add(lDate);

        JLabel lHour = new JLabel("Godzina: ");
        lHour.setBounds(20, 170, 150, 20);
        lHour.setFont(labelFont);
        add(lHour);

        JLabel lDisease = new JLabel("Choroba: ");
        lDisease.setBounds(20, 220, 150, 20);
        lDisease.setFont(labelFont);
        add(lDisease);

        JLabel lCure = new JLabel("Lek: ");
        lCure.setBounds(20, 270, 150, 20);
        lCure.setFont(labelFont);
        add(lCure);

        // Text Areas
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        tDate = new JTextArea();
        Font textFont = new Font(tDate.getFont().getName(), Font.PLAIN, 20);
        tDate.setBounds(140, 115, 250, 40);
        tDate.setBorder(border);
        tDate.setFont(textFont);
        add(tDate);

        tHour = new JTextArea();
        tHour.setBounds(140, 165, 250, 40);
        tHour.setBorder(border);
        tHour.setFont(textFont);
        add(tHour);

        // Combo Boxes
        cDisease = new JComboBox();
        Font comboFont = new Font(cDisease.getFont().getName(), Font.PLAIN, 20);
        cDisease.setBounds(140, 215, 250, 40);
        cDisease.setFont(comboFont);
        add(cDisease);

        // Combo Boxes
        cCure = new JComboBox();
        cCure.setBounds(140, 265, 250, 40);
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
                cLayout.show(pContainer, "pAppointments");

                model.setRowCount(0);

                aDisease.clear();
                aDiseaseNumber.clear();
                aCure.clear();
                aCureCodes.clear();
                aUsedCures.clear();
            }

        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                fillDiseases();
                fillCures();
                fillData();
            }
        });

        bCheckDisease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (cDisease.getSelectedIndex() > 0) {
                    Statement st;

                    try {
                        st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "SELECT baza.Choroba.nazwa, baza.Choroba.opis, baza.Choroba.objawy "
                            + "FROM baza.Choroba "
                            + "WHERE baza.Choroba.numer = " + aDiseaseNumber.get(cDisease.getSelectedIndex() - 1);

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

                    String message = "---- CHOROBA ----\n\nNazwa: " + scores[0] + "\n\nOpis: "
                            + scores[1] + "\n\nObjawy: " + scores[2] + "\n\n---- ZALECENIA ----\n\n";

                    myQuery = "SELECT l.zalecenie FROM baza.Leczenie l WHERE l.nr_choroba = "
                            + aDiseaseNumber.get(cDisease.getSelectedIndex() - 1);

                    String recomendation;

                    try {
                        rs = st.executeQuery(myQuery);
                        rs.next();
                        recomendation = rs.getString(1);
                        message += recomendation + "\n\n";
                    } catch (SQLException ex) {
                    }

                    myQuery = "SELECT l.nazwa, z.jak_czesto, z.ile FROM baza.Zalecane_leki z "
                            + "JOIN baza.Lekarstwo l ON l.kod = z.kod_lekarstwo JOIN baza.Leczenie le ON le.id = z.id_leczenie WHERE "
                            + "le.nr_choroba = " + aDiseaseNumber.get(cDisease.getSelectedIndex() - 1);

                    System.out.println(myQuery);

                    try {
                        rs = st.executeQuery(myQuery);

                        if (rs.isBeforeFirst()) {
                            while (rs.next()) {
                                message += rs.getString(1) + " " + rs.getInt(2) + " do " + rs.getInt(3) + "\n";
                            }
                        }
                    } catch (SQLException ex) {
                    }

                    showMessageDialog(message, "Informacje o chorobie");
                }
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
                            returnString += "Lek 1: " + rs.getString(1) + "  Lek 2: " + rs.getString(3) + "\n"
                                    + "Interakcja: " + rs.getString(5) + "\n\n\n";
                        } catch (SQLException ex) {
                        }
                    }

                }

                if (returnString.isEmpty()) {
                    returnString = "Nie wykryto interakcji";
                }

                showMessageDialog(returnString, "Informacje");
            }
        });

        bDelAppointment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "DELETE FROM baza.Wizyta WHERE baza.Wizyta.id = " + ProjektBazyDanych.currentAppointmentId;

                try {
                    st.executeUpdate(myQuery);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się odwołać wizyty", "Informacja", 1);
                    return;
                }

                JOptionPane.showMessageDialog(null, "Odwołano wizytę", "Informacja", 1);
                cLayout.show(pContainer, "pAppointments");

                model.setRowCount(0);

                aDisease.clear();
                aDiseaseNumber.clear();
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

                String date = tDate.getText().trim().replaceAll(" +", " ");
                String hour = tHour.getText().trim().replaceAll(" +", " ");

                if (date.trim().isEmpty() || hour.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                } else {
                    Statement st;

                    try {
                        st = connection.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "UPDATE baza.Wizyta SET baza.Wizyta.data = \'" + date + "\', baza.Wizyta.godzina = \'"
                            + hour + "\' WHERE baza.Wizyta.id = " + ProjektBazyDanych.currentAppointmentId;

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się uaktualnić wizyty", "Informacja", 1);
                        return;
                    }

                    if (cDisease.getSelectedIndex() > 0) {
                        myQuery = "INSERT INTO baza.Rozpoznanie(id_wizyta, nr_choroba) "
                                + "VALUES(" + ProjektBazyDanych.currentAppointmentId + ", "
                                + aDiseaseNumber.get(cDisease.getSelectedIndex() - 1) + ")";

                        try {
                            st.executeUpdate(myQuery);
                        } catch (SQLException ex) {
                            myQuery = "UPDATE baza.Rozpoznanie SET baza.Rozpoznanie.nr_choroba = "
                                    + aDiseaseNumber.get(cDisease.getSelectedIndex() - 1) + " WHERE "
                                    + "baza.Rozpoznanie.id_wizyta = " + ProjektBazyDanych.currentAppointmentId;

                            try {
                                st.executeUpdate(myQuery);
                            } catch (SQLException ex2) {
                                JOptionPane.showMessageDialog(null, "Nie udało się uaktualnić wizyty", "Informacja", 1);
                                return;
                            }
                        }
                    }

                    if (!aUsedCures.isEmpty()) {
                        for (int i = 0; i < aUsedCures.size(); i++) {
                            myQuery = "INSERT INTO baza.Przypisane_leki(id_wizyta, kod_lekarstwo, "
                                    + "jak_czesto, ile) VALUES(" + ProjektBazyDanych.currentAppointmentId + ", "
                                    + aUsedCures.get(i) + ", " + often.get(i) + ", " + many.get(i) + ")";

                            try {
                                st.executeUpdate(myQuery);
                            } catch (SQLException ex) {
                                myQuery = "UPDATE baza.Przypisane_leki SET baza.Przypisane_leki.jak_czesto = "
                                        + often.get(i) + ", baza.Przypisane_leki.ile = "
                                        + many.get(i) + " WHERE baza.Przypisane_leki.id_wizyta = "
                                        + ProjektBazyDanych.currentAppointmentId + " AND baza.Przypisane_leki.kod_lekarstwo = "
                                        + aUsedCures.get(i);

                                try {
                                    st.executeUpdate(myQuery);
                                } catch (SQLException ex2) {
                                    JOptionPane.showMessageDialog(null, "Nie udało się uaktualnić wizyty", "Informacja", 1);
                                }
                            }
                        }
                    }

                    myQuery = "SELECT baza.Przypisane_leki.kod_lekarstwo FROM baza.Przypisane_leki "
                            + "WHERE baza.Przypisane_leki.id_wizyta = " + ProjektBazyDanych.currentAppointmentId;

                    try {
                        st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    try {
                        ResultSet rs = st.executeQuery(myQuery);

                        if (rs.isBeforeFirst()) {
                            try {
                                st = connection.createStatement();
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                                return;
                            }
                            while (rs.next()) {
                                if (aUsedCures.indexOf(rs.getInt(1)) < 0) {
                                    myQuery = "DELETE FROM baza.Przypisane_leki WHERE baza.Przypisane_leki.id_wizyta = "
                                            + ProjektBazyDanych.currentAppointmentId + " AND "
                                            + "baza.Przypisane_leki.kod_lekarstwo = " + rs.getInt(1);

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
                        JOptionPane.showMessageDialog(null, "Nie udało się pobrać lekarstw", "Informacja", 1);
                        return;
                    }
                }

                JOptionPane.showMessageDialog(null, "Pomyślnie zaktualizowano wizytę", "Informacja", 1);
            }
        });

        bPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patient = lPatientName.getText();
                String doctor = lDoctorName.getText();

                ArrayList<String> curesToPrint = new ArrayList<>();

                Statement st;

                try {
                    st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "SELECT l.nazwa, p.jak_czesto, p.ile FROM baza.Przypisane_leki p "
                        + "JOIN baza.Lekarstwo l ON l.kod = p.kod_lekarstwo "
                        + "WHERE p.id_wizyta = " + ProjektBazyDanych.currentAppointmentId;

                ResultSet rs;
                try {
                    rs = st.executeQuery(myQuery);

                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            curesToPrint.add(rs.getString(1) + " - " + rs.getInt(2) + " do " + rs.getInt(3));
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się odczytać leków", "Informacja", 1);
                    return;
                }

                PDDocument document = new PDDocument();
                PDPage blankPage = new PDPage();
                document.addPage(blankPage);
                PDPageContentStream contentStream;

                try {
                    PDRectangle mediabox = blankPage.getMediaBox();
                    float margin = 72;
                    float startX = mediabox.getLowerLeftX() + margin;
                    float startY = mediabox.getUpperRightY() - margin;
                    float leading = 1.5f * 12;

                    contentStream = new PDPageContentStream(document, blankPage);
                    PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\calibri.ttf"));
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    contentStream.showText("Lekarz: " + doctor);
                    contentStream.newLineAtOffset(0, -leading * 2);
                    contentStream.showText("Pacjent: " + patient);
                    contentStream.newLineAtOffset(0, -leading * 2);

                    contentStream.showText("Przepisane leki: ");
                    contentStream.newLineAtOffset(0, -leading * 2);

                    for (String cureToPrint : curesToPrint) {
                        contentStream.showText(cureToPrint);
                        contentStream.newLineAtOffset(0, -leading);
                    }

                    contentStream.endText();
                    contentStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                PDDocumentInformation pdd = document.getDocumentInformation();
                pdd.setAuthor(doctor);
                pdd.setTitle("Recepta dla " + patient);
                pdd.setCreator(doctor);
                pdd.setSubject("Recepta");

                Calendar date = Calendar.getInstance();
                pdd.setCreationDate(date);
                pdd.setModificationDate(date);

                try {
                    document.save("C:/Pdfy/Recepty/" + Integer.toString(ProjektBazyDanych.currentAppointmentId) + "_" + patient + "_recepta.pdf");
                    document.close();
                    JOptionPane.showMessageDialog(null, "Pomyślnie utworzono raport", "Informacja", 1);

                    if (Desktop.isDesktopSupported()) {
                        try {
                            File myFile = new File("C:/Pdfy/Recepty/" + Integer.toString(ProjektBazyDanych.currentAppointmentId) + "_" + patient + "_recepta.pdf");
                            Desktop.getDesktop().open(myFile);
                        } catch (IOException ex) {
                            // no application registered for PDFs
                        }
                    }
                } catch (IOException ex) {

                }
            }

        });
    }

}
