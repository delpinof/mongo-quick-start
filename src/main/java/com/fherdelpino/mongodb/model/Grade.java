package com.fherdelpino.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    ObjectId id;

    @BsonProperty(value = "student_id")
    Double studentId;

    @BsonProperty(value = "class_id")
    Double classId;

    List<Score> scores;
}
