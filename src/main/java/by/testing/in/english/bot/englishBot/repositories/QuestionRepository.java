package by.testing.in.english.bot.englishBot.repositories;

import by.testing.in.english.bot.englishBot.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
