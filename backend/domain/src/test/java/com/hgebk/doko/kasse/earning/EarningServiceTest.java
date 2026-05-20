package com.hgebk.doko.kasse.earning;

import com.hgebk.doko.kasse.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class EarningServiceTest extends BaseTest {
    @Mock
    private EarningRepository earningRepository;

    @InjectMocks
    private EarningService underTest;

    @Test
    void getAllEarnings_withResults() {
        List<Earning> earnings = List.of(new Earning("id-1", "foo1", 3.5, "ws2425"), new Earning("id-2", "bar1", 7, "ws2425"));
        given(earningRepository.findAll()).willReturn(earnings);

        List<Earning> actualEarnings = underTest.getAllEarnings();

        assertThat(actualEarnings).isNotNull().containsAll(earnings);
    }

    @Test
    void getAllEarnings_withoutResults() {
        List<Earning> earnings = List.of();
        given(earningRepository.findAll()).willReturn(earnings);

        List<Earning> actualEarnings = underTest.getAllEarnings();

        assertThat(actualEarnings).isNotNull().isEmpty();
    }

    @Test
    void getEarning_found() {
        Earning earning = new Earning("id-1", "foo1", 3.5, "ws2425");
        given(earningRepository.findById("id-1")).willReturn(Optional.of(earning));

        Earning actualEarning = underTest.getEarning("id-1");

        assertThat(actualEarning).isNotNull().isEqualTo(earning);
    }

    @Test
    void getEarning_notFound() {
        given(earningRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.getEarning("id-1"));

        assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with id 'id-1' does not exist");
    }

    @Test
    void saveEarning_savesWithoutDuplicateCheck() {
        Earning earning = new Earning(null, "foo1", 3.5, "ws2425");

        assertThatCode(() -> underTest.saveEarning(earning)).doesNotThrowAnyException();
    }

    @Test
    void updateEarning_found() {
        Earning earning = new Earning("id-1", "foo1", 3.5, "ws2425");
        given(earningRepository.findById("id-1")).willReturn(Optional.of(earning));

        assertThatCode(() -> underTest.updateEarning(earning)).doesNotThrowAnyException();
    }

    @Test
    void updateEarning_notFound() {
        Earning earning = new Earning("id-1", "foo1", 3.5, "ws2425");
        given(earningRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.updateEarning(earning));

        assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with id 'id-1' does not exist");
    }

    @Test
    void deleteEarningById_found() {
        Earning earning = new Earning("id-1", "foo1", 3.5, "ws2425");
        given(earningRepository.findById("id-1")).willReturn(Optional.of(earning));

        assertThatCode(() -> underTest.deleteEarningById("id-1")).doesNotThrowAnyException();
    }

    @Test
    void deleteEarningById_notFound() {
        given(earningRepository.findById("id-1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> underTest.deleteEarningById("id-1"));

        assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with id 'id-1' does not exist");
    }

    @Test
    void getTotalEarnings_twoResults() {
        List<Earning> earnings = List.of(new Earning("id-1", "foo1", 3.5, "ws2425"), new Earning("id-2", "bar1", 7, "ws2425"));
        given(earningRepository.findAll()).willReturn(earnings);

        double actualTotalEarnings = underTest.getTotalEarnings();

        assertThat(actualTotalEarnings).isNotNull().isNotNegative().isEqualTo(10.5);
    }

    @Test
    void getTotalEarnings_noResults() {
        List<Earning> earnings = List.of();
        given(earningRepository.findAll()).willReturn(earnings);

        double actualTotalEarnings = underTest.getTotalEarnings();

        assertThat(actualTotalEarnings).isNotNull().isZero();
    }
}
