package by.testing.in.english.bot.englishBot.model;

import javax.persistence.*;

@Entity
@Table(name = "possible_answer")
public class PossibleAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @ManyToOne
    private Answer answer;

    public long getId() {
        return id;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "PossibleAnswer{" +
                "id=" + id +
                ", isCorrect=" + isCorrect +
                ", answer=" + answer +
                '}';
    }
}
