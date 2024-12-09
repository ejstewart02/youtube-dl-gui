import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startHomeScreen);
    }

    public static void startHomeScreen() {
        // Set Look and Feel
        try {
            FlatLaf laf = new FlatDarkLaf();
            laf.setExtraDefaults( Collections.singletonMap( "@accentColor", "#FF0033" ) );
            FlatLaf.setup( laf );
            UIManager.setLookAndFeel(laf);
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        // Create HomeScreen Instance
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.setVisible(true);
    }
}
