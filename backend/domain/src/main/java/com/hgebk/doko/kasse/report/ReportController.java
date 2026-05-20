package com.hgebk.doko.kasse.report;

import com.hgebk.doko.kasse.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = ReportController.PATH)
public class ReportController {
    public static final String PATH = Application.BASE_PATH + "/reports";

    private final ReportService reportService;
    private final SemesterReportModelAssembler semesterReportModelAssembler;
    private final CashReportModelAssembler cashReportModelAssembler;

    @Autowired
    public ReportController(
            ReportService reportService,
            SemesterReportModelAssembler semesterReportModelAssembler,
            CashReportModelAssembler cashReportModelAssembler
    ) {
        this.reportService = reportService;
        this.semesterReportModelAssembler = semesterReportModelAssembler;
        this.cashReportModelAssembler = cashReportModelAssembler;
    }

    @GetMapping("/cash")
    public EntityModel<CashReportDTO> getCashReport() {
        return cashReportModelAssembler.toModel(reportService.getCashReport());
    }

    @GetMapping("/semester")
    public EntityModel<SemesterReportDTO> getSemesterReport(@RequestParam Optional<String> semester) {
        return semesterReportModelAssembler.toModel(reportService.getSemesterReport(semester));
    }
}
