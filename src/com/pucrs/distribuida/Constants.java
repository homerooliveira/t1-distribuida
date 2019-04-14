package com.pucrs.distribuida;

public class Constants {
    static final int SUPER_NODE_RECEIVE_REQUEST_FROM_SUPER_NODE = 1;
    static final int SUPER_NODE_SEND_REQUEST_TO_SUPER_NODE = 1;

    static final int SUPER_NODE_RECEIVE_FILES_FROM_SUPER_NODE = 2;
    static final int SUPER_NODE_SEND_FILES_TO_SUPER_NODE = 2;

    static final int SUPER_NODE_RECEIVE_FILES_FROM_NODE = 3;
    static final int NODE_SEND_FILES_TO_SUPER_NODE = 3;

    static final int SUPER_NODE_RECEIVE_REQUEST_FROM_NODE = 4;
    static final int NODE_SEND_REQUEST_TO_SUPER_NODE = 4;

    static final int SUPER_NODE_SEND_FILES_TO_NODE = 5;
    static final int NODE_RECEIVE_FILES_FROM_SUPER_NODE = 5;

    static final int SUPER_NODE_RECEIVE_LIFE_SIGNAL_FROM_NODE = 6;
    static final int NODE_SEND_LIFE_SIGNAL_TO_SUPER_NODE = 6;

    static final int NODE_REQUEST_FILE_TO_NODE = 7;
    static final int NODE_RECEIVE_FILE_FROM_NODE = 7;
}
