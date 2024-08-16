package com.hgebk.doko.semester;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "doko-com.hgebk.doko.semester")
public class Semester {
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "key")
    private String key;

    @DynamoDBAttribute(attributeName = "label")
    private String label;
}
