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
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class InteractionPanel extends JPanel {

    private final ArrayList<String> availableCures = new ArrayList<>();
    private final ArrayList<Integer> cureCodes = new ArrayList<>();
    private final Connection connection;
    private final JComboBox[] cCures = new JComboBox[2];

    private void fillCures() {
        java.util.Date date = null;

        availableCures.clear();
        cureCodes.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }
        
        String myQuery = "SELECT baza.Lekarstwo.kod, baza.Lekarstwo.nazwa from baza.Lekarstwo "
                + " ORDER BY baza.Lekarstwo.nazwa ASC";

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Żaden lek nie został wprowadzony", "Informacja", 1);
            } else {
                while (rs.next()) {
                    cureCodes.add(rs.getInt(1));
                    availableCures.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać leków", "Informacja", 1);
            return;
        }

        if (!availableCures.isEmpty()) {
            String[] cures = availableCures.toArray(new String[availableCures.size()]);

            cCures[0].removeAllItems();
            cCures[1].removeAllItems();

            for (String cure : cures) {
                cCures[0].addItem(cure);
                cCures[1].addItem(cure);
            }
        }
    }

    public InteractionPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(550, 230, 150, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(350, 230, 150, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bCheck = new JButton("Sprawdź leki");
        bCheck.setBounds(750, 230, 150, 40);
        bCheck.setFont(buttonFont);
        add(bCheck);

        // Labels
        JLabel lCureBeingTaken = new JLabel("Lek brany: ");
        Font labelFont = new Font(lCureBeingTaken.getFont().getName(), Font.PLAIN, 20);
        lCureBeingTaken.setBounds(20, 20, 250, 20);
        lCureBeingTaken.setFont(labelFont);
        add(lCureBeingTaken);

        JLabel lCurePlanned = new JLabel("Lek planowany: ");
        lCurePlanned.setBounds(20, 70, 250, 20);
        lCurePlanned.setFont(labelFont);
        add(lCurePlanned);

        JLabel lInteraction = new JLabel("Interakcja: ");
        lInteraction.setBounds(20, 120, 250, 20);
        lInteraction.setFont(labelFont);
        add(lInteraction);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextArea tInteraction = new JTextArea();
        Font textFont = new Font(tInteraction.getFont().getName(), Font.PLAIN, 20);
        tInteraction.setBounds(200, 115, 700, 100);
        tInteraction.setFont(textFont);
        tInteraction.setBorder(border);
        tInteraction.setLineWrap(true);
        add(tInteraction);

        // Combo Boxes
        cCures[0] = new JComboBox();
        Font comboFont = new Font(cCures[0].getFont().getName(), Font.PLAIN, 20);
        cCures[0].setBounds(200, 15, 190, 40);
        cCures[0].setFont(comboFont);
        add(cCures[0]);

        cCures[1] = new JComboBox();
        cCures[1].setBounds(200, 65, 190, 40);
        cCures[1].setFont(comboFont);
        add(cCures[1]);

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (cCures[0].getItemCount() == 0 || cCures[1].getItemCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                    return;
                }

                int[] codes = new int[2];
                String interaction;
                String[] cures = new String[2];

                interaction = tInteraction.getText().trim().replaceAll(" +", " ");
                cures[0] = (String) cCures[0].getSelectedItem();
                cures[1] = (String) cCures[1].getSelectedItem();

                if (interaction.trim().isEmpty() || cures[0].trim().isEmpty() || cures[1].trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij wszystkie pola", "Błąd", 0);
                } else {
                    codes[0] = cureCodes.get(availableCures.indexOf(cures[0]));
                    codes[1] = cureCodes.get(availableCures.indexOf(cures[1]));

                    if (codes[0] == codes[1]) {
                        JOptionPane.showMessageDialog(null, "Należy podać różne leki", "Błąd", 0);
                        return;
                    }

                    Statement st;
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "INSERT INTO baza.Interakcje(kod_lek1, kod_lek2, interakcja) "
                            + "VALUES(" + codes[0] + ", " + codes[1] + ", \'" + interaction + "\')";
                    
                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się dodać interakcji", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Ustalono interakcję", "Informacja", 1);
                    cLayout.show(pContainer, "pEntry");
                    cCures[0].removeAllItems();
                    cCures[1].removeAllItems();
                    tInteraction.setText("");
                }
            }
        });

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                cCures[0].removeAllItems();
                cCures[1].removeAllItems();
                tInteraction.setText("");
            }
        });

        bCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                fillCures();
            }
        });
    }

}
