package com.hgebk.doko.kasse.evening;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hgebk.doko.kasse.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doko-evenings")
public class Evening implements Comparable<Evening> {
    @Id
    private String date;

    private String semester;

    @Field("jan")
    @JsonProperty("jan")
    private double resultJan;

    @Field("tim")
    @JsonProperty("tim")
    private double resultTim;

    @Field("ole")
    @JsonProperty("ole")
    private double resultOle;

    @Field("louisa")
    @JsonProperty("louisa")
    private double resultLouisa;

    @Field("hannes")
    @JsonProperty("hannes")
    private double resultHannes;

    @Transient
    public List<EveningResultDTO> getResults() {
        return List.of(
                new EveningResultDTO(Player.JAN, this.getResultJan()),
                new EveningResultDTO(Player.TIM, this.getResultTim()),
                new EveningResultDTO(Player.OLE, this.getResultOle()),
                new EveningResultDTO(Player.LOUISA, this.getResultLouisa()),
                new EveningResultDTO(Player.HANNES, this.getResultHannes())
        );
    }

    @Override
    public int compareTo(Evening that) {
        return this.getDate().compareTo(that.getDate());
    }
}
