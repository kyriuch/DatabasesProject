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
import java.sql.Connection;
import java.sql.ResultSet;
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
public class DoctorDetailsPanel extends JPanel {
    
    private final Connection connection;
    private final JLabel lExactNumber;
    private final JLabel lExactFirstName;
    private final JLabel lExactLastName;
    private final JTextField tTel;
    private final JTextField tTelPrefix;
    private final JComboBox[] cStart = new JComboBox[7];
    private final JComboBox[] cStop = new JComboBox[7];

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
    
    private void fillData() {
        Statement st;
        
        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }
        
        String myQuery = "SELECT baza.Lekarz.imie, baza.Lekarz.nazwisko, baza.lekarz.telefon "
                + "FROM baza.Lekarz WHERE numer = " + ProjektBazyDanych.currentDoctorId;
        
        ResultSet rs;
        String[] scores = new String[2];
        String[] tel;
        
        try {
            rs = st.executeQuery(myQuery);
            rs.next();
            scores[0] = rs.getString(1);
            scores[1] = rs.getString(2);
            tel = rs.getString(3).split(" ");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się znaleźć lekarza", "Informacja", 1);
            return;
        }
        
        lExactNumber.setText(Integer.toString(ProjektBazyDanych.currentDoctorId));
        lExactFirstName.setText(scores[0]);
        lExactLastName.setText(scores[1]);
        tTelPrefix.setText(tel[0]);
        tTel.setText(tel[1] + " " + tel[2] + " " + tel[3]);
        
        myQuery = "SELECT baza.Dostepnosc.dzien_tygodnia, baza.Dostepnosc.godzina_start, baza.Dostepnosc.godzina_stop "
                + "FROM baza.Dostepnosc WHERE baza.Dostepnosc.nr_lekarz = " + ProjektBazyDanych.currentDoctorId;
        
        try {
            rs = st.executeQuery(myQuery);
            
            if (rs.isBeforeFirst()) {
                String[] godzinaStart, godzinaStop;
                int dzien;

                while (rs.next()) {
                    dzien = rs.getInt(1);
                    godzinaStart = rs.getString(2).split(":");
                    godzinaStop = rs.getString(3).split(":");
                    
                    cStart[dzien - 1].setSelectedItem(godzinaStart[0] + ":" + godzinaStart[1]);
                    cStop[dzien - 1].setSelectedItem(godzinaStop[0] + ":" + godzinaStop[1]);
                }
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać godzin", "Informacja", 1);
        }
    }

    public DoctorDetailsPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);
        
        connection = con;

        // Buttons
        JButton bUpdate = new JButton("Aktualizuj");
        Font buttonFont = new Font(bUpdate.getFont().getName(), Font.PLAIN, 20);
        bUpdate.setBounds(230, 240, 140, 40);
        bUpdate.setFont(buttonFont);
        add(bUpdate);
        
        JButton bDel = new JButton("Usuń");
        bDel.setBounds(130, 240, 80, 40);
        bDel.setFont(buttonFont);
        add(bDel);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(30, 240, 80, 40);
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
        
        lExactNumber = new JLabel();
        lExactNumber.setBounds(140, 20, 240, 20);
        lExactNumber.setFont(labelFont);
        add(lExactNumber);
        
        lExactFirstName = new JLabel();
        lExactFirstName.setBounds(140, 70, 240, 20);
        lExactFirstName.setFont(labelFont);
        add(lExactFirstName);

        lExactLastName = new JLabel();
        lExactLastName.setBounds(140, 120, 240, 20);
        lExactLastName.setFont(labelFont);
        add(lExactLastName);

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
        tTelPrefix = new JTextField();
        Font textFont = new Font(tTelPrefix.getFont().getName(), Font.PLAIN, 20);
        tTelPrefix.setBounds(140, 170, 50, 40);
        tTelPrefix.setBorder(border);
        tTelPrefix.setFont(textFont);
        add(tTelPrefix);

        tTel = new JTextField();
        tTel.setBounds(220, 170, 150, 40);
        tTel.setBorder(border);
        tTel.setFont(textFont);
        add(tTel);

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
                cLayout.show(pContainer, "pDoctors");
            }

        });

        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String telPrefix, tel;

                telPrefix = tTelPrefix.getText().trim().replaceAll(" +", " ");
                tel = tTel.getText().trim().replaceAll(" +", " ");
                
                if(tel.length() == 9 && telPrefix.contains("+") && !tel.contains(" ")) {
                    tel = tel.substring(0, 3) + " " + tel.substring(3, 6) + " " + tel.substring(6, 9);
                } else if(tel.length() == 7 && !telPrefix.contains("+") && !tel.contains(" ")) {
                    tel = tel.substring(0, 3) + " " + tel.substring(3, 5) + " " + tel.substring(5, 7);
                } else if(!((tel.length() == 9 && !telPrefix.contains("+") && tel.contains(" "))
                        || (tel.length() == 11 && telPrefix.contains("+") && tel.contains(" ")))) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij błędne pola", "Błąd", 0);
                    return;
                }

                Statement st;

                if (telPrefix.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij puste pole", "Błąd", 0);
                    return;
                } else {
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "UPDATE baza.Lekarz SET baza.Lekarz.telefon = \'" + telPrefix + " " + tel
                            + "\' WHERE baza.Lekarz.numer = " + ProjektBazyDanych.currentDoctorId;

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się zaktualizować lekarza", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Zaktualizowano lekarza", "Informacja", 1);
                }

                String[] start = new String[7];
                String[] stop = new String[7];

                for (int i = 0; i < 7; i++) {
                    start[i] = (String) cStart[i].getSelectedItem();
                    stop[i] = (String) cStop[i].getSelectedItem();

                    if (!start[i].equals("WOLNE") && !stop[i].equals("WOLNE")) {
                        String deleteQuery = "DELETE FROM baza.Dostepnosc WHERE baza.Dostepnosc.dzien_tygodnia = " + (i + 1) +
                                " AND baza.Dostepnosc.nr_lekarz = " + ProjektBazyDanych.currentDoctorId;
                        String myQuery = "INSERT INTO baza.Dostepnosc(dzien_tygodnia, godzina_start, godzina_stop, nr_lekarz) "
                                + "VALUES(" + (i + 1) + ", \'" + start[i] + "\', \'" + stop[i]
                                + "\', " + ProjektBazyDanych.currentDoctorId + ")";
                        try {
                            st = con.createStatement();
                            st.executeUpdate(deleteQuery);
                            st.executeUpdate(myQuery);
                        } catch (SQLException sqlEx) {
                            JOptionPane.showMessageDialog(null, "Niestety nie ustawiono godzin pracy", "Błąd", 0);
                        }
                    }
                }

                cLayout.show(pContainer, "pDoctors");
            }
        });
        
        bDel.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Object[] options = {"Tak", "Nie"};
                int n = JOptionPane.showOptionDialog(null, "Czy na pewno chcesz usunąć tego lekarza", "Pytanie", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                
                if(n == 1) {
                    return;
                }
                
                Statement st;
                
                try {
                    st = con.createStatement();
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                    return;
                }
                
                String myQuery = "DELETE FROM baza.Lekarz where numer = " + ProjektBazyDanych.currentDoctorId;
                String cascadeQuery;
                
                try {
                    st.execute(myQuery);
                    cLayout.show(pContainer, "pDoctors");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Nie udało się usunąć lekarza", "Informacja", 1);
                }
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                fillData();
            }
        });
    }
}
