package by.testing.in.english.bot.englishBot.services;

import by.testing.in.english.bot.englishBot.model.Level;
import by.testing.in.english.bot.englishBot.model.PossibleAnswer;
import by.testing.in.english.bot.englishBot.model.Question;
import by.testing.in.english.bot.englishBot.repositories.QuestionRepository;
import by.testing.in.english.bot.englishBot.services.api.IPossibleAnswerService;
import by.testing.in.english.bot.englishBot.services.api.IQuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class QuestionService implements IQuestionService {

    private final IPossibleAnswerService possibleAnswerService;
    private final ObjectMapper mapper;
    private final QuestionRepository repository;

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    public QuestionService(IPossibleAnswerService possibleAnswerService,
                           QuestionRepository repository) {
        this.possibleAnswerService = possibleAnswerService;
        this.mapper = new ObjectMapper();
        this.repository = repository;
    }


    @Override
    @Transactional
    public Question create(String objectJson) {

        Question question;

        List<PossibleAnswer> savedPossibles = new ArrayList<>();


        // создаю Question из JSON
        try {
            question = mapper.readValue(objectJson, Question.class);
        } catch (JsonProcessingException e) {

            log.error("{}", e.getMessage());

            return null;
        }


        // Сохраняю все варианты ответов
        for (PossibleAnswer possibleAnswer : question.getPossibleAnswers()) {

            PossibleAnswer createdPossible = possibleAnswerService.create(possibleAnswer);
            savedPossibles.add(createdPossible);
        }

        question.setPossibleAnswers(savedPossibles);

        return repository.save(question);
    }



    @Override
    public Question getRandom(Level level) {

        int count = repository.countByLevel(level);
        if (count == 0){
            throw new IllegalArgumentException("Вопроса такого уровня еще нету в базе");
        }


        int randomNumber = (int) (Math.random() * count);

        Pageable pageable = PageRequest.of(randomNumber, 1);

        Page<Question> page = repository.findAllByLevel(pageable, level);


        return page.getContent().get(0);
    }



    @Override
    public Question get(long id) {

        return repository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Question not found"));
    }
}
