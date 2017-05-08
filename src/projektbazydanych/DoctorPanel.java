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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class DoctorPanel extends JPanel {

    private final String[] hoursStart = {"WOLNE", "06:00",
        "06:30", "07:00", "07:30", "08:00", "08:30", "09:00",
        "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
        "12:30", "13:00", "13:30", "14:00", "14:30", "15:00",
        "15:30", "16:00", "16:30", "17:00", "17:30", "18:00",
        "18:30", "19:00", "19:30", "20:00", "20:30", "21:00",
        "21:30", "22:00", "22:30", "23:00", "23:30", "00:00",
        "00:30", "01:00", "01:30", "02:00", "02:30", "03:00",
        "03:30", "04:00", "04:30", "05:00", "05:30"
    };

    private final String[] hoursStop = {"WOLNE", "14:00",
        "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
        "17:30", "18:00", "18:30", "19:00", "19:30", "20:00",
        "20:30", "21:00", "21:30", "22:00", "22:30", "23:00",
        "23:30", "00:00", "00:30", "01:00", "01:30", "02:00",
        "02:30", "03:00", "03:30", "04:00", "04:30", "05:00",
        "05:30", "06:00", "06:30", "07:00", "07:30", "08:00",
        "08:30", "09:00", "09:30", "10:00", "10:30", "11:00",
        "11:30", "12:00", "12:30", "13:00", "13:30"
    };

    public DoctorPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(220, 315, 170, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(20, 315, 170, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        // Labels
        JLabel lNumber = new JLabel("Numer: ");
        Font labelFont = new Font(lNumber.getFont().getName(), Font.PLAIN, 20);
        lNumber.setBounds(20, 20, 250, 20);
        lNumber.setFont(labelFont);
        add(lNumber);

        JLabel lFirstName = new JLabel("Imię: ");
        lFirstName.setBounds(20, 70, 250, 20);
        lFirstName.setFont(labelFont);
        add(lFirstName);

        JLabel lLastName = new JLabel("Nazwisko: ");
        lLastName.setBounds(20, 120, 250, 20);
        lLastName.setFont(labelFont);
        add(lLastName);

        JLabel lTel = new JLabel("Telefon: ");
        lTel.setBounds(20, 170, 250, 20);
        lTel.setFont(labelFont);
        add(lTel);

        JLabel lMonday = new JLabel("Poniedziałek: ");
        lMonday.setBounds(450, 20, 400, 20);
        lMonday.setFont(labelFont);
        add(lMonday);

        JLabel lTuesday = new JLabel("Wtorek: ");
        lTuesday.setBounds(450, 70, 250, 20);
        lTuesday.setFont(labelFont);
        add(lTuesday);

        JLabel lWednesday = new JLabel("Środa: ");
        lWednesday.setBounds(450, 120, 250, 20);
        lWednesday.setFont(labelFont);
        add(lWednesday);

        JLabel lThursday = new JLabel("Czwartek: ");
        lThursday.setBounds(450, 170, 250, 20);
        lThursday.setFont(labelFont);
        add(lThursday);

        JLabel lFriday = new JLabel("Piątek: ");
        lFriday.setBounds(450, 220, 250, 20);
        lFriday.setFont(labelFont);
        add(lFriday);

        JLabel lSaturday = new JLabel("Sobota: ");
        lSaturday.setBounds(450, 270, 250, 20);
        lSaturday.setFont(labelFont);
        add(lSaturday);

        JLabel lSunday = new JLabel("Niedziela: ");
        lSunday.setBounds(450, 320, 250, 20);
        lSunday.setFont(labelFont);
        add(lSunday);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextField tNumber = new JTextField();
        Font textFont = new Font(tNumber.getFont().getName(), Font.PLAIN, 20);
        tNumber.setBounds(160, 15, 230, 40);
        tNumber.setBorder(border);
        tNumber.setFont(textFont);
        add(tNumber);

        final JTextField tFirstName = new JTextField();
        tFirstName.setBounds(160, 65, 230, 40);
        tFirstName.setBorder(border);
        tFirstName.setFont(textFont);
        add(tFirstName);

        final JTextField tLastName = new JTextField();
        tLastName.setBounds(160, 115, 230, 40);
        tLastName.setBorder(border);
        tLastName.setFont(textFont);
        add(tLastName);

        final JTextField tTelPrefix = new JTextField();
        tTelPrefix.setBounds(160, 165, 50, 40);
        tTelPrefix.setBorder(border);
        tTelPrefix.setFont(textFont);
        add(tTelPrefix);

        final JTextField tTel = new JTextField();
        tTel.setBounds(240, 165, 150, 40);
        tTel.setBorder(border);
        tTel.setFont(textFont);
        add(tTel);

        // Combo Boxes
        final JComboBox[] cStart = new JComboBox[7];
        final JComboBox[] cStop = new JComboBox[7];

        int y = 15;
        
        Font comboFont;

        for (int i = 0; i < 7; i++) {
            cStart[i] = new JComboBox(hoursStart);
            comboFont = new Font(cStart[i].getFont().getName(), Font.PLAIN, 20);
            cStart[i].setBounds(600, y, 150, 40);
            cStart[i].setFont(comboFont);
            add(cStart[i]);

            cStop[i] = new JComboBox(hoursStop);
            cStop[i].setBounds(800, y, 150, 40);
            cStop[i].setFont(comboFont);
            add(cStop[i]);

            y += 50;
        }

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                tNumber.setText("");
                tFirstName.setText("");
                tLastName.setText("");
                tTelPrefix.setText("");
                tTel.setText("");
            }

        });

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int number;
                String firstName, lastName, telPrefix, tel;

                try {
                    number = Integer.parseInt(tNumber.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy numer", "Błąd", 0);
                    return;
                }

                firstName = tFirstName.getText().trim().replaceAll(" +", " ");
                lastName = tLastName.getText().trim().replaceAll(" +", " ");
                telPrefix = tTelPrefix.getText().trim().replaceAll(" +", " ");
                tel = tTel.getText().trim().replaceAll(" +", " ");

                Statement st;
                
                if(tel.length() == 9 && telPrefix.contains("+") && !tel.contains(" ")) {
                    tel = tel.substring(0, 3) + " " + tel.substring(3, 6) + " " + tel.substring(6, 9);
                } else if(tel.length() == 7 && !telPrefix.contains("+") && !tel.contains(" ")) {
                    tel = tel.substring(0, 3) + " " + tel.substring(3, 5) + " " + tel.substring(5, 7);
                } else if(!((tel.length() == 9 && !telPrefix.contains("+") && tel.contains(" "))
                        || (tel.length() == 11 && telPrefix.contains("+") && tel.contains(" ")))) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij błędne pola", "Błąd", 0);
                    return;
                }

                if (firstName.trim().isEmpty() || lastName.trim().isEmpty()
                        || telPrefix.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij puste pole", "Błąd", 0);
                    return;
                } else {
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    
                    
                    String myQuery = "INSERT INTO baza.Lekarz(numer, nazwisko, imie, telefon) "
                            + "VALUES(" + number + ", \'" + lastName + "\', \'" + firstName
                            + "\', \'" + telPrefix + " " + tel + "\')";

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się dodać lekarza", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Dodano lekarza", "Informacja", 1);
                }

                String[] start = new String[7];
                String[] stop = new String[7];

                for (int i = 0; i < 7; i++) {
                    start[i] = (String) cStart[i].getSelectedItem();
                    stop[i] = (String) cStop[i].getSelectedItem();

                    if (!start[i].equals("WOLNE") && !stop[i].equals("WOLNE")) {
                        String myQuery = "INSERT INTO baza.Dostepnosc(dzien_tygodnia, godzina_start, godzina_stop, nr_lekarz) "
                                + "VALUES(" + (i + 1) + ", \'" + start[i] + "\', \'" + stop[i]
                                + "\', " + number + ")";
                        try {
                            st = con.createStatement();
                            st.executeUpdate(myQuery);
                        } catch (SQLException sqlEx) {
                            JOptionPane.showMessageDialog(null, "Niestety nie ustawiono godzin pracy", "Błąd", 0);
                        }
                    }
                }

                cLayout.show(pContainer, "pEntry");
                tNumber.setText("");
                tFirstName.setText("");
                tLastName.setText("");
                tTelPrefix.setText("");
                tTel.setText("");
            }
        });
    }
}
