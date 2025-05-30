package com.revify.monolith.orders.controller;

import com.revify.monolith.geo.model.shared.Destination;
import com.revify.monolith.orders.models.PathSegment;
import com.revify.monolith.orders.models.PathSegmentDTO;
import com.revify.monolith.orders.service.PathService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/path")

@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @PostMapping("/split")
    public ResponseEntity<PathSegmentDTO> splitOrder(@RequestParam ObjectId orderId, @RequestBody Destination point) {
        //get end point of current courier delivery possibility and calculate new order from point to destination
        PathSegment newSplitOrder = pathService.createNewSplitOrder(orderId, point);
        if (newSplitOrder != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(PathSegmentDTO.from(newSplitOrder));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/changeStatus")
    public void changeStatus(@RequestParam ObjectId fragmentId, @RequestBody Map<String, Boolean> pathChange) {
        pathService.changePathAcceptanceStatus(fragmentId, pathChange);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, List<PathSegmentDTO>>> getUserPaths() {
        Map<String, List<PathSegmentDTO>> response = new HashMap<>();
        response.computeIfAbsent("assigned", k -> new ArrayList<>())
                .addAll(pathService.getCurrentUserPaths().stream().map(PathSegmentDTO::from).toList());
        response.computeIfAbsent("created", k -> new ArrayList<>())
                .addAll(pathService.getCreatedByUserPaths().stream().map(PathSegmentDTO::from).toList());
        response.computeIfAbsent("involved", k -> new ArrayList<>())
                .addAll(pathService.getUserItemsInvolvedInPaths().stream().map(PathSegmentDTO::from).toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/find")
    public ResponseEntity<List<PathSegmentDTO>> getAllPaths(@RequestBody PathsRequest pathsRequest, @RequestParam Integer limit, @RequestParam Integer offset) {
        List<PathSegment> pathsFromPoint = pathService.findPathsFromPoint(pathsRequest.nearEnd, pathsRequest.start, limit, offset);
        if (pathsFromPoint == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(pathsFromPoint.stream().map(PathSegmentDTO::from).toList());
    }

    public record PathsRequest(Destination nearEnd, Destination start) {
    }
}
