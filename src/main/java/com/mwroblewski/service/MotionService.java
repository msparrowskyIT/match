package com.mwroblewski.service;

import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.OutterMotion;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MotionService {

    public List<OutterMotion> filterOutterMotions(Collection<OutterMotion> motions, AdjacencyType type) {
        return motions
                .stream()
                .filter(m -> type.equals(m.getType()))
                .collect(Collectors.toList());
    }

}
