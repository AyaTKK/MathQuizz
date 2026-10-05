package com.hamid.mathquiz;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private final MathQuiz plugin;
    private final QuizManager quizManager;

    public ChatListener(MathQuiz plugin, QuizManager quizManager) {
        this.plugin = plugin;
        this.quizManager = quizManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.getScheduler().runTask(plugin, () -> {
            quizManager.checkAnswer(event.getPlayer(), rawMessage);
        });
    }
}
