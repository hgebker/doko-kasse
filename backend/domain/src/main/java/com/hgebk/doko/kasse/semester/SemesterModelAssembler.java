package com.hgebk.doko.kasse.semester;

import com.hgebk.doko.kasse.evening.EveningController;
import com.hgebk.doko.kasse.report.ReportController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SemesterModelAssembler implements RepresentationModelAssembler<Semester, EntityModel<Semester>> {
    @Override
    public EntityModel<Semester> toModel(Semester semester) {
        return EntityModel
                .of(semester)
                .add(linkTo(methodOn(SemesterController.class).getAllSemester()).withSelfRel())
                .add(linkTo(methodOn(EveningController.class).getEvenings(Optional.of(semester.getKey()))).withRel(
                        "evenings"))
                .add(linkTo(methodOn(ReportController.class).getSemesterReport(Optional.ofNullable(semester.getKey()))).withRel(
                        "report"));
    }
}
