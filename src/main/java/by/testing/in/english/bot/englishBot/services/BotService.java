package by.testing.in.english.bot.englishBot.services;


import by.testing.in.english.bot.englishBot.config.BotConfig;
import by.testing.in.english.bot.englishBot.model.Question;
import by.testing.in.english.bot.englishBot.model.Role;
import by.testing.in.english.bot.englishBot.model.Status;
import by.testing.in.english.bot.englishBot.model.User;
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

    public static final String YES_BUTTON = "YES_BUTTON";
    public static final String NO_BUTTON = "NO_BUTTON";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";


    private final BotConfig config;
    private final IUserService userService;
    private final MessageUtil messageUtil;
    private final IQuestionService questionService;


    private static final Logger logger = LoggerFactory.getLogger(BotService.class);
    private static final String HELP_MESSAGE = "Выбери команду котораятебе подходить:\n\n" +
            "/start - начало работы бота \n\n" +
            "/yes - да\n\n" +
            "/no - нет";

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
                sender = userService.read(chatId);
            } catch (Exception e) {

                    sendMessage(chatId,
                            "Для начала работы с ботам нажмите /start");
                    return;
                }

            if (sender.getStatus().equals(Status.BLOCKED)){

                sendMessage(chatId,
                        "Доступ к Боту заблокирован!");

                return;
                }
            }
            ///////////


            //Рассылка всем пользователям
            if (text.contains("/send") && !sender.getRole().equals(Role.USER)){

                String textToSend = text.substring(text.indexOf(" "));

                List<User> userList = userService.read();

                for (User user : userList) {
                    if (user.getStatus().equals(Status.ACTIVATED)){
                        sendMessage(user.getChatId(), textToSend);
                    }
                }
                logger.info("{} sent {} messages to all users",userName, userList.size());
                return;
            }



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

                    var messageForQuestion = this.messageUtil.sendMessageForQuestion(chatId);
                    this.executeMessage(messageForQuestion, chatId);

                    break;


                    //////////////////////


                case "/help":

                    sendMessage(chatId, HELP_MESSAGE);

                    break;

                default:

                    sendMessage(chatId, "Sorry. /help");
                    logger.warn("User {} entered an invalid command: {}", userName, text);

                    break;
            }
        }

        //если пользовател ответил нажав на кнопку
        else if(update.hasCallbackQuery()){  //если пользовател ответил нажав на кнопку

            String textMessage = update.getMessage().getText();

            String textAnswer = update.getCallbackQuery().getMessage().getText();


            String data = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            //TODO как бот будет понимать какой вариант правильный?


//            switch (data){

//                case YES_BUTTON:
//                    sendMessage(chatId, "Я тоже тебя люблю \uD83D\uDE18");
//                    break;

//                case NO_BUTTON:
//                    changeMessage(chatId, "Поцему??\uD83D\uDE22", messageId);//изменяет сообщение, не создает новое
//                    break;
//                default:
//                    break;
//            }

                if (data.contains(TRUE)) {

                    String response = textAnswer + " \n\nRight";

                    EditMessageText editMessageText = messageUtil.changeMessage(chatId, textMessage, messageId);
                    this.executeMessage(editMessageText, chatId);

                    this.sendMessage(chatId, response);


                } else if (data.contains(FALSE)) {

                    String id = data.substring(data.indexOf(" "));
                    String response = textAnswer + " \n\nWrong";

                    EditMessageText editMessageText = messageUtil.changeMessage(chatId, textMessage, messageId);
                    this.executeMessage(editMessageText, chatId);

                    this.sendMessage(chatId, response);

                    try {
                        Question question = this.questionService.get(Long.parseLong(id));

                        this.sendMessage(chatId, question.getDescription());

                    } catch (NumberFormatException e) {
                        logger.error("Не удалось получить ид Question: {}", e.getMessage());
                    } catch (IllegalArgumentException e){
                        logger.error("Не удалось получить Question из базы: {}", e.getMessage());
                    }

                }



        }


    }


    private void sendMessage(long chatId, String textToSend){

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);


//        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(); //делаем свою клавиатуру
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();//первый ряд кнопак
//        row.add("\uD83D\uDCE5Add task");
//        row.add("\uD83D\uDCDATask");
//
//        KeyboardRow row2 = new KeyboardRow();//первый ряд кнопак
//        row2.add("\uD83D\uDCCAStatistics");
//        row2.add("⚙️Setting");
//
//        keyboardRows.add(row);//добавили ряд кнопока в список кнопак
//        keyboardRows.add(row2);
//
//        replyMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(replyMarkup);//Добавляем кнопки в сообщение



        this.executeMessage(message, chatId);

    }
//
//    private void sendMessageStart(long chatId, String textToSend){
//
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(textToSend);
//
//        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();//Делаем кнопки под сообщением
//        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
//        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
//
//        InlineKeyboardButton yesButton = new InlineKeyboardButton();
//        yesButton.setText("Yes");
//        yesButton.setCallbackData(YES_BUTTON);
//
//        InlineKeyboardButton noButton = new InlineKeyboardButton();
//        noButton.setText("No");
//        noButton.setCallbackData(NO_BUTTON);
//
//
//        rowInLine.add(yesButton);
//        rowInLine.add(noButton);
//
//        rowsInLine.add(rowInLine);
//
//        inlineMarkup.setKeyboard(rowsInLine);
//
//
//        /////
//        //////////////
//        //////////////////////////////
//
//        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(); //делаем свою клавиатуру
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();//первый ряд кнопак
//        row.add("\uD83D\uDCE5Add task");
//        row.add("\uD83D\uDCDATask");
//
//        KeyboardRow row2 = new KeyboardRow();//первый ряд кнопак
//        row2.add("\uD83D\uDCCAStatistics");
//        row2.add("⚙️Setting");
//
//        keyboardRows.add(row);//добавили ряд кнопока в список кнопак
//        keyboardRows.add(row2);
//
//        replyMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(replyMarkup);//Добавляем кнопки в сообщение
//
//        ////////////////
//        ////////////
//        /////
//
//
//
//        message.setReplyMarkup(inlineMarkup);
//
//        this.executeMessage(message, chatId);
//
//    }
//
//    private void sendMessageForQuestion(long chatId){
//
//        hh
//
//    }
//
//
//    private void changeMessage(long chatId, String text, Integer messageId){//изменяет сообщение
//
//        EditMessageText message = new EditMessageText();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.setMessageId(messageId);
//
//        this.executeMessage(message, chatId);
//
//    }

    private <T extends Serializable, Method extends BotApiMethod<T>> void executeMessage(Method message, long chatId){

        try{
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chat {}", chatId);
        }
    }


}
