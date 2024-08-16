package com.hgebk.doko.kasse.earning;

import com.hgebk.doko.kasse.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;

class EarningServiceTest extends BaseTest {
    @Mock
    private EarningRepository earningRepository;

    private EarningService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new EarningService(earningRepository);
    }

    @Test
    void getAllEarnings_withResults() {
        List<Earning> earnings = List.of(new Earning("foo1", 3.5, "foo1"), new Earning("bar1", 7, "bar1"));
        BDDMockito.given(earningRepository.findAll()).willReturn(earnings);

        List<Earning> actualEarnings = underTest.getAllEarnings();

        Assertions.assertThat(actualEarnings).isNotNull().containsAll(earnings);
    }

    @Test
    void getAllEarnings_withoutResults() {
        List<Earning> earnings = List.of();
        BDDMockito.given(earningRepository.findAll()).willReturn(earnings);

        List<Earning> actualEarnings = underTest.getAllEarnings();

        Assertions.assertThat(actualEarnings).isNotNull().isEmpty();
    }

    @Test
    void getEarning_found() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.of(earning));

        Earning actualEarning = underTest.getEarning("foo1");

        Assertions.assertThat(actualEarning).isNotNull().isEqualTo(earning);
    }

    @Test
    void getEarning_notFound() {
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.getEarning("foo1"));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with description 'foo1' does not exist");
    }

    @Test
    void saveEarning_new() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.empty());

        Assertions.assertThatCode(() -> underTest.saveEarning(earning)).doesNotThrowAnyException();
    }

    @Test
    void saveEarning_duplicate() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.of(earning));

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.saveEarning(earning));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(DuplicateEarningException.class)
                .hasMessage("Earning with description foo1 already exists");
    }

    @Test
    void updateEarning_found() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.of(earning));

        Assertions.assertThatCode(() -> underTest.updateEarning(earning)).doesNotThrowAnyException();
    }

    @Test
    void updateEarning_notFound() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.updateEarning(earning));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with description 'foo1' does not exist");
    }

    @Test
    void deleteEarningByDescription_found() {
        Earning earning = new Earning("foo1", 3.5, "foo1");
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.of(earning));

        Assertions.assertThatCode(() -> underTest.deleteEarningByDescription("foo1")).doesNotThrowAnyException();
    }

    @Test
    void deleteEarningByDescription_notFound() {
        BDDMockito.given(earningRepository.findById("foo1")).willReturn(Optional.empty());

        Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> underTest.deleteEarningByDescription("foo1"));

        Assertions
                .assertThat(thrown)
                .isInstanceOf(EarningNotFoundException.class)
                .hasMessage("Earning with description 'foo1' does not exist");
    }

    @Test
    void getTotalEarnings_twoResults() {
        List<Earning> earnings = List.of(new Earning("foo1", 3.5, "foo1"), new Earning("bar1", 7, "bar1"));
        BDDMockito.given(earningRepository.findAll()).willReturn(earnings);

        double actualTotalEarnings = underTest.getTotalEarnings();

        Assertions.assertThat(actualTotalEarnings).isNotNull().isNotNegative().isEqualTo(10.5);
    }

    @Test
    void getTotalEarnings_noResults() {
        List<Earning> earnings = List.of();
        BDDMockito.given(earningRepository.findAll()).willReturn(earnings);

        double actualTotalEarnings = underTest.getTotalEarnings();

        Assertions.assertThat(actualTotalEarnings).isNotNull().isZero();
    }
}