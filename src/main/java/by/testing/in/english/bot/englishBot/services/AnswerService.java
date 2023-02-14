package by.testing.in.english.bot.englishBot.services;


import by.testing.in.english.bot.englishBot.model.Answer;
import by.testing.in.english.bot.englishBot.repositories.AnswerRepository;
import by.testing.in.english.bot.englishBot.services.api.IAnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService implements IAnswerService {

    private final AnswerRepository repository;

    public AnswerService(AnswerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Answer create(Answer answer) {

        if (repository.findByAnswer(answer.getAnswer()).isPresent()){
            return repository.findByAnswer(answer.getAnswer()).get();
        }

        return repository.save(answer);
    }
}
