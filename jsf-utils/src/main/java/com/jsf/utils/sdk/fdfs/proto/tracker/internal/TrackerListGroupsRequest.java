package com.jsf.utils.sdk.fdfs.proto.tracker.internal;

import com.jsf.utils.sdk.fdfs.proto.CmdConstants;
import com.jsf.utils.sdk.fdfs.proto.FdfsRequest;
import com.jsf.utils.sdk.fdfs.proto.ProtoHead;

/**
 * 列出分组命令
 * 
 * @author tobato
 *
 */
public class TrackerListGroupsRequest extends FdfsRequest {

    public TrackerListGroupsRequest() {
        head = new ProtoHead(CmdConstants.TRACKER_PROTO_CMD_SERVER_LIST_GROUP);
    }
}
