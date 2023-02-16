package by.testing.in.english.bot.englishBot.services;


import by.testing.in.english.bot.englishBot.config.BotConfig;
import by.testing.in.english.bot.englishBot.model.*;
import by.testing.in.english.bot.englishBot.services.api.IQuestionService;
import by.testing.in.english.bot.englishBot.services.api.IUserService;
import by.testing.in.english.bot.englishBot.services.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;

@Component
public class BotService extends TelegramLongPollingBot {

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";


    private final BotConfig config;
    private final IUserService userService;
    private final MessageUtil messageUtil;
    private final IQuestionService questionService;


    private static final Logger logger = LoggerFactory.getLogger(BotService.class);
    private static final String HELP_MESSAGE = "Выбери команду которая тебе подходить:\n\n" +
            "/start - начало работы бота \n\n" +
            "/go - получить вопрос\n\n" +
            "/setting - настроить уровень сложгости теста\n\n" +
            "/disable - отключить бот";

    private static final String HELP_MESSAGE_ADMIN =
//            "Выбери команду которая тебе подходить:\n\n" +
//            "/start - начало работы бота \n\n" +
//            "/go - получить вопрос\n\n" +
//            "/setting - настроить уровень сложгости теста\n\n" +
            "\n\n/send - отправить сообщение всем пользователям бота\n\n" +
            "/add_question - добавить новый вопрос\n\n" +
            " \uD83D\uDC47⬇️Pattern⬇️\uD83D\uDC47";

    private static final String ADD_QUESTION_PATTERN = "/add_question {\"question\":\" \",\"description\":\" \"," +
            "\"level\":\" \",\"possibleAnswers\":" +
            "[{\"correct\":true,\"answer\":{\"answer\":\" \"}}," +
            "{\"correct\":false,\"answer\":{\"answer\":\" \"}}," +
            "{\"correct\":false,\"answer\":{\"answer\":\" \"}}]}";


    public BotService(BotConfig config, IUserService userService, MessageUtil messageUtil,
                      IQuestionService questionService) {
        this.config = config;
        this.userService = userService;
        this.messageUtil = messageUtil;
        this.questionService = questionService;
    }


    @Override
    public String getBotUsername() {
         return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }



    @Override //приходит сообщение от пользователя
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()){//если есть сообщение

            String text = update.getMessage().getText();//сообщение пользователя(Текст)
            long chatId = update.getMessage().getChatId();//ид чата с польлзователем
//            String firstName = update.getMessage().getChat().getFirstName();// name user
            String userName = update.getMessage().getChat().getUserName();
            User sender = null;

            //////Если незареганный пользователь захочет выполнить команду  !старт
            if (!text.equals("/start")) {

                try {
                sender = userService.get(chatId);
            } catch (Exception e) {

                    sendMessage(chatId,
                            "Для начала работы с ботам нажмите /start");
                    return;
                }

            if (sender.getStatus().equals(Status.DEACTIVATED)){

                    sendMessage(chatId,
                            "Бот отключен. Для начала работы с ботам нажмите /start");

                    return;
                }

            if (sender.getStatus().equals(Status.BLOCKED)){

                sendMessage(chatId,
                        "Доступ к Боту заблокирован!");

                return;
                }
            }
            ///////////


            /////Рассылка всем пользователям
            if (text.contains("/send") && !sender.getRole().equals(Role.USER)){

                String textToSend = text.substring(text.indexOf(" "));

                List<User> userList = userService.get();

                for (User user : userList) {
                    if (user.getStatus().equals(Status.ACTIVATED)){
                        sendMessage(user.getChatId(), textToSend);
                    }
                }
                logger.info("{} sent {} messages to all users",userName, userList.size());
                return;
            }

            /////////////////


            /////Создаем вопрос
            if (text.contains("/add_question") && !sender.getRole().equals(Role.USER)) {

                String textObject = text.substring(text.indexOf(" "));

                Question question = this.questionService.create(textObject);
                if (question == null){
                    this.sendMessage(chatId, "Не удалось создать Question");
                } else {
                SendMessage sendMessage = this.messageUtil.sendMessageNewQuestion(chatId, question);
                this.executeMessage(sendMessage, chatId);
                }
                    return;
            }

            /////////////////////////


                switch (text) {
                case "/start":
//                    sendMessageStart(chatId, "Hello " + firstName + "\n Приветствие и описание...");

                    logger.info("User {} start", userName);

                    SendMessage sendMessage = this.messageUtil.sendMessageStart(update.getMessage());
                    this.executeMessage(sendMessage, chatId);

//                    this.userService.create(update.getMessage());//Save user to db

                    break;


                    ///////////////////////////////


                case "/go":
                    case "❔Question❓":

                        var messageForQuestion = this.messageUtil.sendMessageForQuestion(chatId);
                    this.executeMessage(messageForQuestion, chatId);

                    break;


                    //////////////////////


                    case "/setting":
                    case "⚙️Setting":


                        SendMessage sendSetting = this.messageUtil.sendSetting(chatId);
                        this.executeMessage(sendSetting, chatId);


                        break;


                    ///////////////////


                case "/help":
                    case "ℹ️Help":

                    User user = userService.get(chatId);

                    if (user.getRole().equals(Role.BOSS) || user.getRole().equals(Role.ADMIN)){

                        sendMessage(chatId, HELP_MESSAGE + HELP_MESSAGE_ADMIN);
                        sendMessage(chatId, ADD_QUESTION_PATTERN);

                        break;
                    }

                    sendMessage(chatId, HELP_MESSAGE);

                    break;


                case "/disable":

                    this.userService.deactivate(chatId);
                    this.sendMessage(chatId, "Bot disabled");

                    break;

                default:

                    sendMessage(chatId, "Sorry. /help");
                    logger.warn("User {} entered an invalid command: {}", userName, text);

                    break;
            }
        }

        //если пользовател ответил нажав на кнопку
        else if(update.hasCallbackQuery()){  //если пользовател ответил нажав на кнопку

            String textMessage = update.getCallbackQuery().getMessage().getText();
            String userName = update.getCallbackQuery().getFrom().getUserName();


            String data = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            User user;
            String response;
            String userLevel;
            EditMessageText editMessageText;

            if (data.contains(TRUE)) {

                    response = " \n\nRight✅";

                    editMessageText = messageUtil.changeMessage(chatId, textMessage, messageId);
                    this.executeMessage(editMessageText, chatId);

                    this.sendMessage(chatId, response);


                } else if (data.contains(FALSE)) {

                    String id = data.substring(data.indexOf(" ") + 1);
                    response = " \n\nWrong❌";

                    editMessageText = messageUtil.changeMessage(chatId, textMessage, messageId);
                    this.executeMessage(editMessageText, chatId);

                    this.sendMessage(chatId, response);

                    try {
                        Question question = this.questionService.get(Long.parseLong(id));

                        this.sendMessage(chatId, "ℹ️ " + question.getDescription());

                    } catch (NumberFormatException e) {
                        logger.error("Не удалось получить ид Question: {}", e.getMessage());
                    } catch (IllegalArgumentException e){
                        logger.error("Не удалось получить Question из базы: {}", e.getMessage());
                    }

                }

                switch (data){
                    case "A1":

                        user = userService.updateLevel(chatId,
                                Level.A1);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    case "A2":

                        user = userService.updateLevel(chatId, Level.A2);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    case "B1":

                        user = userService.updateLevel(chatId, Level.B1);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    case "B2":

                        user = userService.updateLevel(chatId, Level.B2);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    case "C1":

                        user = userService.updateLevel(chatId, Level.C1);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    case "C2":

                        user = userService.updateLevel(chatId, Level.C2);
                        userLevel = user.getLevel().toString();

                        response = "Your English level has been changed to " + userLevel;

                        editMessageText = messageUtil.changeMessage(chatId, response, messageId);
                        this.executeMessage(editMessageText, chatId);

                        logger.info("User {} chose the level: {}", userName, userLevel);

                        break;

                    default:
                        break;

                }
        }

    }


    private void sendMessage(long chatId, String textToSend){

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);


        this.executeMessage(message, chatId);
    }


    private <T extends Serializable, Method extends BotApiMethod<T>> void executeMessage(Method message, long chatId){

        try{
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chat {}: ", chatId, e);
        }
    }


}
