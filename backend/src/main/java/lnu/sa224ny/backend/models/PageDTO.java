package lnu.sa224ny.backend.models;

public class PageDTO {
    public String link;
    public double score;
    public double content;
    public double location;
    public double pageRank;

    public double getScore() {
        return score;
    }
}
