package com.fherdelpino.mongodb;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GradesFactory {

    private static final Random rand = new Random();

    public static Document generateNewGrade(double studentId, double classId) {
        List<Document> scores = Arrays.asList(
                new Document("type", "exam").append("score", rand.nextDouble() * 100),
                new Document("type", "quiz").append("score", rand.nextDouble() * 100),
                new Document("type", "homework").append("score", rand.nextDouble() * 100),
                new Document("type", "homework").append("score", rand.nextDouble() * 100));

        return new Document("_id", new ObjectId())
                .append("student_id", studentId)
                .append("class_id", classId)
                .append("scores", scores);
    }
}
