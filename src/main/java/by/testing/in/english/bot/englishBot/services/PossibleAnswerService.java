package by.testing.in.english.bot.englishBot.services;


import by.testing.in.english.bot.englishBot.model.Answer;
import by.testing.in.english.bot.englishBot.model.PossibleAnswer;
import by.testing.in.english.bot.englishBot.repositories.PossibleAnswerRepository;
import by.testing.in.english.bot.englishBot.services.api.IAnswerService;
import by.testing.in.english.bot.englishBot.services.api.IPossibleAnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PossibleAnswerService implements IPossibleAnswerService {

    private final IAnswerService answerService;
    private final PossibleAnswerRepository repository;


    public PossibleAnswerService(IAnswerService answerService, PossibleAnswerRepository repository) {
        this.answerService = answerService;
        this.repository = repository;
    }


    @Override
    @Transactional
    public PossibleAnswer create(PossibleAnswer possibleAnswer) {

        Answer answer = answerService.create(possibleAnswer.getAnswer());
        possibleAnswer.setAnswer(answer);

        return repository.save(possibleAnswer);
    }
}
