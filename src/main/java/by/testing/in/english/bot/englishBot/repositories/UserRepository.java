package by.testing.in.english.bot.englishBot.repositories;

import by.testing.in.english.bot.englishBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
