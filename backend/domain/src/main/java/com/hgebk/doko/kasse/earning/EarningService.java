package com.hgebk.doko.kasse.earning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EarningService {
    private final EarningRepository earningRepository;

    @Autowired
    public EarningService(EarningRepository earningRepository) {
        this.earningRepository = earningRepository;
    }

    public List<Earning> getAllEarnings() {
        log.info("DBACK: Find all earnings");
        List<Earning> earnings = (List<Earning>) earningRepository.findAll();
        return earnings.stream().sorted().toList();
    }

    public Earning getEarning(String id) {
        log.info("DBACK: Find earning with id {}", id);
        return earningRepository.findById(id).orElseThrow(() -> new EarningNotFoundException(id));
    }

    public void saveEarning(Earning newEarning) {
        log.info("DBACK: Save new earning");
        earningRepository.save(newEarning);
    }

    public void updateEarning(Earning updatedEarning) {
        log.info("DBACK: Find earning to update");
        if (earningRepository.findById(updatedEarning.getId()).isEmpty()) {
            throw new EarningNotFoundException(updatedEarning.getId());
        }

        earningRepository.save(updatedEarning);
    }

    public void deleteEarningById(String id) {
        log.info("DBACK: Find earning to delete");
        Earning earningToDelete = earningRepository
                .findById(id)
                .orElseThrow(() -> new EarningNotFoundException(id));

        earningRepository.delete(earningToDelete);
    }

    public Double getTotalEarnings() {
        log.info("DBACK: Get total from earnings");
        List<Earning> allEarnings = (List<Earning>) earningRepository.findAll();
        return allEarnings.stream().mapToDouble(Earning::getValue).sum();
    }
}
