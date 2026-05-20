package com.hgebk.doko.kasse.earning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doko-earnings")
public class Earning implements Comparable<Earning> {
    @Id
    private String id;

    private String description;

    private double value;

    private String semester;

    @Override
    public int compareTo(Earning that) {
        return this.getDescription().compareTo(that.getDescription());
    }
}
