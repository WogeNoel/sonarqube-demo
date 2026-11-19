package de.demo.shop;

import java.util.List;

/**
 * LEGACY-CODE: Berichtserstellung mit Wartbarkeitsproblemen (Code Smells).
 * Kopierter Code, tiefe Verschachtelung, System.out, auskommentierter Code.
 */
public class ReportGenerator {

    public String dailyReport(List<Order> orders) {
        StringBuilder sb = new StringBuilder();
        double sum = 0;
        int count = 0;
        sb.append("=== Tagesbericht ===\n");
        for (Order o : orders) {
            if (o != null) {
                if (o.getAmount() > 0) {
                    if (o.getStatus() != null) {
                        if (o.getStatus().equals("PAID")) {
                            sum = sum + o.getAmount();
                            count = count + 1;
                            sb.append(o.getId()).append(";").append(o.getAmount()).append("\n");
                        } else if (o.getStatus().equals("OPEN")) {
                            sb.append(o.getId()).append(";offen\n");
                        }
                    }
                }
            }
        }
        sb.append("Summe: ").append(sum).append("\n");
        sb.append("Anzahl: ").append(count).append("\n");
        System.out.println(sb);
        return sb.toString();
    }

    public String weeklyReport(List<Order> orders) {
        StringBuilder sb = new StringBuilder();
        double sum = 0;
        int count = 0;
        sb.append("=== Wochenbericht ===\n");
        for (Order o : orders) {
            if (o != null) {
                if (o.getAmount() > 0) {
                    if (o.getStatus() != null) {
                        if (o.getStatus().equals("PAID")) {
                            sum = sum + o.getAmount();
                            count = count + 1;
                            sb.append(o.getId()).append(";").append(o.getAmount()).append("\n");
                        } else if (o.getStatus().equals("OPEN")) {
                            sb.append(o.getId()).append(";offen\n");
                        }
                    }
                }
            }
        }
        sb.append("Summe: ").append(sum).append("\n");
        sb.append("Anzahl: ").append(count).append("\n");
        // sb.append("Durchschnitt: ").append(sum / count).append("\n");
        System.out.println(sb);
        return sb.toString();
    }
}
