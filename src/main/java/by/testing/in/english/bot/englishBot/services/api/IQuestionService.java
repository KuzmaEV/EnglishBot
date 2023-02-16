package by.testing.in.english.bot.englishBot.services.api;

import by.testing.in.english.bot.englishBot.model.Level;
import by.testing.in.english.bot.englishBot.model.Question;

public interface IQuestionService {

    Question create(String objectMapper);

    Question getRandom(Level level);

    Question get(long id);



}
