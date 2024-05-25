package org.jblackjack.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupDialog extends JDialog {

    private JTextField player1NameField;
    private JTextField player2NameField;
    private JSpinner numberOfPlayersSpinner;
    private JSpinner betAmountSpinner;
    private List<JTextField> nameFields;
    private JLabel player2NameLabel;

    public SetupDialog(Frame parent) {

        super(parent, "Setup", true);
        initComponents();
        pack();
        setResizable(false);
        setSize(500,250);
        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }


    private void initComponents() {
        nameFields = new ArrayList<>();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel numberOfPlayersLabel = new JLabel("Number of players:");
        JLabel betAmountLabel = new JLabel("Bet amount:");
        JLabel player1NameLabel = new JLabel("Player 1 name:");
        player2NameLabel = new JLabel("Player 2 name:");

        numberOfPlayersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
        betAmountSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 1000, 10));
        player1NameField = new JTextField(15);
        player2NameField = new JTextField(15);


        nameFields.add(player1NameField);
        nameFields.add(player2NameField);

        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // Disable the button initially

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }

            private void checkFields() {
                int numPlayers = getNumPlayers();
                for (int i = 0; i < numPlayers; i++) {
                    JTextField field = nameFields.get(i);
                    if (field.getText().trim().isEmpty()) {
                        okButton.setEnabled(false);
                        return;
                    }
                }
                okButton.setEnabled(true);
            }

        };

        player1NameField.getDocument().addDocumentListener(documentListener);
        player2NameField.getDocument().addDocumentListener(documentListener);




        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(numberOfPlayersLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(numberOfPlayersSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(betAmountLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(betAmountSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(player1NameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(player1NameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(player2NameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(player2NameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(okButton, gbc);

        numberOfPlayersSpinner.addChangeListener(e -> updatePlayer2Visibility());

        okButton.addActionListener(e -> handleOkButtonAction());

        updatePlayer2Visibility();

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void updatePlayer2Visibility() {
        int numPlayers = getNumPlayers();
        boolean isPlayer2Visible = numPlayers > 1;
        player2NameLabel.setVisible(isPlayer2Visible);
        player2NameField.setVisible(isPlayer2Visible);
    }

    private void handleOkButtonAction() {
        int numPlayers = getNumPlayers();
        int betAmount = getBetAmount();
        List<String> playerNames = getPlayerNames();
        dispose();
    }

    public int getNumPlayers() {
        return (int) numberOfPlayersSpinner.getValue();
    }

    public int getBetAmount() {
        return (int) betAmountSpinner.getValue();
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < getNumPlayers(); i++) {
            if (nameFields.get(i).getText().length()>10) names.add(nameFields.get(i).getText().substring(0, 10));
            names.add(nameFields.get(i).getText());
        }
        return names;
    }


}
