package interfaces;

import javax.swing.*;

import entité.Client;

import java.awt.*;
import java.awt.event.ActionEvent;

public class modifierpassword extends JFrame {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private int clientId;  

    public modifierpassword(int clientId) {
        this.clientId = clientId;
        initUI();
    }

    private void initUI() {
        setTitle("Changement de mot de passe");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Couleurs du thème
        Color backgroundColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color greenColor = new Color(0, 200, 0);
        Color darkGreenColor = new Color(0, 150, 0);
        Color redColor = new Color(200, 0, 0);
        Color darkRedColor = new Color(150, 0, 0);
        Color fieldBgColor = new Color(50, 50, 50);
        Color borderColor = new Color(80, 80, 80);
        Color successColor = new Color(22, 163, 74); // Vert

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        // Titre
        JLabel titleLabel = new JLabel("Changer votre mot de passe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(greenColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Champ mot de passe actuel
        formPanel.add(createFieldPanel("Mot de passe actuel :", 
            createPasswordDisplay(), textColor, fieldBgColor, borderColor));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ nouveau mot de passe
        formPanel.add(createFieldPanel("Nouveau mot de passe :", 
            newPasswordField = new JPasswordField(), textColor, fieldBgColor, borderColor));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ confirmation
        formPanel.add(createFieldPanel("Confirmer mot de passe :", 
            confirmPasswordField = new JPasswordField(), textColor, fieldBgColor, borderColor));
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel boutons - Solution optimisée
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        
        // Bouton Annuler
        JButton cancelButton = createStyledButton("Annuler", redColor, darkRedColor, new Color(30, 30, 30));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        // Bouton Valider
        JButton submitButton = createStyledButton("Valider", successColor,successColor, new Color(30, 30, 30));
        submitButton.addActionListener(this::handlePasswordChange);
        buttonPanel.add(submitButton);

        formPanel.add(buttonPanel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JLabel createPasswordDisplay() {
        JLabel hiddenPasswordLabel = new JLabel("************");
        hiddenPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hiddenPasswordLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        hiddenPasswordLabel.setBackground(new Color(50, 50, 50));
        hiddenPasswordLabel.setForeground(Color.WHITE);
        hiddenPasswordLabel.setOpaque(true);
        hiddenPasswordLabel.setPreferredSize(new Dimension(250, 40));
        return hiddenPasswordLabel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(20, 20, 20), 2),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol amélioré
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(40, 40, 40), 2),
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(20, 20, 20), 2),
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)
                ));
            }
        });
        
        return button;
    }

    private JPanel createFieldPanel(String labelText, JComponent field, 
                                  Color textColor, Color fieldBgColor, Color borderColor) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(30, 30, 30));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(textColor);

        if (field instanceof JPasswordField) {
            JPasswordField pf = (JPasswordField)field;
            pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            pf.setBackground(fieldBgColor);
            pf.setForeground(textColor);
            pf.setCaretColor(textColor);
            pf.setPreferredSize(new Dimension(250, 40));
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void handlePasswordChange(ActionEvent e) {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty()) {
            showError("Veuillez entrer un nouveau mot de passe");
            newPasswordField.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }

        Controller.ClientController clientController = new Controller.ClientController();
        
        if (clientController.changePassword(clientId, newPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Mot de passe changé avec succès", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
         // Récupérer les informations du client
            Client client = clientController.getClientById(clientId);
            if (client != null) {
                // Ouvrir le dashboard client avec les nouvelles informations
                SwingUtilities.invokeLater(() -> {
                    ClientDashboard dashboard = new ClientDashboard(
                        clientId,
                        client.getNom(),
                        client.getPrenom(),
                        client.getLogin(),
                        client.getDateNaissance(),
                        client.getAdresse(),
                        client.getTelephone()
                    );
                    dashboard.setVisible(true);
                });
            }
            dispose();
         
        } else {
            showError("Erreur lors du changement de mot de passe");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }
}