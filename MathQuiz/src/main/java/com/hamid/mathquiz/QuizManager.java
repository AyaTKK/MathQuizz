package com.hamid.mathquiz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.List;
import java.util.Random;

public class QuizManager {
    private final MathQuiz plugin;
    private final Random random = new Random();
    private BukkitTask task;
    private boolean active = false;
    private int currentAnswer = 0;

    public QuizManager(MathQuiz plugin) {
        this.plugin = plugin;
    }

    public void startScheduler() {
        long intervalTicks = plugin.getConfig().getLong("interval-minutes", 30) * 60 * 20L;
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::generateNewQuestion, intervalTicks, intervalTicks);
    }

    public void stopScheduler() {
        if (task != null && !task.isCancelled()) task.cancel();
        active = false;
    }

    public synchronized void generateNewQuestion() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        int op = random.nextInt(3);
        int num1, num2;
        String questionStr;

        switch (op) {
            case 0:
                num1 = random.nextInt(90) + 10;
                num2 = random.nextInt(90) + 10;
                currentAnswer = num1 + num2;
                questionStr = num1 + " + " + num2;
                break;
            case 1:
                num1 = random.nextInt(100) + 20;
                num2 = random.nextInt(num1 - 10) + 5;
                currentAnswer = num1 - num2;
                questionStr = num1 + " - " + num2;
                break;
            default:
                num1 = random.nextInt(12) + 2;
                num2 = random.nextInt(12) + 2;
                currentAnswer = num1 * num2;
                questionStr = num1 + " × " + num2;
                break;
        }

        active = true;
        String prefix = color(plugin.getConfig().getString("messages.prefix", ""));
        String msg = color(plugin.getConfig().getString("messages.question", "").replace("%question%", questionStr));
        Bukkit.broadcastMessage(prefix + msg);
    }

    public synchronized boolean checkAnswer(Player player, String message) {
        if (!active) return false;
        try {
            int answer = Integer.parseInt(message.trim());
            if (answer == currentAnswer) {
                active = false;
                giveRewards(player);
                return true;
            }
        } catch (NumberFormatException ignored) {}
        return false;
    }

    private void giveRewards(Player player) {
        String prefix = color(plugin.getConfig().getString("messages.prefix", ""));
        String winMsg = color(plugin.getConfig().getString("messages.correct", "")
                .replace("%player%", player.getName())
                .replace("%answer%", String.valueOf(currentAnswer)));
        Bukkit.broadcastMessage(prefix + winMsg);

        List<String> rewards = plugin.getConfig().getStringList("rewards");
        for (String cmd : rewards) {
            String formattedCmd = cmd.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd);
        }
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
