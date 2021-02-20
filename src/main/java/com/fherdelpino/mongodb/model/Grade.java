package com.fherdelpino.mongodb.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class Grade {

    ObjectId id;

    @BsonProperty(value = "student_id")
    Double studentId;

    @BsonProperty(value = "student_id")
    Double classId;

    List<Score> scores;
}
