package com.example.HM.Domain.Concern.Controller;

import com.example.HM.Domain.Concern.Dto.CreateConcernRequestDto;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Service.ConcernService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ConcernController {

    private final ConcernService concernService;

    @Operation(summary = "고민 목록 조회", description = "모든 고민 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Concern.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public List<Concern> getAllConcerns() {
        return concernService.getAllConcerns();
    }

    @Operation(summary = "특정 고민 조회", description = "고유 ID를 통해 특정 고민을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Concern.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 고민")
    })
    @GetMapping("/{id}")
    public Concern getConcernById(@PathVariable Long id) {
        return concernService.getConcernById(id);
    }


    @Operation(summary = "고민 생성", description = "새로운 고민을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "고민 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Concern.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public Concern createConcern(@RequestBody CreateConcernRequestDto request) {
        return concernService.createConcern(request.getTitle(), request.getDescription(), request.getDeadline());
    }

    @Operation(summary = "고민 해결 (직접)", description = "직접 작성한 해결 내용을 통해 고민을 해결합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해결 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Concern.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 고민")
    })
    @PostMapping("/{id}/resolve")
    public Concern resolveConcern(@PathVariable Long id, @RequestParam String solutionContent) {
        return concernService.resolveConcern(id, solutionContent);
    }

    @Operation(summary = "고민 해결 (AI)", description = "AI를 통해 고민 해결 내용을 자동으로 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해결 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Concern.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 고민")
    })
    @PostMapping("/{id}/ai-resolve")
    public Concern resolveWithAI(@PathVariable Long id) {
        return concernService.resolveWithAI(id);
    }
}
