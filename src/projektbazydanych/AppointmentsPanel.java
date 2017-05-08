/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class AppointmentsPanel extends JPanel {

    private final JTextField tDate;
    private final JTextField tDoctor;
    private final JTextField tPatient;
    private final JScrollPane scrollPane;
    private final Connection connection;
    private JTable jTable;
    ArrayList<String> aDoctors = new ArrayList<>();
    ArrayList<String> aPatients = new ArrayList<>();
    ArrayList<String> aDates = new ArrayList<>();
    ArrayList<String> aHours = new ArrayList<>();
    ArrayList<Integer> aIds = new ArrayList<>();

    private void fillTable() {
        aDoctors.clear();
        aPatients.clear();
        aDates.clear();
        aHours.clear();
        aIds.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT baza.Lekarz.imie, baza.Lekarz.nazwisko, baza.Pacjent.imie, baza.Pacjent.nazwisko, "
                + "baza.Wizyta.data, baza.Wizyta.godzina, baza.Wizyta.id "
                + "FROM baza.Lekarz, baza.Pacjent, baza.Wizyta";

        String date = tDate.getText();
        String[] doctor = tDoctor.getText().trim().replaceAll(" +", " ").split(" ");
        String[] patient = tPatient.getText().trim().replaceAll(" +", " ").split(" ");

        String whereClause = "";

        if (!date.isEmpty()) {
            if (doctor.length == 2) {
                if (patient.length == 2) {
                    whereClause = " WHERE baza.Lekarz.nazwisko like \'" + doctor[0] + "%\' AND baza.Lekarz.imie like \'"
                            + doctor[1] + "%\' AND baza.Pacjent.nazwisko like \'" + patient[0] + "%\' AND baza.Pacjent.imie like \'"
                            + patient[1] + "%\' AND baza.Wizyta.data = \'" + date + "\'";
                } else {
                    whereClause = " WHERE baza.Lekarz.nazwisko like \'" + doctor[0] + "%\' AND baza.Lekarz.imie like \'"
                            + doctor[1] + "%\' AND baza.Wizyta.data = \'" + date + "\'";
                }
            } else {
                if (patient.length == 2) {
                    whereClause = " WHERE baza.Pacjent.nazwisko like \'" + patient[0] + "%\' AND baza.Pacjent.imie like \'"
                            + patient[1] + "%\' AND baza.Wizyta.data = \'" + date + "\'";
                } else {
                    whereClause = " WHERE baza.Wizyta.data = \'" + date + "\'";
                }
            }
        } else {
            if (doctor.length == 2) {
                if (patient.length == 2) {
                    whereClause = " WHERE baza.Lekarz.nazwisko like \'" + doctor[0] + "%\' AND baza.Lekarz.imie = \'"
                            + doctor[1] + "\' AND baza.Pacjent.nazwisko like \'" + patient[0] + "%\' AND baza.Pacjent.imie like \'"
                            + patient[1] + "%\'";
                } else {
                    whereClause = " WHERE baza.Lekarz.nazwisko like \'" + doctor[0] + "%\' AND baza.Lekarz.imie like \'"
                            + doctor[1] + "%\'";
                }
            } else {
                if (patient.length == 2) {
                    whereClause = " WHERE baza.Pacjent.nazwisko like \'" + patient[0] + "%\' AND baza.Pacjent.imie = \'"
                            + patient[1] + "\'";
                }
            }
        }

        if (!whereClause.trim().isEmpty()) {
            myQuery += whereClause + " AND baza.Wizyta.nr_pacjent = baza.Pacjent.nr_ubezp AND baza.Wizyta.nr_lekarz = baza.Lekarz.numer";
        } else {
            myQuery += " WHERE baza.Wizyta.nr_pacjent = baza.Pacjent.nr_ubezp AND baza.Wizyta.nr_lekarz = baza.Lekarz.numer";
        }

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                jTable = new JTable();
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Lekarz");
                tableModel.addColumn("Pacjent");
                tableModel.addColumn("Data");
                tableModel.addColumn("Godzina");
                jTable.setModel(tableModel);
                scrollPane.getViewport().add(jTable);
            } else {
                String doctorName, patientName;

                while (rs.next()) {
                    doctorName = rs.getString(2) + " " + rs.getString(1);
                    patientName = rs.getString(4) + " " + rs.getString(3);

                    aDoctors.add(doctorName);
                    aPatients.add(patientName);
                    aDates.add(rs.getString(5));
                    aHours.add(rs.getString(6));
                    aIds.add(rs.getInt(7));
                }
            }
        } catch (SQLException ex) {
            jTable = new JTable();
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Lekarz");
            tableModel.addColumn("Pacjent");
            tableModel.addColumn("Data");
            tableModel.addColumn("Godzina");
            jTable.setModel(tableModel);
            scrollPane.getViewport().add(jTable);
            return;
        }

        if (!aDoctors.isEmpty() && !aPatients.isEmpty() && !aDates.isEmpty() && !aHours.isEmpty()) {
            String[] doctors = aDoctors.toArray(new String[aDoctors.size()]);
            String[] patients = aPatients.toArray(new String[aPatients.size()]);
            String[] dates = aDates.toArray(new String[aDates.size()]);
            String[] hours = aHours.toArray(new String[aHours.size()]);

            String[] columnNames = {"Lekarz", "Pacjent", "Data", "Godzina"};
            Object[][] data = new Object[aDoctors.size()][4];

            int i = 0;

            for (String doc : doctors) {
                data[i][0] = doc;
                i++;
            }

            i = 0;

            for (String pat : patients) {
                data[i][1] = pat;
                i++;
            }

            i = 0;

            for (String appointmentDate : dates) {
                data[i][2] = appointmentDate;
                i++;
            }

            i = 0;

            for (String hour : hours) {
                data[i][3] = hour;
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
        }
    }

    public AppointmentsPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(20, 640, 140, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bGo = new JButton("Idź");
        bGo.setBounds(200, 640, 140, 40);
        bGo.setFont(buttonFont);
        add(bGo);

        JButton bPrint = new JButton("Drukuj podsumowanie");
        bPrint.setBounds(380, 640, 250, 40);
        bPrint.setFont(buttonFont);
        add(bPrint);

        // Labels
        JLabel lNumber = new JLabel("Wyszukaj dla: ");
        Font labelFont = new Font(lNumber.getFont().getName(), Font.PLAIN, 20);
        lNumber.setBounds(20, 70, 140, 20);
        lNumber.setFont(labelFont);
        add(lNumber);

        JLabel lDate = new JLabel("Data");
        lDate.setBounds(150, 20, 140, 20);
        lDate.setFont(labelFont);
        add(lDate);

        JLabel lDoctor = new JLabel("Lekarz");
        lDoctor.setBounds(300, 20, 140, 20);
        lDoctor.setFont(labelFont);
        add(lDoctor);

        JLabel lPatient = new JLabel("Pacjent");
        lPatient.setBounds(560, 20, 140, 20);
        lPatient.setFont(labelFont);
        add(lPatient);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Text Fields
        tDate = new JTextField();
        Font textFont = new Font(tDate.getFont().getName(), Font.PLAIN, 20);
        tDate.setText(dateFormat.format(cal.getTime()));
        tDate.setBounds(150, 65, 140, 40);
        tDate.setBorder(border);
        tDate.setFont(textFont);
        add(tDate);

        tDoctor = new JTextField();
        tDoctor.setBounds(300, 65, 250, 40);
        tDoctor.setBorder(border);
        tDoctor.setFont(textFont);
        add(tDoctor);

        tPatient = new JTextField();
        tPatient.setBounds(560, 65, 250, 40);
        tPatient.setBorder(border);
        tPatient.setFont(textFont);
        add(tPatient);

        // Scroll Pane
        scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 130, 970, 500);
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
                    ProjektBazyDanych.currentAppointmentId = aIds.get(jTable.getSelectedRow());
                    cLayout.show(pContainer, "pAppointmentDetails");
                }
            }
        });

        tDate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }

        });

        tDoctor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }

        });

        tPatient.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                fillTable();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }

        });

        bPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jTable.getSelectedRow() >= 0) {
                    String doctor, patient, date, hour, disease = "";

                    int id = jTable.getSelectedRow();
                    doctor = aDoctors.get(id);
                    patient = aPatients.get(id);
                    date = aDates.get(id);
                    hour = aHours.get(id);

                    Statement st;

                    try {
                        st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "SELECT c.nazwa FROM baza.Choroba c JOIN baza.Rozpoznanie r ON r.id_wizyta = "
                            + aIds.get(id) + " WHERE c.numer = r.nr_choroba";

                    ResultSet rs;

                    try {
                        rs = st.executeQuery(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się pobrać choroby", "Informacja", 1);
                        return;
                    }

                    try {
                        if (rs.isBeforeFirst()) {
                            rs.next();
                            disease = rs.getString(1);
                        }
                    } catch (SQLException ex) {
                    }

                    myQuery = "SELECT l.nazwa, p.jak_czesto, p.ile FROM baza.Przypisane_leki p "
                            + "JOIN baza.Lekarstwo l ON l.kod = p.kod_lekarstwo "
                            + "WHERE p.id_wizyta = " + aIds.get(id);

                    try {
                        st.executeQuery(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się pobrać lekarstw", "Informacja", 1);
                        return;
                    }

                    ArrayList<String> curesToPrint = new ArrayList<>();

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

                    myQuery = "SELECT w.data, w.godzina, l.nazwisko, l.imie, p.nazwisko, p.imie "
                            + "FROM baza.Wizyta w JOIN baza.Lekarz l ON l.numer = w.nr_lekarz JOIN"
                            + " baza.Pacjent p ON p.nr_ubezp = w.nr_pacjent WHERE w.id = " + aIds.get(id);

                    try {
                        rs = st.executeQuery(myQuery);

                        if (rs.isBeforeFirst()) {
                            rs.next();

                            date = rs.getString(1);
                            hour = rs.getString(2);
                            doctor = rs.getString(3) + " " + rs.getString(4);
                            patient = rs.getString(5) + " " + rs.getString(6);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się odczytać uczestników", "Informacja", 1);
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
                        contentStream.showText("Choroba: " + disease);
                        contentStream.newLineAtOffset(0, -leading * 2);
                        contentStream.showText("Data: " + date);
                        contentStream.newLineAtOffset(0, -leading * 2);
                        contentStream.showText("Godzina: " + hour);
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
                    pdd.setTitle("Pdosumowanie dla " + patient + " - " + date);
                    pdd.setCreator(doctor);
                    pdd.setSubject("Podsumowanie");

                    Calendar currDate = Calendar.getInstance();
                    pdd.setCreationDate(currDate);
                    pdd.setModificationDate(currDate);

                    try {
                        document.save("C:/Pdfy/Podsumowania/" + Integer.toString(aIds.get(id)) + "_" + patient + "_podsumowanie.pdf");
                        document.close();
                        JOptionPane.showMessageDialog(null, "Pomyślnie utworzono raport", "Informacja", 1);
                        
                        if (Desktop.isDesktopSupported()) {
                            try {
                                File myFile = new File("C:/Pdfy/Podsumowania/" + Integer.toString(aIds.get(id)) + "_" + patient + "_podsumowanie.pdf");
                                Desktop.getDesktop().open(myFile);
                            } catch (IOException ex) {
                                // no application registered for PDFs
                            }
                        }
                    } catch (IOException ex) {

                    }
                }
            }

        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                fillTable();
            }
        });
    }
}
