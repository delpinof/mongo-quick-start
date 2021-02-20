package com.fherdelpino.mongodb;

import com.fherdelpino.mongodb.model.Grade;
import com.fherdelpino.mongodb.model.Score;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.singletonList;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MongoPojoTest {

    private static MongoClient mongoClient;

    private static MongoDatabase sampleTrainingDb;

    private static MongoCollection<Grade> gradesCollection;

    private static Grade grade;

    @BeforeAll
    public static void setUp() {
        mongoClient = MongoDbConnectionFactory.getConnection();
        sampleTrainingDb = mongoClient.getDatabase("sample_training");
        gradesCollection = sampleTrainingDb.getCollection("grades", Grade.class);
    }

    @Test
    @Order(1)
    public void testInsert() {
        List<Score> scores = singletonList(Score.builder().type("homework").score(50d).build());
        Grade newGrade = Grade.builder()
                .studentId(10003d)
                .classId(10d)
                .scores(scores)
                .build();
        gradesCollection.insertOne(newGrade);
    }

    @Test
    @Order(2)
    public void testFind() {
        grade = gradesCollection.find(eq("student_id", 10003d)).first();
        log.info("{}", grade);
    }

    @Test
    @Order(3)
    public void testFindOneAndReplace() {
        List<Score> newScores = new ArrayList<>(grade.getScores());
        newScores.add(Score.builder().type("exam").score(42d).build());
        grade.setScores(newScores);
        Document filterByGradeId = new Document("_id", grade.getId());
        FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions()
                .returnDocument(ReturnDocument.AFTER);
        Grade updatedGrade = gradesCollection.findOneAndReplace(filterByGradeId, grade, returnDocAfterReplace);
        log.info("{}", updatedGrade);
    }

    @Test
    @Order(4)
    public void testDelete() {
        DeleteResult result = gradesCollection.deleteMany(eq("student_id", 10003d));
        log.info("{}", result);
    }

    @AfterAll
    public static void tearDown() {
        mongoClient.close();
    }
}
