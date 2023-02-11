package by.testing.in.english.bot.englishBot.services.api;

import by.testing.in.english.bot.englishBot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface IUserService {

    void create(Message message);
    User read(Long chatId);
    List<User> read();
    void deactivate(Long chatId);
}
