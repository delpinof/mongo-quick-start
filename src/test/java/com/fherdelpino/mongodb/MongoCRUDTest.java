package com.fherdelpino.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MongoCRUDTest {

    private static MongoClient mongoClient;

    private static MongoDatabase sampleTrainingDb;

    private static MongoCollection<Document> gradesCollection;

    private JsonWriterSettings prettyPrint = JsonWriterSettings.builder().indent(true).build();

    @BeforeAll
    public static void setUp() {
        mongoClient = MongoDbConnectionFactory.getConnection();
        sampleTrainingDb = mongoClient.getDatabase("sample_training");
        gradesCollection = sampleTrainingDb.getCollection("grades");
    }

    @Test
    @Order(1)
    public void connectionTest() {
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
        databases.forEach(db -> log.info(db.toJson()));
    }

    @Test
    @Order(2)
    public void createOneGradeTest() {
        Document grade = GradesFactory.generateNewGrade(10000d, 1d);
        gradesCollection.insertOne(grade);
    }

    @Test
    @Order(3)
    public void createManyGradesTest() {
        List<Document> grades = new ArrayList<>();
        for (double classId = 1d; classId <= 10d; classId++) {
            grades.add(GradesFactory.generateNewGrade(10001d, classId));
        }
        gradesCollection.insertMany(grades);
    }

    @Test
    @Order(4)
    public void readGradeTest() {
        Document grade = gradesCollection.find(new Document().append("student_id", 10000d)).first();
        Document grade2 = gradesCollection.find(Filters.eq("student_id", 10000)).first();
        log.info(grade.toJson());
        log.info(grade2.toJson());
        assertThat(grade).isEqualTo(grade2);
    }

    @Test
    @Order(5)
    public void readGradesStudentIdGreaterThanTest() {
        // without helpers
        FindIterable<Document> iterable = gradesCollection.find(new Document("student_id", new Document("$gte", 10000)));
        // with the Filters.gte() helper
        iterable = gradesCollection.find(gte("student_id", 10000));

        MongoCursor<Document> cursor = iterable.iterator();

        while (cursor.hasNext()) {
            log.info(cursor.next().toJson());
        }

        List<Document> studentList = iterable.into(new ArrayList<>());
        log.info("Student list with an ArrayList:");
        for (Document student : studentList) {
            log.info(student.toJson());
        }

        Consumer<Document> printConsumer = document -> System.out.println(document.toJson());
        gradesCollection.find(gte("student_id", 10000)).forEach(printConsumer);
    }

    @Test
    @Order(6)
    public void complexQueryTest() {
        List<Document> docs = gradesCollection.find(
                and(
                        eq("student_id", 10001),
                        lte("class_id", 5)
                )
        ).projection(
                fields(
                        excludeId(),
                        include("class_id", "student_id")
                )
        )
                .sort(descending("class_id"))
                .skip(2)
                .limit(2)
                .into(new ArrayList<>());
        log.info("Student sorted, skipped, limited and projected: ");
        for (Document student : docs) {
            log.info(student.toJson());
        }

    }

    @Test
    @Order(7)
    public void updateTest() {
        Bson filter = eq("student_id", 10000);
        Bson updateOperation = set("comment", "You should learn MongoDB!");
        UpdateResult updateResult = gradesCollection.updateOne(filter, updateOperation);
        log.info("=> Updating the doc with {{}:{}}. Adding comment.", "student_id", 10000);
        log.info("{}", gradesCollection.find(filter).first().toJson(prettyPrint));
        log.info("{}", updateResult.toString());
    }

    @Test
    @Order(8)
    public void upsertTest() {
        Bson filter = and(eq("student_id", 10002d), eq("class_id", 10d));
        Bson updateOperation = push("comments", "You will learn a lot if you read the MongoDB blog!");
        UpdateOptions options = new UpdateOptions().upsert(true);
        UpdateResult updateResult = gradesCollection.updateOne(filter, updateOperation, options);
        log.info("=> Upsert document with {{}:{}, {}:{}} because it doesn't exist yet.", "student_id", 10002d, "class_id", 10d);
        log.info("{}", updateResult);
        log.info("{}", gradesCollection.find(filter).first().toJson(prettyPrint));
    }

    @Test
    @Order(9)
    public void updateManyTest() {
        Bson filter = eq("student_id", 10001);
        Bson updateOperation = set("comment", "You should learn MongoDB!");
        UpdateResult updateResult = gradesCollection.updateMany(filter, updateOperation);
        log.info("=> Updating all the documents with {{}:{}}.", "student_id", 10001);
        log.info("{}", updateResult);
    }

    @Test
    @Order(10)
    public void deleteOneTest() {
        Bson filter = eq("student_id", 10000);
        DeleteResult result = gradesCollection.deleteOne(filter);
        log.info("{}", result);
    }

    @Test
    @Order(11)
    public void deleteManyTest() {
        Bson filter = eq("student_id", 10000);
        Bson filter1 = eq("student_id", 10001);
        Bson filter2 = eq("student_id", 10002);
        DeleteResult result = gradesCollection.deleteMany(filter);
        DeleteResult result1 = gradesCollection.deleteMany(filter1);
        DeleteResult result2 = gradesCollection.deleteMany(filter2);
        log.info("{}", result);
        log.info("{}", result1);
        log.info("{}", result2);
    }

    @AfterAll
    public static void tearDown() {
        mongoClient.close();
    }

}
