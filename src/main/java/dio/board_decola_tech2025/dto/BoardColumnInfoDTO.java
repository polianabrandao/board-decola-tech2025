package dio.board_decola_tech2025.dto;

import dio.board_decola_tech2025.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
    
    
}
