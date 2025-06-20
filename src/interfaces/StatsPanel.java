package interfaces;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import Controller.*;
import entité.*;

public class StatsPanel extends JPanel {
    private ClientController clientController;
    private ProduitController produitController;
    private CommandeController commandeController;
    private DecimalFormat df = new DecimalFormat("#,###");
    
    // Palette de couleurs moderne mais simple
    private final Color[] COLORS = {
        new Color(70, 130, 180),   // Bleu acier
        new Color(60, 179, 113),   // Vert moyen
        new Color(255, 165, 0),    // Orange
        new Color(147, 112, 219),  // Violet moyen
        new Color(220, 20, 60)      // Rouge
    };

    public StatsPanel(GerantDashboard parent) {
        this.clientController = new ClientController();
        this.produitController = new ProduitController();
        this.commandeController = new CommandeController();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        // Panel pour les cartes statistiques
        JPanel cardsPanel = createCardsPanel();
        
        // Panel pour les graphiques
        JPanel chartsPanel = createChartsPanel();
        
        add(cardsPanel, BorderLayout.NORTH);
        add(chartsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        
        panel.add(createStatCard("Clients", 
            df.format(clientController.getAllClients().size()), 
            getClientTrend(), 
            COLORS[0]));
            
        panel.add(createStatCard("Produits", 
            df.format(produitController.getTousProduits().size()), 
            getTopCategory(), 
            COLORS[1]));
            
        panel.add(createStatCard("Commandes", 
            df.format(commandeController.getAllCommandes().size()), 
            getOrderTrend(), 
            COLORS[2]));
            
        panel.add(createStatCard("Revenu", 
            df.format(calculateRevenue()) + " DT", 
            getRevenueTrend(), 
            COLORS[3]));
        
        return panel;
    }
    
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        panel.add(createBarChart("Clients fidèles (par commandes)", getClientOrderData()));
        panel.add(createPieChart("Types de commandes", getOrderTypeData()));
        
        return panel;
    }
    
    // Méthodes pour les données
    private String getClientTrend() {
        int count = clientController.getAllClients().size();
        return  "↑ " + count + "client inscrit";
    }
    
    private String getTopCategory() {
        return produitController.getTousProduits().stream()
            .collect(Collectors.groupingBy(Produit::getCategorie, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> "Top: " + e.getKey())
            .orElse("N/A");
    }
    
    private String getOrderTrend() {
        List<Commande> allCommandes = commandeController.getAllCommandes();
        
        // Compter uniquement les commandes avec statut "livrée" ou "prête"
        int countLivree = 0;
        int countPrete = 0;
        
        for (Commande cmd : allCommandes) {
            String statut = cmd.getStatut().toLowerCase();
            if ("livrée".equalsIgnoreCase(statut)) {
                countLivree++;
            } else if ("prête".equalsIgnoreCase(statut)) {
                countPrete++;
            }
        }
        
        int total = countLivree + countPrete;
        return (total) +"→ livrée ";
    }
    
    private double calculateRevenue() {
        return commandeController.getAllCommandes().stream()
            .flatMap(c -> c.getProduits().stream())
            .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
            .sum();
    }
    
    private String getRevenueTrend() {
        double revenue = calculateRevenue();
        return revenue > 10000 ? "↑ Excellente" : revenue > 5000 ? "↑ Bonne" : "→ Normale";
    }
    
    private Map<String, Integer> getClientOrderData() {
        return commandeController.getAllCommandes().stream()
            .collect(Collectors.groupingBy(
                c -> clientController.getClientById(c.getClientId()).getNom().split(" ")[0],
                Collectors.summingInt(c -> 1)))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
    }
    
    private Map<String, Integer> getOrderTypeData() {
        return commandeController.getAllCommandes().stream()
            .collect(Collectors.groupingBy(
                Commande::getTypeCommande,
                Collectors.summingInt(c -> 1)));
    }
    
    // Composants UI
    private JPanel createStatCard(String title, String value, String info, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Fond blanc avec bordure arrondie
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Barre colorée en haut
                g2d.setColor(color);
                g2d.fillRect(0, 0, getWidth(), 5);
            }
        };
        
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setPreferredSize(new Dimension(200, 100));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(80, 80, 80));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(50, 50, 50));
        
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(color.darker());
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(infoLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createBarChart(String title, Map<String, Integer> data) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dimensions
                int width = getWidth();
                int height = getHeight();
                int padding = 20;
                int chartWidth = width - 2*padding;
                int chartHeight = height - 2*padding;
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(padding, padding, chartWidth, chartHeight, 15, 15);
                
                // Titre
                g2d.setColor(new Color(70, 70, 70));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString(title, padding + 10, padding + 20);
                
                if (data.isEmpty()) {
                    g2d.drawString("Aucune donnée", width/2 - 40, height/2);
                    return;
                }
                
                // Axes
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawLine(padding + 30, padding + 40, padding + 30, height - padding);
                g2d.drawLine(padding + 30, height - padding, width - padding, height - padding);
                
                // Barres
                int barWidth = Math.min(40, chartWidth / (data.size() * 2));
                int maxValue = Collections.max(data.values());
                int yUnit = (chartHeight - 60) / (maxValue == 0 ? 1 : maxValue);
                
                int i = 0;
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int x = padding + 50 + i * (barWidth + 30);
                    int barHeight = entry.getValue() * yUnit;
                    
                    // Barre
                    g2d.setColor(COLORS[i % COLORS.length]);
                    g2d.fillRect(x, height - padding - barHeight, barWidth, barHeight);
                    
                    // Étiquette
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    
                    // Nom (tronqué si trop long)
                    String name = entry.getKey().length() > 8 ? 
                        entry.getKey().substring(0, 5) + ".." : entry.getKey();
                    g2d.drawString(name, x, height - padding + 15);
                    
                    // Valeur
                    g2d.drawString(entry.getValue().toString(), x, height - padding - barHeight - 5);
                    
                    i++;
                }
            }
        };
    }
    
    private JPanel createPieChart(String title, Map<String, Integer> data) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dimensions
                int width = getWidth();
                int height = getHeight();
                int padding = 20;
                int diameter = Math.min(width, height) - 2*padding;
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(padding, padding, width - 2*padding, height - 2*padding, 15, 15);
                
                // Titre
                g2d.setColor(new Color(70, 70, 70));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString(title, padding + 10, padding + 20);
                
                if (data.isEmpty()) {
                    g2d.drawString("Aucune donnée", width/2 - 40, height/2);
                    return;
                }
                
                // Calcul des angles
                int total = data.values().stream().mapToInt(Integer::intValue).sum();
                if (total == 0) return;
                
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = diameter / 2 - 30;
                int startAngle = 0;
                
                // Dessin des secteurs
                int i = 0;
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int angle = (int) Math.round(360.0 * entry.getValue() / total);
                    
                    // Secteur
                    g2d.setColor(COLORS[i % COLORS.length]);
                    g2d.fillArc(centerX - radius, centerY - radius, 
                                2*radius, 2*radius, startAngle, angle);
                    
                    // Légende
                    int legendX = (i % 2 == 0) ? padding + 20 : centerX + 20;
                    int legendY = padding + 50 + (i/2) * 20;
                    
                    g2d.fillRect(legendX, legendY - 10, 10, 10);
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    
                    String label = String.format("%s (%.1f%%)", 
                        entry.getKey(), 
                        100.0 * entry.getValue() / total);
                    g2d.drawString(label, legendX + 15, legendY);
                    
                    startAngle += angle;
                    i++;
                }
            }
        };
    }
}