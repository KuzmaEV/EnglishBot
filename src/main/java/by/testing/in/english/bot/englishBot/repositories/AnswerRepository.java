package by.testing.in.english.bot.englishBot.repositories;

import by.testing.in.english.bot.englishBot.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByAnswer(String answer);
}
