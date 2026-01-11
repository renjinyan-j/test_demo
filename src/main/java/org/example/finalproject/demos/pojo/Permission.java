package org.example.finalproject.demos.pojo;

import lombok.Data;

@Data
public class Permission {
    private Long id;
    private Long parentId;
    private String permissionName;
    private String permissionCode;
    private String url;
    private String method;
    private Integer type;
}
