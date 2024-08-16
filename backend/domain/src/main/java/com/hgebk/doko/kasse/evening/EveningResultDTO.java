package com.hgebk.doko.kasse.evening;

import com.hgebk.doko.kasse.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EveningResultDTO {
    private Player player;

    private Double value;
}
