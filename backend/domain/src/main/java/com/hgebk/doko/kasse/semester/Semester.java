package com.hgebk.doko.kasse.semester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doko-semesters")
public class Semester {
    @Id
    private String key;

    private String label;
}
