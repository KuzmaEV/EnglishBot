package by.testing.in.english.bot.englishBot.services.util;

import by.testing.in.english.bot.englishBot.model.PossibleAnswer;
import by.testing.in.english.bot.englishBot.model.Question;
import by.testing.in.english.bot.englishBot.services.api.IQuestionService;
import by.testing.in.english.bot.englishBot.services.api.IUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageUtil {


    private final IQuestionService questionService;
    private final IUserService userService;


    public MessageUtil(IQuestionService questionService, IUserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }


    public SendMessage sendMessageStart(Message userMessage){

        Long chatId = userMessage.getChatId();
        String firstName = userMessage.getChat().getFirstName();
        String textToSend = "Hello " + firstName + "!\n Здесь должно быть приветствие и описание...";


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

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

//        message.setReplyMarkup(inlineMarkup);



        /////
        //////////////
        //////////////////////////////

        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(); //делаем свою клавиатуру
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();//первый ряд кнопак
        row.add("❔Question");
//        row.add("☑️Test");

        KeyboardRow row2 = new KeyboardRow();//первый ряд кнопак
        row2.add("ℹ️Help");
//        row2.add("⚙️Setting");

        keyboardRows.add(row);//добавили ряд кнопока в список кнопак
        keyboardRows.add(row2);

        replyMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyMarkup);//Добавляем кнопки в сообщение

        ////////////////
        ////////////
        /////

        this.userService.create(userMessage);//Save user to db

        return message;

    }

    public SendMessage sendMessageForQuestion(long chatId){

        Question question = this.questionService.getRandom();
        List<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(question.getQuestion());


        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();//Делаем кнопки под сообщением
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
//        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        /////////////////

        for (PossibleAnswer possibleAnswer : possibleAnswers) {

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            InlineKeyboardButton possible = new InlineKeyboardButton();
            possible.setText( possibleAnswer.getAnswer().getAnswer());
            possible.setCallbackData(String.valueOf(possibleAnswer.isCorrect()).toUpperCase() +
                    " " + question.getId());

            rowInLine.add(possible);

            rowsInLine.add(rowInLine);
        }



        ////////////////////

//        InlineKeyboardButton possible1 = new InlineKeyboardButton();
//        PossibleAnswer possibleAnswer1 = possibleAnswers.get(0);
//        possible1.setText( possibleAnswer1.getAnswer().getAnswer() );
//        possible1.setCallbackData(String.valueOf(possibleAnswer1.isCorrect()));
//
//        InlineKeyboardButton possible2 = new InlineKeyboardButton();
//        possible2.setText( possibleAnswers.get(1).getAnswer().getAnswer() );
//        possible2.setCallbackData(NO_BUTTON);
//
//        InlineKeyboardButton possible3 = new InlineKeyboardButton();
//        possible3.setText( possibleAnswers.get(2).getAnswer().getAnswer() );
//        possible3.setCallbackData(NO_BUTTON);
//
//        InlineKeyboardButton possible4 = new InlineKeyboardButton();
//        possible4.setText( possibleAnswers.get(3).getAnswer().getAnswer() );
//        possible4.setCallbackData(NO_BUTTON);
//
//
//        rowInLine.add(possible1);
//        rowInLine.add(possible4);
//
//        rowsInLine.add(rowInLine);

        inlineMarkup.setKeyboard(rowsInLine);

        message.setReplyMarkup(inlineMarkup);

        return message;

    }

    public SendMessage sendMessageNewQuestion(long chatId, Question question){

        List<PossibleAnswer> possibleAnswers = question.getPossibleAnswers();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(question.getQuestion());


        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();//Делаем кнопки под сообщением
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        ///////////////// Создаем кнопки

        for (PossibleAnswer possibleAnswer : possibleAnswers) {

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            InlineKeyboardButton possible = new InlineKeyboardButton();
            possible.setText( possibleAnswer.getAnswer().getAnswer());
            possible.setCallbackData(String.valueOf(possibleAnswer.isCorrect()).toUpperCase() +
                    " " + question.getId());

            rowInLine.add(possible);

            rowsInLine.add(rowInLine);
        }


        ////////////////////

        inlineMarkup.setKeyboard(rowsInLine);

        message.setReplyMarkup(inlineMarkup);

        return message;

    }

    public EditMessageText changeMessage(long chatId, String text, Integer messageId){//изменяет сообщение

        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId(messageId);

        return message;

    }


}
