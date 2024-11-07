package org.spring.generic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.spring.generic.exception.NoContentException;
import org.spring.generic.repo.GenericRepository;
import org.spring.generic.service.GenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class GenericController<T> {

    private final GenericService<T> genericService;

    private Class< T > type;

    public GenericController(GenericRepository<T> genericRepository) {
        this.genericService = new GenericService<T>(genericRepository) {};
    }

    @Operation(summary = "Fetch All", description = "Retrieve all data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",content = { @Content(schema = @Schema())}, description = "Data retrieve successfully!" ),
            @ApiResponse(responseCode = "404",content = {@Content(schema = @Schema())}, description = "Data not found!" ),
            @ApiResponse(responseCode = "500",content = {@Content(schema = @Schema())}, description = "Internal server error!" )
    })
    @GetMapping
    public ResponseEntity<?> getAllByPage(
            @RequestParam(defaultValue = "false", required = false) Boolean isList,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "id,asc", required = false) String[] sort){

        if(isList){
            List<T> objectList = genericService.findAll();
            if(ObjectUtils.isEmpty(objectList)){
                throw new NoContentException("No content found!");
            }
            return ResponseEntity.ok(objectList);
        }
        Pageable pageable = setPageRequest(page, size,sort);
        Page<T> pageObject = genericService.findAll(pageable);
        if(ObjectUtils.isEmpty(pageObject.getContent())){
            throw new NoContentException("No content found!");
        }
        return ResponseEntity.ok(pageObject);
    }

    @Operation(summary = "Fetch by id", description = "Retrieve data by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",content = {@Content(schema = @Schema())}, description = "Data retrieve successfully!" ),
            @ApiResponse(responseCode = "404",content = {@Content(schema = @Schema())}, description = "Data not found!" ),
            @ApiResponse(responseCode = "500",content = {@Content(schema = @Schema())}, description = "Internal server error!" )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOneById(@PathVariable Long id){
        return new ResponseEntity<T>(genericService.findById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create", description = "Create new data")
    @ApiResponses({
            @ApiResponse(responseCode = "200",content = {@Content(schema = @Schema())}, description = "Data created successfully!" ),
            @ApiResponse(responseCode = "400",content = {@Content(schema = @Schema())}, description = "Bad request!" ),
            @ApiResponse(responseCode = "500",content = {@Content(schema = @Schema())}, description = "Internal server error!" )
    })
    @PostMapping
    public ResponseEntity<T> save(@RequestBody @Valid T entity){
        return ResponseEntity.ok(genericService.save(entity));
    }

    @Operation(summary = "Update", description = "Update existing data")
    @ApiResponses({
            @ApiResponse(responseCode = "200",content = {@Content(schema = @Schema())}, description = "Data updated successfully!" ),
            @ApiResponse(responseCode = "400",content = {@Content(schema = @Schema())}, description = "Bad request!" ),
            @ApiResponse(responseCode = "404",content = {@Content(schema = @Schema())}, description = "Data not found!" ),
            @ApiResponse(responseCode = "500",content = {@Content(schema = @Schema())}, description = "Internal server error!" )
    })
    @PutMapping
    public ResponseEntity<T> update(@RequestBody @Valid T entity){
        return ResponseEntity.ok(genericService.save(entity));
    }

    @Operation(summary = "Delete by id", description = "Delete by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",content = {@Content(schema = @Schema())}, description = "Data delete successfully!" ),
            @ApiResponse(responseCode = "400",content = {@Content(schema = @Schema())}, description = "Bad request!" ),
            @ApiResponse(responseCode = "404",content = {@Content(schema = @Schema())}, description = "Data not found!" ),
            @ApiResponse(responseCode = "500",content = {@Content(schema = @Schema())}, description = "Internal server error!" )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        genericService.delete(id);
        return ResponseEntity.ok("Data deleted successfully!");
    }

    private Pageable setPageRequest(Integer page, Integer size, String[] sort){
        if(page != null && size != null){
            String sortField = sort[0] == null ? "id" : sort[0];
            String sortDirection = sort[1] == null ? "asc" : sort[1];
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort.Order order = new Sort.Order(direction, sortField);
            return PageRequest.of(page-1, size, Sort.by(order));
        }
        return null;
    }

}
