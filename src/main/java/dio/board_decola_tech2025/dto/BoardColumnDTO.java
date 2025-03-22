package dio.board_decola_tech2025.dto;

import dio.board_decola_tech2025.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnKindEnum kind, int cardsAmount) {


}
