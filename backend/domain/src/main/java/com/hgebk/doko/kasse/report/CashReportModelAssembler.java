package com.hgebk.doko.report;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CashReportModelAssembler
        implements RepresentationModelAssembler<CashReportDTO, EntityModel<CashReportDTO>> {

    @Override
    public EntityModel<CashReportDTO> toModel(CashReportDTO cashReport) {
        return EntityModel.of(cashReport).add(linkTo(methodOn(ReportController.class).getCashReport()).withSelfRel());
    }
}
