/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class ProjektBazyDanych extends JFrame {

    public static int currentAppointmentId;
    public static int currentDoctorId;
    public static int currentDiseaseId;

    // Panels
    JPanel pContainer = new JPanel();
    JPanel pEntry = new JPanel();

    // Layout
    CardLayout cLayout = new CardLayout();

    // Labels
    JLabel lReception = new JLabel("Recepcja: ");
    JLabel lDoctors = new JLabel("Lekarze: ");
    JLabel lReports = new JLabel("Raporty: ");

    // Buttons
    JButton bGoPatient = new JButton("Dodaj pacjenta");
    JButton bGoDoctor = new JButton("Dodaj lekarza");
    JButton bGoDisease = new JButton("Dodaj chorobę");
    JButton bGoCure = new JButton("Dodaj lekarstwo");
    JButton bGoAppointment = new JButton("Ustal wizytę");
    JButton bGoInteraction = new JButton("Ustal interakcję");
    JButton bGoDoctors = new JButton("Lekarze");
    JButton bGoAppointments = new JButton("Wizyty");
    JButton bGoDiseases = new JButton("Choroby");
    JButton bGoCures = new JButton("Lekarstwa");
    JButton bGoPatients = new JButton("Pacjenci");
    JButton bPrintDoctorsCures = new JButton("Lekarze->leki");
    JButton bPrintCuresDiseases = new JButton("Lekarstwa->choroby");
    JButton bPrintDoctorsAppointments = new JButton("Lekarze->ilość wizyt");
    JButton bFillDatabase = new JButton("Uzupełnij bazę");

    // Połączenie DB2
    Connection con;

    Baza baza;

    String months[] = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec",
        "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"
    };

    public ProjektBazyDanych() {
        super("Projekt bazy danych");

        // Connect to DB2
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            con = DriverManager.getConnection("jdbc:db2:lekarze");
        } catch (ClassNotFoundException classEx) {
            JOptionPane.showMessageDialog(null, "Niestety nie udało się wczytać sterownika bazy", "Błąd", 0);
        } catch (SQLException sqlEx) {
            JOptionPane.showMessageDialog(null, "Niestety nie udało się połączyć z bazą danych", "Błąd", 0);
        }

        baza = new Baza(con);

        pContainer.setLayout(cLayout);

        // Set entry window
        pEntry.setLayout(null);

        Font labelFont = new Font(lReception.getFont().getName(), Font.PLAIN, 20);
        Font buttonFont = new Font(bGoPatient.getFont().getName(), Font.PLAIN, 20);

        // Labels
        lReception.setBounds(20, 20, 140, 20);
        lReception.setFont(labelFont);
        pEntry.add(lReception);

        lDoctors.setBounds(360, 20, 140, 20);
        lDoctors.setFont(labelFont);
        pEntry.add(lDoctors);

        lReports.setBounds(700, 20, 140, 20);
        lReports.setFont(labelFont);
        pEntry.add(lReports);

        // Buttons
        bGoPatient.setBounds(20, 70, 250, 40);
        bGoPatient.setFont(buttonFont);
        pEntry.add(bGoPatient);

        bGoDoctor.setBounds(20, 120, 250, 40);
        bGoDoctor.setFont(buttonFont);
        pEntry.add(bGoDoctor);

        bGoDisease.setBounds(20, 170, 250, 40);
        bGoDisease.setFont(buttonFont);
        pEntry.add(bGoDisease);

        bGoCure.setBounds(20, 220, 250, 40);
        bGoCure.setFont(buttonFont);
        pEntry.add(bGoCure);

        bGoAppointment.setBounds(20, 270, 250, 40);
        bGoAppointment.setFont(buttonFont);
        pEntry.add(bGoAppointment);

        bGoInteraction.setBounds(20, 320, 250, 40);
        bGoInteraction.setFont(buttonFont);
        pEntry.add(bGoInteraction);

        bGoDoctors.setBounds(20, 370, 250, 40);
        bGoDoctors.setFont(buttonFont);
        pEntry.add(bGoDoctors);

        bGoAppointments.setBounds(360, 70, 250, 40);
        bGoAppointments.setFont(buttonFont);
        pEntry.add(bGoAppointments);

        bGoDiseases.setBounds(360, 120, 250, 40);
        bGoDiseases.setFont(buttonFont);
        pEntry.add(bGoDiseases);

        bGoCures.setBounds(360, 170, 250, 40);
        bGoCures.setFont(buttonFont);
        pEntry.add(bGoCures);

        bGoPatients.setBounds(360, 220, 250, 40);
        bGoPatients.setFont(buttonFont);
        pEntry.add(bGoPatients);

        bPrintDoctorsCures.setBounds(700, 70, 250, 40);
        bPrintDoctorsCures.setFont(buttonFont);
        pEntry.add(bPrintDoctorsCures);

        bPrintCuresDiseases.setBounds(700, 120, 250, 40);
        bPrintCuresDiseases.setFont(buttonFont);
        pEntry.add(bPrintCuresDiseases);

        bPrintDoctorsAppointments.setBounds(700, 170, 250, 40);
        bPrintDoctorsAppointments.setFont(buttonFont);
        pEntry.add(bPrintDoctorsAppointments);

        bFillDatabase.setBounds(750, 680, 250, 40);
        bFillDatabase.setFont(buttonFont);
        pEntry.add(bFillDatabase);

        PatientPanel pPatient = new PatientPanel(cLayout, pContainer, con);
        DoctorPanel pDoctor = new DoctorPanel(cLayout, pContainer, con);
        DiseasePanel pDisease = new DiseasePanel(cLayout, pContainer, con);
        CurePanel pCure = new CurePanel(cLayout, pContainer, con);
        AppointmentPanel pAppointment = new AppointmentPanel(cLayout, pContainer, con);
        InteractionPanel pInteraction = new InteractionPanel(cLayout, pContainer, con);
        AppointmentsPanel pAppointments = new AppointmentsPanel(cLayout, pContainer, con);
        AppointmentDetailsPanel pAppointmentDetails = new AppointmentDetailsPanel(cLayout, pContainer, con);
        DoctorsPanel pDoctors = new DoctorsPanel(cLayout, pContainer, con);
        DoctorDetailsPanel pDoctorDetails = new DoctorDetailsPanel(cLayout, pContainer, con);
        DiseaseDetailsPanel pDiseaseDetails = new DiseaseDetailsPanel(cLayout, pContainer, con);
        DiseasesPanel pDiseases = new DiseasesPanel(cLayout, pContainer, con);
        CuresPanel pCures = new CuresPanel(cLayout, pContainer, con);
        PatientsPanel pPatients = new PatientsPanel(cLayout, pContainer, con);

        pContainer.add(pEntry, "pEntry");
        pContainer.add(pPatient, "pPatient");
        pContainer.add(pDoctor, "pDoctor");
        pContainer.add(pDisease, "pDisease");
        pContainer.add(pCure, "pCure");
        pContainer.add(pAppointment, "pAppointment");
        pContainer.add(pInteraction, "pInteraction");
        pContainer.add(pDoctors, "pDoctors");
        pContainer.add(pAppointments, "pAppointments");
        pContainer.add(pAppointmentDetails, "pAppointmentDetails");
        pContainer.add(pDoctorDetails, "pDoctorDetails");
        pContainer.add(pDiseaseDetails, "pDiseaseDetails");
        pContainer.add(pDiseases, "pDiseases");
        pContainer.add(pCures, "pCures");
        pContainer.add(pPatients, "pPatients");

        cLayout.show(pContainer, "pEntry");

        bGoPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pPatient");
            }
        });

        bGoDoctor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pDoctor");
            }
        });

        bGoDisease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pDisease");
            }
        });

        bGoCure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pCure");
            }
        });

        bGoAppointment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pAppointment");
            }
        });

        bGoInteraction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pInteraction");
            }
        });

        bGoDoctors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pDoctors");
            }
        });

        bGoAppointments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pAppointments");
            }
        });

        bGoDiseases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pDiseases");
            }
        });

        bGoCures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pCures");
            }
        });

        bGoPatients.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pPatients");
            }
        });

        bPrintDoctorsCures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "select l.nazwisko, l.imie, lek.nazwa, count(lek.nazwa) FROM baza.Lekarz l, baza.Lekarstwo lek JOIN "
                        + "baza.Przypisane_leki p on p.kod_lekarstwo = lek.kod JOIN baza.Wizyta w ON w.id = p.id_wizyta WHERE w.nr_lekarz "
                        + "= l.numer AND month(w.data) = month(now()) GROUP BY l.nazwisko, l.imie, lek.nazwa ORDER BY l.nazwisko, lek.nazwa";

                ResultSet rs;

                ArrayList<String> aDoctors = new ArrayList<>();
                ArrayList<String> aCures = new ArrayList<>();
                ArrayList<Integer> aCounters = new ArrayList<>();

                try {
                    rs = st.executeQuery(myQuery);

                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            aDoctors.add(rs.getString(1) + " " + rs.getString(2));
                            aCures.add(rs.getString(3));
                            aCounters.add(rs.getInt(4));
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się odczytać wizyt/lekarzy", "Informacja", 1);
                    return;
                }

                PDDocument document = new PDDocument();
                PDPage blankPage = new PDPage();
                document.addPage(blankPage);
                PDPageContentStream contentStream;

                Calendar currDate = Calendar.getInstance();
                SimpleDateFormat fMonth = new SimpleDateFormat("MM");
                String sMonth = months[Integer.parseInt(fMonth.format(currDate.getTime())) - 1];
                SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
                String sYear = fYear.format(currDate.getTime());

                try {
                    PDRectangle mediabox = blankPage.getMediaBox();
                    float margin = 72;
                    float startX = mediabox.getLowerLeftX() + margin;
                    float startY = mediabox.getUpperRightY() - margin;
                    float leading = 1.5f * 12;
                    float counter = -margin;

                    System.out.println(mediabox.getHeight());

                    PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\calibri.ttf"));

                    contentStream = new PDPageContentStream(document, blankPage);
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);

                    contentStream.showText(sMonth + " " + sYear);
                    contentStream.newLineAtOffset(0, -leading * 2);
                    counter += (-leading * 2);

                    String lastDoctor = "";

                    for (int i = 0; i < aDoctors.size(); i++) {
                        if (counter < (-mediabox.getHeight() + margin)) {
                            contentStream.endText();
                            contentStream.close();
                            blankPage = new PDPage();
                            document.addPage(blankPage);
                            contentStream = new PDPageContentStream(document, blankPage);
                            contentStream.setFont(font, 12);
                            contentStream.beginText();
                            contentStream.newLineAtOffset(startX, startY);
                            counter = -margin;
                        }

                        if (!lastDoctor.equals(aDoctors.get(i))) {
                            if (i > 0) {
                                contentStream.newLineAtOffset(0, -leading * 2);
                                counter += (-leading * 2);
                            }
                            lastDoctor = aDoctors.get(i);
                            contentStream.showText("Lekarz: " + lastDoctor);
                            contentStream.newLineAtOffset(0, -leading * 2);
                            counter += (-leading * 2);
                        }
                        contentStream.showText(aCures.get(i) + " - " + aCounters.get(i));
                        contentStream.newLineAtOffset(0, -leading * 2);
                        counter += (-leading * 2);
                    }

                    contentStream.endText();
                    contentStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                PDDocumentInformation pdd = document.getDocumentInformation();
                pdd.setAuthor("Przychodnia");
                pdd.setTitle("Lekarstwa na doktorów");
                pdd.setCreator("Przychodnia");
                pdd.setSubject("Lekarze, lekarstwa, aktualny miesiąc");

                pdd.setCreationDate(currDate);
                pdd.setModificationDate(currDate);

                SimpleDateFormat fFullDate = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
                String sFullDate = fFullDate.format(currDate.getTime());

                try {
                    document.save("C:/Pdfy/Przepisywane leki/" + sFullDate + "_przypisywane_leki.pdf");
                    document.close();
                    JOptionPane.showMessageDialog(null, "Pomyślnie utworzono raport", "Informacja", 1);
                    if (Desktop.isDesktopSupported()) {
                        try {
                            File myFile = new File("C:/Pdfy/Przepisywane leki/" + sFullDate + "_przypisywane_leki.pdf");
                            Desktop.getDesktop().open(myFile);
                        } catch (IOException ex) {
                            // no application registered for PDFs
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        bPrintCuresDiseases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "SELECT c.nazwa, l.nazwa FROM baza.Choroba c, baza.Lekarstwo l, baza.Wizyta w "
                        + "JOIN baza.Przypisane_leki p ON p.id_wizyta = w.id JOIN baza.Rozpoznanie r ON "
                        + "r.id_wizyta = w.id WHERE c.numer = r.nr_choroba AND p.kod_lekarstwo = l.kod "
                        + "GROUP BY c.nazwa, l.nazwa ORDER BY c.nazwa, l.nazwa";

                ResultSet rs;

                ArrayList<String> aDiseases = new ArrayList<>();
                ArrayList<String> aCures = new ArrayList<>();

                try {
                    rs = st.executeQuery(myQuery);

                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            aDiseases.add(rs.getString(1));
                            aCures.add(rs.getString(2));
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się odczytać lekarstw/chorób", "Informacja", 1);
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
                    float counter = -margin;

                    contentStream = new PDPageContentStream(document, blankPage);
                    PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\calibri.ttf"));
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    String lastDisease = "";

                    for (int i = 0; i < aDiseases.size(); i++) {
                        if (counter < (-mediabox.getHeight() + margin)) {
                            contentStream.endText();
                            contentStream.close();
                            blankPage = new PDPage();
                            document.addPage(blankPage);
                            contentStream = new PDPageContentStream(document, blankPage);
                            contentStream.setFont(font, 12);
                            contentStream.beginText();
                            contentStream.newLineAtOffset(startX, startY);
                            counter = -margin;
                        }

                        if (!lastDisease.equals(aDiseases.get(i))) {
                            lastDisease = aDiseases.get(i);
                            if (i > 0) {
                                contentStream.newLineAtOffset(0, -leading * 2);
                                counter += (-leading * 2);
                            }
                            contentStream.showText("Choroba: " + lastDisease);
                            contentStream.newLineAtOffset(0, -leading * 2);
                            counter += (-leading * 2);
                        }
                        contentStream.showText("Lekarstwo: " + aCures.get(i));
                        contentStream.newLineAtOffset(0, -leading * 2);
                        counter += (-leading * 2);
                    }

                    contentStream.endText();
                    contentStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                PDDocumentInformation pdd = document.getDocumentInformation();
                pdd.setAuthor("Przychodnia");
                pdd.setTitle("Leki laczące choroby");
                pdd.setCreator("Przychodnia");
                pdd.setSubject("Leki, choroby");

                Calendar currDate = Calendar.getInstance();
                pdd.setCreationDate(currDate);
                pdd.setModificationDate(currDate);

                SimpleDateFormat fFullDate = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
                String sFullDate = fFullDate.format(currDate.getTime());

                try {
                    document.save("C:/Pdfy/Leczenie/" + sFullDate + "_leczenie.pdf");
                    document.close();
                    JOptionPane.showMessageDialog(null, "Pomyślnie utworzono raport", "Informacja", 1);
                    if (Desktop.isDesktopSupported()) {
                        try {
                            File myFile = new File("C:/Pdfy/Leczenie/" + sFullDate + "_leczenie.pdf");
                            Desktop.getDesktop().open(myFile);
                        } catch (IOException ex) {
                            // no application registered for PDFs
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        bPrintDoctorsAppointments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Statement st;

                try {
                    st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }

                String myQuery = "SELECT l.nazwisko, l.imie, count(w.id) AS cnt from baza.Lekarz l JOIN baza.Wizyta "
                        + "w ON w.nr_lekarz = l.numer GROUP BY l.nazwisko, l.imie ORDER BY cnt DESC";

                ResultSet rs;

                ArrayList<String> aDoctors = new ArrayList<>();
                ArrayList<Integer> aAppointments = new ArrayList<>();

                try {
                    rs = st.executeQuery(myQuery);

                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            aDoctors.add(rs.getString(1) + " " + rs.getString(2));
                            aAppointments.add(rs.getInt(3));
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się odczytać wizyt/lekarzy", "Informacja", 1);
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
                    float counter = -margin;

                    contentStream = new PDPageContentStream(document, blankPage);
                    PDType0Font font = PDType0Font.load(document, new File("C:\\Windows\\Fonts\\calibri.ttf"));
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);

                    for (int i = 0; i < aDoctors.size(); i++) {
                        if (counter < (-mediabox.getHeight() + margin)) {
                            contentStream.endText();
                            contentStream.close();
                            blankPage = new PDPage();
                            document.addPage(blankPage);
                            contentStream = new PDPageContentStream(document, blankPage);
                            contentStream.setFont(font, 12);
                            contentStream.beginText();
                            contentStream.newLineAtOffset(startX, startY);
                            counter = -margin;
                        }
                        contentStream.showText(aDoctors.get(i) + " - " + aAppointments.get(i));
                        contentStream.newLineAtOffset(0, -leading * 2);
                        counter += (-leading * 2);
                    }

                    contentStream.endText();
                    contentStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                PDDocumentInformation pdd = document.getDocumentInformation();
                pdd.setAuthor("Przychodnia");
                pdd.setTitle("Liczba wizyt");
                pdd.setCreator("Przychodnia");
                pdd.setSubject("Lekarze, wizyty");

                Calendar currDate = Calendar.getInstance();
                pdd.setCreationDate(currDate);
                pdd.setModificationDate(currDate);

                SimpleDateFormat fFullDate = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
                String sFullDate = fFullDate.format(currDate.getTime());

                try {
                    document.save("C:/Pdfy/Wizyty/" + sFullDate + "_wiziyty.pdf");
                    document.close();
                    JOptionPane.showMessageDialog(null, "Pomyślnie utworzono raport", "Informacja", 1);

                    if (Desktop.isDesktopSupported()) {
                        try {
                            File myFile = new File("C:/Pdfy/Wizyty/" + sFullDate + "_wiziyty.pdf");
                            Desktop.getDesktop().open(myFile);
                        } catch (IOException ex) {
                            // no application registered for PDFs
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        bFillDatabase.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0
            ) {
                baza.fillDatabase();
            }
        }
        );

        add(pContainer);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();

        setSize(
                1024, 768);
        setResizable(
                false);
        setVisible(
                true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ProjektBazyDanych.class.getName()).log(Level.SEVERE, null, ex);
                }
                ProjektBazyDanych projektBazyDanych = new ProjektBazyDanych();
            }
        });

        String[] dirs = new String[6];
        dirs[0] = "C:/Pdfy/Wizyty/";
        dirs[1] = "C:/Pdfy/Ulotki/";
        dirs[2] = "C:/Pdfy/Recepty/";
        dirs[3] = "C:/Pdfy/Leczenie";
        dirs[4] = "C:/Pdfy/Podsumowania";
        dirs[5] = "C:/Pdfy/Przepisywane leki";

        for (int i = 0; i < 6; i++) {
            File file = new File(dirs[i]);

            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
        }

    }

}
