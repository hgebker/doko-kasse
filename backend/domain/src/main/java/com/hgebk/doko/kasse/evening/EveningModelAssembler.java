package com.hgebk.doko.kasse.evening;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Relation(collectionRelation = "evenings", itemRelation = "evening")
public class EveningModelAssembler implements RepresentationModelAssembler<Evening, EveningDTO> {
    @Override
    public EveningDTO toModel(Evening evening) {
        List<EveningResultDTO> results = evening.getResults();
        EveningResults resultsDomain = new EveningResults(results);

        EveningDTO dto = new EveningDTO();
        dto.setDate(evening.getDate());
        dto.setSemester(evening.getSemester());
        dto.setResults(results);
        dto.setSum(resultsDomain.getSum());
        dto.setAvg(resultsDomain.getAvg());
        dto.setMin(resultsDomain.getMinResult().orElse(null));
        dto.setMax(resultsDomain.getMaxResult().orElse(null));

        return dto.add(linkTo(methodOn(EveningController.class).getEvening(evening.getDate())).withSelfRel());
    }
}
