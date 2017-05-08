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
import java.sql.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class PatientPanel extends JPanel {

    public PatientPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(320, 265, 250, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(50, 265, 250, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        // Labels
        JLabel lNumber = new JLabel("Numer ubezpieczenia: ");
        Font labelFont = new Font(lNumber.getFont().getName(), Font.PLAIN, 20);
        lNumber.setBounds(20, 20, 240, 20);
        lNumber.setFont(labelFont);
        add(lNumber);

        JLabel lFirstName = new JLabel("Imię: ");
        lFirstName.setBounds(20, 70, 240, 20);
        lFirstName.setFont(labelFont);
        add(lFirstName);

        JLabel lLastName = new JLabel("Nazwisko: ");
        lLastName.setBounds(20, 120, 240, 20);
        lLastName.setFont(labelFont);
        add(lLastName);

        JLabel lAddress = new JLabel("Adres: ");
        lAddress.setBounds(20, 170, 240, 20);
        lAddress.setFont(labelFont);
        add(lAddress);

        JLabel lTel = new JLabel("Telefon: ");
        lTel.setBounds(20, 220, 240, 20);
        lTel.setFont(labelFont);
        add(lTel);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextField tNumber = new JTextField();
        Font textFont = new Font(tNumber.getFont().getName(), Font.PLAIN, 20);
        tNumber.setBounds(240, 15, 330, 40);
        tNumber.setBorder(border);
        tNumber.setFont(textFont);
        add(tNumber);

        final JTextField tFirstName = new JTextField();
        tFirstName.setBounds(240, 65, 330, 40);
        tFirstName.setBorder(border);
        tFirstName.setFont(textFont);
        add(tFirstName);

        final JTextField tLastName = new JTextField();
        tLastName.setBounds(240, 115, 330, 40);
        tLastName.setBorder(border);
        tLastName.setFont(textFont);
        add(tLastName);

        final JTextField tAddress = new JTextField();
        tAddress.setBounds(240, 165, 330, 40);
        tAddress.setBorder(border);
        tAddress.setFont(textFont);
        add(tAddress);

        final JTextField tTelPrefix = new JTextField();
        tTelPrefix.setBounds(240, 215, 50, 40);
        tTelPrefix.setBorder(border);
        tTelPrefix.setFont(textFont);
        add(tTelPrefix);

        final JTextField tTel = new JTextField();
        tTel.setBounds(320, 215, 250, 40);
        tTel.setBorder(border);
        tTel.setFont(textFont);
        add(tTel);

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int number;
                String firstName, lastName, address, telPrefix, tel;

                try {
                    number = Integer.parseInt(tNumber.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy numer ubezpieczenia", "Błąd", 0);
                    return;
                }

                firstName = tFirstName.getText().trim().replaceAll(" +", " ");
                lastName = tLastName.getText().trim().replaceAll(" +", " ");
                address = tAddress.getText().trim().replaceAll(" +", " ");
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

                if (firstName.trim().isEmpty() || lastName.trim().isEmpty()
                        || address.trim().isEmpty() || telPrefix.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij puste pola", "Błąd", 0);
                } else {
                    Statement st;
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "INSERT INTO baza.Pacjent(nr_ubezp, nazwisko, imie, adres, telefon) "
                            + "VALUES(" + number + ", \'" + lastName + "\', \'" + firstName
                            + "\', \'" + address + "\', \'" + telPrefix + " " + tel + "\')";

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się dodać pacjenta", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Dodano pacjenta", "Informacja", 1);
                    cLayout.show(pContainer, "pEntry");

                    tNumber.setText("");
                    tFirstName.setText("");
                    tLastName.setText("");
                    tAddress.setText("");
                    tTelPrefix.setText("");
                    tTel.setText("");
                }
            }
        });

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                tNumber.setText("");
                tFirstName.setText("");
                tLastName.setText("");
                tAddress.setText("");
                tTelPrefix.setText("");
                tTel.setText("");
            }
        });
    }
}
