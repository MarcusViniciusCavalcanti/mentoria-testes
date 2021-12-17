package br.com.zonework.coopvotes.application.controller;


import br.com.zonework.coopvotes.application.api.StaveApi;
import br.com.zonework.coopvotes.application.dto.InputNewStaveDto;
import br.com.zonework.coopvotes.application.dto.InputStartSessionDto;
import br.com.zonework.coopvotes.application.dto.InputUpdateStaveDto;
import br.com.zonework.coopvotes.application.dto.StaveDto;
import br.com.zonework.coopvotes.application.usecase.CancelarStave;
import br.com.zonework.coopvotes.application.usecase.CreatorStave;
import br.com.zonework.coopvotes.application.usecase.StartSessionVote;
import br.com.zonework.coopvotes.application.usecase.UpdatorStave;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/staves")
@RequiredArgsConstructor
public class StaveEndpoint implements StaveApi {

    private final CreatorStave creatorStave;
    private final StartSessionVote startSessionVote;
    private final UpdatorStave updatorStave;
    private final CancelarStave cancelarStave;

    @Override
    @PostMapping
    public ResponseEntity<StaveDto> create(InputNewStaveDto inputNewStaveDto) {
        log.info(
            "Receive request in /staves with payload {}", inputNewStaveDto);
        var response = creatorStave.create(inputNewStaveDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PatchMapping("/start-session-vote/{id}")
    public ResponseEntity<Void> startSession(
        @PathVariable("id") Long id,
        InputStartSessionDto inputStartSessionDto) {
        log.info(
            "Receive request in /start-session-vote/{} with payload {}", id, inputStartSessionDto);
        startSessionVote.startSession(id, inputStartSessionDto);

        return ResponseEntity.accepted().build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<StaveDto> update(
        @PathVariable("id") Long id,
        InputUpdateStaveDto inputUpdateStaveDto) {
        log.info("Receive request in /stave/{} with payload {} to update", id, inputUpdateStaveDto);
        var response = updatorStave.update(id, inputUpdateStaveDto);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long id) {
        log.info("Receive request in /stave/{} to cancel", id);
        cancelarStave.cancelStave(id);
        return ResponseEntity.noContent().build();
    }
}
