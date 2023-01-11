package lnu.sa224ny.backend.models;

public class PageDTO {
    public String link;
    public double score = 0.0;
    public double content = 0.0;
    public double location = 0.0;
    public double pageRank = 0.0;

    public double getScore() {
        return score;
    }
}
