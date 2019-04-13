package com.pucrs.distribuida;

import java.util.Collections;
import java.util.List;

public class Node {
    private final List<File> files;

    public Node(List<File> files) {
        this.files = files;
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }
}
