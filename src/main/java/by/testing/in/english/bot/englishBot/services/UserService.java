package by.testing.in.english.bot.englishBot.services;


import by.testing.in.english.bot.englishBot.model.Level;
import by.testing.in.english.bot.englishBot.model.Role;
import by.testing.in.english.bot.englishBot.model.Status;
import by.testing.in.english.bot.englishBot.model.User;
import by.testing.in.english.bot.englishBot.repositories.UserRepository;
import by.testing.in.english.bot.englishBot.services.api.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void create(Message message){

        Long chatId = message.getChatId();
        Chat chat = message.getChat();

        if (!repository.existsById(chatId)){

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            user.setRole(Role.USER);
            user.setStatus(Status.ACTIVATED);
            user.setLevel(Level.A1);

            this.repository.save(user);

            logger.info("New user saved: " + user);

        } else {

            User user = this.repository.findById(chatId).get();
            if (user.getStatus().equals(Status.DEACTIVATED)){

                user.setStatus(Status.ACTIVATED);
                this.repository.save(user);
            }
        }


    }

    @Override
    public User get(Long chatId){
        if (chatId == null){
            logger.error("chatId is null");
            throw new IllegalArgumentException("chatId is null");
        }
        Optional<User> userOptional = repository.findById(chatId);

        if (userOptional.isEmpty()){
            logger.error("User {} is not found ", chatId);
            throw new IllegalArgumentException("User is not found");
        }

        return userOptional.get();

    }

    @Override
    public List<User> get(){
        return this.repository.findAll();
    }


    @Override
    @Transactional
    public User updateLevel(Long chatId, Level level) {

        User user = this.get(chatId);

        user.setLevel(level);

        return this.repository.save(user);
    }


    @Override
    @Transactional
    public void deactivate(Long chatId){

        User user = this.get(chatId);
        user.setStatus(Status.DEACTIVATED);

        this.repository.save(user);
        logger.info("User {} deactivated", user.getUserName());

    }
}
