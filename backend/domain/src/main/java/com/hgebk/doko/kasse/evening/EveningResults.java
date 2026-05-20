package com.hgebk.doko.kasse.evening;

import com.hgebk.doko.kasse.Player;
import com.hgebk.doko.kasse.report.SemesterResultDTO;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
public class EveningResults {
    private List<EveningResultDTO> results;

    public EveningResults(List<EveningResultDTO> results) {
        this.results = results;
    }

    public long getParticipations() {
        return this.results.stream().filter(result -> result.value() > 0).count();
    }

    public double getSum() {
        return this.results.stream().mapToDouble(EveningResultDTO::value).sum();
    }

    public double getAvg() {
        long participations = getParticipations();

        if (participations == 0) {
            return 0;
        }

        return getSum() / participations;
    }

    public Optional<EveningResultDTO> getMinResult() {
        return this.results
                .stream()
                .filter(playerResult -> playerResult.value() > 0)
                .min(Comparator.comparing(EveningResultDTO::value));
    }

    public Optional<EveningResultDTO> getMaxResult() {
        return this.results
                .stream()
                .filter(playerResult -> playerResult.value() > 0)
                .max(Comparator.comparing(EveningResultDTO::value));
    }

    public SemesterResultDTO toSemesterResult(Player player) {
        return SemesterResultDTO
                .builder()
                .player(player)
                .sum(getSum())
                .avg(getAvg())
                .min(getMinResult().map(EveningResultDTO::value).orElse(0.0))
                .max(getMaxResult().map(EveningResultDTO::value).orElse(0.0))
                .participations(getParticipations())
                .build();
    }
}
