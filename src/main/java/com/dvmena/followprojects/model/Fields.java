package com.dvmena.followprojects.model;

public enum Fields {
    OWNER("Owner"),
    NAME("Name"),
    DESCRIPTION("Description"),
    LINK("Link");

    private String field;
    Fields(String field){;
         this.field = field;
    }

    public String getField() {
        return field;
    }
}
