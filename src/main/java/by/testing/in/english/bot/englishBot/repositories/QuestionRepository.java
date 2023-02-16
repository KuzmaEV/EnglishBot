package by.testing.in.english.bot.englishBot.repositories;

import by.testing.in.english.bot.englishBot.model.Level;
import by.testing.in.english.bot.englishBot.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAllByLevel(Pageable pageable, Level level);
    int countByLevel(Level level);
}
