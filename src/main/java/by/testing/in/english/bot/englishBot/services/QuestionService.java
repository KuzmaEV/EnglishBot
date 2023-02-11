package by.testing.in.english.bot.englishBot.services;

import by.testing.in.english.bot.englishBot.model.Question;
import by.testing.in.english.bot.englishBot.repositories.QuestionRepository;
import by.testing.in.english.bot.englishBot.services.api.IPossibleAnswerService;
import by.testing.in.english.bot.englishBot.services.api.IQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionService implements IQuestionService {

    private final IPossibleAnswerService possibleAnswerService;
    private final ObjectMapper objectMapper;
    private final QuestionRepository repository;


    public QuestionService(IPossibleAnswerService possibleAnswerService, ObjectMapper objectMapper,
                           QuestionRepository repository) {
        this.possibleAnswerService = possibleAnswerService;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }


    @Override
    @Transactional
    public Question create(String objectMapper) {

        return null;
    }

    @Override
    public Question getRandom() {

        long count = repository.count();
        int randomNumber = (int) (Math.random() * count);

        Pageable pageable = PageRequest.of(randomNumber, 1);

        Page<Question> page = repository.findAll(pageable);


        return page.getContent().get(0);
    }

    @Override
    public Question get(long id) {

        return repository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Question not found"));
    }
}
