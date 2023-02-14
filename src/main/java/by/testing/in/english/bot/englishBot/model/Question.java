package by.testing.in.english.bot.englishBot.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String question;

    private String description;

    @OneToMany
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private List<PossibleAnswer> possibleAnswers;

    public Question() {
    }

    public Question(long id, String question, String description, List<PossibleAnswer> possibleAnswers) {
        this.id = id;
        this.question = question;
        this.description = description;
        this.possibleAnswers = possibleAnswers;
    }

    public long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PossibleAnswer> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<PossibleAnswer> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", description='" + description + '\'' +
                ", possibleAnswers=" + possibleAnswers +
                '}';
    }
}
