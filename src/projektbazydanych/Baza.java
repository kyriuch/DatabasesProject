/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Tomek
 */
public class Baza {
    
    private final Connection con;
    
    public Baza(Connection con) {
        this.con = con;
    }
    
    public void fillDatabase() {
        Path[] path = {Paths.get("lekarstwa.txt"), Paths.get("choroby.txt"), Paths.get("lekarze.txt"), Paths.get("pacjenci.txt")};
        List<String> cures;
        List<String> diseases;
        List<String> doctors;
        List<String> patients;
        try {
            cures = new ArrayList<>(Files.readAllLines(path[0], StandardCharsets.UTF_8));
            diseases = new ArrayList<>(Files.readAllLines(path[1], StandardCharsets.UTF_8));
            doctors = new ArrayList<>(Files.readAllLines(path[2], StandardCharsets.UTF_8));
            patients = new ArrayList<>(Files.readAllLines(path[3], StandardCharsets.UTF_8));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Błąd odczytu pliku", "Błąd", 0);
            return;
        }
        
        Statement st;
        
        try {
                st = con.createStatement();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }
        
        String query;
        
        for (String cur : cures) 
        {  
            query = "INSERT " + cur.substring(cur.indexOf("INTO"), cur.length());

            try {
                st.executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
                
        }
        
        for (String dis : diseases) 
        {  
            query = "INSERT " + dis.substring(dis.indexOf("INTO"), dis.length());
            try {
                st.executeUpdate(query);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Nie udało się dodać choroby", "Informacja", 1);
                return;
            }
        }
        
        for (String doc : doctors) 
        {  
            query = "INSERT " + doc.substring(doc.indexOf("INTO"), doc.length());
            try {
                st.executeUpdate(query);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Nie udało się dodać lekarza", "Informacja", 1);
                return;
            }
        }
        
        for (String pat : patients) 
        {  
            query = "INSERT " + pat.substring(pat.indexOf("INTO"), pat.length());
            try {
                st.executeUpdate(query);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Nie udało się dodać pacjenta", "Informacja", 1);
                return;
            }
        }
    }
}
