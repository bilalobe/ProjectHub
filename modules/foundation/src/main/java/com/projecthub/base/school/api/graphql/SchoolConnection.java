package com.projecthub.base.school.api.graphql;

import com.projecthub.base.school.api.dto.SchoolDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class SchoolConnection {
    private List<SchoolDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public static SchoolConnection from(Page<SchoolDTO> page) {
        return SchoolConnection.builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
