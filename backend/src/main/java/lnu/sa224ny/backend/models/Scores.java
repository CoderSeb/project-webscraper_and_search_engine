package lnu.sa224ny.backend.models;

public class Scores {
    public double[] content;
    public double[] location;

    public Scores(int size) {
        this.content = new double[size];
        this.location = new double[size];
    }
}
