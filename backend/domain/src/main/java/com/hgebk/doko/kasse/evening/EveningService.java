package com.hgebk.doko.kasse.evening;

import com.hgebk.doko.kasse.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EveningService {
    private final EveningRepository eveningRepository;

    @Autowired
    public EveningService(EveningRepository eveningRepository) {
        this.eveningRepository = eveningRepository;
    }

    public List<Evening> searchEvenings(Optional<String> semester) {
        List<Evening> evenings;

        if (semester.isPresent()) {
            log.info("DBACK: Find evenings for com.hgebk.doko.semester {}", semester.get());
            evenings = eveningRepository.findBySemester(semester.get());
        } else {
            log.info("DBACK: Find all evenings");
            evenings = (List<Evening>) eveningRepository.findAll();
        }

        return evenings.stream().sorted().toList();
    }

    public Evening getEvening(String date) {
        log.info("DBACK: Find com.hgebk.doko.evening for date {}", date);
        return eveningRepository.findById(date).orElseThrow(() -> new EveningNotFoundException(date));
    }

    public void saveEvening(Evening newEvening) {
        log.info("DBACK: Find com.hgebk.doko.evening with same date");
        Optional<Evening> eveningWithSameDate = eveningRepository.findById(newEvening.getDate());

        if (eveningWithSameDate.isPresent()) {
            throw new DuplicateEveningException(newEvening.getDate());
        }

        eveningRepository.save(newEvening);
    }

    public void updateEvening(Evening updatedEvening) {
        log.info("DBACK: Find com.hgebk.doko.evening to update");
        Optional<Evening> eveningWithDate = eveningRepository.findById(updatedEvening.getDate());

        if (eveningWithDate.isPresent() == false) {
            throw new EveningNotFoundException(updatedEvening.getDate());
        }

        eveningRepository.save(updatedEvening);
    }

    public void deleteEveningByDate(String date) {
        log.info("DBACK: Find com.hgebk.doko.evening to delete");
        Evening toDelete = eveningRepository.findById(date).orElseThrow(() -> new EveningNotFoundException(date));

        eveningRepository.delete(toDelete);
    }

    public Double getTotalIncomeFromEvenings(Optional<String> semesterKey) {
        log.info("DBACK: Get total income from evenings");
        List<Evening> allEvenings = searchEvenings(semesterKey);

        return allEvenings.stream().mapToDouble(evening -> {
            return evening.getResultJan() + evening.getResultTim() + evening.getResultOle() +
                    evening.getResultLouisa() + evening.getResultHannes();
        }).sum();
    }

    public Map<Player, List<EveningResultDTO>> getEveningResultsByPlayer(
            Optional<String> semester
    ) {
        List<Evening> allEvenings = searchEvenings(semester);
        return allEvenings
                .stream()
                .map(Evening::getResults)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(EveningResultDTO::getPlayer));
    }
}
