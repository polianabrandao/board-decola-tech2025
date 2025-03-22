package dio.board_decola_tech2025.service;

import dio.board_decola_tech2025.dto.BoardColumnInfoDTO;
import dio.board_decola_tech2025.dto.CardDetailsDTO;
import dio.board_decola_tech2025.exception.CardBlockedException;
import dio.board_decola_tech2025.exception.CardFinishedException;
import dio.board_decola_tech2025.exception.EntityNotFoundException;
import dio.board_decola_tech2025.persistence.dao.BlockDAO;
import dio.board_decola_tech2025.persistence.dao.CardDAO;
import dio.board_decola_tech2025.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static dio.board_decola_tech2025.persistence.entity.BoardColumnKindEnum.CANCEL;
import static dio.board_decola_tech2025.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card do id %s nao foi encontrado".formatted(cardId)));

            if(dto.blocked()){
                var message = "O card %s esta bloqueado. é necessario desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }

            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId())).findFirst().orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card ja foi finalizado.");
            }
            var nextColumn = boardColumnsInfo.stream().filter(bc -> bc.order() == currentColumn.order() + 1).findFirst().orElseThrow(() -> new IllegalStateException("O card esta cancelado."));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card do id %s nao foi encontrado".formatted(cardId)));

            if(dto.blocked()){
                var message = "O card %s esta bloqueado. é necessario desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId())).findFirst().orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card ja foi finalizado.");
            }
            boardColumnsInfo.stream().filter(bc -> bc.order() == currentColumn.order() + 1).findFirst().orElseThrow(() -> new IllegalStateException("O card esta cancelado."));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card do id %s nao foi encontrado".formatted(id)));

            if(dto.blocked()){
                var message = "O card %s ja esta bloqueado.".formatted(id);
                throw new CardBlockedException(message);
            }

            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId())).findFirst().orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "O card esta em uma coluna do tipo %s e nao pode ser bloqueado.".formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card do id %s nao foi encontrado".formatted(id)));

            if(!dto.blocked()){
                var message = "O card %s nao esta bloqueado.".formatted(id);
                throw new CardBlockedException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
}
