package com.hgebk.doko.kasse.report;

import com.hgebk.doko.kasse.semester.SemesterController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SemesterReportModelAssembler
        implements RepresentationModelAssembler<SemesterReportDTO, EntityModel<SemesterReportDTO>> {
    @Override
    public EntityModel<SemesterReportDTO> toModel(SemesterReportDTO semesterReport) {
        EntityModel<SemesterReportDTO> model = EntityModel
                .of(semesterReport)
                .add(linkTo(methodOn(ReportController.class).getSemesterReport(Optional.ofNullable(semesterReport.getSemesterKey()))).withSelfRel());

        if (semesterReport.getSemesterKey() != null) {
            model.add(linkTo(methodOn(SemesterController.class).getSemester(semesterReport.getSemesterKey())).withRel(
                    "semester"));
        }

        return model;

    }
}
