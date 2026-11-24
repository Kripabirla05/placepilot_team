package model;

public class Question {

    private String type;
    private String text;
    private String[] options;
    private String answer;

    public Question(String type, String text, String[] options, String answer) {
        this.type = type;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public String getAnswer() { return answer; }

    public String[] getKeywords() {
        return answer.toLowerCase().split(";");
    }
}
