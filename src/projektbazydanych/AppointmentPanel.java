/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Tomek
 */
public class AppointmentPanel extends JPanel {

    private final ArrayList<String> availableDoctors = new ArrayList<>();
    private final ArrayList<Integer> doctorNumbers = new ArrayList<>();
    private final Connection connection;
    private final JTextField tDate;
    private final JTextField tHour;
    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final DateFormat hourFormat = new SimpleDateFormat("HH:mm");
    private final JComboBox cDoctors;

    private void fillDoctors() {
        java.util.Date date;

        availableDoctors.clear();
        doctorNumbers.clear();
        cDoctors.removeAllItems();

        try {
            date = dateFormat.parse(tDate.getText());
        } catch (ParseException ex) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        if (day == 1) {
            day += 6;
        } else {
            day -= 1;
        }

        String hour = tHour.getText();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            cDoctors.removeAllItems();
            return;
        }
        
        String myQuery = "SELECT baza.Lekarz.numer, baza.Lekarz.imie, baza.Lekarz.nazwisko from baza.Lekarz, "
                + "baza.Dostepnosc WHERE baza.Dostepnosc.nr_lekarz = baza.Lekarz.numer AND "
                + "\'" + hour + "\' >= baza.Dostepnosc.godzina_start AND \'" + hour
                + "\' < baza.Dostepnosc.godzina_stop AND " + day + " = baza.Dostepnosc.dzien_tygodnia "
                + "ORDER BY baza.Lekarz.imie ASC";

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                return;
            } else {
                String doctorName;
                while (rs.next()) {
                    doctorName = rs.getString(2) + " " + rs.getString(3);

                    doctorNumbers.add(rs.getInt(1));
                    availableDoctors.add(doctorName);
                }
            }
        } catch (SQLException ex) {
            cDoctors.removeAllItems();
            return;
        }

        if (!availableDoctors.isEmpty()) {
            String[] doctors = availableDoctors.toArray(new String[availableDoctors.size()]);

            cDoctors.removeAllItems();

            for (String doctor : doctors) {
                cDoctors.addItem(doctor);
            }
        }

    }

    public AppointmentPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(420, 215, 150, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(230, 215, 150, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        // Labels
        JLabel lNumber = new JLabel("Numer ubezpieczenia pacjenta: ");
        Font labelFont = new Font(lNumber.getFont().getName(), Font.PLAIN, 20);
        lNumber.setBounds(20, 20, 350, 20);
        lNumber.setFont(labelFont);
        add(lNumber);

        JLabel lDate = new JLabel("Data: ");
        lDate.setBounds(20, 70, 140, 20);
        lDate.setFont(labelFont);
        add(lDate);

        JLabel lHour = new JLabel("Godzina: ");
        lHour.setBounds(20, 120, 140, 20);
        lHour.setFont(labelFont);
        add(lHour);

        JLabel lDoctor = new JLabel("Dostępni lekarze: ");
        lDoctor.setBounds(20, 170, 230, 20);
        lDoctor.setFont(labelFont);
        add(lDoctor);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextField tInsuranceNumber = new JTextField();
        Font textFont = new Font(tInsuranceNumber.getFont().getName(), Font.PLAIN, 20);
        tInsuranceNumber.setBounds(320, 15, 250, 40);
        tInsuranceNumber.setBorder(border);
        tInsuranceNumber.setFont(textFont);
        add(tInsuranceNumber);

        Calendar cal = Calendar.getInstance();

        tDate = new JTextField();
        tDate.setText(dateFormat.format(cal.getTime()));
        tDate.setBounds(320, 65, 250, 40);
        tDate.setBorder(border);
        tDate.setFont(textFont);
        add(tDate);
        
        tHour = new JTextField();
        tHour.setText(hourFormat.format(cal.getTime()));
        tHour.setBounds(320, 115, 250, 40);
        tHour.setBorder(border);
        tHour.setFont(textFont);
        add(tHour);

        // Combo Boxes
        cDoctors = new JComboBox();
        Font comboFont = new Font(cDoctors.getFont().getName(), Font.PLAIN, 20);
        cDoctors.setBounds(320, 165, 250, 40);
        cDoctors.setFont(comboFont);
        add(cDoctors);

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (cDoctors.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                    return;
                }

                int insuranceNumber, doctorNumber;

                try {
                    insuranceNumber = Integer.parseInt(tInsuranceNumber.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy numer ubezpieczenia", "Błąd", 0);
                    return;
                }

                String date, hour, doctorName;

                doctorName = (String) cDoctors.getSelectedItem();
                date = tDate.getText().trim().replaceAll(" +", " ");
                hour = tHour.getText().trim().replaceAll(" +", " ");

                if (doctorName.trim().isEmpty() || date.trim().isEmpty() || hour.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                } else {
                    doctorNumber = doctorNumbers.get(availableDoctors.indexOf(doctorName));

                    Statement st;
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "INSERT INTO baza.Wizyta(data, godzina, nr_pacjent, nr_lekarz) "
                            + "VALUES(\'" + date + "\', \'" + hour + "\', " + insuranceNumber
                            + ", " + doctorNumber + ")";
                    
                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się ustalić wizyty", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Ustalono wizytę", "Informacja", 1);
                    cLayout.show(pContainer, "pEntry");

                    cDoctors.removeAllItems();
                    tInsuranceNumber.setText("");
                }
            }
        });

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                cDoctors.removeAllItems();
                tInsuranceNumber.setText("");
            }

        });
        
        tDate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                fillDoctors();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }
        });
        
        tHour.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                fillDoctors();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                fillDoctors();
            }
        });
    }
}
