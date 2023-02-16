package by.testing.in.english.bot.englishBot.services.api;

import by.testing.in.english.bot.englishBot.model.Level;
import by.testing.in.english.bot.englishBot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface IUserService {

    void create(Message message);
    User get(Long chatId);
    List<User> get();
    User updateLevel(Long chatId, Level level);
    void deactivate(Long chatId);
}
